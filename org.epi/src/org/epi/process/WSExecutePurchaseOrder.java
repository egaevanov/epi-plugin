package org.epi.process;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_POHeader;
import org.epi.ws.model.API_Model_POLines;

public class WSExecutePurchaseOrder {
	
	public static Integer CreatePurchaseOrder(int AD_Client_ID, int AD_Org_ID, API_Model_POHeader dataHeader, API_Model_POLines[] dataDetails,Properties ctx , String trxName,int C_BPartner_ID, int M_Warehouse_ID,HashMap<String, Integer>product) {
		
		Integer rs = 0;
		
		try {		
						
			MOrder po = new MOrder(ctx, 0, trxName);
			
			po.setAD_Org_ID(AD_Org_ID);
			
			Timestamp DateOrdered = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.po_date);
	
			po.setDateOrdered(DateOrdered);
			po.setIsSOTrx(false);
			
			StringBuilder SQLGetDocumentType = new StringBuilder();
			SQLGetDocumentType.append("SELECT Description::NUMERIC ");
			SQLGetDocumentType.append(" FROM AD_Param ");
			SQLGetDocumentType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocumentType.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocumentType.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetDocumentType.append(" AND Name = 'TargetDocumentType'");
			Integer C_DocType_ID = DB.getSQLValue(trxName, SQLGetDocumentType.toString());

			po.setC_DocTypeTarget_ID(C_DocType_ID);
			po.setDocumentNo(dataHeader.po_no);
			po.setPOReference(dataHeader.pr_no);
			po.set_ValueNoCheck("OrderType", dataHeader.po_type);
			po.set_ValueNoCheck("UnitType", dataHeader.unit_type);
			po.setDescription(dataHeader.remaks);
			po.setDateOrdered(DateOrdered);
			po.setDatePromised(DateOrdered);
			
			
			StringBuilder SQLGetBPLoc = new StringBuilder();
			SQLGetBPLoc.append("SELECT C_BPartner_Location_ID  ");
			SQLGetBPLoc.append(" FROM C_BPartner_Location ");
			SQLGetBPLoc.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetBPLoc.append(" AND name = '"+dataHeader.vendor_id+"'");
			
			Integer C_BPartner_Location_ID =DB.getSQLValue(trxName, SQLGetBPLoc.toString());
			po.setC_BPartner_ID(C_BPartner_ID);
			po.setC_BPartner_Location_ID(C_BPartner_Location_ID);
			
			po.setM_Warehouse_ID(M_Warehouse_ID);
			
			StringBuilder SQLGetPriceList = new StringBuilder();
			SQLGetPriceList.append("SELECT Description::NUMERIC ");
			SQLGetPriceList.append(" FROM AD_Param ");
			SQLGetPriceList.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPriceList.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPriceList.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetPriceList.append(" AND Name = 'PriceList'");
			
			Integer M_Pricelist_ID = DB.getSQLValue(trxName, SQLGetPriceList.toString());
		

			po.setM_PriceList_ID(M_Pricelist_ID);
			
			
			StringBuilder SQLCheckCurr = new StringBuilder();
			
			SQLCheckCurr.append("SELECT C_Currency_ID ");
			SQLCheckCurr.append(" FROM C_Currency ");
			SQLCheckCurr.append(" WHERE ISO_Code = '"+dataHeader.currency+"'");

			Integer C_Currency_ID = DB.getSQLValueEx(trxName, SQLCheckCurr.toString());

			
			po.setC_Currency_ID(C_Currency_ID);
			
			StringBuilder SQLGetComAgent = new StringBuilder();
			SQLGetComAgent.append("SELECT Description::NUMERIC ");
			SQLGetComAgent.append(" FROM AD_Param ");
			SQLGetComAgent.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetComAgent.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetComAgent.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetComAgent.append(" AND Name = 'CompanyAgent'");
			
			Integer SalesRep_ID = DB.getSQLValue(trxName, SQLGetComAgent.toString());
			
			po.setSalesRep_ID(SalesRep_ID);
			
			po.setIsSOTrx(false);
			po.setDatePromised(DateOrdered);
			
			StringBuilder SQLGetPayRule = new StringBuilder();
			SQLGetPayRule.append("SELECT Description ");
			SQLGetPayRule.append(" FROM AD_Param ");
			SQLGetPayRule.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetPayRule.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetPayRule.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetPayRule.append(" AND Name = 'PaymentRule'");
			
			String payRule = DB.getSQLValueString(trxName, SQLGetPayRule.toString());
			
			po.setSalesRep_ID(SalesRep_ID);
			po.setPaymentRule(payRule);
			po.setC_PaymentTerm_ID(dataHeader.termin_id);
			po.setC_Project_ID(dataHeader.project_id);
			
			StringBuilder SQLGetDocStatus = new StringBuilder();
			SQLGetDocStatus.append("SELECT Description ");
			SQLGetDocStatus.append(" FROM AD_Param ");
			SQLGetDocStatus.append(" WHERE AD_Client_ID = "+AD_Client_ID);
			SQLGetDocStatus.append(" AND AD_Org_ID = "+AD_Org_ID);
			SQLGetDocStatus.append(" AND Value = 'PO_DefaultParameter'");
			SQLGetDocStatus.append(" AND Name = 'DocStatus'");
			
			String docStatus = DB.getSQLValueString(trxName, SQLGetDocStatus.toString());
//			po.setDocStatus("DR");
			po.setDocAction(docStatus);
			
			int noLine = 0;

			if(po.save()) {
				
				for (API_Model_POLines DataDetail : dataDetails) {
					noLine = noLine+1;
					MOrderLine line = new MOrderLine(ctx, 0, trxName);
					
					line.setC_Order_ID(po.getC_Order_ID());
					line.setM_Warehouse_ID(po.getM_Warehouse_ID());
					line.setC_BPartner_ID(po.getC_BPartner_ID());
					line.setC_BPartner_Location_ID(po.getC_BPartner_Location_ID());
					line.setLine(noLine);
					if(DataDetail.charge_id != null) {
						line.setC_Charge_ID(DataDetail.charge_id);
					}else if(DataDetail.material_id != null) {
						
					
						Integer M_Product_ID = product.get(DataDetail.material_id);						
						MProduct prod = new MProduct(ctx, M_Product_ID, trxName);
						line.setC_UOM_ID(prod.getC_UOM_ID());

						
					}
					line.setQtyOrdered(DataDetail.qty_order);
					line.setPriceList(DataDetail.price);
					line.setPriceEntered(DataDetail.price);
					line.setPriceActual(DataDetail.price);
					line.setLineNetAmt(DataDetail.qty_order.multiply(DataDetail.price));
					
					if(dataHeader.ppn.toUpperCase().equals("YES")) {
						line.setC_Tax_ID(1000013);
					}else if(dataHeader.ppn.toUpperCase().equals("NO")) {
						line.setC_Tax_ID(1000014);
					}
					
					line.saveEx();
						
				}
							
			}
			
			
			
			if(noLine > 0) {
				
				po.processIt(MOrder.ACTION_Complete);
				
				if(po.save()) {
					
					rs = po.getC_Order_ID();
					
				}
				
			}
		
		} catch (Exception e) {

			rs = 0;
		
		}
		
		return rs;
		
		
	}
	
	public static HashMap<String, Integer> CheckProductData(int AD_Client_ID, int AD_Org_ID, API_Model_POHeader dataHeader,API_Model_POLines[] dataDetails, Properties ctx , String trxName) {

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		StringBuilder SQLCheckProduct = new StringBuilder();
		
		for (API_Model_POLines line : dataDetails) {
			
			
			if(line.charge_id == null && line.material_id != null) {
			
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
		}

		return result;
	}
	
	public static Integer CheckWarehouse(int AD_Client_ID, int AD_Org_ID, API_Model_POHeader dataHeader, Properties ctx , String trxName) {

		Integer result = 0; 
		
		StringBuilder SQLCheckWarehouse = new StringBuilder();
		
		SQLCheckWarehouse.append("SELECT M_Warehouse_ID ");
		SQLCheckWarehouse.append(" FROM M_Warehouse ");
		SQLCheckWarehouse.append(" WHERE Value = '"+dataHeader.location_id+"'");

		Integer M_Warehouse_ID = DB.getSQLValueEx(trxName, SQLCheckWarehouse.toString());

		
		if(M_Warehouse_ID > 0) {
			result = M_Warehouse_ID;
		}else if(M_Warehouse_ID <= 0 || M_Warehouse_ID == null) {
			
			
			MWarehouse newWH = new MWarehouse(ctx, M_Warehouse_ID, trxName);
			newWH.setAD_Org_ID(AD_Org_ID);	
			newWH.setValue(dataHeader.location_id);
			newWH.setName("Data not yes sync");
			newWH.setDescription("Data not yes sync");
			newWH.setIsDisallowNegativeInv(false);
			newWH.setSeparator("*");
			
			if(newWH.save()) {
				
				MLocator locator = new MLocator(ctx, 0, trxName);
				locator.setAD_Org_ID(AD_Org_ID);
				locator.setIsActive(true);
				locator.setM_Warehouse_ID(newWH.getM_Warehouse_ID());
				locator.setValue(dataHeader.location_id);
				locator.setIsDefault(true);
				locator.setPriorityNo(10);
				
				locator.setX("1");
				locator.setY("1");
				locator.setZ("1");
				locator.saveEx();
				
			}
			
			result = newWH.getM_Warehouse_ID();
		}

		return result;
	}
	
	
	public static Integer CheckVendor(int AD_Client_ID, int AD_Org_ID, API_Model_POHeader dataHeader, Properties ctx , String trxName) {

		Integer result = 0; 
		
		StringBuilder SQLCheckVendor = new StringBuilder();
		
		SQLCheckVendor.append("SELECT C_BPartner_ID ");
		SQLCheckVendor.append(" FROM C_BPartner ");
		SQLCheckVendor.append(" WHERE Value = '"+dataHeader.vendor_id+"'");

		Integer C_BPartner_ID = DB.getSQLValueEx(trxName, SQLCheckVendor.toString());

		
		if(C_BPartner_ID > 0) {
			result = C_BPartner_ID;
		}else if(C_BPartner_ID <= 0 || C_BPartner_ID == null) {
			
			
			MBPartner newVendor = new MBPartner(ctx, C_BPartner_ID, trxName);
			newVendor.setAD_Org_ID(AD_Org_ID);	
			newVendor.setIsVendor(true);
			newVendor.setIsActive(true);
			
			newVendor.setValue(dataHeader.vendor_id);
			newVendor.setName("Data not yet sync");
			newVendor.setName2("Data not yet sync");
			
			if(newVendor.save()){
				
				MLocation location = new MLocation(ctx, 0, trxName);
				location.setAD_Org_ID(AD_Org_ID);
				location.setIsActive(true);
				location.setAddress1("Data not yet sync");
				location.setC_Country_ID(209);
				location.setPostal("Data not yet sync");
				location.saveEx();
				
				
				if(location!= null){
					MBPartnerLocation BpLoc = new MBPartnerLocation(ctx, 0, trxName);
					BpLoc.setIsActive(true);
					BpLoc.setC_BPartner_ID(newVendor.getC_BPartner_ID());
					BpLoc.setC_Location_ID(location.getC_Location_ID());
					BpLoc.setPhone("Data not yet sync");
					BpLoc.setPhone2("Data not yet sync");
					BpLoc.setIsShipTo(true);
					BpLoc.setIsPayFrom(true);
					BpLoc.setIsBillTo(true);
					BpLoc.setIsRemitTo(true);
					BpLoc.saveEx();			
				
				}
			}
			
			result = newVendor.getC_BPartner_ID();
		}

		return result;
	}
	
	
}
