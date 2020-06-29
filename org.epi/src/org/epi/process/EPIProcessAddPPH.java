package org.epi.process;

import java.math.BigDecimal;

import org.compiere.model.MCharge;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MProduct;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class EPIProcessAddPPH extends SvrProcess {

	private int C_Invoice_ID = 0;
	
	
	@Override
	protected void prepare() {
		
		C_Invoice_ID = getRecord_ID();
			
	}

	@Override
	protected String doIt() throws Exception {
		
		MProduct prod = null;

		if(C_Invoice_ID > 0) {
			
			MInvoice inv = new MInvoice(getCtx(), C_Invoice_ID, get_TrxName());
			MInvoiceLine [] lines = inv.getLines();
			
			boolean IsService = false;
			
			for(MInvoiceLine line : lines) {
				
				int M_Product_ID = line.getM_Product_ID();
				
				if(M_Product_ID > 0) {
					
					prod = new MProduct(getCtx(), M_Product_ID, get_TrxName());
					
					if(prod.getProductType().equals(MProduct.PRODUCTTYPE_Service)) {
						
						IsService = true;
						
					}
					
				}
				
			}
			
			if(IsService) {
				
				BigDecimal pphRate = (BigDecimal) prod.get_Value("RatePPH");
				
				BigDecimal pph = pphRate.divide(Env.ONEHUNDRED);
				
				BigDecimal PPhAmt = inv.getTotalLines().multiply(pph);
				
				MInvoiceLine invLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
				invLine.setAD_Org_ID(inv.getAD_Org_ID());
				invLine.setC_Invoice_ID(C_Invoice_ID);
				
				StringBuilder getPPh = new StringBuilder();
				getPPh.append("SELECT C_Charge_ID ");
				getPPh.append(" FROM  C_Charge ");
				getPPh.append(" WHERE lower(name)='pph 23'");
				getPPh.append(" AND AD_Client_ID =  "+inv.getAD_Client_ID());
				
				Integer C_Charge_ID = DB.getSQLValueEx(get_TrxName(), getPPh.toString());
				MCharge charge = new MCharge(getCtx(), C_Charge_ID, get_TrxName());
				
				StringBuilder getTax = new StringBuilder();
				getTax.append("SELECT C_Tax_ID ");
				getTax.append(" FROM  C_Tax ");
				getTax.append(" WHERE C_TaxCategory_ID = "+ charge.getC_TaxCategory_ID());
				getTax.append(" AND AD_Client_ID =  "+inv.getAD_Client_ID());
				Integer C_Tax_ID = DB.getSQLValueEx(get_TrxName(), getTax.toString());
				
				invLine.setC_Charge_ID(C_Charge_ID);
				invLine.setC_Tax_ID(C_Tax_ID);
				invLine.setQtyEntered(Env.ONE);
				invLine.setQty(Env.ONE);
				invLine.setQtyInvoiced(Env.ONE);
				invLine.setPriceEntered(PPhAmt.negate());
				invLine.setPriceActual(PPhAmt.negate());
				invLine.saveEx();
				
						
			}
			
			
			
		}
		
		return "";
	}

	
	
}
