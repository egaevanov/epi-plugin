package org.epi.process;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MUser;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.epi.model.ISM_Model_User;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ISMWSTest extends SvrProcess{

	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_Process_ID = 0;
	private final String Error = "ERROR";
	
	@Override
	protected void prepare() {

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("AD_Client_ID"))
				p_AD_Client_ID = (int)para[i].getParameterAsInt();
			
			else if(name.equals("AD_Org_ID"))
				p_AD_Org_ID = (int)para[i].getParameterAsInt();
			
			else if(name.equals("Process_ID"))
				p_Process_ID = (int)para[i].getParameterAsInt();
			
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {

		StringBuilder SQLExtractJSon = new StringBuilder();
		SQLExtractJSon.append("SELECT AD_Client_ID,AD_Org_ID,Master,I_Master_Temp_ID,Pos ");
		SQLExtractJSon.append(" FROM  I_Master_Temp ");
		SQLExtractJSon.append(" WHERE AD_Client_ID = " + p_AD_Client_ID );
		SQLExtractJSon.append(" AND AD_Org_ID = " + p_AD_Org_ID);
		SQLExtractJSon.append(" AND Insert_Master = 'N' ");
		SQLExtractJSon.append(" AND Process_ID =  " +p_Process_ID);
		
		String JSonString = "";
		int AD_Client_ID = 0;
		int AD_Org_ID= 0;
//		int I_Master_Temp_ID = 0;
		String rslt = "";
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLExtractJSon.toString(), null);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					
					AD_Client_ID = rs.getInt(1);
					AD_Org_ID = rs.getInt(2);
					JSonString = rs.getString(3);
//					I_Master_Temp_ID = rs.getInt(4);
					String posType= rs.getString(5);		
					
					Gson gson = new Gson();
					JsonObject json = new JsonObject();
					JsonParser parser = new JsonParser();
				    json = parser.parse(JSonString).getAsJsonObject();
					
				    if(posType.equals("C")){
				    	
				    	ISM_Model_User data = gson.fromJson(json.toString(), ISM_Model_User.class);
				    	
				    	rslt = createUser(AD_Client_ID, AD_Org_ID, data);
				    }
				    	   			 				  
				}

			} catch (SQLException err) {
				log.log(Level.SEVERE, SQLExtractJSon.toString(), err);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
						
			if(rslt.equals(Error)){
				rollback();
				
//				X_I_Master_Temp master = new X_I_Master_Temp(getCtx(), I_Master_Temp_ID, get_TrxName());
//	 			master.setinsert_master(false);
//	 			master.set_CustomColumn("Result", "Proses Gagal");
//	 			master.saveEx();
				
			}else{
			    	
//			    X_I_Master_Temp master = new X_I_Master_Temp(getCtx(), I_Master_Temp_ID, get_TrxName());
// 			 	master.setinsert_master(true);
// 			 	master.set_CustomColumn("Result", rslt);
// 			 	master.saveEx();	   			 					    
			 
			 }   
			
		return "";
	}



private String createUser(int AD_Client_ID,int AD_Org_ID,ISM_Model_User data){
		
		String rs = "";
	
		try{
	
			
			MUser user = new MUser(getCtx(), 0, get_TrxName());
			
				user.setAD_Org_ID(AD_Org_ID);
				user.setName(data.Name);
				user.setValue(data.Value);
				user.setEMail(data.EMail);
				user.setDescription(data.Description);
				user.saveEx();
				
			
		}catch (Exception e) {
			rs = Error;
		}	
		
		return rs;
		
	}

}
