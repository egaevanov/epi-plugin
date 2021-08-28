package org.epi.process;

import java.util.Properties;

import org.compiere.model.MLocator;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_MasterLocation;

public class WSExecuteMasterLocator {
	
	public static Integer CreateMasterLocator(int AD_Client_ID, int AD_Org_ID, API_Model_MasterLocation data, Properties ctx , String trxName) {
		Integer rs = 0;
		MLocator locator= null;
		
		StringBuilder SQLCheckLocator = new StringBuilder();
		
		SQLCheckLocator.append("SELECT M_Locator_ID ");
		SQLCheckLocator.append(" FROM M_Locator ");
		SQLCheckLocator.append(" WHERE Value = '"+data.location_id+"'");

		
		Integer M_Locator_ID = DB.getSQLValueEx(trxName, SQLCheckLocator.toString());
		
		try {
			
			
			locator = new MLocator(ctx, M_Locator_ID, trxName);
			
			locator.setAD_Org_ID(AD_Org_ID);
			locator.setM_Warehouse_ID(1000013);
			locator.setValue(data.location_id);
			locator.set_CustomColumn("Code", data.location_code);
			locator.set_CustomColumn("Name", data.location_name);
			locator.setPriorityNo(50);
			locator.setX(data.location_id);
			locator.setY(data.location_id);
			locator.setZ(data.location_id);
			
			locator.saveEx();
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.getMessage();
		}
		
		if(locator != null && locator.getM_Locator_ID() > 0) {
			rs = locator.getM_Locator_ID();
		}
		
		return rs;
	}

}
