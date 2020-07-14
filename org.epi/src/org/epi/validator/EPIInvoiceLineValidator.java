package org.epi.validator;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_C_InvoiceLineDtl;
import org.epi.model.X_M_InOutLineDtl;
import org.epi.process.EPICalculateFinalInvoice;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIInvoiceLineValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIInvoiceLineValidator.class);

	public static String executeInvoiceLine(Event event, PO po) {
		
		String msgInv= "";
		MInvoiceLine InvLine = (MInvoiceLine) po;
		
		MOrg org = new MOrg(InvLine.getCtx(), InvLine.getAD_Org_ID(), null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
				msgInv = beforeSaveEPI(InvLine);
			}
		}
		
	return msgInv;

	}
	
	
	private static String beforeSaveEPI(MInvoiceLine InvLine) {
		
		String rslt = "";
		MInvoice Invoice = new MInvoice(null, InvLine.getC_Invoice_ID(), InvLine.get_TrxName());
		
		if(Invoice.isSOTrx()) {
			
			int M_InoutLine_ID = 0;
			
			MInvoiceLine invLine = new MInvoiceLine(InvLine.getCtx(), InvLine.getC_InvoiceLine_ID(), InvLine.get_TrxName());		
			
			if(invLine.getC_Charge_ID() > 0) 
				return"";
			
			
			StringBuilder SQLGetShipLineDetail = new StringBuilder();
			M_InoutLine_ID = invLine.getM_InOutLine_ID();		
			BigDecimal LineNetAmt = Env.ZERO;
			
			if(M_InoutLine_ID > 0) {
				
			
				SQLGetShipLineDetail.append("SELECT M_InOutLineDtl_ID ");
				SQLGetShipLineDetail.append(" FROM M_InOutLineDtl");
				SQLGetShipLineDetail.append(" WHERE AD_Client_ID = ?");
				SQLGetShipLineDetail.append(" AND M_InOutLine_ID = ?");
				
				int lineCnt = 10;
				
				PreparedStatement pstmt = null;
			 	ResultSet rs = null;
					try {
						pstmt = DB.prepareStatement(SQLGetShipLineDetail.toString(), null);
						pstmt.setInt(1,InvLine.getAD_Client_ID());	
						pstmt.setInt(2,M_InoutLine_ID);	
			
						rs = pstmt.executeQuery();
						while (rs.next()) {
			
							X_M_InOutLineDtl ShipLineDetail = new X_M_InOutLineDtl(InvLine.getCtx(), rs.getInt(1), InvLine.get_TrxName());
							X_C_InvoiceLineDtl InvLineDetail = new X_C_InvoiceLineDtl(InvLine.getCtx(), 0, InvLine.get_TrxName());
							
							InvLineDetail.setAD_Org_ID(invLine.getAD_Org_ID());
							InvLineDetail.setC_InvoiceLine_ID(InvLine.getC_InvoiceLine_ID());
							InvLineDetail.setLineNo(lineCnt);
							InvLineDetail.setM_Product_ID(ShipLineDetail.getM_Product_ID());
							InvLineDetail.setPriceEntered(ShipLineDetail.getPriceEntered());
							InvLineDetail.setDescription(ShipLineDetail.getDescription());
							InvLineDetail.set_CustomColumn("C_OrderLineDtl_ID", ShipLineDetail.getC_OrderlineDtl_ID());
							InvLineDetail.set_CustomColumn("QtyInternalUse", ShipLineDetail.getQtyInternalUse());
							InvLineDetail.set_CustomColumn("LineNetAmt", ShipLineDetail.getLineNetAmt());
							InvLineDetail.saveEx();
							
							lineCnt = lineCnt+10;
										
							LineNetAmt = LineNetAmt.add((BigDecimal) InvLineDetail.get_Value("LineNetAmt"));
							
						}
			
					} catch (SQLException err) {
						
						log.log(Level.SEVERE, SQLGetShipLineDetail.toString(), err);
										
					} finally {
						
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
						
					}
				
				}
				
				
				MInvoice inv = new MInvoice(null, invLine.getC_Invoice_ID(), invLine.get_TrxName());
				BigDecimal FirstDP = (BigDecimal) inv.get_Value("FirstDP");
				BigDecimal FirstPayAmt = (BigDecimal) inv.get_Value("FirstPayment");
				Integer invDP_ID = (Integer) inv.get_Value("C_InvoiceDP_ID");
				
				if(invDP_ID == null) {
					invDP_ID =0;
				}
				
				Boolean IsInvoiceDP = false;
				
				
				if(FirstDP == null) {
					FirstDP = Env.ZERO;
				}
				
				if(FirstPayAmt == null) {
					FirstPayAmt = Env.ZERO;
				}
				
				if(FirstDP.compareTo(Env.ZERO)>0 && FirstPayAmt.compareTo(Env.ZERO)==0 && invDP_ID == 0) {
					IsInvoiceDP = true;
				}
				
				
				if(IsInvoiceDP) {
					
					MOrder ord = new MOrder(inv.getCtx(), inv.getC_Order_ID(), null);
					BigDecimal percent = FirstDP.divide(Env.ONEHUNDRED);
					BigDecimal invAmt = percent.multiply(ord.getTotalLines());
					
					invLine.setPriceEntered(invAmt);
					invLine.setPriceActual(invAmt);
					invLine.saveEx();
					
				}else {
				
					Integer C_InvoiceDP_ID = EPICalculateFinalInvoice.getInvoiceDP(invLine.getAD_Client_ID(), invLine.getAD_Org_ID(), inv.getC_Order_ID(),inv.getC_Invoice_ID());
					BigDecimal QtyEntered = invLine.getQtyEntered(); 
					System.out.println();
					MInvoice InvDP = new MInvoice(Env.getCtx(), C_InvoiceDP_ID, null);
					
					BigDecimal pphAmt = Env.ZERO;
					
					
					MInvoiceLine[] lines = InvDP.getLines();
					for(MInvoiceLine line : lines) {
						
						if(line.getC_Charge_ID() > 0) {
						
							pphAmt = line.getLineNetAmt().abs();
						}
					}
					
					//if LineNetAmt is 0 
					if(LineNetAmt.compareTo(Env.ZERO) == 0) {
						
						MOrderLine ordLine = new MOrderLine(Env.getCtx(), InvLine.getC_OrderLine_ID(), null);
						if(ordLine != null) {
							
							LineNetAmt = ordLine.getLineNetAmt();

						}
						
					}//
					
					BigDecimal finalAmt = LineNetAmt.subtract(InvDP.getTotalLines().add(pphAmt));
					BigDecimal piceActual = finalAmt.divide(QtyEntered);
					
					invLine.setPriceEntered(piceActual);
					invLine.setPriceActual(piceActual);
					invLine.saveEx();		
					
					inv.set_CustomColumn("FirstDP", InvDP.get_Value("FirstDP"));
					inv.set_CustomColumn("FirstPayment", InvDP.getGrandTotal());
					inv.saveEx();
							
				}
		}
		return rslt;
		
	}
	

}
