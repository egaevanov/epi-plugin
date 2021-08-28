package org.epi.process;

import java.util.Properties;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_MasterCustomer;

public class WSExecuteMasterCustomer {


	public static Integer CreateCustomer(int AD_Client_ID,int AD_Org_ID,API_Model_MasterCustomer data,Properties ctx,String trxName){
		Integer rs = 0;
		MBPartner bp =  null;
		
		
		StringBuilder SQLCheckBP = new StringBuilder();
		
		SQLCheckBP.append("SELECT C_BPartner_ID ");
		SQLCheckBP.append(" FROM C_BPartner");
		SQLCheckBP.append(" WHERE Value = '"+data.bp_value+"'");

		
		Integer C_BPartner_ID = DB.getSQLValueEx(trxName, SQLCheckBP.toString());
		
		if(C_BPartner_ID <= 0)
			C_BPartner_ID = 0;
		
		try {
			
			bp = new MBPartner(ctx, C_BPartner_ID, trxName);
				
			bp.setClientOrg(AD_Client_ID, AD_Org_ID);
			bp.setIsVendor(false);
			bp.setIsCustomer(true);
			bp.setC_BP_Group_ID(data.bp_group);
			bp.setValue(data.bp_value);
			bp.setName(data.bp_name);
			bp.setC_PaymentTerm_ID(data.bp_termin_id);
			bp.saveEx();
				
			if(bp!= null){
				

				StringBuilder SQLCheckLocation = new StringBuilder();
				
				SQLCheckLocation.append("SELECT C_Location_ID ");
				SQLCheckLocation.append(" FROM C_BPartner_Location ");
				SQLCheckLocation.append(" WHERE C_BPartner_ID = "+bp.getC_BPartner_ID());

				
				Integer C_Location_ID = DB.getSQLValueEx(trxName, SQLCheckLocation.toString());
					
				MLocation location = new MLocation(ctx, C_Location_ID, trxName);
				location.setAD_Org_ID(AD_Org_ID);
				location.setAddress1(data.bp_address);
				location.setC_Country_ID(209);
				location.setPostal(data.bp_postal);
				location.setCity(data.bp_city);
				location.saveEx();
					
					
				if(location!= null){
					
					StringBuilder SQLCheckBPLocation = new StringBuilder();
					
					SQLCheckBPLocation.append("SELECT C_BPartner_Location_ID ");
					SQLCheckBPLocation.append(" FROM C_BPartner_Location ");
					SQLCheckBPLocation.append(" WHERE C_BPartner_ID = "+bp.getC_BPartner_ID());

					
					Integer C_BPartner_Location_ID = DB.getSQLValueEx(trxName, SQLCheckBPLocation.toString());
					
					
					MBPartnerLocation BpLoc = new MBPartnerLocation(ctx, C_BPartner_Location_ID, trxName);
					BpLoc.setC_BPartner_ID(bp.getC_BPartner_ID());
					BpLoc.setC_Location_ID(location.getC_Location_ID());
					BpLoc.setPhone(data.bp_phone);
					BpLoc.setPhone2(data.bp_phone);
					BpLoc.setFax(data.bp_fax);
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
