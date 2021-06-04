package org.epi.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.epi.ws.model.API_Model_GL;
import org.epi.ws.model.API_Model_GLLines;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class WSCreateGL extends SvrProcess {
	
	private int p_AD_Org_ID = 0;
	private String p_JSONHeader = "";
	private String p_JSONDetail = "";

	public static CLogger log = CLogger.getCLogger(PO.class);

	@Override
	protected void prepare() {	

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;

			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = (int) para[i].getParameterAsInt();

			else if (name.equals("JSONHeader"))
				p_JSONHeader = (String) para[i].getParameterAsString();
			
			else if (name.equals("JSONDetail"))
				p_JSONDetail = (String) para[i].getParameterAsString();
			
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		String rslt = "";
		
		try {
		
			String JSonHeaderString = "";
			String JSonDetailsString = "";
		
			JSonHeaderString = p_JSONHeader;
			JSonDetailsString = p_JSONDetail;
			
			Gson gson = new Gson();
			JsonArray jsonHeader = gson.fromJson(JSonHeaderString, JsonArray.class);
			JsonArray jsonDetails = gson.fromJson(JSonDetailsString, JsonArray.class);
	
			API_Model_GL dataHeader = gson.fromJson(jsonHeader.toString(), API_Model_GL.class);
			API_Model_GLLines[] dataDetails = gson.fromJson(jsonDetails.toString(), API_Model_GLLines[].class);
			
			MJournal journal = new MJournal(getCtx(), 0, get_TrxName());
			
			Timestamp DateAcct = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.DateAcct);
			Timestamp DateDoc = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.DateDoc);
						
			journal.setAD_Org_ID(p_AD_Org_ID);
			journal.setC_AcctSchema_ID(dataHeader.C_AcctSchema_ID);
			journal.setDescription(dataHeader.Description);
			journal.setPostingType(dataHeader.PostingType);
			journal.setDateAcct(DateAcct);
			journal.setC_Period_ID(dataHeader.C_Period_ID);
			journal.setDateDoc(DateDoc);
			journal.setC_Currency_ID(dataHeader.C_Currency_ID);
			journal.setC_DocType_ID(dataHeader.C_DocType_ID);
			journal.setGL_Category_ID(dataHeader.GL_Category_ID);
			journal.setC_ConversionType_ID(dataHeader.C_ConversionType_ID);
			
			int noLine = 0;

			if(journal.save()) {
				
				for (API_Model_GLLines DataDetail : dataDetails) {
					noLine = noLine+10;
					MJournalLine journalLine = new MJournalLine(getCtx(), 0, get_TrxName());
					
					Timestamp DateAcctDtl = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(DataDetail.DateAcct);
				
					journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
					journalLine.setAD_Org_ID(p_AD_Org_ID);
					journalLine.setDescription(journal.getDescription());
					journalLine.setC_Currency_ID(journal.getC_Currency_ID());
					journalLine.setAccount_ID(DataDetail.Account_ID);
					journalLine.setAmtSourceDr(DataDetail.AmtSourceDr);
					journalLine.setAmtSourceCr(DataDetail.AmtSourceCr);
					journalLine.setAmtAcct(DataDetail.AmtSourceDr, DataDetail.AmtSourceCr);
					journalLine.setC_ConversionType_ID(DataDetail.C_ConversionType_ID);
					journalLine.setDateAcct(DateAcctDtl);
					journalLine.saveEx();
						
				}
							
			}
			
			if(noLine > 0) {
				
				journal.processIt(MOrder.ACTION_Complete);
				
				if(journal.save()) {
					
					rslt = ""+journal.getDocumentNo();
					
				}
				
			}
		
		} catch (Exception e) {

			rollback();
		
		}
			
		return rslt;
	}

}
