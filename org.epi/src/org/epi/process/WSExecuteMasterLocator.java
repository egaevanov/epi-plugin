package org.epi.process;

import java.util.Properties;

import org.compiere.model.MLocator;
import org.epi.ws.model.API_Model_MasterLocation;

public class WSExecuteMasterLocator {
	
	public static Integer CreateMasterLocator(int AD_Client_ID, int AD_Org_ID, API_Model_MasterLocation data, Properties ctx , String trxName) {
		Integer rs = 0;
		
		
		try {
			
			
			MLocator locator = new MLocator(ctx, 0, trxName);
			
			locator.setAD_Org_ID(AD_Org_ID);
			locator.setM_Warehouse_ID(1000013);
			locator.setValue(data.location_id);
			locator.set_CustomColumn("Code", data.location_code);
			locator.set_CustomColumn("Name", data.location_name);
			locator.setPriorityNo(30);
			locator.setX("1");
			locator.setY("2");
			locator.setZ("3");
			
			locator.saveEx();
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		return rs;
	}

}
