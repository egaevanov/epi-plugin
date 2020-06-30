package org.epi.validator;

import java.util.Calendar;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MDocType;
import org.compiere.model.MGLCategory;
import org.compiere.model.MJournal;
import org.compiere.model.MOrg;
import org.compiere.model.MSequence;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIGLJournalValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIGLJournalValidator.class);

	public static String executeJournal(Event event, PO po) {
		
		String msgJournal= "";
		MJournal journal = (MJournal) po;
		
		MOrg org = new MOrg(journal.getCtx(), journal.getAD_Org_ID(), null);
		
	if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
			
		if(org.getValue().equals(FinalVariableGlobal.EPI)) {
			msgJournal = beforeSaveEPI(journal);
		}
	
	}
		
	return msgJournal;

	}
	
	
	private static String beforeSaveEPI(MJournal journal) {

		
		String rslt = "";
		
		int GL_Category_ID = journal.getGL_Category_ID();
		String typeCode = "";
		String monthStr	= "";
		String yearStr	= "";
		String nextStr = "";
		String DocRef = "";

		
		if(GL_Category_ID > 0) {
			
			MGLCategory GLCat = new MGLCategory(journal.getCtx(), GL_Category_ID, null);
			typeCode = GLCat.get_ValueAsString("CategoryCode");
			
			
		}
		
		
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(journal.getDateDoc());
	    
	    Integer month = calendar.get(Calendar.MONTH)+1;
	    
	    if(month != 11 && month != 12) {
		    monthStr = "0"+String.valueOf(month);
	    }else {
	    	monthStr = String.valueOf(month);
	    }
	    
	    Integer year = calendar.get(Calendar.YEAR);
	    yearStr = String.valueOf(year).substring(2);
	    
	    //seq
		int C_DocType_ID = journal.getC_DocType_ID();
		
		if(C_DocType_ID > 0) {
			
			MDocType docType = new MDocType(journal.getCtx(), C_DocType_ID, null);
			
			if(docType.getDocNoSequence_ID() > 0) {
				
				MSequence docSeq = new MSequence(journal.getCtx(), docType.getDocNoSequence_ID(), null);
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
		
		journal.set_ValueNoCheck("ReferenceNo", DocRef);
	    journal.saveEx();
		
		
		return rslt;
		
}
	
	
}
