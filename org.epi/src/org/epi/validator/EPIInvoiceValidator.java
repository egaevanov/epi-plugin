package org.epi.validator;

import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MConversionRate;
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
import org.epi.process.EPICheckCurrency;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIInvoiceValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIInvoiceValidator.class);

	public static String executeInvoice(Event event, PO po) {
		
		String msgInv= "";
		MInvoice Invoice = (MInvoice) po;
		
		MOrg org = new MOrg(Invoice.getCtx(), Invoice.getAD_Org_ID(), null);
		
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			if (event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {	
				msgInv = beforeCompleteEPI(Invoice);	
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REVERSECORRECT)) {
					msgInv = beforeReverseEPI(Invoice);
			}else if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
					msgInv = beforeSaveEPI(Invoice);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {
			
			if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
				msgInv = beforeSaveTBU(Invoice);
			}else if (event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {	
				msgInv = beforeCompleteTBU(Invoice);	
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REVERSECORRECT)) {
				msgInv = beforeReverseTBU(Invoice);
		}
			
		}
			
	return msgInv;

	}

	private static String beforeCompleteEPI(MInvoice Inv) {

		String rslt = "";

		if(Inv.isSOTrx() && !Inv.isReversal()) {	
			
		}else if(!Inv.isSOTrx() && !Inv.isReversal()) {
				
			boolean isMatchCur = CurrencyCheck(Inv);
			
			if(isMatchCur) {	
				createBudgetTrx(Inv,null);
			}else {
				Integer convRate = EPICheckCurrency.ConvertionRateCheck(Inv.getAD_Client_ID(), Inv.getC_Currency_ID(), Inv.getDateInvoiced(), Inv.get_TrxName());
				if(convRate == null) {
					convRate = 0;
				}
				
				if(convRate <= 0) {
					return "Currency Rate Setup Is Not Available";
				}
						
				MConversionRate rate = new MConversionRate(Inv.getCtx(), convRate, Inv.get_TrxName());
				
				createBudgetTrx(Inv, rate);
				
			}
			
		}
	
		return rslt;
		
	}
	
	
	private static String beforeCompleteTBU(MInvoice Inv) {

		String rslt = "";
		
		
		StringBuilder SQLUpdateBAOperation = new StringBuilder();
		SQLUpdateBAOperation.append("UPDATE TBU_BAOperation");
		SQLUpdateBAOperation.append(" SET IsInvoiced = 'Y' ");
		SQLUpdateBAOperation.append(" WHERE AD_Client_ID = ? ");
		SQLUpdateBAOperation.append(" AND IsActive = 'Y' ");
		SQLUpdateBAOperation.append(" AND IsInvoiced = 'N' ");
		SQLUpdateBAOperation.append(" AND C_Invoice_ID = ? ");

		
		if(Inv.isSOTrx() && !Inv.isReversal()) {	
			DB.executeUpdateEx(SQLUpdateBAOperation.toString(), new Object[] {Inv.getAD_Client_ID(),Inv.getC_Invoice_ID()}, Inv.get_TrxName());
		}else if(!Inv.isSOTrx() && !Inv.isReversal()) {
			DB.executeUpdateEx(SQLUpdateBAOperation.toString(), new Object[] {Inv.getAD_Client_ID(),Inv.getC_Invoice_ID()}, Inv.get_TrxName());			
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
	
	private static String beforeReverseTBU(MInvoice Inv) {

		String rslt = "";
		
		StringBuilder SQLUpdateBAOperation = new StringBuilder();
		SQLUpdateBAOperation.append("UPDATE TBU_BAOperation");
		SQLUpdateBAOperation.append(" SET IsInvoiced = 'N' , C_Invoice_ID = null");
		SQLUpdateBAOperation.append(" WHERE AD_Client_ID = ? ");
		SQLUpdateBAOperation.append(" AND IsActive = 'Y' ");
		SQLUpdateBAOperation.append(" AND IsInvoiced = 'Y' ");
		SQLUpdateBAOperation.append(" AND C_Invoice_ID = ? ");

		
		if(Inv.isSOTrx() && !Inv.isReversal()) {	
			DB.executeUpdateEx(SQLUpdateBAOperation.toString(), new Object[] {Inv.getAD_Client_ID(),Inv.getC_Invoice_ID()}, Inv.get_TrxName());
		}else if(!Inv.isSOTrx() && !Inv.isReversal()) {
			DB.executeUpdateEx(SQLUpdateBAOperation.toString(), new Object[] {Inv.getAD_Client_ID(),Inv.getC_Invoice_ID()}, Inv.get_TrxName());			
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
	
	
	private static void createBudgetTrx(MInvoice Inv, MConversionRate rate) {
		
		MInvoiceLine[] lines = Inv.getLines();
		
		
		for(MInvoiceLine line : lines ) {
			
			MOrderLine ordLine = new MOrderLine(Env.getCtx(), line.getC_OrderLine_ID(), null);
			
			X_ISM_Budget_Transaction BudgetTrx = new X_ISM_Budget_Transaction(Env.getCtx(), 0, Inv.get_TrxName());
			BudgetTrx.setAD_Org_ID(line.getAD_Org_ID());
			
			if(rate != null) {
				BudgetTrx.setBudgetAmt(line.getLineNetAmt().multiply(rate.getMultiplyRate()));
			}else {
				BudgetTrx.setBudgetAmt(line.getLineNetAmt());
			}
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
	
//	private static Integer ConvertionRateCheck(MInvoice inv) {
//		Integer rslt = 0;
//		
//		StringBuilder SQLCheckCurRate = new StringBuilder();
//		SQLCheckCurRate.append("SELECT c_conversion_rate_id ");
//		SQLCheckCurRate.append(" FROM c_conversion_rate");
//		SQLCheckCurRate.append(" WHERE isactive = 'Y'");
//		SQLCheckCurRate.append(" AND ad_client_id = "+ inv.getAD_Client_ID());
//		SQLCheckCurRate.append(" AND c_currency_id = "+ inv.getC_Currency_ID());
//		SQLCheckCurRate.append(" AND validto >= '"+ inv.getDateOrdered()+"'");
//		
//		rslt = DB.getSQLValueEx(inv.get_TrxName(), SQLCheckCurRate.toString());
//			
//		return rslt;
//		
//	}
	
	private static boolean CurrencyCheck(MInvoice inv) {
		boolean rslt = false;
		
		StringBuilder SQLGetCur = new StringBuilder();
		SQLGetCur.append("SELECT CASE WHEN inv.c_currency_id = acs.c_currency_id ");
		SQLGetCur.append(" THEN 1 ELSE 0 END AS return ");
		SQLGetCur.append(" FROM c_invoice inv");
		SQLGetCur.append(" LEFT JOIN c_acctschema acs on inv.ad_client_id = acs.ad_client_id");
		SQLGetCur.append(" WHERE inv.c_invoice_id = "+ inv.getC_Invoice_ID());
		SQLGetCur.append(" AND acs.isactive = 'Y'");
		
		int cur = DB.getSQLValueEx(inv.get_TrxName(), SQLGetCur.toString());
		
		if(cur == 1) {
			rslt = true;
		}
		
		return rslt;
		
	}
	
	
	
	private static String beforeSaveTBU(MInvoice Inv) {

		
		String rslt = "";
		
		if(Inv.isSOTrx()) {
			
			StringBuilder FuncFormatDocNo = new StringBuilder();
			FuncFormatDocNo.append("select  f_update_docno_invcust(c_invoice_id,c_doctypetarget_id) ");
			FuncFormatDocNo.append(" from  c_invoice");
			FuncFormatDocNo.append(" where  c_invoice_id = "+Inv.getC_Invoice_ID());
			
			Integer rs = DB.executeUpdate(FuncFormatDocNo.toString(), true, Inv.get_TrxName());
			
			if(rs == 0) {
				rslt = "Error";
			}
		}
		
		
		return rslt;
		
	}
}
