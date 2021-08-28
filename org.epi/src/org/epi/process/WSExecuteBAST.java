package org.epi.process;

import java.util.Properties;

import org.compiere.model.MAsset;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_BAST;

public class WSExecuteBAST {
	
	
	public static Integer CreateBAST(int AD_Client_ID, int AD_Org_ID, API_Model_BAST dataHeader,Properties ctx , String trxName,int A_Asset_ID , int C_BPartner_ID, int C_Order_ID) {
		
		Integer result = 0;
		
		try {
					
			
			MAsset asset = new MAsset(ctx, A_Asset_ID, trxName);
			asset.setC_BPartner_ID(C_BPartner_ID);
			asset.set_ValueOfColumn("AssetStatus", dataHeader.status);
			asset.set_ValueOfColumn("SO_ID", C_Order_ID);
			
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
		SQLCheckAsset.append(" WHERE Value = '"+dataHeader.asset_id+"'");

		
		Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLCheckAsset.toString());
		
		if(A_Asset_ID > 0) {
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
		}

		return result;
	}
	
	public static Integer CheckSO(int AD_Client_ID, int AD_Org_ID, API_Model_BAST dataHeader, Properties ctx , String trxName) {

		Integer result = 0; 
		
		StringBuilder SQLCheckVendor = new StringBuilder();
		
		SQLCheckVendor.append("SELECT C_Order_ID ");
		SQLCheckVendor.append(" FROM C_Order ");
		SQLCheckVendor.append(" WHERE DocumentNo = '"+dataHeader.so_no+"'");

		Integer C_Order_ID = DB.getSQLValueEx(trxName, SQLCheckVendor.toString());

		
		if(C_Order_ID > 0) {
			result = C_Order_ID;
		}

		return result;
	}
}
