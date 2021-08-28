package org.epi.validator;

import java.math.BigDecimal;
import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrg;
import org.compiere.model.MPayment;
import org.compiere.model.MSequence;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
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
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.WRG)) {
			if(event.getTopic().equals(IEventTopics.DOC_AFTER_COMPLETE)) {
				
				msgPay = beforeCompleteWS(pay,event);
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
	

private static String beforeCompleteWS(MPayment payment , Event event) {
	
	String rslt = "";
	
	MOrg org = new MOrg(payment.getCtx(), payment.getAD_Org_ID(), null);
	
	if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
		
		if(payment.getC_Invoice_ID() > 0) {
			
			MInvoice invoice = new MInvoice(payment.getCtx(), payment.getC_Invoice_ID(), payment.get_TrxName());
			
			if(invoice.isSOTrx()) {
			
			BigDecimal pphRate = FinalVariableGlobal.ISMPPH23RATE;	
			BigDecimal pph = pphRate.divide(Env.ONEHUNDRED);
			BigDecimal PPhAmt = invoice.getTotalLines().multiply(pph);
			
			StringBuilder sqlPayDocType = new StringBuilder();
			sqlPayDocType.append("SELECT C_DocType_ID");
			sqlPayDocType.append(" FROM C_DocType");
			sqlPayDocType.append(" WHERE AD_Client_ID = ?");
			sqlPayDocType.append(" AND DocBaseType = ?");
			int pay_C_DocType_ID = DB.getSQLValueEx(payment.get_TrxName(), sqlPayDocType.toString(), new Object[]{payment.getAD_Client_ID(),MDocType.DOCBASETYPE_APPayment});
			
			StringBuilder getPPh = new StringBuilder();
			getPPh.append("SELECT C_Charge_ID ");
			getPPh.append(" FROM  C_Charge ");
			getPPh.append(" WHERE lower(name)='pph 23'");
			getPPh.append(" AND AD_Client_ID =  "+payment.getAD_Client_ID());
			Integer C_Charge_ID = DB.getSQLValueEx(payment.get_TrxName(), getPPh.toString());
						
			if(C_Charge_ID < 0)
				return"Charge PPh 23 Belum Terdaftar";
			
			MPayment payPPh = new MPayment(payment.getCtx(), 0, invoice.get_TrxName());
			payPPh.setAD_Org_ID(invoice.getAD_Org_ID());
			payPPh.setIsReceipt(true);

			payPPh.setC_DocType_ID(pay_C_DocType_ID);
			payPPh.setC_BPartner_ID(payment.getC_BPartner_ID());
			payPPh.setDescription("PPH23 Atas Invoice "+invoice.getDocumentNo());
			payPPh.setDateTrx(payment.getDateTrx());
			payPPh.setDateAcct(payment.getDateAcct());
			payPPh.setC_BankAccount_ID(payment.getC_BankAccount_ID());
			payPPh.setTenderType(MPayment.TENDERTYPE_Cash);
			payPPh.setPayAmt(PPhAmt);
			payPPh.setC_Currency_ID(payment.getC_Currency_ID());
			payPPh.setC_Charge_ID(C_Charge_ID);
			payPPh.saveEx();
			
			if(payPPh.processIt("CO")) {
				payPPh.saveEx();
			}
		
			
			}
			
			
		}
		
	}
	
	return rslt;
}
	
	
}
