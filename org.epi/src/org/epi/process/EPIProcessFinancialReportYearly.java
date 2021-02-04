package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MYear;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class EPIProcessFinancialReportYearly extends SvrProcess{
	
	
	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_C_AcctSchema_ID = 0;
	private int p_PA_Report_ID = 0;
	private int	p_C_Year_ID  = 0;

	@Override
	protected void prepare() {
		
	ProcessInfoParameter[] para = getParameter();
	for (int i = 0; i < para.length; i++)
	{
		String name = para[i].getParameterName();

		if (para[i].getParameter() == null)
			;		
		else if (name.equals("AD_Client_ID")) {
			p_AD_Client_ID = para[i].getParameterAsInt();
		}else if (name.equals("AD_Org_ID")) {
			p_AD_Org_ID = para[i].getParameterAsInt();
		}else if (name.equals("C_AcctSchema_ID")) {
			p_C_AcctSchema_ID = para[i].getParameterAsInt();
		}else if (name.equals("PA_Report_ID")) {
			p_PA_Report_ID = para[i].getParameterAsInt();
		}else if (name.equals("C_Year_ID")) {
			p_C_Year_ID = para[i].getParameterAsInt();
		}else
			log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}
	@Override
	protected String doIt() throws Exception {
		
 		
		MYear year = new MYear(getCtx(), p_C_Year_ID, get_TrxName());
		Integer fiscalYear = Integer.valueOf(year.getFiscalYear());
		Integer LastfiscalYear = fiscalYear-1;
				
		StringBuilder SQLGetPeriod = new StringBuilder();
		
		SQLGetPeriod.append("SELECT ROW_NUMBER() OVER (Order By FiscalYear::numeric ASC, PeriodNo ASC ) as arrayindex,");
		SQLGetPeriod.append(" 	a.C_Period_ID,");
		SQLGetPeriod.append("	a.name ");
		SQLGetPeriod.append("FROM(");
		SQLGetPeriod.append("	SELECT cp.C_Period_ID, ");
		SQLGetPeriod.append("		cp.Name,");
		SQLGetPeriod.append("		cp.PeriodNo, ");
		SQLGetPeriod.append("		cy.fiscalYear  ");
		SQLGetPeriod.append("	FROM C_Period cp");
		SQLGetPeriod.append("	LEFT JOIN C_Year cy ON cy.C_Year_ID = cp.C_Year_ID ");
		SQLGetPeriod.append("	WHERE cy.FiscalYear::numeric IN("+LastfiscalYear+")");
		SQLGetPeriod.append("	AND cp.AD_Client_ID ="+p_AD_Client_ID);
		SQLGetPeriod.append("	AND cp.PeriodNo =12");
		SQLGetPeriod.append("	UNION");
		SQLGetPeriod.append("	SELECT cp.C_Period_ID, ");
		SQLGetPeriod.append("		cp.Name,");
		SQLGetPeriod.append("		cp.PeriodNo, ");
		SQLGetPeriod.append("		cy.fiscalYear  ");
		SQLGetPeriod.append("	FROM C_Period cp");
		SQLGetPeriod.append("	LEFT JOIN C_Year cy ON cy.C_Year_ID = cp.C_Year_ID ");
		SQLGetPeriod.append("	WHERE cy.FiscalYear::numeric IN("+fiscalYear+")");
		SQLGetPeriod.append("	AND cp.AD_Client_ID ="+p_AD_Client_ID);
		SQLGetPeriod.append(")a");

		StringBuilder SQLCalculateReport = new StringBuilder();
		SQLCalculateReport.append("SELECT  f_insert_fact_acct_temp (?,?,?,?,?) ");

		PreparedStatement pstmtPeriod = null;
     	ResultSet rsPeriod = null;
     	int C_Period_ID = 0;
     	
			try {
				pstmtPeriod = DB.prepareStatement(SQLGetPeriod.toString(), get_TrxName());
				
				rsPeriod = pstmtPeriod.executeQuery();
				while (rsPeriod.next()) {
					
					C_Period_ID = rsPeriod.getInt(2);
					
					PreparedStatement pstmt = null;
			     	ResultSet rs = null;
						try {
							pstmt = DB.prepareStatement(SQLCalculateReport.toString(), get_TrxName());
							pstmt.setInt(1,p_AD_Client_ID);	
							pstmt.setInt(2,p_AD_Org_ID);	
							pstmt.setInt(3,p_C_AcctSchema_ID);
							pstmt.setInt(4,p_PA_Report_ID);
							pstmt.setInt(5,C_Period_ID);

							rs = pstmt.executeQuery();
							while (rs.next()) {
	
							}

						} catch (SQLException err) {
							
							log.log(Level.SEVERE, SQLCalculateReport.toString(), err);
							rollback();
							
						} finally {
							
							DB.close(rs, pstmt);
							rs = null;
							pstmt = null;
							
						}	
					
				}

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetPeriod.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rsPeriod, pstmtPeriod);
				rsPeriod = null;
				pstmtPeriod = null;
				
			}	
		
		return "";
	}

}
