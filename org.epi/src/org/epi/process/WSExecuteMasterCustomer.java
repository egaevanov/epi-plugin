package org.epi.process;

import java.util.Properties;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.epi.ws.model.API_Model_MasterCustomer;

public class WSExecuteMasterCustomer {


	public static Integer CreateCustomer(int AD_Client_ID,int AD_Org_ID,API_Model_MasterCustomer data,Properties ctx,String trxName){
		Integer rs = 0;
		MBPartner bp =  null;
		
		
		try {
			
			bp = new MBPartner(ctx, 0, trxName);
				
			bp.setClientOrg(AD_Client_ID, AD_Org_ID);
			bp.setIsVendor(true);	
			bp.setC_BP_Group_ID(data.bp_group);
			bp.setValue(data.bp_value);
			bp.setName(data.bp_name);
			bp.setC_PaymentTerm_ID(data.bp_termin_id);
			bp.saveEx();
				
			if(bp!= null){
					
				MLocation location = new MLocation(ctx, 0, trxName);
				location.setAD_Org_ID(AD_Org_ID);
				location.setAddress1(data.bp_address);
				location.setC_Country_ID(209);
				location.setPostal(data.bp_postal);
				location.saveEx();
					
					
				if(location!= null){
					MBPartnerLocation BpLoc = new MBPartnerLocation(ctx, 0, trxName);
					BpLoc.setC_BPartner_ID(bp.getC_BPartner_ID());
					BpLoc.setC_Location_ID(location.getC_Location_ID());
					BpLoc.setPhone(data.bp_phone);
					BpLoc.setPhone2(data.bp_phone);
					BpLoc.setIsShipTo(true);
					BpLoc.setIsPayFrom(true);
					BpLoc.setIsBillTo(true);
					BpLoc.setIsRemitTo(true);
					BpLoc.saveEx();			
					
				}
				
				
				if(bp!= null && bp.getC_BPartner_ID() > 0){
					rs = bp.getC_BPartner_ID();
				}
			}
			
		} catch (Exception e) {
		
			
		}
		
	return rs;

	}


}
