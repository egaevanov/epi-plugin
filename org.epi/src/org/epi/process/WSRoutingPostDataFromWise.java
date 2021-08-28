package org.epi.process;

import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.epi.ws.model.API_Model_AssetDisposal;
import org.epi.ws.model.API_Model_BAST;
import org.epi.ws.model.API_Model_GoodIssue;
import org.epi.ws.model.API_Model_GoodIssueLines;
import org.epi.ws.model.API_Model_GoodReceipt;
import org.epi.ws.model.API_Model_GoodReceiptLines;
import org.epi.ws.model.API_Model_MasterAsset;
import org.epi.ws.model.API_Model_MasterCustomer;
import org.epi.ws.model.API_Model_MasterLocation;
import org.epi.ws.model.API_Model_POHeader;
import org.epi.ws.model.API_Model_POLines;
import org.epi.ws.model.API_Model_SOHeader;
import org.epi.ws.model.API_Model_SOLines;
import org.epi.ws.model.API_Model_StockOpname;
import org.epi.ws.model.API_Model_StockOpnameLines;
import org.epi.ws.model.API_Model_TimeSheetHeader;
import org.epi.ws.model.API_Model_TimeSheetLines;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WSRoutingPostDataFromWise extends SvrProcess{
	
	
	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_DataType = 0;
	private String p_PreparedBy ="";
	private String p_LastApprovedBy = "";
	private String p_JSON = "";
	
	private final int DATA_TYPE_DISPOSAL_ASSET = 1;
	private final int DATA_TYPE_GOOD_ISSUE = 2;
	private final int DATA_TYPE_STOCK_OPNAME = 3;
	private final int DATA_TYPE_GOOD_RECEIPT = 4;
	private final int DATA_TYPE_PURCHASE_ORDER = 5;
	private final int DATA_TYPE_SALES_ORDER = 6;
	private final int DATA_TYPE_BAST = 7;
	private final int DATA_TYPE_TIMESHEET = 8;
	private final int DATA_TYPE_MASTER_ASSET = 9;
	private final int DATA_TYPE_MASTER_CUSTOMER = 10;
	private final int DATA_TYPE_MASTER_LOCATION = 13;


	

	public static CLogger log = CLogger.getCLogger(PO.class);


	@Override
	protected void prepare() {
	
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Client_ID"))
				p_AD_Client_ID = (int) para[i].getParameterAsInt();
			
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = (int) para[i].getParameterAsInt();

			else if (name.equals("DataType"))
				p_DataType = (int) para[i].getParameterAsInt();
			
			else if (name.equals("PreparedBy"))
				p_PreparedBy = (String) para[i].getParameterAsString();
			
			else if (name.equals("LastApprovedBy"))
				p_LastApprovedBy = (String) para[i].getParameterAsString();
			
			else if (name.equals("Data"))
				p_JSON = (String) para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		System.out.println("AD_Client_ID = "+p_AD_Client_ID);
		System.out.println("AD_Org_ID = " +p_AD_Org_ID);
		System.out.println("TypeData ="+p_DataType);
		System.out.println("PreparedBy = " + p_PreparedBy);
		System.out.println("LastApprovedBy = "+p_LastApprovedBy);
		System.out.println("Data = "+p_JSON);

		
		
	}

	@Override
	protected String doIt() throws Exception {
		
		String result = "";

		result = ValidasiParameter();
		
		
		if(result != "" && !result.isEmpty() && result != null ) {
			
			return result;
		}
		
		
		if(p_DataType == DATA_TYPE_DISPOSAL_ASSET) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_AssetDisposal dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_AssetDisposal.class);
				
			rs = WSExecuteAssetDisposal.CreateAssetDisposal(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result = rs.toString();
			}
			
		}else if(p_DataType == DATA_TYPE_GOOD_ISSUE) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_GoodIssue dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_GoodIssue.class);
			
			JsonArray jsonDetails = gson.fromJson(dataHeader.details.toString(), JsonArray.class);
			API_Model_GoodIssueLines[] dataDetails= gson.fromJson(jsonDetails.toString(), API_Model_GoodIssueLines[].class);
			
			
			HashMap<String, Integer> prod = WSExecuteGoodIssue.CheckProductData(p_AD_Client_ID, p_AD_Org_ID, dataHeader, dataDetails, getCtx(), get_TrxName());
			Integer A_Asset_ID = WSExecuteGoodIssue.CheckAssetData(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());
				
			rs = WSExecuteGoodIssue.CreateGoodIssue(p_AD_Client_ID, p_AD_Org_ID, dataHeader, dataDetails, getCtx(), get_TrxName(),A_Asset_ID,prod);
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Goods Issue transaction has been successfully posted on iDempiere ERP System";
				
			}
			
			
			
		}else if(p_DataType == DATA_TYPE_STOCK_OPNAME) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_StockOpname dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_StockOpname.class);
			
			JsonArray jsonDetails = gson.fromJson(dataHeader.details.toString(), JsonArray.class);
			API_Model_StockOpnameLines[] dataDetails= gson.fromJson(jsonDetails.toString(), API_Model_StockOpnameLines[].class);
					
			HashMap<String, Integer> product = WSExecuteStockOpname.CheckProductData(p_AD_Client_ID, p_AD_Org_ID, dataDetails, getCtx(), get_TrxName());

			rs = WSExecuteStockOpname.CreateStockOpname(p_AD_Client_ID, p_AD_Org_ID, dataHeader, dataDetails, getCtx(), get_TrxName(),product);
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Stock Opname transaction has been successfully posted on iDempiere ERP System";
				
			}
			
					
		}else if(p_DataType == DATA_TYPE_GOOD_RECEIPT) {
			

			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_GoodReceipt dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_GoodReceipt.class);
			
			JsonArray jsonDetails = gson.fromJson(dataHeader.details.toString(), JsonArray.class);
			API_Model_GoodReceiptLines[] dataDetails= gson.fromJson(jsonDetails.toString(), API_Model_GoodReceiptLines[].class);
					
			HashMap<String, Integer> product = WSExecuteGoodReceipt.CheckProductData(p_AD_Client_ID, p_AD_Org_ID, dataDetails, getCtx(), get_TrxName());

			rs = WSExecuteGoodReceipt.CreateGoodReceipt(p_AD_Client_ID, p_AD_Org_ID, dataHeader, dataDetails, getCtx(), get_TrxName(),product);
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Goods Receipt transaction has been successfully posted on iDempiere ERP System";
				
			}
			
		}else if(p_DataType == DATA_TYPE_PURCHASE_ORDER) {
			
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_POHeader dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_POHeader.class);
			
			JsonArray jsonDetails = gson.fromJson(dataHeader.details.toString(), JsonArray.class);
			API_Model_POLines[] dataDetails= gson.fromJson(jsonDetails.toString(), API_Model_POLines[].class);
			
			HashMap<String, Integer> product = WSExecutePurchaseOrder.CheckProductData(p_AD_Client_ID, p_AD_Org_ID, dataHeader,dataDetails, getCtx(), get_TrxName());
			Integer Warehouse = WSExecutePurchaseOrder.CheckWarehouse(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());
			Integer C_BPartner_ID = WSExecutePurchaseOrder.CheckVendor(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());	
	
			rs = WSExecutePurchaseOrder.CreatePurchaseOrder(p_AD_Client_ID, p_AD_Org_ID, dataHeader,dataDetails, getCtx(), get_TrxName(),C_BPartner_ID,Warehouse,product);
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result = rs.toString();
			}
			
		}else if(p_DataType == DATA_TYPE_SALES_ORDER) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_SOHeader dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_SOHeader.class);
			
			JsonArray jsonDetails = gson.fromJson(dataHeader.details.toString(), JsonArray.class);
			API_Model_SOLines[] dataDetails= gson.fromJson(jsonDetails.toString(), API_Model_SOLines[].class);
	
			Integer M_Warehouse_ID= WSExecuteSalesOrder.CheckWarehouse(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());
			Integer C_BPartner_ID = WSExecuteSalesOrder.CheckCustomer(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());	
	
			
			rs = WSExecuteSalesOrder.CreateSalesOrder(p_AD_Client_ID, p_AD_Org_ID, dataHeader,dataDetails, getCtx(), get_TrxName(),M_Warehouse_ID,C_BPartner_ID);
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result = rs.toString();
			}
			
		}else if(p_DataType == DATA_TYPE_BAST) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_BAST dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_BAST.class);
			
			
			Integer A_Asset_ID = WSExecuteBAST.CheckAssetData(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());
			Integer C_BPartner_ID = WSExecuteBAST.CheckCustomer(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());	
	
			rs = WSExecuteBAST.CreateBAST(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName(),A_Asset_ID,C_BPartner_ID);
			
			if(rs <= 0) {
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result = rs.toString();
			
			}
		
		}else if(p_DataType == DATA_TYPE_TIMESHEET) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_TimeSheetHeader dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_TimeSheetHeader.class);
			
			JsonArray jsonDetails = gson.fromJson(dataHeader.details.toString(), JsonArray.class);
			API_Model_TimeSheetLines[] dataDetails= gson.fromJson(jsonDetails.toString(), API_Model_TimeSheetLines[].class);
					
			Integer M_Warehouse_ID = WSExecuteTimesheetDelivery.CheckWarehouse(p_AD_Client_ID, p_AD_Org_ID, dataHeader, getCtx(), get_TrxName());

			rs = WSExecuteTimesheetDelivery.CreateTimesheetDelivery(p_AD_Client_ID, p_AD_Org_ID, dataHeader, dataDetails, getCtx(), get_TrxName(),M_Warehouse_ID);
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Timeshhet Delivery transaction has been successfully posted on iDempiere ERP System";
				
			}
		}else if(p_DataType == DATA_TYPE_MASTER_ASSET) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_MasterAsset dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_MasterAsset.class);
			
					


			rs = WSExecuteMasterAsset.CreateAssetData(p_AD_Client_ID, p_AD_Org_ID, dataHeader,getCtx(), get_TrxName());
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Timeshhet Delivery transaction has been successfully posted on iDempiere ERP System";
				
			}
			
			
		}else if(p_DataType == DATA_TYPE_MASTER_CUSTOMER) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_MasterCustomer dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_MasterCustomer.class);
			
					


			rs = WSExecuteMasterCustomer.CreateCustomer(p_AD_Client_ID, p_AD_Org_ID, dataHeader,getCtx(), get_TrxName());
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Master Customer has been successfully posted on iDempiere ERP System";
				
			}
			
			
		}else if(p_DataType == DATA_TYPE_MASTER_LOCATION) {
			
			String JSonString = p_JSON;
			Integer rs = 0;

			Gson gson = new Gson();
			JsonObject jsonHeader = gson.fromJson(JSonString, JsonObject.class);
			API_Model_MasterLocation dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_MasterLocation.class);
			
					


			rs = WSExecuteMasterLocator.CreateMasterLocator(p_AD_Client_ID, p_AD_Org_ID, dataHeader,getCtx(), get_TrxName());
			
			if(rs <= 0) {
				
				result = "ERROR";
				rollback();
				return "Process Gagal";
			}else {
				
				result ="Master Locator has been successfully posted on iDempiere ERP System";
				
			}
			
			
		}
		
		
		return result;
	}
	
	private String ValidasiParameter() {
		
		String result = "";
		
		//Client And Organization Validation
		if(p_AD_Client_ID <= 0 || p_AD_Org_ID <= 0) {
			
			result = "Parameter Client And Organization is Mandatory";
			return result;
			
		}
		
		//Type Data Validation
		if(p_DataType <= 0) {
			
			result = "Parameter Type Data is Mandatory";
			return result;
		}
				
		//PreparedBy Validation 
		if(p_PreparedBy == null && p_PreparedBy.isEmpty() && p_PreparedBy == "" ) {
			
			result = "Parameter PreparedBy is Mandatory";
			return result;
			
		}
			
		//LastApprovedBy Validation
		if(p_LastApprovedBy == null && p_LastApprovedBy.isEmpty() && p_LastApprovedBy == "" ) {
			
			result = "Parameter LastApproveBy is Mandatory";
			return result;
		}
		
		//JSONDataValidation
		if(p_JSON == null && p_JSON.isEmpty() && p_JSON == "" ) {
			
			result = "Parameter Data is Mandatory";
			return result;
		}
		
		
		
		 
		return result; 
	}
	
	
	

}
