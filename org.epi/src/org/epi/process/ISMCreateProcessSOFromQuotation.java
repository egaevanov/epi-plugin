package org.epi.process;

import java.math.BigDecimal;
import java.util.HashMap;
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
	private int paramCnt = 0;

	
	private int p_Termin = 0;
	private int p_C_Order_ID = 0;
	private BigDecimal p_term1=Env.ZERO; 
	private BigDecimal p_term2=Env.ZERO; 
	private BigDecimal p_term3=Env.ZERO; 
	private BigDecimal p_term4=Env.ZERO; 
	private BigDecimal p_term5=Env.ZERO; 
	private HashMap<Integer, BigDecimal> HashTermin = new HashMap<Integer, BigDecimal>();
	private HashMap<Integer, String>HashDesc = new HashMap<Integer, String>();
	private String p_desc_term1 = "";
	private String p_desc_term2 = "";
	private String p_desc_term3 = "";
	private String p_desc_term4 = "";
	private String p_desc_term5 = "";

	
	
	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0 ; i < para.length ;i++){
			
			String name = para[i].getParameterName();
			
			if(para[i].getParameter()==null)
				;
			else if(name.equals("C_Quotation_ID")) {
				p_C_Quotation_ID = (int)para[i].getParameterAsInt();
			}else if(name.equals("termLabel1")) {
				p_desc_term1 = (String)para[i].getParameterAsString();
			}else if(name.equals("termPercent1")) {
				p_term1 = (BigDecimal)para[i].getParameterAsBigDecimal();
				if(p_term1 != null) {
					if(p_term1.compareTo(Env.ZERO)>0) {
						paramCnt = paramCnt +1;			
						HashTermin.put(paramCnt, p_term1);
						HashDesc.put(paramCnt, p_desc_term1);
					}
				}
			}else if(name.equals("termLabel2")) {
				p_desc_term2 = (String)para[i].getParameterAsString();
			}else if(name.equals("termPercent2")){
				p_term2 = (BigDecimal)para[i].getParameterAsBigDecimal();
				if(p_term2 != null) {
					if(p_term2.compareTo(Env.ZERO)>0) {
						paramCnt = paramCnt +1;
						HashTermin.put(paramCnt, p_term2);
						HashDesc.put(paramCnt, p_desc_term2);

					}
				}	
			}else if(name.equals("termLabel3")) {
				p_desc_term3 = (String)para[i].getParameterAsString();
			}else if(name.equals("termPercent3")) {
				p_term3 = (BigDecimal)para[i].getParameterAsBigDecimal();
				if(p_term3 != null) {
					if(p_term3.compareTo(Env.ZERO)>0) {
						paramCnt = paramCnt +1;
						HashTermin.put(paramCnt, p_term3);
						HashDesc.put(paramCnt, p_desc_term3);

					}
				}	
			}else if(name.equals("termLabel4")) {
				p_desc_term4 = (String)para[i].getParameterAsString();
			}else if(name.equals("termPercent4")) {
				p_term4 = (BigDecimal)para[i].getParameterAsBigDecimal();
				if(p_term4 != null) {
					if(p_term4.compareTo(Env.ZERO)>0) {
						paramCnt = paramCnt +1;
						HashTermin.put(paramCnt, p_term4);
						HashDesc.put(paramCnt, p_desc_term4);

					}
				}	
			}else if(name.equals("termLabel5")) {
				p_desc_term5 = (String)para[i].getParameterAsString();
			}else if(name.equals("termPercent5")) {
				p_term5 = (BigDecimal)para[i].getParameterAsBigDecimal();		
				if(p_term5 != null) {
					if(p_term5.compareTo(Env.ZERO)>0) {
						paramCnt = paramCnt +1;
						HashTermin.put(paramCnt, p_term5);
						HashDesc.put(paramCnt, p_desc_term5);
					}
				}
				
			}
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			
		}
		
		
		p_C_Order_ID = getRecord_ID();
		p_Termin = paramCnt;
	}

	@Override
	protected String doIt() throws Exception {
		
		String rs = "";
		MQuotation quo = new MQuotation(getCtx(), p_C_Quotation_ID, get_TrxName());	
		MOrder Order = new MOrder(getCtx(), p_C_Order_ID, get_TrxName());

		
		if(!quo.getDocStatus().equals(MQuotation.DOCACTION_Complete)) {
			rs = "Cant Convert Quotation - Quotation Status Not Complete n n.";
			return rs;		
		}
		
		if(paramCnt != p_Termin ) {
			rs = "Parameter Persentase Belum Sesuai Dengan Jumlah Termin Yang Ditentukan";
			return rs;
		}
		
		
		try {
				
			
			if(quo.getC_Order_ID() > 0) {
				rs = "Quotation Sudah Pernah Di Generata Pada Dokumen Sales Order Lain ";
				return rs;
			}
			
//			if(quo.getDocStatus()!= "CO") {
//				rs = "Hanya Quotation Dengan Status Complete Yang Bisa Di Generate Menjadi Dokumen Sales Order";
//				return rs;
//			}
			
			
			BigDecimal totalPersentase = Env.ZERO;
			for (int i = 1 ; i <= HashTermin.size() ; i++) {
				totalPersentase = totalPersentase.add(HashTermin.get(i));		
			}
			
			if(totalPersentase.compareTo(Env.ONEHUNDRED)>0) {
				rs = "Total Persentase Termin Yang Anda Inputkan Melebihi 100%";
				return rs;		
			}
			
			
	
			StringBuilder getTax = new StringBuilder();
			getTax.append("SELECT C_Tax_ID ");
			getTax.append(" FROM C_Tax ");
			getTax.append(" WHERE AD_Client_ID = "+getAD_Client_ID());
			
			X_C_QuotationLine[] lines  = quo.getLines();
				
			int M_Product_ID = 0;
			int C_UOM_ID = 0;
			BigDecimal price = quo.getGrandTotal();
			
			for (X_C_QuotationLine quotline : lines) {
				
				M_Product_ID = quotline.getM_Product_ID();
				C_UOM_ID = quotline.getC_UOM_ID();
				
			}
			
			MProduct prod = new MProduct(getCtx(), M_Product_ID, get_TrxName());
			MTaxCategory taxCat = new MTaxCategory(getCtx(), prod.getC_TaxCategory_ID(), get_TrxName());
			getTax.append(" AND C_TaxCategory_ID = "+taxCat.getC_TaxCategory_ID());

			int C_Tax_ID = DB.getSQLValue(get_TrxName(), getTax.toString()); 	
			
			for(int i = 0 ; i < p_Termin ; i++) {
				int lineNo = (i+1)*10;
				BigDecimal terminPrice = Env.ZERO;
				BigDecimal percenTerm = Env.ZERO;
				BigDecimal term = Env.ZERO;
				String desc = "";
						
				MOrderLine OrderLine = new MOrderLine(getCtx(), 0, get_TrxName());
				
				OrderLine.setAD_Org_ID(Order.getAD_Org_ID());
				OrderLine.setC_Order_ID(Order.getC_Order_ID());
				OrderLine.setM_Product_ID(M_Product_ID);
				OrderLine.setC_UOM_ID(C_UOM_ID);
				OrderLine.setQtyEntered(Env.ONE);
				OrderLine.setQtyOrdered(Env.ONE);
				OrderLine.setLine(lineNo);	
				
				//calculate price
				if(i+1 == 1) {
					desc = HashDesc.get(1);
					term = HashTermin.get(1);
					percenTerm = term.divide(Env.ONEHUNDRED);
				}else if(i+1 ==2) {
					desc = HashDesc.get(2);
					term =HashTermin.get(2);
					percenTerm = term.divide(Env.ONEHUNDRED);
				}else if(i+1 ==3) {
					desc = HashDesc.get(3);
					term =HashTermin.get(3);
					percenTerm = term.divide(Env.ONEHUNDRED);
				}else if(i+1 ==4) {
					desc = HashDesc.get(4);
					term = HashTermin.get(4);
					percenTerm = term.divide(Env.ONEHUNDRED);
				}else if(i+1 ==5) {
					desc = HashDesc.get(5);
					term = HashTermin.get(5);
					percenTerm = term.divide(Env.ONEHUNDRED);
				}
				
				OrderLine.setDescription(desc+" : "+term+"%");
				terminPrice = price.multiply(percenTerm);
	
				OrderLine.setC_Tax_ID(C_Tax_ID);
				OrderLine.setPriceList(terminPrice);
				OrderLine.setPriceEntered(terminPrice);
				OrderLine.setPriceActual(terminPrice);
				OrderLine.setLineNetAmt(terminPrice.multiply(Env.ONE));
				OrderLine.setC_Currency_ID(Order.getC_Currency_ID());
				OrderLine.set_CustomColumn("BillPrecentage", term);
				OrderLine.save();	
				
			}
		
			
			
		} catch (Exception e) {

			rollback();
			return rs;
		
		}
		

		quo.setC_Order_ID(Order.getC_Order_ID());
		quo.saveEx();
		return rs;
	}
		
		

}
