package org.epi.process;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.logging.Level;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

public class EPIProcessRunReport extends SvrProcess {

	private int AD_Process_ID = 0;
	private int Record_ID = 0;
	
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("AD_Process_ID"))
				AD_Process_ID  = (int)para[i].getParameterAsInt();
				
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		Record_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		
		// Copy the list of parameters from the financial report
		ProcessInfoParameter oldpara[] = getParameter();
		for (int i = 0; i < oldpara.length; i++) {
			list.add (oldpara[i]);
		}
		
		// and add the T_Report_AD_PInstance_ID parameter
		list.add (new ProcessInfoParameter("Record_ID", Record_ID, null, null, null));

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
		
		return "";
		
		
	}
		
		
		
}

