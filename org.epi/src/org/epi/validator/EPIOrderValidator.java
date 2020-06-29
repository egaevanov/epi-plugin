package org.epi.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.base.event.IEventTopics;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
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
import org.osgi.service.event.Event;

public class EPIOrderValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIOrdLineDtlValidator.class);

	public static String executeOrder(Event event, PO po) {
		
		String msgOrd= "";
		MOrder order = (MOrder) po;
		if (event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {
			
			msgOrd = beforeComplete(order);
			
		}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_CLOSE)) {
			
			msgOrd = beforeClose(order);
		}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_VOID)) {
			msgOrd = beforeClose(order);	
		}
		
	return msgOrd;

	}

	private static String beforeComplete(MOrder Ord) {
		
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
			
			MOrderLine[] lines = Ord.getLines();
			
			for(MOrderLine line  : lines) {
				
				X_ISM_Budget_Transaction BudgetTrx = new X_ISM_Budget_Transaction(Env.getCtx(), 0, Ord.get_TrxName());
				BudgetTrx.setAD_Org_ID(line.getAD_Org_ID());
				BudgetTrx.setC_Order_ID(line.getC_Order_ID());
				BudgetTrx.setC_OrderLine_ID(line.getC_OrderLine_ID());
				BudgetTrx.setBudgetAmt(line.getLineNetAmt());
				BudgetTrx.setBudget_Status("BO");
				BudgetTrx.setDateOrdered(line.getDateOrdered());
				BudgetTrx.setISM_Budget_Line_ID(line.get_ValueAsInt("ISM_Budget_Line_ID"));
				BudgetTrx.saveEx();
				
			}
			
		
		}
		return rslt;
		
	}
	
	
	
	private static String beforeClose(MOrder Ord) {
		
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
	
}
