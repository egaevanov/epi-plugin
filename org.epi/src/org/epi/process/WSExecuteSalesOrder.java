package org.epi.process;

import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_SOHeader;
import org.epi.ws.model.API_Model_SOLines;

public class WSExecuteSalesOrder {
	
public static Integer CreateSalesOrder(int AD_Client_ID, int AD_Org_ID, API_Model_SOHeader dataHeader, API_Model_SOLines[] dataDetails,Properties ctx , String trxName,int M_Warehouse_ID,int C_BPartner_ID) {
		
		Integer rs = 0;
		
		try {		
						
			MOrder so = new MOrder(ctx, 0, trxName);
			
			so.setAD_Org_ID(AD_Org_ID);
			
			StringBuilder SQLGetDocumentType = new StringBuilder();
			SQLGetDocumentType.append("SELECT Description::NUMERIC ");
			SQLGetDocumentType.append(" FROM AD_Param ");
			SQLGetDocumentType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocumentType.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocumentType.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetDocumentType.append(" AND Name = 'TargetDocumentType'");
			Integer C_DocType_ID = DB.getSQLValue(trxName, SQLGetDocumentType.toString());
			
			so.setC_DocTypeTarget_ID(C_DocType_ID);
			Timestamp DateOrdered = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.so_date);
			
			
			
			so.setDocumentNo(dataHeader.so_no);
			so.setPOReference(dataHeader.po_cust);
			so.set_ValueNoCheck("OrderType", dataHeader.so_type);
			so.set_ValueNoCheck("UnitType", dataHeader.unit_type);
			so.setDescription(dataHeader.remark);
			so.setDateOrdered(DateOrdered);
			so.setDatePromised(DateOrdered);
			so.setIsSOTrx(true);
			
			
			so.setC_BPartner_ID(C_BPartner_ID);
			
			StringBuilder SQLGetBPLoc = new StringBuilder();
			SQLGetBPLoc.append("SELECT C_BPartner_Location_ID  ");
			SQLGetBPLoc.append(" FROM C_BPartner_Location ");
			SQLGetBPLoc.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetBPLoc.append(" AND C_BPartner_ID = "+C_BPartner_ID);
			
			Integer C_BPartner_Location_ID =DB.getSQLValue(trxName, SQLGetBPLoc.toString());
			so.setC_BPartner_Location_ID(C_BPartner_Location_ID);
			so.setM_Warehouse_ID(M_Warehouse_ID);		

			StringBuilder SQLGetPriceList = new StringBuilder();
			SQLGetPriceList.append("SELECT Description::NUMERIC ");
			SQLGetPriceList.append(" FROM AD_Param ");
			SQLGetPriceList.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPriceList.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPriceList.append(" AND Value = 'SO_DefaultParameter'");
			SQLGetPriceList.append(" AND Name = 'PriceList'");
			
			Integer M_Pricelist_ID = DB.getSQLValue(trxName, SQLGetPriceList.toString());
			
			StringBuilder SQLGetComAgent = new StringBuilder();
			SQLGetComAgent.append("SELECT Description::NUMERIC ");
			SQLGetComAgent.append(" FROM AD_Param ");
			SQLGetComAgent.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetComAgent.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetComAgent.append(" AND Value = 'SO_DefaultParameter'");
			SQLGetComAgent.append(" AND Name = 'SalesRep'");
			
			Integer SalesRep_ID = DB.getSQLValue(trxName, SQLGetComAgent.toString());
		
			so.setSalesRep_ID(SalesRep_ID);
			so.setC_PaymentTerm_ID(dataHeader.termin_id);
			
			so.setM_PriceList_ID(M_Pricelist_ID);
			
			StringBuilder SQLCheckCurr = new StringBuilder();
			
			SQLCheckCurr.append("SELECT C_Currency_ID ");
			SQLCheckCurr.append(" FROM C_Currency ");
			SQLCheckCurr.append(" WHERE ISO_Code = '"+dataHeader.currency+"'");

			Integer C_Currency_ID = DB.getSQLValueEx(trxName, SQLCheckCurr.toString());

			
			so.setC_Currency_ID(C_Currency_ID);
			so.setIsSOTrx(true);
			StringBuilder SQLGetPayRule = new StringBuilder();
			SQLGetPayRule.append("SELECT Description ");
			SQLGetPayRule.append(" FROM AD_Param ");
			SQLGetPayRule.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPayRule.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPayRule.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetPayRule.append(" AND Name = 'PaymentRule'");
			
			String payRule = DB.getSQLValueString(trxName, SQLGetPayRule.toString());
			
			so.setPaymentRule(payRule);
			
			StringBuilder SQLGetDocStatus = new StringBuilder();
			SQLGetDocStatus.append("SELECT Description ");
			SQLGetDocStatus.append(" FROM AD_Param ");
			SQLGetDocStatus.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocStatus.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocStatus.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetDocStatus.append(" AND Name = 'DocStatus'");
			
			String docStatus = DB.getSQLValueString(trxName, SQLGetDocStatus.toString());
			
//			so.setDocStatus(docStatus);
			
			so.saveEx();
						
			int noLine = 0;

			if(so.save()) {
				
				for (API_Model_SOLines DataDetail : dataDetails) {
					noLine = noLine+1;
					MOrderLine line = new MOrderLine(ctx, 0, trxName);
					
					line.setLine(noLine);

					line.setC_Order_ID(so.getC_Order_ID());
					line.setM_Warehouse_ID(so.getM_Warehouse_ID());
					line.setC_BPartner_ID(so.getC_BPartner_ID());
					line.setC_BPartner_Location_ID(so.getC_BPartner_Location_ID());
					line.setDescription(DataDetail.description);
					line.setC_Charge_ID(DataDetail.charge_id);
					line.setQtyOrdered(DataDetail.qty_order);
					line.setPriceList(DataDetail.price);
					line.setPriceEntered(DataDetail.price);
					line.setPriceActual(DataDetail.price);
					line.setLineNetAmt(DataDetail.price.multiply(DataDetail.qty_order));		

					line.saveEx();
						
				}
							
			}
			
			
			
			if(noLine > 0) {
				so.setDocAction(docStatus);
				so.processIt(MOrder.ACTION_Complete);
				
				if(so.save()) {
					
					rs = so.getC_Order_ID();
					
				}
				
			}
		
		} catch (Exception e) {

			rs = 0;
		
		}
		
		return rs;
		
		
	}

public static Integer CheckWarehouse(int AD_Client_ID, int AD_Org_ID, API_Model_SOHeader dataHeader, Properties ctx , String trxName) {

	Integer result = 0; 
	
	StringBuilder SQLCheckWarehouse = new StringBuilder();
	
	SQLCheckWarehouse.append("SELECT M_Warehouse_ID ");
	SQLCheckWarehouse.append(" FROM M_Warehouse ");
	SQLCheckWarehouse.append(" WHERE Value = '"+dataHeader.location_id+"'");

	Integer M_Warehouse_ID = DB.getSQLValueEx(trxName, SQLCheckWarehouse.toString());

	
	if(M_Warehouse_ID > 0) {
		result = M_Warehouse_ID;
	}else if(M_Warehouse_ID <= 0 || M_Warehouse_ID == null) {
		
		
		MWarehouse newWH = new MWarehouse(ctx, M_Warehouse_ID, trxName);
		newWH.setAD_Org_ID(AD_Org_ID);	
		newWH.setValue(dataHeader.location_id);
		newWH.setName("Data not yes sync");
		newWH.setDescription("Data not yes sync");
		newWH.setIsDisallowNegativeInv(false);
		newWH.setSeparator("*");
		
		if(newWH.save()) {
			
			MLocator locator = new MLocator(ctx, 0, trxName);
			locator.setAD_Org_ID(AD_Org_ID);
			locator.setIsActive(true);
			locator.setM_Warehouse_ID(newWH.getM_Warehouse_ID());
			locator.setValue(dataHeader.location_id);
			locator.setIsDefault(true);
			locator.setPriorityNo(10);
			
			locator.setX("1");
			locator.setY("1");
			locator.setZ("1");
			locator.saveEx();
			
		}
		
		result = newWH.getM_Warehouse_ID();
	}

	return result;
}

public static Integer CheckCustomer(int AD_Client_ID, int AD_Org_ID, API_Model_SOHeader dataHeader, Properties ctx , String trxName) {

	Integer result = 0; 
	
	StringBuilder SQLCheckVendor = new StringBuilder();
	
	SQLCheckVendor.append("SELECT C_BPartner_ID ");
	SQLCheckVendor.append(" FROM C_BPartner ");
	SQLCheckVendor.append(" WHERE Value = '"+dataHeader.customer_id+"'");

	Integer C_BPartner_ID = DB.getSQLValueEx(trxName, SQLCheckVendor.toString());

	
	if(C_BPartner_ID > 0) {
		result = C_BPartner_ID;
	}else if(C_BPartner_ID <= 0 || C_BPartner_ID == null) {
		
		
		MBPartner newVendor = new MBPartner(ctx, C_BPartner_ID, trxName);
		newVendor.setAD_Org_ID(AD_Org_ID);	
		newVendor.setIsCustomer(true);
		newVendor.setIsActive(true);
		
		newVendor.setValue(dataHeader.customer_id);
		newVendor.setName("Data not yet sync");
		newVendor.setName2("Data not yet sync");
		
		if(newVendor.save()){
			
			MLocation location = new MLocation(ctx, 0, trxName);
			location.setAD_Org_ID(AD_Org_ID);
			location.setIsActive(true);
			location.setAddress1("Data not yet sync");
			location.setC_Country_ID(209);
			location.setPostal("Data not yet sync");
			location.saveEx();
			
			
			if(location!= null){
				MBPartnerLocation BpLoc = new MBPartnerLocation(ctx, 0, trxName);
				BpLoc.setIsActive(true);
				BpLoc.setC_BPartner_ID(newVendor.getC_BPartner_ID());
				BpLoc.setC_Location_ID(location.getC_Location_ID());
				BpLoc.setPhone("Data not yet sync");
				BpLoc.setPhone2("Data not yet sync");
				BpLoc.setIsShipTo(true);
				BpLoc.setIsPayFrom(true);
				BpLoc.setIsBillTo(true);
				BpLoc.setIsRemitTo(true);
				BpLoc.saveEx();			
			
			}
		}
		
		result = newVendor.getC_BPartner_ID();
	}

	return result;
}


}
