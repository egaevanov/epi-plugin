package org.epi.process;

import java.util.logging.Level;

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

public class TBUProcessCreateInvoiceFromTemp extends SvrProcess{

	private int p_Record_ID = 0;
	
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_Invoice_OutStanding_ID"))
			;
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		
		p_Record_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {

			MInvoice inv = new MInvoice(getCtx(), 0, get_TrxName());
			
			if(inv.save()) {
				
				MInvoiceLine[] lines = inv.getLines();
				
				for(MInvoiceLine line : lines) {
					
					line.setC_Invoice_ID(inv.getC_Invoice_ID());
					line.setAD_Org_ID(inv.getAD_Org_ID());
					line.setLine(line.getLine());
//					line.setC_Tax_ID(C_Tax_ID);
					line.setQtyEntered(Env.ONE);
					line.setQty(Env.ONE);
					line.setQtyInvoiced(Env.ONE);
					
					line.saveEx();
					
				}
				
				
			}
			
			
			
		
		return null;
	}

}
