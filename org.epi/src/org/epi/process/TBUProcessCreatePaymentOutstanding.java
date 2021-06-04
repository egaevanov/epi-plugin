package org.epi.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_C_Invoice_OutStanding;

public class TBUProcessCreatePaymentOutstanding extends SvrProcess {
	
	
	private int p_C_Invoice_OutStanding_ID = 0;
	private int p_C_BankAccount_ID  = 0;
	private BigDecimal p_pph23Amt = Env.ZERO;
	private int p_GL_Journal_ID = 0;
	

	@Override
	protected void prepare() {
	
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_Invoice_OutStanding_ID"))
				p_C_Invoice_OutStanding_ID  = (int)para[i].getParameterAsInt();
			else if(name.equals("C_BankAccount_ID"))
				p_C_BankAccount_ID = (int)para[i].getParameterAsInt();
			else if(name.equals("TaxAmount"))
				p_pph23Amt= (BigDecimal)para[i].getParameterAsBigDecimal();
			
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_GL_Journal_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		if(p_C_BankAccount_ID == 0) {
			return"";
		}
		
		MJournal journal = new MJournal(getCtx(), p_GL_Journal_ID, get_TrxName());
		X_C_Invoice_OutStanding invOS = new X_C_Invoice_OutStanding(getCtx(), p_C_Invoice_OutStanding_ID, get_TrxName());
		
		
		if(invOS != null) {
			
			journal.setDescription(invOS.getDescription());
			journal.saveEx();
			
			
			StringBuilder SQLGetAccount = new StringBuilder();
			SQLGetAccount.append("SELECT Account_ID ");
			SQLGetAccount.append(" FROM C_ValidCombination ");
			SQLGetAccount.append(" WHERE C_ValidCombination_ID = ");
			
			StringBuilder SQLGetBPAcct = new StringBuilder();
			SQLGetBPAcct.append(" (SELECT V_Liability_Acct  ");
			SQLGetBPAcct.append(" FROM C_BP_Vendor_Acct ");
			SQLGetBPAcct.append(" WHERE C_BPartner_ID = "+invOS.getC_BPartner_ID());
			SQLGetBPAcct.append(" AND C_AcctSchema_ID = 1000003)");
			
			StringBuilder SQLGetBankAcct = new StringBuilder();
			SQLGetBankAcct.append(" (SELECT B_Asset_Acct   ");
			SQLGetBankAcct.append(" FROM C_BankAccount_Acct ");
			SQLGetBankAcct.append(" WHERE C_BankAccount_ID = "+p_C_BankAccount_ID);
			SQLGetBankAcct.append(" AND C_AcctSchema_ID = 1000003)");
			
			
			int AccountBP_ID = DB.getSQLValue(get_TrxName(), SQLGetAccount.toString()+SQLGetBPAcct.toString());
			int AccountBank_ID = DB.getSQLValue(get_TrxName(), SQLGetAccount.toString()+SQLGetBankAcct.toString());
			MJournalLine payOSLine = null;

			
			for(int i = 0 ; i < 2 ; i++) {

				payOSLine = new MJournalLine(getCtx(), 0, get_TrxName());
				
				payOSLine.setGL_Journal_ID(journal.getGL_Journal_ID());
				payOSLine.setAD_Org_ID(journal.getAD_Org_ID());
				payOSLine.setLine((i+1)*10);
				payOSLine.setC_BPartner_ID(invOS.getC_BPartner_ID());
				payOSLine.setC_Currency_ID(journal.getC_Currency_ID());
							
				payOSLine.setC_Activity_ID(invOS.getC_Activity_ID());
				payOSLine.setC_Project_ID(invOS.getC_Project_ID());
				payOSLine.set_CustomColumn("ISM_Department_ID", invOS.getISM_Department_ID());					
				payOSLine.setDateAcct(journal.getDateAcct());
				payOSLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());	
				payOSLine.setDescription(invOS.getDescription());					
				
				if(i==0) {
					payOSLine.setAccount_ID(AccountBP_ID);
					payOSLine.setAmtSourceDr(invOS.getTotalOutstanding());
					payOSLine.setAmtSourceCr(Env.ZERO);
					payOSLine.setAmtAcct(invOS.getTotalOutstanding(), Env.ZERO);		
				}else {
					payOSLine.setAccount_ID(AccountBank_ID);
					payOSLine.setAmtSourceDr(Env.ZERO);
					payOSLine.setAmtSourceCr(invOS.getTotalOutstanding());
					payOSLine.setAmtAcct(Env.ZERO,invOS.getTotalOutstanding());		
				}
				
				payOSLine.saveEx();
				
			}
			
			
			if(p_pph23Amt.compareTo(Env.ZERO)> 0) {
				
				
				StringBuilder SQLGetPPhAcct = new StringBuilder();
				SQLGetPPhAcct.append(" (SELECT description::numeric  ");
				SQLGetPPhAcct.append(" FROM AD_Param ");
				SQLGetPPhAcct.append(" WHERE AD_Client_ID = "+getAD_Client_ID());
				SQLGetPPhAcct.append(" AND AD_Org_ID = "+journal.getAD_Org_ID());
				SQLGetPPhAcct.append(" AND Value = 'AccountTaxPayableInvoiceOutStanding')");
				
				int AccountPPh_ID = DB.getSQLValue(get_TrxName(), SQLGetPPhAcct.toString());

				
				for(int i = 0 ; i < 2 ; i++) {

					payOSLine = new MJournalLine(getCtx(), 0, get_TrxName());
					
					payOSLine.setGL_Journal_ID(journal.getGL_Journal_ID());
					payOSLine.setAD_Org_ID(journal.getAD_Org_ID());
					payOSLine.setLine((i+3)*10);
					payOSLine.setC_BPartner_ID(invOS.getC_BPartner_ID());
					payOSLine.setC_Currency_ID(journal.getC_Currency_ID());
								
					payOSLine.setC_Activity_ID(invOS.getC_Activity_ID());
					payOSLine.setC_Project_ID(invOS.getC_Project_ID());
					payOSLine.set_CustomColumn("ISM_Department_ID", invOS.getISM_Department_ID());					
					payOSLine.setDateAcct(journal.getDateAcct());
					payOSLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());	
					payOSLine.setDescription(invOS.getDescription());					
					
					if(i==0) {
						payOSLine.setAccount_ID(AccountBank_ID);
						payOSLine.setAmtSourceDr(p_pph23Amt);
						payOSLine.setAmtSourceCr(Env.ZERO);
						payOSLine.setAmtAcct(p_pph23Amt, Env.ZERO);		
					}else {
						payOSLine.setAccount_ID(AccountPPh_ID);
						payOSLine.setAmtSourceDr(Env.ZERO);
						payOSLine.setAmtSourceCr(p_pph23Amt);
						payOSLine.setAmtAcct(Env.ZERO,p_pph23Amt);		
					}
					
					payOSLine.saveEx();
					
				}
				
				
			}
			
			if(payOSLine != null) {
				
			invOS.setGL_Journal_ID(journal.getGL_Journal_ID());
			invOS.saveEx();
				
			}
			
		}
		
		
		
		return "";
	}

}
