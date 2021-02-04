package org.epi.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MOrg;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.utils.FinalVariableGlobal;

public class CallOutQuotation  extends CalloutEngine implements IColumnCallout{

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
	Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
	
			if(mField.getColumnName().equals("C_BPartner_ID")){
				return CallOutBPLocation(ctx, WindowNo, mTab, mField, value);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			/*
			 * TODO
			 */		
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {
			
			/*
			 * TODO
			 */		
		}
		return "";
	}
	
	

	public String CallOutBPLocation (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		

		Integer C_BPartner_ID = (Integer)mTab.getValue("C_BPartner_ID");
		
		if(C_BPartner_ID == null) {
			return"";
		}
		
		
		StringBuilder getBPLocation = new StringBuilder();
		getBPLocation.append("SELECT C_BPartner_Location_ID ");
		getBPLocation.append(" FROM C_BPartner_Location ");
		getBPLocation.append(" WHERE AD_Client_ID =  "+Env.getAD_Client_ID(ctx));
		getBPLocation.append(" AND C_BPartner_ID ="+C_BPartner_ID);
	
		Integer C_BPartnerLocation_ID = DB.getSQLValue(null, getBPLocation.toString());
		
		if(C_BPartnerLocation_ID == null || C_BPartnerLocation_ID <= 0) {
			
			return "Anda Belum Menentukan Lokasi Pada Customer Yang Anda Pilih";
		}	
		
		mTab.setValue("C_BPartner_Location_ID", C_BPartnerLocation_ID);

		return"";
	
	}

}
