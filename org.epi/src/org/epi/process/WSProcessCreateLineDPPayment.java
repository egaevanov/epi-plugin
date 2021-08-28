package org.epi.process;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class WSProcessCreateLineDPPayment extends SvrProcess {
	
	private int				p_AD_Org_ID = 0;
	private int				p_C_BankAccount_ID = 0;
	private int				p_DP_Invoice_ID =0; 
	private int 			p_DP_Payment_ID = 0;
	private Properties 		m_ctx;

	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("GL_Journal_ID"))
				p_DP_Invoice_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("C_BankAccount_ID"))
				p_C_BankAccount_ID = ((BigDecimal)para[i].getParameter()).intValue();

				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		m_ctx = Env.getCtx();
		p_AD_Org_ID = Env.getAD_Org_ID(m_ctx);
		p_DP_Payment_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {

		
		MJournal DPPayment = new MJournal(m_ctx, p_DP_Payment_ID, get_TrxName());
		MJournal DPInvoice = new MJournal(getCtx(), p_DP_Invoice_ID, get_TrxName());
		
		
		MJournalLine[] lines = DPInvoice.getLines(false);
		
		for (MJournalLine lineDPInvoice : lines) {
			
			MJournalLine lineDPPayment = new MJournalLine(getCtx(), 0, get_TrxName());
			
			lineDPPayment.setGL_Journal_ID(DPPayment.getGL_Journal_ID());
			lineDPPayment.setLine(lineDPInvoice.getLine());
			lineDPPayment.setAD_Org_ID(DPPayment.getAD_Org_ID());
			lineDPPayment.setC_Currency_ID(DPPayment.getC_Currency_ID());
			lineDPPayment.setC_BPartner_ID(lineDPInvoice.getC_BPartner_ID());
			lineDPPayment.setC_Project_ID(lineDPInvoice.getC_Project_ID());
			lineDPPayment.setC_ConversionType_ID(DPPayment.getC_ConversionType_ID());

			
			if(lineDPInvoice.getAmtSourceDr().compareTo(Env.ZERO) > 0) {
				
				lineDPPayment.setAccount_ID(lineDPInvoice.getAccount_ID());
				lineDPPayment.setAmtSourceDr(lineDPInvoice.getAmtSourceDr());
				lineDPPayment.setAmtSourceCr(Env.ZERO);
				lineDPPayment.setAmtAcct(lineDPInvoice.getAmtSourceDr(),Env.ZERO);
				
			}else if(lineDPInvoice.getAmtSourceCr().compareTo(Env.ZERO) > 0) {
						
				StringBuilder SQLGetAcct = new StringBuilder();
				SQLGetAcct.append("SELECT cvc.Account_ID  ");
				SQLGetAcct.append(" FROM C_BankAccount_Acct cbaa   ");
				SQLGetAcct.append(" INNER JOIN C_BankAccount cba ON cba.C_BankAccount_ID = cbaa.C_BankAccount_ID");
				SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = cbaa.B_Asset_Acct");
				SQLGetAcct.append(" WHERE cba.C_BankAccount_ID = "+p_C_BankAccount_ID);
				
				
				Integer Account_ID = DB.getSQLValueEx(get_TrxName(), SQLGetAcct.toString());
				
				lineDPPayment.setAccount_ID(Account_ID);
				lineDPPayment.setAmtSourceDr(Env.ZERO);
				lineDPPayment.setAmtSourceCr(lineDPInvoice.getAmtSourceCr());
				lineDPPayment.setAmtAcct(Env.ZERO,lineDPInvoice.getAmtSourceCr());
			}
			
			
			lineDPPayment.saveEx();
						
		}
			
		DPPayment.set_ValueOfColumn("DP_Invoice_ID", DPInvoice.getGL_Journal_ID());
		DPPayment.set_ValueOfColumn("ReferenceNo", DPInvoice.getDocumentNo());
		
		
		
		return "";
	}

}
