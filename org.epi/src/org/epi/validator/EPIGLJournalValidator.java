package org.epi.validator;

import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MGLCategory;
import org.compiere.model.MJournal;
import org.compiere.model.MOrg;
import org.compiere.model.MPeriod;
import org.compiere.model.MYear;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
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
		
}
