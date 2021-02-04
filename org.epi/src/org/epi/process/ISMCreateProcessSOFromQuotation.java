package org.epi.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MTaxCategory;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.MQuotation;
import org.epi.model.X_C_QuotationLine;

public class ISMCreateProcessSOFromQuotation extends SvrProcess{
	
	
	private int p_C_Quotation_ID = 0;

	
	private int p_Termin = 0;
	private int p_C_Order_ID = 0;
	private BigDecimal p_term1=Env.ZERO; 
	private BigDecimal p_term2=Env.ZERO; 
	private BigDecimal p_term3=Env.ZERO; 
	private BigDecimal p_term4=Env.ZERO; 
	private BigDecimal p_term5=Env.ZERO; 

	
	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0 ; i < para.length ;i++){
			
			String name = para[i].getParameterName();
			
			if(para[i].getParameter()==null)
				;
			else if(name.equals("C_Quotation_ID"))
				p_C_Quotation_ID = (int)para[i].getParameterAsInt();		
			else if(name.equals("Termin"))
				p_Termin = (int)para[i].getParameterAsInt();
			else if(name.equals("term1"))
				p_term1 = (BigDecimal)para[i].getParameterAsBigDecimal();
			else if(name.equals("term2"))
				p_term2 = (BigDecimal)para[i].getParameterAsBigDecimal();
			else if(name.equals("term3"))
				p_term3 = (BigDecimal)para[i].getParameterAsBigDecimal();
			else if(name.equals("term4"))
				p_term4 = (BigDecimal)para[i].getParameterAsBigDecimal();
			else if(name.equals("term5"))
				p_term5 = (BigDecimal)para[i].getParameterAsBigDecimal();
	
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			
		}
		
		
		p_C_Order_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		
		MQuotation quo = new MQuotation(getCtx(), p_C_Quotation_ID, get_TrxName());
//		
//		
		MOrder Order = new MOrder(getCtx(), p_C_Order_ID, get_TrxName());
//		Order.setAD_Org_ID(quo.getAD_Org_ID());
//		Order.setC_BPartner_ID(quo.getC_BPartner_ID());
//		Order.setC_BPartner_Location_ID(quo.getC_BPartner_Location_ID());
//		Order.setPOReference(quo.getDocumentNo());
//		Order.setDescription(quo.getDescription());
//		Order.setDeliveryRule(quo.getDeliveryRule());
//		Order.setDeliveryViaRule(quo.getDeliveryViaRule());
//		Order.setSalesRep_ID(quo.getSalesRep_ID());
//		Order.setC_Currency_ID(303);
//		//Order.setC_DocType_ID(quo.getOrderDocType_ID());
//		//Order.setC_DocTypeTarget_ID(quo.getOrderDocType_ID());
//		Order.setDateOrdered(quo.getDateOrdered());
//		Order.setDateAcct(quo.getDateOrdered());
//		Order.setDatePromised(quo.getDateOrdered());
//		Order.setM_Warehouse_ID(quo.getM_Warehouse_ID());
//		Order.setC_PaymentTerm_ID(quo.getC_PaymentTerm_ID());
//		Order.setM_PriceList_ID(quo.getM_PriceList_ID());
//		Order.setTotalLines(quo.getTotalLines());
//		Order.setGrandTotal(quo.getGrandTotal());
//		Order.setPaymentRule(quo.getPaymentRule());
//		Order.setIsSelfService(true);
//		Order.setIsSOTrx(true);
//		Order.setPOReference(quo.getDocumentNo());
//		Order.save();
		
		
		StringBuilder getTax = new StringBuilder();
		getTax.append("SELECT C_Tax_ID ");
		getTax.append(" FROM C_Tax ");
		getTax.append(" WHERE AD_Client_ID = "+getAD_Client_ID());
		
		X_C_QuotationLine[] lines  = quo.getLines();
			
		int M_Product_ID = 0;
		int C_UOM_ID = 0;
		BigDecimal price = Env.ZERO;
		
		for (X_C_QuotationLine quotline : lines) {
			
			M_Product_ID = quotline.getM_Product_ID();
			C_UOM_ID = quotline.getC_UOM_ID();
			price = quotline.getPrice();
			
		}
		
		
		for(int i = 0 ; i < p_Termin ; i++) {
			int lineNo = (i)*10;
			
			MOrderLine OrderLine = new MOrderLine(getCtx(), 0, get_TrxName());
			
			OrderLine.setAD_Org_ID(Order.getAD_Org_ID());
			OrderLine.setC_Order_ID(Order.getC_Order_ID());
			OrderLine.setM_Product_ID(M_Product_ID);
			OrderLine.setC_UOM_ID(C_UOM_ID);
			OrderLine.setQtyEntered(Env.ONE);
			OrderLine.setQtyOrdered(Env.ONE);
			OrderLine.setPriceList(price);
			OrderLine.setPriceEntered(price);
			OrderLine.setPriceActual(price);
			OrderLine.setDescription("Termin : "+ i+1 + " "+p_term1+"%");
			OrderLine.setLine(lineNo);
			
			MProduct prod = new MProduct(getCtx(), M_Product_ID, get_TrxName());
			MTaxCategory taxCat = new MTaxCategory(getCtx(), prod.getC_TaxCategory_ID(), get_TrxName());
			getTax.append(" AND C_TaxCatergory_ID = "+taxCat.getC_TaxCategory_ID());

			int C_Tax_ID = DB.getSQLValue(get_TrxName(), getTax.toString()); 			

			OrderLine.setC_Tax_ID(C_Tax_ID);
			OrderLine.setLineNetAmt(price.multiply(Env.ONE));
			OrderLine.setC_Currency_ID(Order.getC_Currency_ID());
			OrderLine.save();	
			
		}
		
//		for (X_C_QuotationLine quotline : lines) {
//			
//			MOrderLine OrderLine = new MOrderLine(getCtx(), 0, get_TrxName());
//			
//			OrderLine.setAD_Org_ID(Order.getAD_Org_ID());
//			OrderLine.setC_Order_ID(Order.getC_Order_ID());
//			OrderLine.setM_Product_ID(quotline.getM_Product_ID());
//			OrderLine.setC_UOM_ID(quotline.getC_UOM_ID());
//			OrderLine.setQtyEntered(quotline.getQty());
//			OrderLine.setQtyOrdered(quotline.getQty());
//			OrderLine.setPriceList(quotline.getPrice());
//			OrderLine.setPriceEntered(quotline.getPrice());
//			OrderLine.setPriceActual(quotline.getPrice());
//			OrderLine.setDescription(quotline.getDescription());
//			OrderLine.setLine(quotline.getLine());
//			
//			MProduct prod = new MProduct(getCtx(), quotline.getM_Product_ID(), get_TrxName());
//			MTaxCategory taxCat = new MTaxCategory(getCtx(), prod.getC_TaxCategory_ID(), get_TrxName());
//			getTax.append(" AND C_TaxCatergory_ID = "+taxCat.getC_TaxCategory_ID());
//
//			int C_Tax_ID = DB.getSQLValue(get_TrxName(), getTax.toString()); 
//			
//
//			OrderLine.setC_Tax_ID(C_Tax_ID);
//			OrderLine.setLineNetAmt(quotline.getLineNetAmt());
//			OrderLine.setC_Currency_ID(Order.getC_Currency_ID());
//			OrderLine.save();
//			
//		}
		
		
		return "";
	}

}
