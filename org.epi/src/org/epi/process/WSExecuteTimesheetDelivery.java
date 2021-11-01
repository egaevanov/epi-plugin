package org.epi.process;

import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_TimeSheetHeader;
import org.epi.ws.model.API_Model_TimeSheetLines;

public class WSExecuteTimesheetDelivery {

	public static Integer CreateTimesheetDelivery(int AD_Client_ID, int AD_Org_ID, API_Model_TimeSheetHeader dataHeader,API_Model_TimeSheetLines[] dataDetails, Properties ctx , String trxName,int M_Warehouse_ID) {

		Integer result = 0;
		
		try {
			
			MInOut TimeSheet = new MInOut(ctx, 0, trxName);
			
			StringBuilder SQLCheckLocator = new StringBuilder();
			
			SQLCheckLocator.append("SELECT M_Locator_ID ");
			SQLCheckLocator.append(" FROM M_Locator ");
			SQLCheckLocator.append(" WHERE Value = '"+dataHeader.location_id+"'");

			
			Integer M_Locator_ID = DB.getSQLValueEx(trxName, SQLCheckLocator.toString());
			
			
			TimeSheet.setDocumentNo(dataHeader.ts_no);
			TimeSheet.setAD_Org_ID(AD_Org_ID);
			
			StringBuilder SQLGetPO = new StringBuilder();
			SQLGetPO.append("SELECT C_Order_ID ");
			SQLGetPO.append(" FROM C_Order ");
			SQLGetPO.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPO.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPO.append(" AND IsSoTrx = 'Y'");
			SQLGetPO.append(" AND DocumentNo = '"+dataHeader.so_no+"'");
			Integer C_Order_ID = DB.getSQLValue(trxName, SQLGetPO.toString());	
			
			MOrder ord = new MOrder(ctx, C_Order_ID, trxName);
			
			TimeSheet.setC_Order_ID(ord.getC_Order_ID());
			
			Timestamp DateAcct = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.ts_date);
			TimeSheet.setDateAcct(DateAcct);
			TimeSheet.setMovementDate(DateAcct);
			TimeSheet.setDateOrdered(DateAcct);
			
			
			StringBuilder SQLGetDocType = new StringBuilder();
			SQLGetDocType.append("SELECT Description::NUMERIC ");
			SQLGetDocType.append(" FROM AD_Param ");
			SQLGetDocType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocType.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocType.append(" AND Value = 'TS_DefaultParameter'");
			SQLGetDocType.append(" AND Name = 'DocumentType'");
			Integer C_DocType_ID = DB.getSQLValue(trxName, SQLGetDocType.toString());	
			
			TimeSheet.setC_DocType_ID(C_DocType_ID);
			
			
			TimeSheet.setC_BPartner_ID(ord.getC_BPartner_ID());
			TimeSheet.setC_BPartner_Location_ID(ord.getC_BPartner_Location_ID());
			TimeSheet.setDescription(dataHeader.delivery_note);

			StringBuilder SQLCheckAsset = new StringBuilder();
			SQLCheckAsset.append("SELECT A_Asset_ID ");
			SQLCheckAsset.append(" FROM A_Asset ");
			SQLCheckAsset.append(" WHERE Value = '"+dataHeader.asset_id+"'");

			
			Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
			
			TimeSheet.set_CustomColumn("A_Asset_ID", A_Asset_ID);
			TimeSheet.setM_Warehouse_ID(M_Warehouse_ID);
			TimeSheet.setPriorityRule(ord.getPriorityRule());
//			TimeSheet.setC_Project_ID(dataHeader.project_id);
			
			StringBuilder SQLGetDeliveryRule = new StringBuilder();
			SQLGetDeliveryRule.append("SELECT Description ");
			SQLGetDeliveryRule.append(" FROM AD_Param ");
			SQLGetDeliveryRule.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDeliveryRule.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDeliveryRule.append(" AND Value = 'TS_DefaultParameter'");
			SQLGetDeliveryRule.append(" AND Name = 'DeliveryRule'");
			
			String deliRule = DB.getSQLValueString(trxName, SQLGetDeliveryRule.toString());
				
			TimeSheet.setDeliveryRule(deliRule);
			

			StringBuilder SQLGetDeliveryViaRule = new StringBuilder();
			SQLGetDeliveryViaRule.append("SELECT Description ");
			SQLGetDeliveryViaRule.append(" FROM AD_Param ");
			SQLGetDeliveryViaRule.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDeliveryViaRule.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDeliveryViaRule.append(" AND Value = 'TS_DefaultParameter'");
			SQLGetDeliveryViaRule.append(" AND Name = 'DeliveryVia'");
			String deliViaRule = DB.getSQLValueString(trxName, SQLGetDeliveryViaRule.toString());

			TimeSheet.setDeliveryViaRule(deliViaRule);
			
			TimeSheet.setSalesRep_ID(ord.getSalesRep_ID());
			TimeSheet.setMovementType(MInOut.MOVEMENTTYPE_CustomerShipment);
			TimeSheet.set_ValueOfColumn("URL1", dataHeader.url_1);
			TimeSheet.set_ValueOfColumn("URL2", dataHeader.url_2);
			TimeSheet.set_ValueOfColumn("URL3", dataHeader.url_3);
			TimeSheet.set_ValueOfColumn("URL4", dataHeader.url_4);
			

			Timestamp startdate = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.startdate);
			Timestamp enddate = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.enddate);

			TimeSheet.set_ValueOfColumn("startdate", startdate);
			TimeSheet.set_ValueOfColumn("enddate", enddate);
			
			
			StringBuilder SQLGetDocStatus = new StringBuilder();
			SQLGetDocStatus.append("SELECT Description ");
			SQLGetDocStatus.append(" FROM AD_Param ");
			SQLGetDocStatus.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocStatus.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocStatus.append(" AND Value = 'MR_DefaultParameter'");
			SQLGetDocStatus.append(" AND Name = 'DocStatus'");
			String DocStatus = DB.getSQLValueStringEx(trxName, SQLGetDocStatus.toString());	
			TimeSheet.setIsSOTrx(true);
			int noLine = 0;

			if(TimeSheet.save()) {
								
				for(API_Model_TimeSheetLines detail : dataDetails) {
					
					noLine = noLine+1;
					
					MInOutLine ShipLine = new MInOutLine(ctx, 0, trxName);
					ShipLine.setAD_Org_ID(AD_Org_ID);
					ShipLine.setLine(detail.line);
					ShipLine.setC_OrderLine_ID(detail.so_line);
					ShipLine.setM_InOut_ID(TimeSheet.getM_InOut_ID());
					ShipLine.setM_Locator_ID(M_Locator_ID);
					

					StringBuilder SQLGetCharge = new StringBuilder();
					SQLGetCharge.append("SELECT C_Charge_ID ");
					SQLGetCharge.append(" FROM C_Charge ");
					SQLGetCharge.append(" WHERE AD_Client_ID = "+AD_Client_ID);
					SQLGetCharge.append(" AND AD_Org_ID = "+AD_Org_ID);
					SQLGetCharge.append(" AND C_Charge_ID = '"+detail.charge_id+"'");
					Integer C_Charge_ID = DB.getSQLValue(trxName, SQLGetCharge.toString());	
					ShipLine.setC_Charge_ID(C_Charge_ID);

					
					ShipLine.setDescription(detail.description);
					ShipLine.setQty(detail.qty_receipt);
					ShipLine.setQtyEntered(detail.qty_receipt);

					StringBuilder SQLGetOrderLine = new StringBuilder();
					SQLGetOrderLine.append("SELECT C_OrderLine_ID ");
					SQLGetOrderLine.append(" FROM C_OrderLine ");
					SQLGetOrderLine.append(" WHERE AD_Client_ID = "+AD_Client_ID);
					SQLGetOrderLine.append(" AND AD_Org_ID = "+AD_Org_ID);
					SQLGetOrderLine.append(" AND C_Order_ID = "+ord.getC_Order_ID());
					SQLGetOrderLine.append(" AND Line = "+ detail.so_line);

					Integer C_OrderLine_ID = DB.getSQLValue(trxName, SQLGetOrderLine.toString());	
					
					ShipLine.setC_OrderLine_ID(C_OrderLine_ID);					
					ShipLine.saveEx();
					
				}
				
			
			}
			
			if(noLine > 0) {
				
				if(TimeSheet != null) {
					
					TimeSheet.setDocAction(DocStatus);
					TimeSheet.processIt(MInOut.DOCACTION_Complete);
					
					if(TimeSheet.save()) {
						
						result = TimeSheet.getM_InOut_ID();
					}
				}
				
			}

			
		} catch (Exception e) {

			result = 0;
		}
	
				
		return result;
		
	}
	
}
