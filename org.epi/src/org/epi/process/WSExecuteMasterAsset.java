package org.epi.process;

import java.util.Properties;

import org.compiere.model.MAsset;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_MasterAsset;

public class WSExecuteMasterAsset {
	
	
	public static Integer CreateAssetData(int AD_Client_ID, int AD_Org_ID, API_Model_MasterAsset dataHeader, Properties ctx , String trxName) {

		Integer result = 0;
		
		StringBuilder SQLCheckAsset = new StringBuilder();
		
		SQLCheckAsset.append("SELECT A_Asset_ID ");
		SQLCheckAsset.append(" FROM A_Asset ");
		SQLCheckAsset.append(" WHERE InventoryNo = '"+dataHeader.asset_id+"'");

		
		Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
		
		if(A_Asset_ID <= 0) {
			
			MAsset asset = new MAsset(ctx, 0, trxName);
			asset.setAD_Org_ID(AD_Org_ID);

			asset.setValue(dataHeader.asset_id);			
			
			asset.setName(dataHeader.asset_name);
			asset.setInventoryNo(dataHeader.asset_id);
			asset.set_ValueOfColumn("PoliceNo", dataHeader.nopol);
			asset.set_ValueOfColumn("PoliceNo_Before", dataHeader.nopol_before);
			asset.set_ValueOfColumn("HullNo", dataHeader.nolam);
			asset.set_ValueOfColumn("AssetType", dataHeader.asset_type);
			asset.set_ValueOfColumn("AssetClass", dataHeader.asset_class);
			asset.set_ValueOfColumn("EngineNo", dataHeader.nosin);
			asset.set_ValueOfColumn("BodyNo", dataHeader.norka);
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

}
