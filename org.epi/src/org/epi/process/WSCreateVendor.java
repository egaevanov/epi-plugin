package org.epi.process;

import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.epi.ws.model.API_Model_Vendor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class WSCreateVendor extends SvrProcess {
	
	final int AD_Table_ID = 291;

	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private String p_JSON = "";
	public static CLogger log = CLogger.getCLogger(PO.class);
	public HashMap<String, String> ColumnName = null; 

	@Override
	protected void prepare() {

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;

			else if (name.equals("AD_Client_ID"))
				p_AD_Client_ID = (int) para[i].getParameterAsInt();

			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = (int) para[i].getParameterAsInt();

			else if (name.equals("JSON"))
				p_JSON = (String) para[i].getParameterAsString();

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		
	}

	@Override
	protected String doIt() throws Exception {
		
		String JSonString = "";
		int AD_Client_ID = 0;
		int AD_Org_ID = 0;
		String rslt = "";

		AD_Client_ID = p_AD_Client_ID;
		AD_Org_ID = p_AD_Org_ID;
		JSonString = p_JSON;

		Gson gson = new Gson();
		JsonArray json = gson.fromJson(JSonString, JsonArray.class);

		API_Model_Vendor[] data = gson.fromJson(json.toString(), API_Model_Vendor[].class);

		rslt = createVendor(AD_Client_ID, AD_Org_ID, data);

		return rslt;
		
	}
	
	private String createVendor(int AD_Client_ID,int AD_Org_ID,API_Model_Vendor[] datas){
		String rs = "";

		try {
			
//			boolean isValid = DataSetupValidation.IsValidDataMaster(AD_Client_ID, AD_Org_ID,MBPartner.Table_Name, MBPartner.COLUMNNAME_Value, data.Name);
	
			for(API_Model_Vendor vendor : datas) {
			
			
				MBPartner bp = new MBPartner(getCtx(), vendor.C_BPartner_ID, get_TrxName());
				
				bp.setClientOrg(AD_Client_ID, AD_Org_ID);
				bp.setIsVendor(true);
				if(vendor.IsActive.equals("Y")){
					bp.setIsActive(true);
				}else if(vendor.IsActive.equals("N")){
					bp.setIsActive(false);
				}
				
				bp.setValue(vendor.Name);
				bp.setName(vendor.Name);
				System.out.println(vendor.CreditLimit);
				bp.setSO_CreditLimit(vendor.CreditLimit);
//				bp.setC_PaymentTerm_ID(DataSetupValidation.getDefaultPaymentTerm(AD_Client_ID));
				bp.saveEx();
				
				if(bp!= null){
					
					MLocation location = new MLocation(getCtx(), vendor.C_Location_ID, get_TrxName());
					//location.setClientOrg(AD_Client_ID, AD_Org_ID);
					location.setAD_Org_ID(AD_Org_ID);
					if(vendor.IsActive.equals("Y")){
						location.setIsActive(true);
					}else if(vendor.IsActive.equals("N")){
						location.setIsActive(false);
					}
					location.setAddress1(vendor.Address1);
					location.setC_Country_ID(209);
					location.setPostal(vendor.Postal);
					//location.setC_City_ID(vendor.C_City_ID);
					location.saveEx();
					
					
					if(location!= null){
						MBPartnerLocation BpLoc = new MBPartnerLocation(getCtx(), vendor.C_BPartner_Location_ID, get_TrxName());
						if(vendor.IsActive.equals("Y")){
							BpLoc.setIsActive(true);
						}else if(vendor.IsActive.equals("N")){
							BpLoc.setIsActive(false);
						}
						
						BpLoc.setC_BPartner_ID(bp.getC_BPartner_ID());
						BpLoc.setC_Location_ID(location.getC_Location_ID());
						System.out.println(vendor.Phone);
						BpLoc.setPhone(vendor.Phone);
						System.out.println(vendor.Phone2);
						BpLoc.setPhone2(vendor.Phone2);
						BpLoc.setIsShipTo(true);
						BpLoc.setIsPayFrom(true);
						//BpLoc.set_CustomColumnReturningBoolean("IsTax", true);
						BpLoc.setIsBillTo(true);
						BpLoc.setIsRemitTo(true);
						BpLoc.saveEx();			
					
					}
				
				
				if(bp!= null && vendor.C_BPartner_ID == 0){
					rs = "Penambahan Bussines Partner "+ bp.getName()+" Berhasil";	
				}else if(bp!= null && vendor.C_BPartner_ID > 0){
					rs = "Edit Bussines Partner Berhasil";	
				}
			}
		
		}
			
		} catch (Exception e) {
			rs = "error";
			rollback();
		}
		
		return rs;

	}


}
