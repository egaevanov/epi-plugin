package org.epi.process;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.report.MReport;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

public class EPIProcessFinancialReportExport extends SvrProcess{

	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_C_AcctSchema_ID = 0;
	private int p_PA_Report_ID = 0;
	private int p_C_Period_ID = 0;
	private int p_C_Project_ID = 0;
	
	
	@Override
	protected void prepare() {
	
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("AD_Client_ID")) {
				p_AD_Client_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("AD_Org_ID")) {
				p_AD_Org_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("C_AcctSchema_ID")) {
				p_C_AcctSchema_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("PA_Report_ID")) {
				p_PA_Report_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("C_Period_ID")) {
				p_C_Period_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("C_Project_ID")) {
				p_C_Project_ID  = (int)para[i].getParameterAsInt();
			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		
		StringBuilder SQLExecFucntion = new StringBuilder();
		SQLExecFucntion.append("SELECT f_insert_fact_acct_temp(?,?,?,?,?,?)");
		boolean IsOK = true;
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLExecFucntion.toString(), null);
				pstmt.setInt(1,p_AD_Client_ID);	
				pstmt.setInt(2,p_AD_Org_ID);	
				pstmt.setInt(3,p_C_AcctSchema_ID);	
				pstmt.setInt(4,p_PA_Report_ID);	
				pstmt.setInt(5,p_C_Period_ID);	
				pstmt.setInt(6,p_C_Project_ID);	

				rs = pstmt.executeQuery();
				while (rs.next()) {
					
					
					
				}

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLExecFucntion.toString(), err);
				IsOK = false;
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
			
			
			if(IsOK) {
				
				MReport report = new MReport(getCtx(), p_PA_Report_ID, get_TrxName());
				
				if(report != null) {
					
					int AD_Process_ID = report.getJasperProcess_ID();
					
					if(AD_Process_ID > 0) {
						
						
						ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
						
						// Copy the list of parameters from the financial report
						ProcessInfoParameter oldpara[] = getParameter();
						for (int i = 0; i < oldpara.length; i++) {
							list.add (oldpara[i]);
						}
						
						// and add the T_Report_AD_PInstance_ID parameter
						list.add (new ProcessInfoParameter("AD_Client_ID", p_AD_Client_ID, null, null, null));
						list.add (new ProcessInfoParameter("AD_Org_ID", p_AD_Org_ID, null, null, null));
						list.add (new ProcessInfoParameter("C_AcctSchema_ID", p_C_AcctSchema_ID, null, null, null));
						list.add (new ProcessInfoParameter("PA_Report_ID", p_PA_Report_ID, null, null, null));
						list.add (new ProcessInfoParameter("C_Period_ID", p_C_Period_ID, null, null, null));
						list.add (new ProcessInfoParameter("C_Project_ID", p_C_Project_ID , null, null, null));

						
						ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
						list.toArray(pars);

						MProcess proc = new MProcess(getCtx(), AD_Process_ID, get_TrxName());
					    MPInstance instance = new MPInstance(proc, getRecord_ID());
					    instance.saveEx();
					    ProcessInfo poInfo = new ProcessInfo(proc.getName(), proc.getAD_Process_ID());
					    poInfo.setParameter(pars);
					    poInfo.setRecord_ID(getRecord_ID());
					    poInfo.setAD_Process_ID(proc.getAD_Process_ID());
					    poInfo.setAD_PInstance_ID(instance.getAD_PInstance_ID());
					    poInfo.setAD_Process_UU(proc.getAD_Process_UU());	

					    Trx trx = Trx.get(get_TrxName(), true);
					    trx.commit();
					    
					    if (proc.getProcedureName() != null && proc.getProcedureName().length() > 0) {
							//  execute on this thread/connection
							String sql = "{call " + proc.getProcedureName() + "(?)}";
							CallableStatement cstmt = null;
							try
							{
								cstmt = DB.prepareCall(sql);	//	ro??
								cstmt.setInt(1, getAD_PInstance_ID());
								cstmt.executeUpdate();
							}
							catch (Exception e)
							{
								log.log(Level.SEVERE, sql, e);
								poInfo.setSummary (Msg.getMsg(Env.getCtx(), "ProcessRunError") + " " + e.getLocalizedMessage());
							}
							finally
							{
								DB.close(cstmt);
								cstmt = null;
							}
					    }
					    
					    ProcessUtil.startJavaProcess(getCtx(), poInfo, trx);
						
					}
					
				}
				
			}
			
		
		
		return "";
	}

}
