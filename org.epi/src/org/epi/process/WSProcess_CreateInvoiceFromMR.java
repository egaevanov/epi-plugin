package org.epi.process;

import java.util.logging.Level;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class WSProcess_CreateInvoiceFromMR extends SvrProcess {
	
	private int p_M_InOut_ID = 0;
	private int p_Invoice_ID = 0;
	

	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("M_InOut_ID")) {
				p_M_InOut_ID  = (int)para[i].getParameterAsInt();
			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_Invoice_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		String rslt = "";
		
		
		MInvoice Inv = new MInvoice(getCtx(), p_Invoice_ID, get_TrxName());
		MInOut MReceipt = new MInOut(getCtx(), p_M_InOut_ID, get_TrxName());
		MOrder Order = new MOrder(getCtx(), MReceipt.getC_Order_ID(), get_TrxName());
		
		Inv.setAD_Org_ID(MReceipt.getAD_Org_ID());
		Inv.setC_Order_ID(MReceipt.getC_Order_ID());
		Inv.setC_BPartner_ID(MReceipt.getC_BPartner_ID());
		Inv.setC_BPartner_Location_ID(MReceipt.getC_BPartner_Location_ID());
		Inv.setM_PriceList_ID(Order.getM_PriceList_ID());
		Inv.setC_Currency_ID(Order.getC_Currency_ID());
		Inv.setPaymentRule(Order.getPaymentRule());
		Inv.setC_PaymentTerm_ID(Order.getC_PaymentTerm_ID());
		Inv.setC_Project_ID(Order.getC_Project_ID());
		Inv.setC_Activity_ID(Order.getC_Activity_ID());
		
		if(MReceipt.getC_Order_ID() > 0) {
		
			StringBuilder SQLGetAsset = new StringBuilder();
			SQLGetAsset.append("SELECT A_Asset_ID ");
			SQLGetAsset.append(" FROM A_Asset ");
			SQLGetAsset.append(" WHERE C_Order_ID = "+MReceipt.getC_Order_ID());
			
			int A_Asset_ID = DB.getSQLValueEx(get_TrxName(), SQLGetAsset.toString());
			
			if(A_Asset_ID > 0) {
				Inv.set_CustomColumn("A_Asset_ID", A_Asset_ID);
			}
		}
		
		Inv.saveEx();
		
		MInOutLine[] lines = MReceipt.getLines();
		
		for (MInOutLine line : lines) {
			
			MInvoiceLine invLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
			MOrderLine ordLine = new MOrderLine(getCtx(), line.getC_OrderLine_ID(), get_TrxName());
			
			invLine.setAD_Org_ID(line.getAD_Org_ID());
			invLine.setC_Invoice_ID(Inv.getC_Invoice_ID());
			
			if(line.getC_Charge_ID() > 0) {
				invLine.setC_Charge_ID(line.getC_Charge_ID());
			}
			
			if(line.getM_Product_ID() > 0) {	
				invLine.setM_Product_ID(line.getM_Product_ID());	
			}
			
			invLine.setLine(line.getLine());
			invLine.setC_Tax_ID(ordLine.getC_Tax_ID());
			
			invLine.setQtyEntered(line.getQtyEntered());
			invLine.setQty(line.getQtyEntered());
			invLine.setQtyInvoiced(line.getQtyEntered());	
			
			invLine.setPriceEntered(ordLine.getPriceEntered());
			invLine.setPriceActual(ordLine.getPriceEntered());
			invLine.saveEx();
			
			invLine.saveEx();
			
		}
		
		return rslt;
	}

}
