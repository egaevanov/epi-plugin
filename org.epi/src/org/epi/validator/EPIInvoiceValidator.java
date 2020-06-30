package org.epi.validator;

import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MSequence;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_ISM_Budget_Transaction;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIInvoiceValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIInvoiceValidator.class);

	public static String executeInvoice(Event event, PO po) {
		
		String msgInv= "";
		MInvoice Invoice = (MInvoice) po;
		
		MOrg org = new MOrg(Invoice.getCtx(), Invoice.getAD_Org_ID(), null);

		if (event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {
			
			if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
				msgInv = beforeCompleteEPI(Invoice);
			}
			
		}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REVERSECORRECT)) {
			if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
				msgInv = beforeReverseEPI(Invoice);
			}
		
		}else if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
			if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
				msgInv = beforeSaveEPI(Invoice);
			}

		}
		
	return msgInv;

	}

	private static String beforeCompleteEPI(MInvoice Inv) {

		String rslt = "";

		if(Inv.isSOTrx() && !Inv.isReversal()) {
			
			
			
		}else if(!Inv.isSOTrx() && !Inv.isReversal()) {
			
			
			
			MInvoiceLine[] lines = Inv.getLines();
			
			
			for(MInvoiceLine line : lines ) {
				
				MOrderLine ordLine = new MOrderLine(Env.getCtx(), line.getC_OrderLine_ID(), null);
				
				X_ISM_Budget_Transaction BudgetTrx = new X_ISM_Budget_Transaction(Env.getCtx(), 0, Inv.get_TrxName());
				BudgetTrx.setAD_Org_ID(line.getAD_Org_ID());
				BudgetTrx.setBudgetAmt(line.getLineNetAmt());
				BudgetTrx.setC_Invoice_ID(line.getC_Invoice_ID());
				BudgetTrx.setC_InvoiceLine_ID(line.getC_InvoiceLine_ID());
				BudgetTrx.setBudget_Status("AL");
				BudgetTrx.setDateInvoiced(Inv.getDateOrdered());
				BudgetTrx.setISM_Budget_Line_ID(ordLine.get_ValueAsInt("ISM_Budget_Line_ID"));
				BudgetTrx.saveEx();
				
				StringBuilder getBudgetTrx = new StringBuilder();

				getBudgetTrx.append("SELECT ISM_Budget_Transaction_ID ");
				getBudgetTrx.append(" FROM ISM_Budget_Transaction ");
				getBudgetTrx.append(" WHERE AD_Client_ID = ? ");
				getBudgetTrx.append(" AND AD_Org_ID = ? ");
				getBudgetTrx.append(" AND C_OrderLine_ID = ?");
				getBudgetTrx.append(" AND Budget_Status = 'BO'");
				
				Integer ISM_Budget_Transaction_ID = DB.getSQLValueEx(Inv.get_TrxName(), getBudgetTrx.toString(), new Object[]{Inv.getAD_Client_ID(),Inv.getAD_Org_ID(),line.getC_OrderLine_ID()});

				if(ISM_Budget_Transaction_ID > 0) {
					
					X_ISM_Budget_Transaction budgetOrdTrx = new X_ISM_Budget_Transaction(Env.getCtx(), ISM_Budget_Transaction_ID, Inv.get_TrxName());
					budgetOrdTrx.setBudgetAmt(Env.ZERO);
					budgetOrdTrx.saveEx();
					
				}
				
			}	
			
		}
	
		return rslt;
		
	}
	
	
	private static String beforeReverseEPI(MInvoice Inv) {

		String rslt = "";
		
		MInvoiceLine[] lines = Inv.getLines();
		
		
		for(MInvoiceLine line : lines ) {
			
			StringBuilder getBudgetTrx = new StringBuilder();
			
			getBudgetTrx.append("SELECT ISM_Budget_Transaction_ID ");
			getBudgetTrx.append(" FROM ISM_Budget_Transaction ");
			getBudgetTrx.append(" WHERE AD_Client_ID = ? ");
			getBudgetTrx.append(" AND AD_Org_ID = ? ");
			getBudgetTrx.append(" AND C_InvoiceLine_ID = ?");

			Integer ISM_Budget_Transaction_ID = DB.getSQLValueEx(Inv.get_TrxName(), getBudgetTrx.toString(), new Object[]{line.getAD_Client_ID(),line.getAD_Org_ID(),line.getC_InvoiceLine_ID()});

			if(ISM_Budget_Transaction_ID > 0) {
				
				X_ISM_Budget_Transaction budgetOrdTrx = new X_ISM_Budget_Transaction(Env.getCtx(), ISM_Budget_Transaction_ID, line.get_TrxName());
				budgetOrdTrx.setBudget_Status("VO");
				budgetOrdTrx.setBudgetAmt(Env.ZERO);
				budgetOrdTrx.saveEx();
				
				StringBuilder getBudgetTrxOrdLine = new StringBuilder();
				
				getBudgetTrxOrdLine.append("SELECT ISM_Budget_Transaction_ID ");
				getBudgetTrxOrdLine.append(" FROM ISM_Budget_Transaction ");
				getBudgetTrxOrdLine.append(" WHERE AD_Client_ID = ? ");
				getBudgetTrxOrdLine.append(" AND AD_Org_ID = ? ");
				getBudgetTrxOrdLine.append(" AND C_OrderLine_ID = ?");
				
				Integer ISM_Budget_Trx_ID = DB.getSQLValueEx(Inv.get_TrxName(), getBudgetTrxOrdLine.toString(), new Object[]{line.getAD_Client_ID(),line.getAD_Org_ID(),line.getC_OrderLine_ID()});
				X_ISM_Budget_Transaction budgetOrdLineTrx = new X_ISM_Budget_Transaction(Env.getCtx(), ISM_Budget_Trx_ID, line.get_TrxName());
				MOrderLine Ordline = new MOrderLine(Env.getCtx(), line.getC_OrderLine_ID(), null);
				budgetOrdLineTrx.setBudget_Status("BO");
				budgetOrdLineTrx.setBudgetAmt(Ordline.getLineNetAmt());
				budgetOrdLineTrx.saveEx();
				
			}
			
			
		}
		
		return rslt;
		
	}
	
	
	private static String beforeSaveEPI(MInvoice inv) {

	
		String rslt = "";
		
		boolean IsSOTrx = inv.isSOTrx();
		String typeCode = "";
		String monthStr	= "";
		String yearStr	= "";
		String nextStr = "";
		String DocRef = "";

		
		if(IsSOTrx) {
			typeCode ="AR";
		}else {
			typeCode ="AP";
		}
		
		
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(inv.getDateAcct());
	    
	    Integer month = calendar.get(Calendar.MONTH)+1;
	    
	    if(month != 11 && month != 12) {
		    monthStr = "0"+String.valueOf(month);
	    }else {
	    	monthStr = String.valueOf(month);
	    }
	    
	    Integer year = calendar.get(Calendar.YEAR);
	    yearStr = String.valueOf(year).substring(2);
	    
	    //seq
		int C_DocType_ID = inv.getC_DocTypeTarget_ID();
		
		if(C_DocType_ID > 0) {
			
			MDocType docType = new MDocType(inv.getCtx(), C_DocType_ID, null);
			
			if(docType.getDocNoSequence_ID() > 0) {
				
				MSequence docSeq = new MSequence(inv.getCtx(), docType.getDocNoSequence_ID(), null);
				StringBuilder SQLSeqNO = new StringBuilder();
				
				SQLSeqNO.append("SELECT CurrentNext");
				SQLSeqNO.append(" FROM AD_Sequence_No ");
				SQLSeqNO.append(" WHERE AD_Sequence_ID = "+docSeq.getAD_Sequence_ID());
				
				if(docSeq.isStartNewYear() && !docSeq.isStartNewMonth()) {
					SQLSeqNO.append(" AND CalendarYearMonth ='"+String.valueOf(year)+"'");
				}else if(docSeq.isStartNewYear() && docSeq.isStartNewMonth()) {
					SQLSeqNO.append(" AND CalendarYearMonth ='"+String.valueOf(year)+monthStr+"'");
				}
				
				Integer next = DB.getSQLValueEx(null, SQLSeqNO.toString());
				nextStr = "000"+String.valueOf(next);
				
				if(nextStr.length()>4) {
					
					nextStr = nextStr.substring(nextStr.length()-4);
					
				}
			}
			
			
		}
		
		
		DocRef = typeCode+monthStr+"-"+yearStr+"-"+nextStr;
		
		inv.set_ValueNoCheck("ReferenceNo", DocRef);
	    inv.saveEx();
		
		
		return rslt;
		
}
	
	
}
