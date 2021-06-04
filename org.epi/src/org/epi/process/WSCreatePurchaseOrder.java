package org.epi.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.epi.ws.model.API_Model_POHeader;
import org.epi.ws.model.API_Model_POLines;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class WSCreatePurchaseOrder extends SvrProcess {
	
	private int p_AD_Org_ID = 0;
	private String p_JSONHeader = "";
	private String p_JSONDetail = "";

	public static CLogger log = CLogger.getCLogger(PO.class);

	@Override
	protected void prepare() {
	
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;

			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = (int) para[i].getParameterAsInt();

			else if (name.equals("JSONHeader"))
				p_JSONHeader = (String) para[i].getParameterAsString();
			
			else if (name.equals("JSONDetail"))
				p_JSONDetail = (String) para[i].getParameterAsString();
			
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		String rslt = "";
		
		try {
		
			String JSonHeaderString = "";
			String JSonDetailsString = "";
	
			int AD_Org_ID = 0;
				
			AD_Org_ID = p_AD_Org_ID;
			JSonHeaderString = p_JSONHeader;
			JSonDetailsString = p_JSONDetail;
			
			Gson gson = new Gson();
			JsonArray jsonHeader = gson.fromJson(JSonHeaderString, JsonArray.class);
			JsonArray jsonDetails = gson.fromJson(JSonDetailsString, JsonArray.class);
	
			API_Model_POHeader dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_POHeader.class);
			API_Model_POLines[] dataDetails = gson.fromJson(jsonDetails.toString(), API_Model_POLines[].class);
			
			MOrder po = new MOrder(getCtx(), 0, get_TrxName());
			
			po.setAD_Org_ID(AD_Org_ID);
			po.setM_Warehouse_ID(dataHeader.M_Warehouse_ID);
			
			Timestamp DateOrdered = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.DateOrdered);
	
			po.setDateOrdered(DateOrdered);
			po.setIsSOTrx(false);
			po.setC_DocTypeTarget_ID();
			po.setC_BPartner_ID(dataHeader.C_BPartner_ID);
			po.setC_BPartner_Location_ID(dataHeader.C_BPartner_ID);
			po.setM_PriceList_ID(dataHeader.M_PriceList_ID);
			po.setDescription(dataHeader.Description);
			po.set_CustomColumn("Wise_Order_ID", dataHeader.Wise_Order_ID);
			po.set_CustomColumn("Wise_PR_Number", dataHeader.Wise_PR_Number);
			po.set_CustomColumn("Wise_PO_Number", dataHeader.Wise_PO_Number);
			po.set_CustomColumn("Wise_Title", dataHeader.Wise_Title);
			po.set_CustomColumn("Wise_PO_Type", dataHeader.Wise_PO_Type);
			po.set_CustomColumn("Wise_Unit_Type", dataHeader.Wise_Unit_Type);
			po.set_CustomColumn("Wise_Delivery_Point", dataHeader.Wise_Delivery_Point);
			po.set_CustomColumn("Wise_Remark", dataHeader.Wise_Remark);
			
			int noLine = 0;

			if(po.save()) {
				
				for (API_Model_POLines DataDetail : dataDetails) {
					noLine = noLine+10;
					MOrderLine line = new MOrderLine(getCtx(), 0, get_TrxName());
					
					line.setC_Order_ID(DataDetail.C_Order_ID);
					line.setC_Charge_ID(DataDetail.C_Charge_ID);
					line.setC_UOM_ID(DataDetail.C_UOM_ID);
					line.setQtyOrdered(DataDetail.QtyOrdered);
					line.setPriceList(DataDetail.PriceList);
					line.setPriceEntered(DataDetail.PriceEntered);
					line.setPriceActual(DataDetail.PriceActual);
					line.setLineNetAmt(DataDetail.LineNetAmt);

					line.saveEx();
						
				}
							
			}
			
			if(noLine > 0) {
				
				po.processIt(MOrder.ACTION_Complete);
				
				if(po.save()) {
					
					rslt = ""+po.getDocumentNo();
					
				}
				
			}
		
		} catch (Exception e) {

			rollback();
		
		}
		
		
		
		return rslt;
	}

}
