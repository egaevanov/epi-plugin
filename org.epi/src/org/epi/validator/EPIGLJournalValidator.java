package org.epi.validator;

import java.math.BigDecimal;
import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MBPartner;
import org.compiere.model.MGLCategory;
import org.compiere.model.MJournal;
import org.compiere.model.MOrg;
import org.compiere.model.MPeriod;
import org.compiere.model.MYear;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_GL_Category_SeqNo;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIGLJournalValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIGLJournalValidator.class);

	public static String executeJournal(Event event, PO po) {
		
		String msgJournal= "";
		MJournal journal = (MJournal) po;
		
		MOrg org = new MOrg(journal.getCtx(), journal.getAD_Org_ID(), null);
		if(org.getValue().equals(FinalVariableGlobal.EPI)) {	
			if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW) 
					||event.getTopic().equals(IEventTopics.PO_AFTER_NEW_REPLICATION)) {		
				msgJournal = beforeSaveEPI(journal);
			}
		}if(org.getValue().equals(FinalVariableGlobal.TBU)) {	
			if(event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {	
				
				msgJournal = beforeCompleteTBU(journal);
				
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_VOID)) {	
				
				msgJournal = beforeVoidTBU(journal); 
				
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_CLOSE)) {		
				
				
				
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REVERSEACCRUAL)) {	
				
				msgJournal = beforeReverseAccrueTBU(journal);
				
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REVERSECORRECT)) {	
				
				msgJournal = beforeReverseCorrectTBU(journal);
				
			}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REACTIVATE)) {		
				
				msgJournal = beforeReActiveTBU(journal);
				
			}else if(event.getTopic().equals(IEventTopics.PO_BEFORE_DELETE)) {		
				
				msgJournal = beforeDeleteTBU(journal);
				
			}
		}else if(org.getValue().equals(FinalVariableGlobal.WRG)) {
			
			if(event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {	
				
				
			}
		}
		
	return msgJournal;

	}
	
	
	private static String beforeSaveEPI(MJournal journal) {
	
	
		String rslt = "";
		
		int GL_Category_ID = journal.getGL_Category_ID();
		String monthStr	= "";
		String yearStr	= "";
		String nextStr = "";
		String DocRef = "";
		MGLCategory GLCat = new MGLCategory(journal.getCtx(), GL_Category_ID, null);
		int increment = (int) GLCat.get_Value("IncrementNo");
	
		String catCode	 = "";
		X_GL_Category_SeqNo docSeq = null;
		
		if(GLCat != null) {
				
			
//			int StartNo = (int) GLCat.get_Value("StartNo");
			boolean IsOrgLevel = GLCat.get_ValueAsBoolean("IsOrgLevelSequence");
			boolean IsYearReset = GLCat.get_ValueAsBoolean("StartNewYear");
			boolean IsMonthReset = GLCat.get_ValueAsBoolean("StartNewMonth");
			catCode = GLCat.get_ValueAsString("CategoryCode");	
			int AD_Org_ID = GLCat.getAD_Org_ID();
			
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(journal.getDateDoc());
		    
		    
		    
		    int C_Period_ID = journal.getC_Period_ID();
		    boolean IsExistSeq = true;
			MPeriod period = new MPeriod(journal.getCtx(), C_Period_ID, null);
		    
		    
			Integer month = period.getPeriodNo();
		    
		    if(month != 11 && month != 12) {
			    monthStr = "0"+String.valueOf(month);
		    }else {
		    	monthStr = String.valueOf(month);
		    }
			
		    MYear yearNow = new MYear(period.getCtx(), period.getC_Year_ID(), null);
		    
		    yearStr = String.valueOf(yearNow.getFiscalYear()).substring(2);
		    Integer next = 0;
		    
			if(IsOrgLevel) {
				
				
				
				
				StringBuilder SQLGetSeqNo = new StringBuilder();
				SQLGetSeqNo.append("SELECT GL_Category_SeqNo_ID "); 
				SQLGetSeqNo.append(" FROM GL_Category_SeqNo "); 
				SQLGetSeqNo.append(" WHERE AD_Org_ID = "+AD_Org_ID);
				SQLGetSeqNo.append(" AND GL_Category_ID = "+GLCat.getGL_Category_ID());
				
				if(IsYearReset && !IsMonthReset) {
					SQLGetSeqNo.append(" AND C_Year_ID = "+period.getC_Year_ID());
				}else if(IsYearReset && IsMonthReset) {
					SQLGetSeqNo.append(" AND C_Year_ID = "+period.getC_Year_ID());
					SQLGetSeqNo.append(" AND C_Period_ID = "+C_Period_ID);

				}
				
				int seqExistIndex = DB.getSQLValueEx(journal.get_TrxName(), SQLGetSeqNo.toString());
				
				if(seqExistIndex <= 0) {
					IsExistSeq = false;
				}
				
				
				if(IsExistSeq) {
					docSeq = new X_GL_Category_SeqNo(journal.getCtx(), seqExistIndex, null);
					
					next = docSeq.getCurrentNext();

				}else {
					
					docSeq = new X_GL_Category_SeqNo(journal.getCtx(), 0, null);
					
					docSeq.setAD_Org_ID(journal.getAD_Org_ID());
					docSeq.set_CustomColumn("C_Year_ID", period.getC_Year_ID());
					docSeq.setC_Period_ID(period.getC_Period_ID());
					docSeq.setCalendarYearMonth(period.getName());
					docSeq.setGL_Category_ID(GLCat.getGL_Category_ID());
					docSeq.setCurrentNext(1);
					docSeq.saveEx();
					
					next = docSeq.getCurrentNext();
					
				}
				
				nextStr = "000"+String.valueOf(next);
				
				if(nextStr.length()>4) {
					
					nextStr = nextStr.substring(nextStr.length()-4);
						
				}	
				
			}
				
				
		}
			
			
		DocRef = catCode+monthStr+"-"+yearStr+"-"+nextStr;
			
		journal.set_ValueNoCheck("ReferenceNo", DocRef);
		
		StringBuilder updateRef = new StringBuilder();
		updateRef.append("UPDATE GL_Journal");
		updateRef.append(" SET ReferenceNo = '"+DocRef+"'");
		updateRef.append(" WHERE GL_Journal_ID ="+journal.getGL_Journal_ID());
		DB.executeUpdateEx(updateRef.toString(), journal.get_TrxName());
		
		StringBuilder updateCurrentNext = new StringBuilder();
		int next = docSeq.getCurrentNext()+increment;
		updateCurrentNext.append("UPDATE GL_Category_SeqNo");
		updateCurrentNext.append(" SET CurrentNext = "+next);
		updateCurrentNext.append(" WHERE GL_Category_SeqNo_ID ="+docSeq.getGL_Category_SeqNo_ID());
		DB.executeUpdateEx(updateCurrentNext.toString(), docSeq.get_TrxName());

		return rslt;
		
	}
	
	private static String beforeVoidTBU(MJournal journal) {
		String rslt = "";
		
		MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());
		
		if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {
		
		
			StringBuilder SQLUpdateVoid = new StringBuilder();
			SQLUpdateVoid.append("UPDATE TBU_BAOperation ");
			SQLUpdateVoid.append(" SET GL_JOurnal_ID = null ");
			SQLUpdateVoid.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			DB.executeUpdate(SQLUpdateVoid.toString(), journal.get_TrxName());
		
		}else if(cat.get_ValueAsString("CategoryCode").equals("IOT")) {
			
			StringBuilder SQLUpdateComplete = new StringBuilder();
			SQLUpdateComplete.append("UPDATE C_Invoice_OutStanding ");
			SQLUpdateComplete.append(" SET GL_Journal_ID = NULL");
			SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
			DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
			
		}
		
		return rslt;
	}
	
	private static String beforeCompleteTBU(MJournal journal) {
		String rslt = "";
		
		
		if(journal.getReversal_ID() <= 0) {
			
			
			MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());
		
			if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {
			
				StringBuilder SQLUpdateComplete = new StringBuilder();
				SQLUpdateComplete.append("UPDATE TBU_BAOperation ");
				SQLUpdateComplete.append(" SET IsUnbilled = 'Y' ");
				SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
				
				DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
			
			}else if(cat.get_ValueAsString("CategoryCode").equals("IOT")) {
				
				StringBuilder SQLGetBP = new StringBuilder();
				SQLGetBP.append("SELECT C_BPartner_ID  ");
				SQLGetBP.append(" FROM GL_JournalLine  ");
				SQLGetBP.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
				
				int C_BPartner_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetBP.toString());
				
				StringBuilder SQLGetAccount = new StringBuilder();
				SQLGetAccount.append("SELECT Account_ID ");
				SQLGetAccount.append(" FROM C_ValidCombination ");
				SQLGetAccount.append(" WHERE C_ValidCombination_ID = ");
				
				StringBuilder SQLGetBPAcct = new StringBuilder();
				SQLGetBPAcct.append(" (SELECT V_Liability_Acct  ");
				SQLGetBPAcct.append(" FROM C_BP_Vendor_Acct ");
				SQLGetBPAcct.append(" WHERE C_BPartner_ID = "+C_BPartner_ID);
				SQLGetBPAcct.append(" AND C_AcctSchema_ID = 1000003)");
				
				StringBuilder SQLGetOSAmt = new StringBuilder();
				SQLGetOSAmt.append("SELECT TotalOutstanding  ");
				SQLGetOSAmt.append(" FROM C_Invoice_OutStanding  ");
				SQLGetOSAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
				
				int AccountBP_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetAccount.toString()+SQLGetBPAcct.toString());
				
				StringBuilder SQLGetpaymentAmt = new StringBuilder();
				SQLGetpaymentAmt.append("SELECT AmtSourceDr ");
				SQLGetpaymentAmt.append(" FROM GL_JournalLine  ");
				SQLGetpaymentAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
				SQLGetpaymentAmt.append(" AND Account_ID = "+AccountBP_ID);

				BigDecimal paymentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetpaymentAmt.toString());
				BigDecimal currentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetOSAmt.toString());

				BigDecimal rsOSAmt = currentOSAmt.subtract(paymentOSAmt);
				
				
				if(paymentOSAmt.compareTo(Env.ZERO)>0) {
					
					MBPartner bp = new MBPartner(journal.getCtx(), C_BPartner_ID, journal.get_TrxName());
					
					BigDecimal currentOpenBalance = bp.getTotalOpenBalance();
					BigDecimal rsOpenBalance = currentOpenBalance.add(paymentOSAmt);
					
					bp.setTotalOpenBalance(rsOpenBalance);
					bp.saveEx();
					
				}
				
				if(rsOSAmt.compareTo(Env.ZERO)==0) {
				
					StringBuilder SQLUpdateComplete = new StringBuilder();
					SQLUpdateComplete.append("UPDATE C_Invoice_OutStanding ");
					SQLUpdateComplete.append(" SET TotalOutstanding = "+rsOSAmt);
					SQLUpdateComplete.append(" ,IsPaid = 'Y'");
					SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
					DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
				}else {
					StringBuilder SQLUpdateComplete = new StringBuilder();
					SQLUpdateComplete.append("UPDATE C_Invoice_OutStanding ");
					SQLUpdateComplete.append(" SET TotalOutstanding = "+rsOSAmt);
					SQLUpdateComplete.append(" ,IsPaid = 'N'");
					SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
					DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
					
				}
				
			}
			
		}
		return rslt;
	}
	
	private static String beforeReverseCorrectTBU(MJournal journal) {
		String rslt = "";
		
		
		MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());
		
		if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {
	
			StringBuilder SQLUpdateReverseCorrect = new StringBuilder();
			SQLUpdateReverseCorrect.append("UPDATE TBU_BAOperation ");
			SQLUpdateReverseCorrect.append(" SET IsUnbilled = 'N' ");
			SQLUpdateReverseCorrect.append(" ,GL_Journal_ID = null ");
			SQLUpdateReverseCorrect.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			DB.executeUpdate(SQLUpdateReverseCorrect.toString(), journal.get_TrxName());
			
		}else if(cat.get_ValueAsString("CategoryCode").equals("IOT")) {
			
			StringBuilder SQLGetBP = new StringBuilder();
			SQLGetBP.append("SELECT C_BPartner_ID  ");
			SQLGetBP.append(" FROM GL_JournalLine  ");
			SQLGetBP.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			int C_BPartner_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetBP.toString());
			
			StringBuilder SQLGetAccount = new StringBuilder();
			SQLGetAccount.append("SELECT Account_ID ");
			SQLGetAccount.append(" FROM C_ValidCombination ");
			SQLGetAccount.append(" WHERE C_ValidCombination_ID = ");
			
			StringBuilder SQLGetBPAcct = new StringBuilder();
			SQLGetBPAcct.append(" (SELECT V_Liability_Acct  ");
			SQLGetBPAcct.append(" FROM C_BP_Vendor_Acct ");
			SQLGetBPAcct.append(" WHERE C_BPartner_ID = "+C_BPartner_ID);
			SQLGetBPAcct.append(" AND C_AcctSchema_ID = 1000003)");
			
			StringBuilder SQLGetOSAmt = new StringBuilder();
			SQLGetOSAmt.append("SELECT TotalOutstanding  ");
			SQLGetOSAmt.append(" FROM C_Invoice_OutStanding  ");
			SQLGetOSAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			int AccountBP_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetAccount.toString()+SQLGetBPAcct.toString());
			
			StringBuilder SQLGetpaymentAmt = new StringBuilder();
			SQLGetpaymentAmt.append("SELECT AmtSourceDr ");
			SQLGetpaymentAmt.append(" FROM GL_JournalLine  ");
			SQLGetpaymentAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			SQLGetpaymentAmt.append(" AND Account_ID = "+AccountBP_ID);

			BigDecimal paymentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetpaymentAmt.toString());
			BigDecimal currentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetOSAmt.toString());

			BigDecimal rsOSAmt = currentOSAmt.add(paymentOSAmt);
			
			if(paymentOSAmt.compareTo(Env.ZERO)==0) {
				
				MBPartner bp = new MBPartner(journal.getCtx(), C_BPartner_ID, journal.get_TrxName());
				
				BigDecimal currentOpenBalance = bp.getTotalOpenBalance();
				BigDecimal rsOpenBalance = currentOpenBalance.subtract(paymentOSAmt);
				
				bp.setTotalOpenBalance(rsOpenBalance);
				bp.saveEx();
				
			}
			
			StringBuilder SQLUpdateComplete = new StringBuilder();
			SQLUpdateComplete.append("UPDATE C_Invoice_OutStanding ");
			SQLUpdateComplete.append(" SET TotalOutstanding = "+rsOSAmt);
			SQLUpdateComplete.append(" ,IsPaid = 'N'");
			SQLUpdateComplete.append(" ,GL_Journal_ID = null ");
			SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
			DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
			
		}
		return rslt;
	}
	
	private static String beforeReverseAccrueTBU(MJournal journal) {
		String rslt = "";
		
		MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());
		
		if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {
		
			StringBuilder SQLUpdateReverseAccrue = new StringBuilder();
			SQLUpdateReverseAccrue.append("UPDATE TBU_BAOperation ");
			SQLUpdateReverseAccrue.append(" SET IsUnbilled = 'N' ");
			SQLUpdateReverseAccrue.append(" ,GL_Journal_ID = null ");
			SQLUpdateReverseAccrue.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			DB.executeUpdate(SQLUpdateReverseAccrue.toString(), journal.get_TrxName());
		
		}else if(cat.get_ValueAsString("CategoryCode").equals("IOT")) {
			
			StringBuilder SQLGetBP = new StringBuilder();
			SQLGetBP.append("SELECT C_BPartner_ID  ");
			SQLGetBP.append(" FROM GL_JournalLine  ");
			SQLGetBP.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			int C_BPartner_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetBP.toString());
			
			StringBuilder SQLGetAccount = new StringBuilder();
			SQLGetAccount.append("SELECT Account_ID ");
			SQLGetAccount.append(" FROM C_ValidCombination ");
			SQLGetAccount.append(" WHERE C_ValidCombination_ID = ");
			
			StringBuilder SQLGetBPAcct = new StringBuilder();
			SQLGetBPAcct.append(" (SELECT V_Liability_Acct  ");
			SQLGetBPAcct.append(" FROM C_BP_Vendor_Acct ");
			SQLGetBPAcct.append(" WHERE C_BPartner_ID = "+C_BPartner_ID);
			SQLGetBPAcct.append(" AND C_AcctSchema_ID = 1000003)");
			
			StringBuilder SQLGetOSAmt = new StringBuilder();
			SQLGetOSAmt.append("SELECT TotalOutstanding  ");
			SQLGetOSAmt.append(" FROM C_Invoice_OutStanding  ");
			SQLGetOSAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			int AccountBP_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetAccount.toString()+SQLGetBPAcct.toString());
			
			StringBuilder SQLGetpaymentAmt = new StringBuilder();
			SQLGetpaymentAmt.append("SELECT AmtSourceDr ");
			SQLGetpaymentAmt.append(" FROM GL_JournalLine  ");
			SQLGetpaymentAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			SQLGetpaymentAmt.append(" AND Account_ID = "+AccountBP_ID);

			BigDecimal paymentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetpaymentAmt.toString());
			BigDecimal currentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetOSAmt.toString());
			
			
			if(paymentOSAmt.compareTo(Env.ZERO)==0) {
				
				MBPartner bp = new MBPartner(journal.getCtx(), C_BPartner_ID, journal.get_TrxName());
				
				BigDecimal currentOpenBalance = bp.getTotalOpenBalance();
				BigDecimal rsOpenBalance = currentOpenBalance.subtract(paymentOSAmt);
				
				bp.setTotalOpenBalance(rsOpenBalance);
				bp.saveEx();
				
			}

			BigDecimal rsOSAmt = currentOSAmt.add(paymentOSAmt);
			StringBuilder SQLUpdateComplete = new StringBuilder();
			SQLUpdateComplete.append("UPDATE C_Invoice_OutStanding ");
			SQLUpdateComplete.append(" SET TotalOutstanding = "+rsOSAmt);
			SQLUpdateComplete.append(" ,IsPaid = 'N'");
			SQLUpdateComplete.append(" ,GL_Journal_ID = null ");
			SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
			DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
			
		}
		return rslt;
	}
		
	
	private static String beforeReActiveTBU(MJournal journal) {
		
		String rslt = "";
		
		MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());

		if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {

			
			StringBuilder SQLUpdateReActive = new StringBuilder();
			SQLUpdateReActive.append("UPDATE TBU_BAOperation ");
			SQLUpdateReActive.append(" SET IsUnbilled = 'N' ");
			SQLUpdateReActive.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			DB.executeUpdate(SQLUpdateReActive.toString(), journal.get_TrxName());
		
		}else if(cat.get_ValueAsString("CategoryCode").equals("IOT")) {
			
			StringBuilder SQLGetBP = new StringBuilder();
			SQLGetBP.append("SELECT C_BPartner_ID  ");
			SQLGetBP.append(" FROM GL_JournalLine  ");
			SQLGetBP.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			int C_BPartner_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetBP.toString());
			
			StringBuilder SQLGetAccount = new StringBuilder();
			SQLGetAccount.append("SELECT Account_ID ");
			SQLGetAccount.append(" FROM C_ValidCombination ");
			SQLGetAccount.append(" WHERE C_ValidCombination_ID = ");
			
			StringBuilder SQLGetBPAcct = new StringBuilder();
			SQLGetBPAcct.append(" (SELECT V_Liability_Acct  ");
			SQLGetBPAcct.append(" FROM C_BP_Vendor_Acct ");
			SQLGetBPAcct.append(" WHERE C_BPartner_ID = "+C_BPartner_ID);
			SQLGetBPAcct.append(" AND C_AcctSchema_ID = 1000003)");
			
			StringBuilder SQLGetOSAmt = new StringBuilder();
			SQLGetOSAmt.append("SELECT TotalOutstanding  ");
			SQLGetOSAmt.append(" FROM C_Invoice_OutStanding  ");
			SQLGetOSAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			int AccountBP_ID = DB.getSQLValue(journal.get_TrxName(), SQLGetAccount.toString()+SQLGetBPAcct.toString());
			
			StringBuilder SQLGetpaymentAmt = new StringBuilder();
			SQLGetpaymentAmt.append("SELECT AmtSourceDr ");
			SQLGetpaymentAmt.append(" FROM GL_JournalLine  ");
			SQLGetpaymentAmt.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			SQLGetpaymentAmt.append(" AND Account_ID = "+AccountBP_ID);

			BigDecimal paymentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetpaymentAmt.toString());
			BigDecimal currentOSAmt = DB.getSQLValueBD(journal.get_TrxName(), SQLGetOSAmt.toString());

			BigDecimal rsOSAmt = currentOSAmt.add(paymentOSAmt);
			
			if(paymentOSAmt.compareTo(Env.ZERO)==0) {
				
				MBPartner bp = new MBPartner(journal.getCtx(), C_BPartner_ID, journal.get_TrxName());
				
				BigDecimal currentOpenBalance = bp.getTotalOpenBalance();
				BigDecimal rsOpenBalance = currentOpenBalance.subtract(paymentOSAmt);
				
				bp.setTotalOpenBalance(rsOpenBalance);
				bp.saveEx();
				
			}
			
			StringBuilder SQLUpdateReActive = new StringBuilder();
			SQLUpdateReActive.append("UPDATE C_Invoice_OutStanding ");
			SQLUpdateReActive.append(" SET TotalOutstanding = "+rsOSAmt);
			SQLUpdateReActive.append(" ,IsPaid = 'N'");
			SQLUpdateReActive.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
			DB.executeUpdate(SQLUpdateReActive.toString(), journal.get_TrxName());
			
		}
		
		return rslt;
	}  		
	
	
	private static String beforeDeleteTBU(MJournal journal) {
		String rslt = "";
		
		MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());
		
		if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {
		
		}else if(cat.get_ValueAsString("CategoryCode").equals("IOT")) {

			StringBuilder SQLUpdateComplete = new StringBuilder();
			SQLUpdateComplete.append("UPDATE C_Invoice_OutStanding ");
			SQLUpdateComplete.append(" SET IsPaid = 'N'");
			SQLUpdateComplete.append(" ,GL_Journal_ID = null ");
			SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());	
			DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
			
		}
		return rslt;
	}
	
	private static String beforeCompleteWS(MJournal journal) {
		String rslt = "";
		
		MGLCategory cat = new MGLCategory(journal.getCtx(), journal.getGL_Category_ID(), journal.get_TrxName());
		
		if(cat.get_ValueAsString("CategoryCode").equals("PU") ||cat.get_ValueAsString("CategoryCode").equals("RU") ) {
		
			StringBuilder SQLUpdateComplete = new StringBuilder();
			SQLUpdateComplete.append("UPDATE TBU_BAOperation ");
			SQLUpdateComplete.append(" SET IsUnbilled = 'Y' ");
			SQLUpdateComplete.append(" WHERE GL_Journal_ID = "+ journal.getGL_Journal_ID());
			
			DB.executeUpdate(SQLUpdateComplete.toString(), journal.get_TrxName());
		
		}
		
		return rslt;
		
		
	}
}
