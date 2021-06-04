package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.epi.model.ISM_Model_User;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class ISMWSAddUser extends SvrProcess {

	final int AD_Table_ID = 114;

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

		ISM_Model_User[] data = gson.fromJson(json.toString(), ISM_Model_User[].class);

		rslt = createUser(AD_Client_ID, AD_Org_ID, data);

		return rslt;
	}

	private String createUser(int AD_Client_ID, int AD_Org_ID, ISM_Model_User[] datas) {

		String rs = null;

		rs = CheckMandatory(AD_Table_ID, datas);

		if (rs != null)
			return rs;

		for (ISM_Model_User data : datas) {

			try {

				MUser user = new MUser(getCtx(), 0, get_TrxName());

				user.setAD_Org_ID(AD_Org_ID);
				user.setName(data.Name);
				user.setValue(data.Value);
				user.setEMail(data.EMail);
				user.setDescription(data.Description);
				user.saveEx();

			} catch (Exception e) {
				rs = e.getLocalizedMessage();
				rollback();
			}
			
			if(rs == null) {
				rs = "Complete";
			}
			
		}
		return rs;

	}

	private String CheckMandatory(int AD_Table_ID,ISM_Model_User[]datas) {
		String rslt = "";
		
		ColumnName = ISM_Model_User.ISM_Model_User_Column();
		HashMap< Integer , String> IsMandatory= new HashMap<Integer, String>();
		HashMap< Integer, ISM_Model_User> dataMap =new HashMap<Integer, ISM_Model_User>();
		ArrayList<String>outPut = new ArrayList<String>();

		for(ISM_Model_User data : datas) {
			
			dataMap.put(data.AD_User_ID, data);
			
		}
		
		StringBuilder SQLGetColumn = new StringBuilder();
		
		SQLGetColumn.append("SELECT AD_Column_ID, ColumnName");
		SQLGetColumn.append(" FROM AD_Column");
		SQLGetColumn.append(" WHERE AD_Table_ID = "+ AD_Table_ID);
		SQLGetColumn.append(" AND IsMandatory = 'Y'");
		SQLGetColumn.append(" AND IsUpdateable = 'Y'");
		SQLGetColumn.append(" AND DefaultValue is null");
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
     	int index = 0;
			try {
				pstmt = DB.prepareStatement(SQLGetColumn.toString(), null);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
										
					IsMandatory.put(index, rs.getString(2));
					index++;
														
				}	
			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetColumn.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
		
		
			for(Integer key : dataMap.keySet()) {
				
				ISM_Model_User cek = dataMap.get(key);
				HashMap< String , Object> dataExtract= new HashMap<String, Object>();

				dataExtract.put("IsActive", cek.IsActive);
				dataExtract.put("Name", !cek.Name.isEmpty()? cek.Name: null);
				dataExtract.put("Value", !cek.Value.isEmpty()? cek.Value: null);
				dataExtract.put("Description", !cek.Description.isEmpty()? cek.Description: null);
				dataExtract.put("Password", !cek.Password.isEmpty()? cek.Password: null);
				dataExtract.put("EMail", !cek.EMail.isEmpty()? cek.EMail: null);
				
				for(int j=0 ; j < IsMandatory.size() ; j++) {
					
					if(dataExtract.get(IsMandatory.get(j))== null 
							|| dataExtract.get(IsMandatory.get(j))== ""
							||dataExtract.get(IsMandatory.get(j)).toString().isEmpty()) {
						
						outPut.add("Column "+ IsMandatory.get(j)+ " pada User ID = "+(cek.AD_User_ID)+ " Tidak Boleh Kosong");
					}
					
				}
				
			}
			
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < outPut.size() ; i++) {
			
			
			result.append(outPut.get(i));
			result.append("\n");

			
		}
		
		rslt = !result.toString().isEmpty()?result.toString():null;

		System.out.println(rslt);

		return rslt;
		
	}

	}
