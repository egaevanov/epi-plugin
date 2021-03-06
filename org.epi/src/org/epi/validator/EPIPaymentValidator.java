package org.epi.validator;

import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.model.MOrg;
import org.compiere.model.MPayment;
import org.compiere.model.MSequence;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIPaymentValidator {
	
	
	public static CLogger log = CLogger.getCLogger(EPIPaymentValidator.class);

	public static String executePayment(Event event, PO po) {
		
		String msgPay= "";
		MPayment pay = (MPayment) po;
		
		MOrg org = new MOrg(pay.getCtx(), pay.getAD_Org_ID(), null);
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			if (event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
				msgPay = beforeSaveEPI(pay,event);	
			}else if(event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE)) {
				msgPay = beforeSaveEPI(pay,event);
			}
		}
	return msgPay;

	}

	
private static String beforeSaveEPI(MPayment pay,Event event) {

	
		String rslt = "";
		
		int C_BankAccount_ID = pay.getC_BankAccount_ID();
		String bankCode = "";
		String monthStr	= "";
		String yearStr	= "";
		String nextStr = "";
		String DocRef = "";

		//
		if(C_BankAccount_ID > 0) {
					
			MBankAccount bankAcct = new MBankAccount(pay.getCtx(), C_BankAccount_ID, pay.get_TrxName());
			bankCode = bankAcct.get_ValueAsString("BankCode");
			if(bankCode.equals("")||bankCode == null ||bankCode.isEmpty()) {
				
				return"Kode Bank Belum Terdefinisi";
			}
			
			
			
		}
		
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(pay.getDateTrx());
	    
	    Integer month = calendar.get(Calendar.MONTH)+1;
	    
	    if(month != 10 && month != 11 && month != 12) {
		    monthStr = "0"+String.valueOf(month);
	    }else {
	    	monthStr = String.valueOf(month);
	    }
	    
	    Integer year = calendar.get(Calendar.YEAR);
	    yearStr = String.valueOf(year).substring(2);
	    
	    //seq
		int C_DocType_ID = pay.getC_DocType_ID();
		
		if(C_DocType_ID > 0) {
			
			MDocType docType = new MDocType(pay.getCtx(), C_DocType_ID, null);
			
			if(docType.getDocNoSequence_ID() > 0) {
				
				MSequence docSeq = new MSequence(pay.getCtx(), docType.getDocNoSequence_ID(), null);
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
				if(next == null ||next <= 0) {
					next = 1;
				}
				
				nextStr = "000"+String.valueOf(next);
				
				if(nextStr.length()>4) {
					
					nextStr = nextStr.substring(nextStr.length()-4);
					
				}
			}
			
			
		}
		
		
		DocRef = bankCode+monthStr+"-"+yearStr+"-"+nextStr;
		
		
		if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {	
			pay.set_CustomColumn("ReferenceNo", DocRef);
			pay.saveEx();
		}else if(event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE)) {
			
			StringBuilder SQLUpdate = new StringBuilder();
			SQLUpdate.append("UPDATE C_Payment ");
			SQLUpdate.append(" SET ReferenceNo = '"+DocRef+"'");
			SQLUpdate.append(" WHERE AD_Client_ID = "+pay.getAD_Client_ID());
			SQLUpdate.append(" AND AD_Org_ID =  "+pay.getAD_Org_ID());
			SQLUpdate.append(" AND C_Payment_ID =  "+pay.getC_Payment_ID());

			DB.executeUpdate(SQLUpdate.toString(), pay.get_TrxName());

		}
		
		return rslt;
		
}
	
	
	
}
