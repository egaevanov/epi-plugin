package org.epi.process;

import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_BPartner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class TBUProcessAPI extends SvrProcess {

	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private String p_JSonData = null;
	private final String Error = "ERROR";
	private String p_PostType = "";
	
	private final String TYPE_BP = "BP";

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

			else if (name.equals("JSonData"))
				p_JSonData = (String) para[i].getParameterAsString();

			else if (name.equals("PostType"))
				p_PostType = (String) para[i].getParameterAsString();

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}

	@Override
	protected String doIt() throws Exception {

		String rslt = "";

		int AD_Client_ID = p_AD_Client_ID;
		int AD_Org_ID = p_AD_Org_ID;
		String JSonString = p_JSonData;

		Gson gson = new Gson();
		JsonArray json = gson.fromJson(JSonString, JsonArray.class);

		if (p_PostType.equals(TYPE_BP)) {

			API_Model_BPartner[] data = gson.fromJson(json.toString(), API_Model_BPartner[].class);
			rslt = createBPartner(AD_Client_ID, AD_Org_ID, data);

		}


		return rslt;
	}

	private String createBPartner(int AD_Client_ID, int AD_Org_ID, API_Model_BPartner[] datas) {
		
		String rs = "";

		for (API_Model_BPartner data : datas) {

			try {

				MBPartner bp = new MBPartner(getCtx(), data.C_BPartner_ID, get_TrxName());

				bp.setClientOrg(AD_Client_ID, AD_Org_ID);
				if (data.IsVendor.equals("Y")) {
					bp.setIsVendor(true);
				} else if (data.IsVendor.equals("N")) {
					bp.setIsVendor(false);
				}

				if (data.IsCustomer.equals("Y")) {
					bp.setIsCustomer(true);
				} else if (data.IsCustomer.equals("N")) {
					bp.setIsCustomer(false);
				}

				if (data.IsActive.equals("Y")) {
					bp.setIsActive(true);
				} else if (data.IsActive.equals("N")) {
					bp.setIsActive(false);
				}

				bp.setValue(data.Name);
				bp.setName(data.Name);
				System.out.println(data.CreditLimit);
				bp.setSO_CreditLimit(data.CreditLimit);
				// bp.setC_PaymentTerm_ID(DataSetupValidation.getDefaultPaymentTerm(AD_Client_ID));
				bp.saveEx();

				if (bp != null) {

					MLocation location = new MLocation(getCtx(), data.C_Location_ID, get_TrxName());
					// location.setClientOrg(AD_Client_ID, AD_Org_ID);
					location.setAD_Org_ID(AD_Org_ID);
					if (data.IsActive.equals("Y")) {
						location.setIsActive(true);
					} else if (data.IsActive.equals("N")) {
						location.setIsActive(false);
					}
					location.setAddress1(data.Address1);
					location.setC_Country_ID(209);
					System.out.println(data.Postal);
					location.setPostal(data.Postal);
					location.setC_City_ID(data.C_City_ID);
					location.saveEx();

					if (location != null) {
						MBPartnerLocation BpLoc = new MBPartnerLocation(getCtx(), data.C_BPartner_Location_ID,
								get_TrxName());
						if (data.IsActive.equals("Y")) {
							BpLoc.setIsActive(true);
						} else if (data.IsActive.equals("N")) {
							BpLoc.setIsActive(false);
						}

						BpLoc.setC_BPartner_ID(bp.getC_BPartner_ID());
						BpLoc.setC_Location_ID(location.getC_Location_ID());
						BpLoc.setPhone(data.Phone);
						BpLoc.setPhone2(data.Phone2);
						BpLoc.setIsShipTo(true);
						BpLoc.setIsPayFrom(true);
						// BpLoc.set_CustomColumnReturningBoolean("IsTax", true);
						BpLoc.setIsBillTo(true);
						BpLoc.setIsRemitTo(true);
						BpLoc.saveEx();

					}
				}

				if (bp != null && data.C_BPartner_ID == 0) {
					rs = "Penambahan Bussines Partner Berhasil";
				} else if (bp != null && data.C_BPartner_ID > 0) {
					rs = "Edit Bussines Partner Berhasil";
				}

			} catch (Exception e) {
				rs = Error;
				rollback();
			}

		}
		return rs;

	}

	public boolean checkData(String tableName) {

		boolean rs = false;

		StringBuilder CheckData = new StringBuilder();
		CheckData.append("SELECT " + tableName + "_ID");
		CheckData.append(" FROM " + tableName);
		CheckData.append(" WHERE");
		CheckData.append("");
		CheckData.append("");
		CheckData.append("");

		return rs;

	}

	public boolean checkDataVendor(int I_BPartner_ID) {

		boolean rs = false;

		StringBuilder CheckVendor = new StringBuilder();

		CheckVendor.append("SELECT  COUNT(C_BPartner_ID)");
		CheckVendor.append(" FROM  C_BPartner ");
		CheckVendor.append(" WHERE  AD_Org_ID  = ( SELECT AD_Org_ID FROM I_BPartner WHERE I_BPartner_ID = ? )");
		CheckVendor.append(" AND  Value    = ( SELECT Value FROM I_BPartner WHERE I_BPartner_ID = ? )");
		CheckVendor.append(" AND  IsVendor  = 'Y';");

		Integer cnt = DB.getSQLValueEx(get_TrxName(), CheckVendor.toString(),
				new Object[] { I_BPartner_ID, I_BPartner_ID });

		if (cnt <= 0 || cnt == null) {
			rs = true;
		}

		return rs;
	}

}
