package org.epi.process;

import java.util.Properties;

import org.compiere.model.MAsset;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_BAST;

public class WSExecuteBAST {
	
	
	public static Integer CreateBAST(int AD_Client_ID, int AD_Org_ID, API_Model_BAST dataHeader,Properties ctx , String trxName,int A_Asset_ID , int C_BPartner_ID) {
		
		Integer result = 0;
		
		try {
					
//			StringBuilder UpdateAsset = new StringBuilder();
//			UpdateAsset.append("UPDATE A_Asset ");
//			UpdateAsset.append(" SET C_BPartner_ID = ");
//			UpdateAsset.append(" (SELECT C_BPartner_ID FROM C_BPartner WHERE value ='"+dataHeader.customer_id+"'),");
//			UpdateAsset.append(" AssetStatus = '"+dataHeader.status+"'");
//			DB.executeUpdateEx(UpdateAsset.toString(), trxName);
			
			MAsset asset = new MAsset(ctx, A_Asset_ID, trxName);
			asset.setC_BPartner_ID(C_BPartner_ID);
			asset.set_ValueOfColumn("AssetStatus", dataHeader.status);
			
			if(asset.save()) {
				
				result = asset.getA_Asset_ID();
				
			}
			
		} catch (Exception e) {
			
			result = 0;
		
		}
		
		return result;
	}
	
	public static Integer CheckAssetData(int AD_Client_ID, int AD_Org_ID, API_Model_BAST dataHeader, Properties ctx , String trxName) {

		Integer result = 0;
		
		StringBuilder SQLCheckAsset = new StringBuilder();
		
		SQLCheckAsset.append("SELECT A_Asset_ID ");
		SQLCheckAsset.append(" FROM A_Asset ");
		SQLCheckAsset.append(" WHERE InventoryNo = '"+dataHeader.asset_id+"'");

		
		Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
		
		if(A_Asset_ID <= 0) {
			
			MAsset asset = new MAsset(ctx, 0, trxName);
			asset.setValue(dataHeader.asset_id);			
			asset.setInventoryNo("Data not yet sync");
			
			asset.setAD_Org_ID(AD_Org_ID);
			asset.setName("Data not yet sync");
			asset.setInventoryNo("Data not yet sync");
			asset.set_ValueOfColumn("PoliceNo", "Data not yet sync");
			asset.set_ValueOfColumn("PoliceNo_Before", "Data not yet sync");
			asset.set_ValueOfColumn("HullNo", "Data not yet sync");
			asset.set_ValueOfColumn("AssetType", "");
			asset.set_ValueOfColumn("AssetClass", "");
			asset.set_ValueOfColumn("EngineNo", "Data not yet sync");
			asset.set_ValueOfColumn("BodyNo", "Data not yet sync");
			asset.setDescription("");
//			asset.setC_BPartner_ID(null);
//			asset.setManufacturedYear(null);
//			asset.setC_Project_ID(null);

			asset.saveEx();

			
			result = asset.getA_Asset_ID();
			
		}else if(A_Asset_ID > 0) {
			result = A_Asset_ID;
		}
		
		return result;
	}
	
	public static Integer CheckCustomer(int AD_Client_ID, int AD_Org_ID, API_Model_BAST dataHeader, Properties ctx , String trxName) {

		Integer result = 0; 
		
		StringBuilder SQLCheckVendor = new StringBuilder();
		
		SQLCheckVendor.append("SELECT C_BPartner_ID ");
		SQLCheckVendor.append(" FROM C_BPartner ");
		SQLCheckVendor.append(" WHERE Value = '"+dataHeader.customer_id+"'");

		Integer C_BPartner_ID = DB.getSQLValueEx(trxName, SQLCheckVendor.toString());

		
		if(C_BPartner_ID > 0) {
			result = C_BPartner_ID;
		}else if(C_BPartner_ID <= 0 || C_BPartner_ID == null) {
			
			
			MBPartner newVendor = new MBPartner(ctx, C_BPartner_ID, trxName);
			newVendor.setAD_Org_ID(AD_Org_ID);	
			newVendor.setIsCustomer(true);
			newVendor.setIsActive(true);
			
			newVendor.setValue(dataHeader.customer_id);
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
