package org.epi.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.compiere.model.MCharge;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.ws.model.API_Model_GoodIssue;
import org.epi.ws.model.API_Model_GoodIssueLines;

public class WSExecuteGoodIssue {
	
	
	public static Integer CreateGoodIssue(int AD_Client_ID, int AD_Org_ID, API_Model_GoodIssue dataHeader,API_Model_GoodIssueLines[] dataDetails, Properties ctx , String trxName) {

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


			Timestamp DateAcct = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.issued_date);
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
			journal.set_ValueOfColumn("ReferenceNo", dataHeader.gi_no);
			

			
			
			if(journal.save()) {
				
				int lineNo = 0;
				
				for (API_Model_GoodIssueLines detail : dataDetails) {
					
			
					for(int i = 0 ; i < 2 ; i++) {
						
						StringBuilder SQLCheckAsset = new StringBuilder();
						SQLCheckAsset.append("SELECT A_Asset_ID ");
						SQLCheckAsset.append(" FROM A_Asset ");
						SQLCheckAsset.append(" WHERE Value = '"+dataHeader.asset_id+"'");

						
						Integer Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
						
						if(Asset_ID <= 0)
							Asset_ID = 0;
						
//						Integer M_Product_ID = MapProduct.get(detail.material_id);
						
						lineNo = lineNo+10;
						MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
						
						journalLine.setLine(lineNo);
						journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
						journalLine.setAD_Org_ID(AD_Org_ID);
//						journalLine.setM_Product_ID(M_Product_ID);
						journalLine.setQty(detail.qty_issued);
						journalLine.setC_Project_ID(dataHeader.project_id);
						journalLine.setA_Asset_ID(Asset_ID);
						journalLine.setDescription(dataHeader.description);	
						journalLine.setC_Currency_ID(journal.getC_Currency_ID());
						journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());	
						journalLine.setDateAcct(journal.getDateAcct());
						journalLine.setA_Asset_ID(dataHeader.asset_id);
										
						
						if(i == 0) {
							
							MCharge charge = new MCharge(ctx, detail.charge_id, trxName);
							
							StringBuilder SQLGetAcct = new StringBuilder();
							SQLGetAcct.append("SELECT cvc.Account_ID  ");
							SQLGetAcct.append(" FROM C_Charge_Acct cca  ");
							SQLGetAcct.append(" INNER JOIN C_Charge cc ON cc.C_Charge_ID = cca.C_Charge_ID");
							SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = cca.Ch_Expense_Acct");
							SQLGetAcct.append(" WHERE cc.C_Charge_ID = "+charge.getC_Charge_ID());
							
							Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
							
							StringBuilder SQLGetCombination = new StringBuilder();
							SQLGetCombination.append("SELECT C_ValidCombination_ID  ");
							SQLGetCombination.append(" FROM C_ValidCombination ");
							SQLGetCombination.append(" WHERE Account_ID = "+Account_ID);
							SQLGetCombination.append(" AND AD_Org_ID = "+AD_Org_ID);

							
							Integer C_ValidCombination_ID = DB.getSQLValueEx(trxName, SQLGetCombination.toString());
								
							BigDecimal ProdCost = detail.material_po_average;
							BigDecimal DR = detail.qty_issued.multiply(ProdCost);
							
							journalLine.setAccount_ID(Account_ID);
							journalLine.setC_ValidCombination_ID(C_ValidCombination_ID);
							
							journalLine.setAmtSourceDr(DR);
							journalLine.setAmtSourceCr(Env.ZERO);
							journalLine.setAmtAcct(DR, Env.ZERO);
							
						}else if(i == 1) {
								
//							StringBuilder SQLGetAcct = new StringBuilder();
//							SQLGetAcct.append("SELECT cvc.Account_ID  ");
//							SQLGetAcct.append(" FROM M_Product_Acct mpa  ");
//							SQLGetAcct.append(" INNER JOIN M_Product mp ON mp.M_Product_ID = mpa.M_Product_ID");
//							SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = mpa.P_Asset_Acct ");
//							SQLGetAcct.append(" WHERE mp.M_Product_ID = "+M_Product_ID);
							
							StringBuilder SQLGetAcct = new StringBuilder();
							SQLGetAcct.append("SELECT Description::numeric  ");
							SQLGetAcct.append(" FROM AD_Param  ");
							SQLGetAcct.append(" WHERE AD_Client_ID = "+Env.getAD_Client_ID(ctx));
							SQLGetAcct.append(" AND AD_Org_ID = "+journal.getAD_Org_ID());
							SQLGetAcct.append(" AND Value = '"+"AccountGlCredit'");

							
							Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
							
							StringBuilder SQLGetCombination = new StringBuilder();
							SQLGetCombination.append("SELECT C_ValidCombination_ID  ");
							SQLGetCombination.append(" FROM C_ValidCombination ");
							SQLGetCombination.append(" WHERE Account_ID = "+Account_ID);
							SQLGetCombination.append(" AND AD_Org_ID = "+AD_Org_ID);
							
							Integer C_ValidCombination_ID = DB.getSQLValueEx(trxName, SQLGetCombination.toString());
								
							journalLine.setC_ValidCombination_ID(C_ValidCombination_ID);

							BigDecimal ProdCost = detail.material_po_average;
							
							BigDecimal CR = detail.qty_issued.multiply(ProdCost);
							journalLine.setAccount_ID(Account_ID);
							journalLine.setAmtSourceDr(Env.ZERO);
							journalLine.setAmtSourceCr(CR);
							journalLine.setAmtAcct(Env.ZERO, CR);
						}
						
						journalLine.saveEx();
						
					}
					

					
				}
				
				
			}
			
			journal.processIt(MJournal.ACTION_Complete);
			if(journal.save()) {
				
				result = journal.getGL_Journal_ID();
			}
			
		} catch (Exception e) {

		}
		
		
		return result;
		
		
	}
	
	
//	public static HashMap<String, Integer> CheckProductData(int AD_Client_ID, int AD_Org_ID, API_Model_GoodIssue dataHeader,API_Model_GoodIssueLines[] dataDetails, Properties ctx , String trxName) {
//
//		HashMap<String, Integer> result = new HashMap<String, Integer>();
//		
//		
//		for (API_Model_GoodIssueLines line : dataDetails) {
//			StringBuilder SQLCheckProduct = new StringBuilder();
//			SQLCheckProduct.append("SELECT M_Product_ID ");
//			SQLCheckProduct.append(" FROM M_Product ");
//			SQLCheckProduct.append(" WHERE Value = '"+line.material_id+"'");
//
//			
//			Integer M_Product_ID = DB.getSQLValueEx(trxName, SQLCheckProduct.toString());
//			
//			if(M_Product_ID < 0) {
//	
//				StringBuilder SQLGetProdType = new StringBuilder();
//				SQLGetProdType.append("SELECT description ");
//				SQLGetProdType.append(" FROM ad_param ");
//				SQLGetProdType.append(" WHERE ad_client_id = "+AD_Client_ID);
//				SQLGetProdType.append(" AND ad_org_id =  "+ AD_Org_ID);
//				SQLGetProdType.append(" AND value = 'MPR_DefaultParameter' ");
//				SQLGetProdType.append(" AND name = 'ProductType' ");
//				
//				String ProdType = DB.getSQLValueStringEx(trxName, SQLGetProdType.toString());
//				
//				StringBuilder SQLGetProdCat = new StringBuilder();
//				SQLGetProdCat.append("SELECT description::numeric ");
//				SQLGetProdCat.append(" FROM ad_param ");
//				SQLGetProdCat.append(" WHERE ad_client_id = "+AD_Client_ID);
//				SQLGetProdCat.append(" AND ad_org_id =  "+ AD_Org_ID);
//				SQLGetProdCat.append(" AND value = 'MPR_DefaultParameter' ");
//				SQLGetProdCat.append(" AND name = 'TaxCategory' ");
//				Integer ProdCat = DB.getSQLValueEx(trxName, SQLGetProdCat.toString());
//
//				
//				StringBuilder SQLGetTaxCat = new StringBuilder();
//				SQLGetTaxCat.append("SELECT description::numeric ");
//				SQLGetTaxCat.append(" FROM ad_param ");
//				SQLGetTaxCat.append(" WHERE ad_client_id = "+AD_Client_ID);
//				SQLGetTaxCat.append(" AND ad_org_id =  "+ AD_Org_ID);
//				SQLGetTaxCat.append(" AND value = 'MPR_DefaultParameter' ");
//				SQLGetTaxCat.append(" AND name = 'ProductCategory' ");
//				Integer ProdTaxCat = DB.getSQLValueEx(trxName, SQLGetTaxCat.toString());
//
//				MProduct product = new MProduct(ctx, 0, trxName);
//				product.setAD_Org_ID(AD_Org_ID);	
//				product.setIsActive(true);
//				
//				product.setValue(line.material_id);
//				product.setName("Data no yet sync");
//				product.setDescription("Data no yet sync");
//				
//				product.setIsStocked(true);
//				product.setM_Product_Category_ID(ProdCat);
//				product.setC_TaxCategory_ID(ProdTaxCat);
//				product.setC_UOM_ID(100);
//				
//				product.setProductType(ProdType);
//				product.setIsPurchased(true);
//				product.setIsSold(true);
//				product.setDocumentNote("");
//				product.saveEx();
//							
//				StringBuilder SQLGetPriceList = new StringBuilder();
//				SQLGetPriceList.append("SELECT description::numeric ");
//				SQLGetPriceList.append(" FROM ad_param ");
//				SQLGetPriceList.append(" WHERE ad_client_id = "+AD_Client_ID);
//				SQLGetPriceList.append(" AND ad_org_id =  "+ AD_Org_ID);
//				SQLGetPriceList.append(" AND value = 'MPR_DefaultParameter' ");
//				SQLGetPriceList.append(" AND name = 'POPriceList' ");
//				Integer prodPriceList = DB.getSQLValueEx(trxName, SQLGetPriceList.toString());
//				
//				MProductPrice pricingBuy = new MProductPrice(ctx, 0 , trxName);		
//				pricingBuy.setM_PriceList_Version_ID(prodPriceList);
//				pricingBuy.setM_Product_ID(product.getM_Product_ID());
//				pricingBuy.setPriceList(line.material_po_average);
//				pricingBuy.setPriceStd(line.material_po_average);
//				pricingBuy.setPriceLimit(line.material_po_average);
//				pricingBuy.saveEx();
//				
//				
//				result.put(line.material_id, product.getM_Product_ID());
//			}else if(M_Product_ID > 0) {
//				
//				result.put(line.material_id, M_Product_ID);
//			}
//			
//		}
//
//		return result;
//	}
	
//	public static Integer CheckAssetData(int AD_Client_ID, int AD_Org_ID, API_Model_GoodIssue dataHeader, Properties ctx , String trxName) {
//
//		Integer result = 0;
//		
//		StringBuilder SQLCheckAsset = new StringBuilder();
//		
//		SQLCheckAsset.append("SELECT A_Asset_ID ");
//		SQLCheckAsset.append(" FROM A_Asset ");
//		SQLCheckAsset.append(" WHERE Value = '"+dataHeader.asset_id+"'");
//
//		
//		Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
//		
//		if(A_Asset_ID <= 0) {
//			
//			MAsset asset = new MAsset(ctx, 0, trxName);
//			asset.setValue(dataHeader.asset_id);
//			
//			asset.setInventoryNo("Data not yet sync");
//			
//			asset.setName("Data not yet sync");
//			asset.setInventoryNo("Data not yet sync");
//			asset.set_ValueOfColumn("PoliceNo", "Data not yet sync");
//			asset.set_ValueOfColumn("PoliceNo_Before", "Data not yet sync");
//			asset.set_ValueOfColumn("HullNo", "Data not yet sync");
//			asset.set_ValueOfColumn("AssetType", "");
//			asset.set_ValueOfColumn("AssetClass", "");
//			asset.set_ValueOfColumn("EngineNo", "Data not yet sync");
//			asset.set_ValueOfColumn("BodyNo", "Data not yet sync");
//			asset.setDescription("");
////			asset.setC_BPartner_ID(null);
////			asset.setManufacturedYear(null);
////			asset.setC_Project_ID(null);
//
//			asset.saveEx();
//			
//			result = asset.getA_Asset_ID();
//			
//		}else if(A_Asset_ID > 0) {
//			result = A_Asset_ID;
//		}
//		
//		
//		
//
//		return result;
//	}
}
