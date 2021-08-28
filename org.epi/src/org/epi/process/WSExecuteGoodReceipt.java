package org.epi.process;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_GoodReceipt;
import org.epi.ws.model.API_Model_GoodReceiptLines;

public class WSExecuteGoodReceipt {
	
	public static Integer CreateGoodReceipt(int AD_Client_ID, int AD_Org_ID, API_Model_GoodReceipt dataHeader,API_Model_GoodReceiptLines[] dataDetails, Properties ctx , String trxName,HashMap<String, Integer> prod) {

		Integer result = 0;
		
		try {
			
			MInOut GReceipt = new MInOut(ctx, 0, trxName);
			
			StringBuilder SQLGetDocType = new StringBuilder();
			SQLGetDocType.append("SELECT Description::NUMERIC ");
			SQLGetDocType.append(" FROM AD_Param ");
			SQLGetDocType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocType.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocType.append(" AND Value = 'MR_DefaultParameter'");
			SQLGetDocType.append(" AND Name = 'DocumentType'");
			Integer C_DocType_ID = DB.getSQLValue(trxName, SQLGetDocType.toString());	
			
			
			GReceipt.setAD_Org_ID(AD_Org_ID);
			GReceipt.setC_DocType_ID(C_DocType_ID);
			GReceipt.setDocumentNo(dataHeader.gr_no);
			
			StringBuilder SQLGetPO = new StringBuilder();
			SQLGetPO.append("SELECT C_Order_ID ");
			SQLGetPO.append(" FROM C_Order ");
			SQLGetPO.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPO.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPO.append(" AND DocumentNo = '"+dataHeader.po_no+"'");
			Integer C_Order_ID = DB.getSQLValue(trxName, SQLGetPO.toString());	
			
			MOrder ord = new MOrder(ctx, C_Order_ID, trxName);
			
			GReceipt.setC_Order_ID(ord.getC_Order_ID());
			GReceipt.setDescription(dataHeader.remark);
			
			
			Timestamp DateAcct = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.gr_date);
			
			GReceipt.setDateAcct(DateAcct);
			GReceipt.setMovementDate(DateAcct);
			
			StringBuilder SQLCheckAsset = new StringBuilder();
			
			SQLCheckAsset.append("SELECT A_Asset_ID ");
			SQLCheckAsset.append(" FROM A_Asset ");
			SQLCheckAsset.append(" WHERE Value = '"+dataHeader.asset_id+"'");

			
			Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
			
			GReceipt.set_CustomColumn("A_Asset_ID", A_Asset_ID);
			GReceipt.setC_BPartner_ID(ord.getC_BPartner_ID());
			GReceipt.setC_BPartner_Location_ID(ord.getC_BPartner_Location_ID());

			StringBuilder SQLGetWH = new StringBuilder();
			SQLGetWH.append("SELECT M_Warehouse_ID ");
			SQLGetWH.append(" FROM M_Warehouse ");
			SQLGetWH.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetWH.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetWH.append(" AND Value = '"+dataHeader.location_id+"'");
			Integer M_Warehouse_ID = DB.getSQLValue(trxName, SQLGetWH.toString());	
			GReceipt.setM_Warehouse_ID(M_Warehouse_ID);
			GReceipt.setPriorityRule(ord.getPriorityRule());
			GReceipt.setC_Project_ID(dataHeader.project_id);
			GReceipt.setMovementType(MInOut.MOVEMENTTYPE_VendorReceipts);
			
			StringBuilder SQLGetDocStatus = new StringBuilder();
			SQLGetDocStatus.append("SELECT Description ");
			SQLGetDocStatus.append(" FROM AD_Param ");
			SQLGetDocStatus.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocStatus.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocStatus.append(" AND Value = 'MR_DefaultParameter'");
			SQLGetDocStatus.append(" AND Name = 'DocStatus'");
			String DocStatus = DB.getSQLValueStringEx(trxName, SQLGetDocStatus.toString());	
			GReceipt.setIsSOTrx(false);
			int noLine = 0;

			if(GReceipt.save()) {
								
				for(API_Model_GoodReceiptLines detail : dataDetails) {
					
					noLine = noLine+1;
					
					MInOutLine MRLine = new MInOutLine(ctx, 0, trxName);
					MRLine.setAD_Org_ID(AD_Org_ID);
					MRLine.setLine(noLine);
					MRLine.setC_OrderLine_ID(detail.po_line);
					MRLine.setM_InOut_ID(GReceipt.getM_InOut_ID());
					
					if(detail.product_id != null && !detail.product_id.isEmpty() && detail.product_id != "") {
						
						StringBuilder SQLGetProd = new StringBuilder();
						SQLGetProd.append("SELECT M_Product_ID ");
						SQLGetProd.append(" FROM M_Product ");
						SQLGetProd.append(" WHERE AD_Client_ID = "+AD_Client_ID);
						SQLGetProd.append(" AND AD_Org_ID = "+AD_Org_ID);
						SQLGetProd.append(" AND Value = '"+detail.product_id+"'");
						Integer M_Prod_ID = prod.get(detail.product_id);
						MRLine.setM_Product_ID(M_Prod_ID);
						
						
						StringBuilder SQLGetLocator = new StringBuilder();
						SQLGetLocator.append("SELECT M_Locator_ID ");
						SQLGetLocator.append(" FROM M_Locator ");
						SQLGetLocator.append(" WHERE AD_Client_ID = "+AD_Client_ID);
						SQLGetLocator.append(" AND AD_Org_ID = "+AD_Org_ID);
						SQLGetLocator.append(" AND Value = '"+dataHeader.location_id+"'");
						Integer M_Locator_ID = DB.getSQLValue(trxName, SQLGetLocator.toString());	
						MRLine.setM_Product_ID(M_Prod_ID);
						MRLine.setM_Locator_ID(M_Locator_ID);

						MProduct product = new MProduct(ctx, M_Prod_ID, trxName);					
						MRLine.setC_UOM_ID(product.getC_UOM_ID());
						
					}else if(detail.charge_id != null) {
						
						StringBuilder SQLGetCharge = new StringBuilder();
						SQLGetCharge.append("SELECT C_Charge_ID ");
						SQLGetCharge.append(" FROM C_Charge ");
						SQLGetCharge.append(" WHERE AD_Client_ID = "+AD_Client_ID);
						SQLGetCharge.append(" AND AD_Org_ID = "+AD_Org_ID);
						SQLGetCharge.append(" AND C_Charge_ID = '"+detail.charge_id+"'");
						Integer C_Charge_ID = DB.getSQLValue(trxName, SQLGetCharge.toString());	
						MRLine.setC_Charge_ID(C_Charge_ID);

					}
					MRLine.setDescription(detail.description);
					MRLine.setQty(detail.qty_receipt);
					MRLine.setQtyEntered(detail.qty_receipt);

					StringBuilder SQLGetOrderLine = new StringBuilder();
					SQLGetOrderLine.append("SELECT C_OrderLine_ID ");
					SQLGetOrderLine.append(" FROM C_OrderLine ");
					SQLGetOrderLine.append(" WHERE AD_Client_ID = "+AD_Client_ID);
					SQLGetOrderLine.append(" AND AD_Org_ID = "+AD_Org_ID);
					SQLGetOrderLine.append(" AND C_Order_ID = "+ord.getC_Order_ID());
					SQLGetOrderLine.append(" AND Line = "+ detail.po_line);

					Integer C_OrderLine_ID = DB.getSQLValue(trxName, SQLGetOrderLine.toString());	
					
					
					MRLine.setC_OrderLine_ID(C_OrderLine_ID);					
					MRLine.saveEx();
					
				}
				
			
			}
			
			if(noLine > 0) {
				
				if(GReceipt != null) {
					
					GReceipt.setDocAction(DocStatus);
					GReceipt.processIt(MInOut.DOCACTION_Complete);
					
					if(GReceipt.save()) {
						
						result = GReceipt.getM_InOut_ID();
					}
				}
				
			}

			
		} catch (Exception e) {

			result = 0;
		}
	
				
		return result;
		
	}
	
	
	public static HashMap<String, Integer> CheckProductData(int AD_Client_ID, int AD_Org_ID,API_Model_GoodReceiptLines[] dataDetails, Properties ctx , String trxName) {

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		
		for (API_Model_GoodReceiptLines line : dataDetails) {
			
			if(line.charge_id == null && line.product_id != null) {
			
				StringBuilder SQLCheckProduct = new StringBuilder();
				SQLCheckProduct.append("SELECT M_Product_ID ");
				SQLCheckProduct.append(" FROM M_Product ");
				SQLCheckProduct.append(" WHERE Value = '"+line.product_id+"'");
	
				
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
					
					product.setValue(line.product_id);
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
					
					
					result.put(line.product_id, product.getM_Product_ID());
				}else if(M_Product_ID > 0) {
					
					result.put(line.product_id, M_Product_ID);
				}
			}	
		}

		return result;
	}
}
