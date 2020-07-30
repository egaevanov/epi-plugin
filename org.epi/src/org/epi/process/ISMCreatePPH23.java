package org.epi.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrg;
import org.compiere.model.MPayment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.utils.FinalVariableGlobal;

public class ISMCreatePPH23 extends SvrProcess {

	
	private Timestamp p_DateAcct = null;
	int p_C_Payment_ID = 0;
	
	
	@Override
	protected void prepare() {
	
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
						
			if (para[i].getParameter() == null)
				;		
			else if (name.equals("DateAcct")) {
				p_DateAcct = para[i].getParameterAsTimestamp();	
			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_C_Payment_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		
	MPayment payment = new MPayment(getCtx(), p_C_Payment_ID, get_TrxName());
	MOrg org = new MOrg(payment.getCtx(), payment.getAD_Org_ID(), null);
	
	if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
		
		if(payment.getC_Invoice_ID() > 0) {
			
			MInvoice invoice = new MInvoice(getCtx(), payment.getC_Invoice_ID(), payment.get_TrxName());
			
			if(invoice.isSOTrx()) {
			
			BigDecimal pphRate = FinalVariableGlobal.ISMPPH23RATE;	
			BigDecimal pph = pphRate.divide(Env.ONEHUNDRED);
			BigDecimal PPhAmt = invoice.getTotalLines().multiply(pph);
			
			StringBuilder sqlPayDocType = new StringBuilder();
			sqlPayDocType.append("SELECT C_DocType_ID");
			sqlPayDocType.append(" FROM C_DocType");
			sqlPayDocType.append(" WHERE AD_Client_ID = ?");
			sqlPayDocType.append(" AND DocBaseType = ?");
			int pay_C_DocType_ID = DB.getSQLValueEx(get_TrxName(), sqlPayDocType.toString(), new Object[]{getAD_Client_ID(),MDocType.DOCBASETYPE_APPayment});

			
			StringBuilder getPPh = new StringBuilder();
			getPPh.append("SELECT C_Charge_ID ");
			getPPh.append(" FROM  C_Charge ");
			getPPh.append(" WHERE lower(name)='pph 23'");
			getPPh.append(" AND AD_Client_ID =  "+payment.getAD_Client_ID());
			Integer C_Charge_ID = DB.getSQLValueEx(get_TrxName(), getPPh.toString());
						
			if(C_Charge_ID < 0)
				return"Charge PPh 23 Belum Terdaftar";
			
			MPayment payPPh = new MPayment(getCtx(), 0, invoice.get_TrxName());
			payPPh.setAD_Org_ID(invoice.getAD_Org_ID());
			payPPh.setIsReceipt(true);

			payPPh.setC_DocType_ID(pay_C_DocType_ID);
			payPPh.setC_BPartner_ID(payment.getC_BPartner_ID());
			payPPh.setDescription("PPH23 Atas Invoice "+invoice.getDocumentNo());
			payPPh.setDateTrx(p_DateAcct);
			payPPh.setDateAcct(p_DateAcct);
			payPPh.setC_BankAccount_ID(payment.getC_BankAccount_ID());
			payPPh.setTenderType(MPayment.TENDERTYPE_Cash);
			payPPh.setPayAmt(PPhAmt);
			payPPh.setC_Currency_ID(payment.getC_Currency_ID());
			payPPh.setC_Charge_ID(C_Charge_ID);
			payPPh.saveEx();
			
			if(payPPh.processIt("CO")) {
				payPPh.saveEx();
			}else {
				rollback();
			}
		
			
			}
			
			
		}
		
	}
	
	
		return "";
	}

}
