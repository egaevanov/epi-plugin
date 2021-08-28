package org.epi.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.ws.model.API_Model_StockOpname;
import org.epi.ws.model.API_Model_StockOpnameLines;

public class WSExecuteStockOpname {
	
	public static Integer CreateStockOpname(int AD_Client_ID, int AD_Org_ID, API_Model_StockOpname dataHeader,API_Model_StockOpnameLines[] dataDetails, Properties ctx , String trxName, HashMap<String, Integer> MapProduct) {
		
		Integer result = 0;
		
		try {
			
			StringBuilder SQLGetAcctSchema = new StringBuilder();
			SQLGetAcctSchema.append("SELECT Description::NUMERIC ");
			SQLGetAcctSchema.append(" FROM AD_Param ");
			SQLGetAcctSchema.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetAcctSchema.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetAcctSchema.append(" AND Value = 'GL_DefaultParameter'");
			SQLGetAcctSchema.append(" AND Name = 'AccountingSchema'");
			
			Integer C_AcctSchema_ID = DB.getSQLValue(trxName, SQLGetAcctSchema.toString());
			
			StringBuilder SQLGetPostingType = new StringBuilder();
			SQLGetPostingType.append("SELECT Description ");
			SQLGetPostingType.append(" FROM AD_Param ");
			SQLGetPostingType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPostingType.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPostingType.append(" AND Value = 'GL_DefaultParameter'");
			SQLGetPostingType.append(" AND Name = 'PostingType'");
			String postingType = DB.getSQLValueString(trxName, SQLGetPostingType.toString());

			
			StringBuilder SQLGetGLCategory = new StringBuilder();
			SQLGetGLCategory.append("SELECT Description::NUMERIC ");
			SQLGetGLCategory.append(" FROM AD_Param ");
			SQLGetGLCategory.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetGLCategory.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetGLCategory.append(" AND Value = 'GL_DefaultParameter'");
			SQLGetGLCategory.append(" AND Name = 'DataType=1'");
			Integer GL_Category_ID = DB.getSQLValue(trxName, SQLGetGLCategory.toString());

			
			StringBuilder SQLGetDocumentType = new StringBuilder();
			SQLGetDocumentType.append("SELECT Description::NUMERIC ");
			SQLGetDocumentType.append(" FROM AD_Param ");
			SQLGetDocumentType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocumentType.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocumentType.append(" AND Value = 'GL_DefaultParameter'");
			SQLGetDocumentType.append(" AND Name = 'DocumentType'");
			Integer C_DocType_ID = DB.getSQLValue(trxName, SQLGetDocumentType.toString());


			Timestamp DateAcct = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.opname_date);
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(DateAcct);
		    calendar.add(Calendar.DAY_OF_WEEK, -1);

		    
		    @SuppressWarnings("static-access")
			Integer month = calendar.getInstance().get(Calendar.MONTH)+1;
		    @SuppressWarnings("static-access")
			Integer year = calendar.getInstance().get(Calendar.YEAR);
		    
		    
			StringBuilder SQLGetPeriod = new StringBuilder();
			SQLGetPeriod.append("SELECT C_Period_ID ");
			SQLGetPeriod.append(" FROM C_Period cp");
			SQLGetPeriod.append(" LEFT JOIN C_Year cy on cy.C_Year_ID = cp.C_Year_ID ");
			SQLGetPeriod.append(" WHERE cy.fiscalyear = '"+year+"'");
			SQLGetPeriod.append(" AND cp.periodno = '"+month+"'");

			Integer C_Period_ID = DB.getSQLValue(trxName, SQLGetPeriod.toString());
			
			StringBuilder SQLGetCurrency = new StringBuilder();
			SQLGetCurrency.append("SELECT Description::NUMERIC ");
			SQLGetCurrency.append(" FROM AD_Param ");
			SQLGetCurrency.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetCurrency.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetCurrency.append(" AND Value = 'GL_DefaultParameter'");
			SQLGetCurrency.append(" AND Name = 'Currency'");
			Integer C_Currency_ID = DB.getSQLValue(trxName, SQLGetCurrency.toString());	
			
			MJournal journal = new MJournal(ctx, 0, trxName);
			journal.setAD_Org_ID(AD_Org_ID);
			journal.setC_AcctSchema_ID(C_AcctSchema_ID);
			journal.setDescription(dataHeader.description);
			journal.setPostingType(postingType);
			journal.setDateAcct(DateAcct);
			journal.setC_Period_ID(C_Period_ID);
			journal.setDateDoc(DateAcct);
			journal.setC_Currency_ID(C_Currency_ID);
			journal.setC_DocType_ID(C_DocType_ID);
			journal.setGL_Category_ID(GL_Category_ID);
			journal.setC_ConversionType_ID(114);
			journal.set_ValueOfColumn("ReferenceNo", dataHeader.stockopname_no);
			
			
			if(journal.save()) {
				
				int lineNo = 0;
				
				for (API_Model_StockOpnameLines detail : dataDetails) {
					
					Integer M_Product_ID = MapProduct.get(detail.material_id);
							
					for (int i = 0 ; i < 2 ; i++) {
						
						lineNo = lineNo+1;

						MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
						
						journalLine.setLine(lineNo);
						journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
						journalLine.setAD_Org_ID(AD_Org_ID);
						journalLine.setM_Product_ID(M_Product_ID);
						journalLine.setQty(detail.qty);
						journalLine.setC_Project_ID(dataHeader.project_id);
	//					journalLine.setA_Asset_ID(A_Asset_ID);
						journalLine.setDescription(dataHeader.description);	
						journalLine.setC_Currency_ID(journal.getC_Currency_ID());
						journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());	
						journalLine.setDateAcct(journal.getDateAcct());
						
						if(detail.qty.compareTo(detail.qty_before) < 0) {
						
							if(i == 0) {
								StringBuilder SQLGetAcct = new StringBuilder();
								SQLGetAcct.append("SELECT Description::NUMERIC ");
								SQLGetAcct.append(" FROM AD_Param ");
								SQLGetAcct.append(" WHERE AD_Client_ID = "+AD_Client_ID);
								SQLGetAcct.append(" AND AD_Org_ID = "+AD_Org_ID);
								SQLGetAcct.append(" AND Value = 'GL_DefaultParameter'");
								SQLGetAcct.append(" AND Name = 'StockDifferentExpense'");
								
								Integer Account_ID = DB.getSQLValue(trxName, SQLGetAcct.toString());
								
								
								StringBuilder SQLGetProdCost = new StringBuilder();
								SQLGetProdCost.append("SELECT f_get_product_cost (?, ?, ?, ?); ");
								
								BigDecimal ProdCost = DB.getSQLValueBD(trxName, SQLGetProdCost.toString(), new Object[] {AD_Client_ID,AD_Org_ID,C_AcctSchema_ID,MapProduct.get(detail.material_id)});																	
								
								BigDecimal qtyFinal = detail.qty_before.subtract(detail.qty);
								BigDecimal amtFinal = qtyFinal.multiply(ProdCost);
								
								journalLine.setAccount_ID(Account_ID);
								journalLine.setAmtSourceDr(amtFinal);
								journalLine.setAmtSourceCr(Env.ZERO);
								journalLine.setAmtAcct(amtFinal, Env.ZERO);
								
								
							}else if(i == 1) {

								StringBuilder SQLGetAcct = new StringBuilder();
								SQLGetAcct.append("SELECT cvc.Account_ID  ");
								SQLGetAcct.append(" FROM M_Product_Acct mpa  ");
								SQLGetAcct.append(" INNER JOIN M_Product mp ON mp.M_Product_ID = mpa.M_Product_ID");
								SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = mpa.P_Asset_Acct ");
								SQLGetAcct.append(" WHERE mp.M_Product_ID = "+M_Product_ID);
								
								Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
							
								StringBuilder SQLGetProdCost = new StringBuilder();
								SQLGetProdCost.append("SELECT f_get_product_cost (?, ?, ?, ?); ");
								
								BigDecimal ProdCost = DB.getSQLValueBD(trxName, SQLGetProdCost.toString(), new Object[] {AD_Client_ID,AD_Org_ID,C_AcctSchema_ID,MapProduct.get(detail.material_id)});																	
	
								BigDecimal qtyFinal = detail.qty_before.subtract(detail.qty);
								BigDecimal amtFinal = qtyFinal.multiply(ProdCost);
								
								
								journalLine.setAccount_ID(Account_ID);
								journalLine.setAmtSourceDr(Env.ZERO);
								journalLine.setAmtSourceCr(amtFinal);
								journalLine.setAmtAcct(Env.ZERO,amtFinal);
								
							
							}	
						
							journalLine.saveEx();
							
						}else if(detail.qty.compareTo(detail.qty_before) > 0) {
							
							//line10 = DR -- line20 = CR
							if(i==0) {
							
								StringBuilder SQLGetAcct = new StringBuilder();
								SQLGetAcct.append("SELECT cvc.Account_ID  ");
								SQLGetAcct.append(" FROM M_Product_Acct mpa  ");
								SQLGetAcct.append(" INNER JOIN M_Product mp ON mp.M_Product_ID = mpa.M_Product_ID");
								SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = mpa.P_Asset_Acct ");
								SQLGetAcct.append(" WHERE mp.M_Product_ID = "+M_Product_ID);
								
								Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
								
								StringBuilder SQLGetProdCost = new StringBuilder();
								SQLGetProdCost.append("SELECT f_get_product_cost (?, ?, ?, ?); ");
								
								BigDecimal ProdCost = DB.getSQLValueBD(trxName, SQLGetProdCost.toString(), new Object[] {AD_Client_ID,AD_Org_ID,C_AcctSchema_ID,MapProduct.get(detail.material_id)});																	
								
								BigDecimal qtyFinal = detail.qty.subtract(detail.qty_before);
								BigDecimal amtFinal = qtyFinal.multiply(ProdCost);
								
								journalLine.setAccount_ID(Account_ID);
								journalLine.setAmtSourceDr(amtFinal);
								journalLine.setAmtSourceCr(Env.ZERO);
								journalLine.setAmtAcct(amtFinal, Env.ZERO);
								
								
							}else if(i==1) {
								
								StringBuilder SQLGetAcct = new StringBuilder();
								SQLGetAcct.append("SELECT Description::NUMERIC ");
								SQLGetAcct.append(" FROM AD_Param ");
								SQLGetAcct.append(" WHERE AD_Client_ID = "+AD_Client_ID);
								SQLGetAcct.append(" AND AD_Org_ID = "+AD_Org_ID);
								SQLGetAcct.append(" AND Value = 'GL_DefaultParameter'");
								SQLGetAcct.append(" AND Name = 'StockDifferentExpense'");
								
								Integer Account_ID = DB.getSQLValue(trxName, SQLGetAcct.toString());
							
							
								StringBuilder SQLGetProdCost = new StringBuilder();
								SQLGetProdCost.append("SELECT f_get_product_cost (?, ?, ?, ?); ");
								
								BigDecimal ProdCost = DB.getSQLValueBD(trxName, SQLGetProdCost.toString(), new Object[] {AD_Client_ID,AD_Org_ID,C_AcctSchema_ID,MapProduct.get(detail.material_id)});																	
	
								BigDecimal qtyFinal = detail.qty.subtract(detail.qty_before);
								BigDecimal amtFinal = qtyFinal.multiply(ProdCost);
								
								
								journalLine.setAccount_ID(Account_ID);
								journalLine.setAmtSourceDr(Env.ZERO);
								journalLine.setAmtSourceCr(amtFinal);
								journalLine.setAmtAcct(Env.ZERO,amtFinal);
								
							
							}	
							
							journalLine.saveEx();

							
						}
						
						
					}
									
				}
				
			journal.processIt(MJournal.ACTION_Complete);
			if(journal.save()) {
				
				result = journal.getGL_Journal_ID();
			}
			
			}
			
			
		} catch (Exception e) {
			
			result = 0;
			
		}
		
		
		return result;
		
	}
	
	public static HashMap<String, Integer> CheckProductData(int AD_Client_ID, int AD_Org_ID,API_Model_StockOpnameLines[] dataDetails, Properties ctx , String trxName) {

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		
		for (API_Model_StockOpnameLines line : dataDetails) {
			
			StringBuilder SQLCheckProduct = new StringBuilder();
			SQLCheckProduct.append("SELECT M_Product_ID ");
			SQLCheckProduct.append(" FROM M_Product ");
			SQLCheckProduct.append(" WHERE Value = '"+line.material_id+"'");

			
			Integer M_Product_ID = DB.getSQLValueEx(trxName, SQLCheckProduct.toString());
			
			if(M_Product_ID < 0) {
	
				StringBuilder SQLGetProdType = new StringBuilder();
				SQLGetProdType.append("SELECT description ");
				SQLGetProdType.append(" FROM ad_param ");
				SQLGetProdType.append(" WHERE ad_client_id = "+AD_Client_ID);
				SQLGetProdType.append(" AND ad_org_id =  "+ AD_Org_ID);
				SQLGetProdType.append(" AND value = 'MPR_DefaultParameter' ");
				SQLGetProdType.append(" AND name = 'ProductType' ");
				
				String ProdType = DB.getSQLValueStringEx(trxName, SQLGetProdType.toString());
				
				StringBuilder SQLGetProdCat = new StringBuilder();
				SQLGetProdCat.append("SELECT description::numeric ");
				SQLGetProdCat.append(" FROM ad_param ");
				SQLGetProdCat.append(" WHERE ad_client_id = "+AD_Client_ID);
				SQLGetProdCat.append(" AND ad_org_id =  "+ AD_Org_ID);
				SQLGetProdCat.append(" AND value = 'MPR_DefaultParameter' ");
				SQLGetProdCat.append(" AND name = 'TaxCategory' ");
				Integer ProdCat = DB.getSQLValueEx(trxName, SQLGetProdCat.toString());

				
				StringBuilder SQLGetTaxCat = new StringBuilder();
				SQLGetTaxCat.append("SELECT description::numeric ");
				SQLGetTaxCat.append(" FROM ad_param ");
				SQLGetTaxCat.append(" WHERE ad_client_id = "+AD_Client_ID);
				SQLGetTaxCat.append(" AND ad_org_id =  "+ AD_Org_ID);
				SQLGetTaxCat.append(" AND value = 'MPR_DefaultParameter' ");
				SQLGetTaxCat.append(" AND name = 'ProductCategory' ");
				Integer ProdTaxCat = DB.getSQLValueEx(trxName, SQLGetTaxCat.toString());

				MProduct product = new MProduct(ctx, 0, trxName);
				product.setAD_Org_ID(AD_Org_ID);	
				product.setIsActive(true);
				
				product.setValue(line.material_id);
				product.setName("Data no yet sync");
				product.setDescription("Data no yet sync");
				
				product.setIsStocked(true);
				product.setM_Product_Category_ID(ProdCat);
				product.setC_TaxCategory_ID(ProdTaxCat);
				product.setC_UOM_ID(100);
				
				product.setProductType(ProdType);
				product.setIsPurchased(true);
				product.setIsSold(true);
				product.setDocumentNote("");
				product.saveEx();
							
				StringBuilder SQLGetPriceList = new StringBuilder();
				SQLGetPriceList.append("SELECT description::numeric ");
				SQLGetPriceList.append(" FROM ad_param ");
				SQLGetPriceList.append(" WHERE ad_client_id = "+AD_Client_ID);
				SQLGetPriceList.append(" AND ad_org_id =  "+ AD_Org_ID);
				SQLGetPriceList.append(" AND value = 'MPR_DefaultParameter' ");
				SQLGetPriceList.append(" AND name = 'POPriceList' ");
				Integer prodPriceList = DB.getSQLValueEx(trxName, SQLGetPriceList.toString());
				
				MProductPrice pricingBuy = new MProductPrice(ctx, 0 , trxName);		
				pricingBuy.setM_PriceList_Version_ID(prodPriceList);
				pricingBuy.setM_Product_ID(product.getM_Product_ID());
				pricingBuy.setPriceList(line.price);
				pricingBuy.setPriceStd(line.price);
				pricingBuy.setPriceLimit(line.price);
				pricingBuy.saveEx();
				
				
				result.put(line.material_id, product.getM_Product_ID());
			}else if(M_Product_ID > 0) {
				
				result.put(line.material_id, M_Product_ID);
			}
			
		}

		return result;
	}

}
