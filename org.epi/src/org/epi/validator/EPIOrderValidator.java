package org.epi.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.base.event.IEventTopics;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MProduct;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.epi.model.X_C_OrderlineDtl;
import org.epi.model.X_ISM_Budget_Transaction;
import org.epi.model.X_M_InOutLineDtl;
import org.epi.process.EPICheckCurrency;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIOrderValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIOrdLineDtlValidator.class);

	public static String executeOrder(Event event, PO po) {
		
		String msgOrd= "";
		MOrder order = (MOrder) po;
		
		MOrg org = new MOrg(order.getCtx(), order.getAD_Org_ID(), null);
		
		
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			if (event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {
				msgOrd = beforeCompleteEPI(order);
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_CLOSE)) {
				msgOrd = beforeCloseEPI(order);
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_VOID)){
				msgOrd = beforeCloseEPI(order);
			}
		
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {

		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {

		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.RMH)) {

		}

		
	return msgOrd;

	}

	private static String beforeCompleteEPI(MOrder Ord) {
		
		String rslt = "";
	
		if(Ord.isSOTrx()) {
		
			boolean IsAutoShipment = true;
			MOrderLine [] lines = Ord.getLines();
			
			for (MOrderLine line : lines ) {
				
				MProduct prod = new MProduct(Ord.getCtx(), line.getM_Product_ID(), Ord.get_TrxName());
				
				if(!prod.get_ValueAsBoolean("IsAutoShipment")) {
					IsAutoShipment = false;
				}
				
			}
			
			if(IsAutoShipment) {
			
			MDocType dt = MDocType.get(Env.getCtx(), Ord.getDocTypeID());
			
			
			if (log.isLoggable(Level.INFO)) log.info("For " + dt);
			MInOut shipment = new MInOut (Ord, dt.getC_DocTypeShipment_ID(), Ord.getDateOrdered());
		//	shipment.setDateAcct(getDateAcct());
			if (!shipment.save(Ord.get_TrxName()))
			{
				rslt = "Could not create Shipment";
				return null;
			}
	
			MOrderLine[] oLines = Ord.getLines(true, null);
			for (int i = 0; i < oLines.length; i++)
			{
				MOrderLine oLine = oLines[i];
				MInOutLine ioLine = new MInOutLine(shipment);
				BigDecimal MovementQty = oLine.getQtyOrdered().subtract(oLine.getQtyDelivered()); 
				int M_Locator_ID = MStorageOnHand.getM_Locator_ID (oLine.getM_Warehouse_ID(),oLine.getM_Product_ID(), oLine.getM_AttributeSetInstance_ID(), MovementQty, Ord.get_TrxName());
	//			Get default Location
				if (M_Locator_ID == 0){
					MWarehouse wh = MWarehouse.get(Ord.getCtx(), oLine.getM_Warehouse_ID());
					M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
				}
	
				ioLine.setOrderLine(oLine, M_Locator_ID, MovementQty);
				ioLine.setQty(MovementQty);
				if (oLine.getQtyEntered().compareTo(oLine.getQtyOrdered()) != 0)
					ioLine.setQtyEntered(MovementQty.multiply(oLine.getQtyEntered()).divide(oLine.getQtyOrdered(), 6, RoundingMode.HALF_UP));
				if (!ioLine.save(Ord.get_TrxName()))
				{
					rslt = "Could not create Shipment Line";
					return null;
				}
				
			
				int C_OrderLine_ID = ioLine.getC_OrderLine_ID();
				
				StringBuilder SQLGetSOLineDetail = new StringBuilder();
				
				SQLGetSOLineDetail.append("SELECT C_OrderlineDtl_ID ");
				SQLGetSOLineDetail.append(" FROM C_OrderlineDtl");
				SQLGetSOLineDetail.append(" WHERE AD_Client_ID = ?");
				SQLGetSOLineDetail.append(" AND C_OrderLine_ID = ?");
				
				PreparedStatement pstmt = null;
		     	ResultSet rs = null;
					try {
						pstmt = DB.prepareStatement(SQLGetSOLineDetail.toString(), null);
						pstmt.setInt(1,Ord.getAD_Client_ID());	
						pstmt.setInt(2,C_OrderLine_ID);	
	
						rs = pstmt.executeQuery();
						while (rs.next()) {
	
							X_C_OrderlineDtl SOLineDetail = new X_C_OrderlineDtl(Ord.getCtx(), rs.getInt(1), Ord.get_TrxName());
							X_M_InOutLineDtl InOutLineDetail = new X_M_InOutLineDtl(Ord.getCtx(), 0, Ord.get_TrxName());
							MLocator SOLocator = new MLocator(Ord.getCtx(), SOLineDetail.getM_Locator_ID(), Ord.get_TrxName());  
							
							InOutLineDetail.setAD_Org_ID(ioLine.getAD_Org_ID());
							InOutLineDetail.setLineNo(SOLineDetail.getLineNo());
							InOutLineDetail.setM_InOutLine_ID(ioLine.getM_InOutLine_ID());
							InOutLineDetail.setC_OrderlineDtl_ID(SOLineDetail.getC_OrderlineDtl_ID());
							InOutLineDetail.setM_Locator_ID(SOLineDetail.getM_Locator_ID());
							InOutLineDetail.setM_Warehouse_ID(SOLocator.getM_Warehouse_ID());
							InOutLineDetail.setM_Product_ID(SOLineDetail.getM_Product_ID());
							InOutLineDetail.setC_UOM_ID(SOLineDetail.getC_UOM_ID());
							InOutLineDetail.setM_AttributeSetInstance_ID(SOLineDetail.getM_AttributeSetInstance_ID());
							InOutLineDetail.setDescription(SOLineDetail.getDescription());
							InOutLineDetail.setQtyInternalUse(SOLineDetail.getQtyInternalUse());
							InOutLineDetail.setPriceEntered(SOLineDetail.getPriceEntered());
							InOutLineDetail.setLineNetAmt(SOLineDetail.getLineNetAmt());
							InOutLineDetail.saveEx();
							
						}
	
					} catch (SQLException err) {
						
						log.log(Level.SEVERE, SQLGetSOLineDetail.toString(), err);
					
						
					} finally {
						
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
						
					}	
				
				
			}
			
	
			if (!shipment.processIt(DocAction.ACTION_Complete))
				throw new AdempiereException(Msg.getMsg(Ord.getCtx(), "FailedProcessingDocument") + " - " + shipment.getProcessMsg());
			// end added
			shipment.saveEx(Ord.get_TrxName());
			if (!MOrder.DOCSTATUS_Completed.equals(shipment.getDocStatus()))
			{
				rslt = "@M_InOut_ID@: " + shipment.getProcessMsg();
				return null;
			}
			 	
			
		}
			
		}else if(!Ord.isSOTrx()) {
			
			boolean isMatchCur = CurrencyCheck(Ord);
			
			if(isMatchCur) {	
				createBudgetTrx(Ord,null);
			}else {
				Integer convRate = EPICheckCurrency.ConvertionRateCheck(Ord.getAD_Client_ID(), Ord.getC_Currency_ID(), Ord.getDateOrdered(), Ord.get_TrxName());
				if(convRate == null) {
					convRate = 0;
				}
				
				if(convRate <= 0) {
					return "Currency Rate Setup Is Not Available";
				}
				
				
				MConversionRate rate = new MConversionRate(Ord.getCtx(), convRate, Ord.get_TrxName());
				
				createBudgetTrx(Ord, rate);
				
			}
			
		}
		return rslt;
		
	}
	
	
	
	private static String beforeCloseEPI(MOrder Ord) {
		
		String rslt = "";
		
		
		StringBuilder getBudgetTrx = new StringBuilder();
		getBudgetTrx.append("SELECT ISM_Budget_Transaction_ID ");
		getBudgetTrx.append(" FROM ISM_Budget_Transaction ");
		getBudgetTrx.append(" WHERE AD_Client_ID = ? ");
		getBudgetTrx.append(" AND AD_Org_ID = ? ");
		getBudgetTrx.append(" AND C_Order_ID = ?");
	
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(getBudgetTrx.toString(), null);
				pstmt.setInt(1,Ord.getAD_Client_ID());	
				pstmt.setInt(2,Ord.getAD_Org_ID());
				pstmt.setInt(3,Ord.getC_Order_ID());

				rs = pstmt.executeQuery();
				while (rs.next()) {
					
					Integer ISM_Budget_Transaction_ID = rs.getInt(1);
					
					if(ISM_Budget_Transaction_ID > 0) {
					
						X_ISM_Budget_Transaction budgetTrx = new X_ISM_Budget_Transaction(Env.getCtx(), ISM_Budget_Transaction_ID, Ord.get_TrxName());
						budgetTrx.setBudget_Status("VO");
						budgetTrx.setBudgetAmt(Env.ZERO);
						budgetTrx.saveEx();
						
					}

				
				}

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, getBudgetTrx.toString(), err);
				
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
		
		
		return rslt;
	}
	
	
	private static boolean CurrencyCheck(MOrder ord) {
		boolean rslt = false;
		
		StringBuilder SQLGetCur = new StringBuilder();
		SQLGetCur.append("SELECT CASE WHEN ord.c_currency_id = acs.c_currency_id ");
		SQLGetCur.append(" THEN 1 ELSE 0 END AS return ");
		SQLGetCur.append(" FROM c_order ord");
		SQLGetCur.append(" LEFT JOIN c_acctschema acs on ord.ad_client_id = acs.ad_client_id");
		SQLGetCur.append(" WHERE ord.c_order_id = "+ ord.getC_Order_ID());
		SQLGetCur.append(" AND acs.isactive = 'Y'");
		
		int cur = DB.getSQLValueEx(ord.get_TrxName(), SQLGetCur.toString());
		
		if(cur == 1) {
			rslt = true;
		}
		
		return rslt;
		
	}
	
//	private static Integer ConvertionRateCheck(MOrder ord) {
//		Integer rslt = 0;
//		
//		StringBuilder SQLCheckCurRate = new StringBuilder();
//		SQLCheckCurRate.append("SELECT c_conversion_rate_id ");
//		SQLCheckCurRate.append(" FROM c_conversion_rate");
//		SQLCheckCurRate.append(" WHERE isactive = 'Y'");
//		SQLCheckCurRate.append(" AND ad_client_id = "+ ord.getAD_Client_ID());
//		SQLCheckCurRate.append(" AND c_currency_id = "+ ord.getC_Currency_ID());
//		SQLCheckCurRate.append(" AND validto >= '"+ ord.getDateOrdered()+"'");
//		
//		rslt = DB.getSQLValueEx(ord.get_TrxName(), SQLCheckCurRate.toString());
//			
//		return rslt;
//		
//	}
	
	private static void createBudgetTrx(MOrder Ord, MConversionRate CurrRate) {
		
		
		MOrderLine[] lines = Ord.getLines();
		
		for(MOrderLine line  : lines) {
			
			X_ISM_Budget_Transaction BudgetTrx = new X_ISM_Budget_Transaction(Env.getCtx(), 0, Ord.get_TrxName());
			BudgetTrx.setAD_Org_ID(line.getAD_Org_ID());
			BudgetTrx.setC_Order_ID(line.getC_Order_ID());
			BudgetTrx.setC_OrderLine_ID(line.getC_OrderLine_ID());
			if(CurrRate != null) {
				BudgetTrx.setBudgetAmt(line.getLineNetAmt().multiply(CurrRate.getMultiplyRate()));
			}else {
				BudgetTrx.setBudgetAmt(line.getLineNetAmt());
			}
			BudgetTrx.setBudget_Status("BO");
			BudgetTrx.setDateOrdered(line.getDateOrdered());
			BudgetTrx.setISM_Budget_Line_ID(line.get_ValueAsInt("ISM_Budget_Line_ID"));
			BudgetTrx.saveEx();
			
		}
		
		
	}
	
	
	
}
