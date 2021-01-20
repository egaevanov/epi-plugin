package org.epi.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.compiere.model.MBankStatement;
import org.compiere.model.MElementValue;
import org.compiere.model.MOrg;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_T_Report_Ledger;

public class EPICalculateLedger extends SvrProcess{

	
	private Timestamp p_dateAcct = null;
	private Timestamp p_dateAcctTo  = null;
	
	private String p_AccountNo = "";
	private String p_AccountNoTo = "";
	
	private boolean p_IsListAccount = false;
	private int p_C_ElementValue_ID = 0;
	private int p_C_ElementValueTo_ID = 0;

	private int p_ad_org_id = 0;
	private MOrg org = null;	
	
	@Override
	protected void prepare() {
		

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
						
			if (para[i].getParameter() == null)
				;		
			else if (name.equals("AD_Org_ID")) {
				p_ad_org_id = para[i].getParameterAsInt();
			}else if (name.equals("DateAcct")) {
				p_dateAcct = para[i].getParameterAsTimestamp();
				p_dateAcctTo = para[i].getParameter_ToAsTimestamp();			
			}else if (name.equals("AccountNo")) {
				p_AccountNo = para[i].getParameterAsString();
				p_AccountNoTo = (String) para[i].getParameter_To();
			}else if (name.equals("IsListAccount")) {
				p_IsListAccount = para[i].getParameterAsBoolean();
			}else if (name.equals("C_ElementValue_ID")) {
				p_C_ElementValue_ID = para[i].getParameterAsInt();
				p_C_ElementValueTo_ID = para[i].getParameter_ToAsInt();

			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_AccountNo.trim();
		p_AccountNoTo = p_AccountNoTo.trim();
		
	
	}

	@Override
	protected String doIt() throws Exception {
		
		
		if(p_ad_org_id > 0) {
			
			org = new MOrg(getCtx(), p_ad_org_id, get_TrxName());
		}
		
		StringBuilder SQLGetTempTable = new StringBuilder();
		
		SQLGetTempTable.append("SELECT count(t_report_ledger_id) ");
		SQLGetTempTable.append(" FROM t_report_ledger ");
		SQLGetTempTable.append(" WHERE ad_client_id = ?");
		Integer count = DB.getSQLValueEx(get_TrxName(), SQLGetTempTable.toString(),new Object[] {Env.getAD_Client_ID(getCtx())});

		
		if(count > 0) {
			StringBuilder SQLDelTemp = new StringBuilder();
			
			SQLDelTemp.append("DELETE FROM t_report_ledger ");
			SQLDelTemp.append(" WHERE ad_client_id = "+Env.getAD_Client_ID(getCtx()));	
			DB.executeUpdate(SQLDelTemp.toString(), get_TrxName());

		}
		
		
		StringBuilder SQLAccountDetect = new StringBuilder();
		SQLAccountDetect.append("SELECT DISTINCT ce.value,fa.account_id,ce.name ");
		SQLAccountDetect.append(" FROM fact_acct fa");
		SQLAccountDetect.append(" INNER JOIN ad_table tab on tab.ad_table_id = fa.ad_table_id");
		SQLAccountDetect.append(" INNER JOIN c_elementvalue ce on ce.c_elementvalue_id = fa.account_id" );
		SQLAccountDetect.append(" WHERE fa.ad_client_id = ? ");
		SQLAccountDetect.append(" AND fa.ad_org_id = ? ");
		SQLAccountDetect.append(" AND fa.dateacct BETWEEN '"+p_dateAcct+"' AND '"+p_dateAcctTo+"'");
		
		if(!p_IsListAccount) {
			if(p_AccountNo !=  null && p_AccountNoTo != null && !p_AccountNoTo.isEmpty()) {
				SQLAccountDetect.append(" AND ce.value::numeric BETWEEN "+p_AccountNo+" AND "+p_AccountNoTo);
			}else if(p_AccountNo !=  null && p_AccountNoTo.isEmpty()) {
				SQLAccountDetect.append(" AND ce.value = '"+p_AccountNo+"'");
			}
		}else if(p_IsListAccount) {
			if(p_C_ElementValue_ID >  0 && p_C_ElementValueTo_ID >0) {
				
				MElementValue AccountFrom = new MElementValue(getCtx(), p_C_ElementValue_ID, get_TrxName());
				MElementValue AccountFromTo = new MElementValue(getCtx(), p_C_ElementValueTo_ID, get_TrxName());

				SQLAccountDetect.append(" AND ce.value::numeric BETWEEN "+AccountFrom.getValue()+" AND "+AccountFromTo.getValue());
			}else if(p_C_ElementValue_ID >  0 && p_C_ElementValueTo_ID <= 0) {
				MElementValue AccountFrom = new MElementValue(getCtx(), p_C_ElementValue_ID, get_TrxName());

				SQLAccountDetect.append(" AND ce.value = '"+AccountFrom.getValue()+"'");
			}
		}
		SQLAccountDetect.append(" Order by ce.value asc");	

		
		StringBuilder SQLSaldoAwal = new StringBuilder();
		SQLSaldoAwal.append(" SELECT SUM(fa.amtacctdr) - SUM(fa.amtacctcr) as saldoawal  ");
		SQLSaldoAwal.append(" FROM fact_acct fa");
		SQLSaldoAwal.append(" INNER JOIN ad_table tab on tab.ad_table_id = fa.ad_table_id");
		SQLSaldoAwal.append(" INNER JOIN c_elementvalue ce on ce.c_elementvalue_id = fa.account_id" );
		SQLSaldoAwal.append(" WHERE fa.ad_client_id = ? ");
		SQLSaldoAwal.append(" AND fa.ad_org_id = ? ");
		SQLSaldoAwal.append(" AND ce.value = ? ");
		SQLSaldoAwal.append(" AND fa.dateacct < '"+p_dateAcct+"'");

		
		//SQLtrx	
		StringBuilder SQLTrx = new StringBuilder();
		SQLTrx.append("SELECT org.ad_org_id, "
				+ "org.name as orgname,"
				+ "tab.tablename,"
				+ "fa.record_id,"
				+ "ce.value as account,"
				+ "ce.name,"
				+ "coalesce(fa.amtacctcr,0),"
				+ "coalesce(fa.amtacctdr,0),"
				+ "ce.c_elementvalue_id,"
				+ "fa.dateacct,"
				+ "ce.value::numeric, "
				+ "fa.line_id ");
		SQLTrx.append(" FROM fact_acct fa");
		SQLTrx.append(" INNER JOIN ad_table tab on tab.ad_table_id = fa.ad_table_id");
		SQLTrx.append(" INNER JOIN ad_org org  on org.ad_org_id = fa.ad_org_id");
		SQLTrx.append(" INNER JOIN c_elementvalue ce on ce.c_elementvalue_id = fa.account_id");
		SQLTrx.append(" WHERE fa.ad_client_id = ? ");
		SQLTrx.append(" AND fa.ad_org_id = ? ");
		SQLTrx.append(" AND fa.account_id = ? ");
		SQLTrx.append(" AND fa.dateacct BETWEEN '"+p_dateAcct+"' AND '"+p_dateAcctTo+"'");
		SQLTrx.append(" Order by ce.value::numeric ,fa.dateacct asc");	
		
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(p_dateAcct);
	    calendar.add(Calendar.DAY_OF_WEEK, -1);
	    Timestamp newtime = new Timestamp(calendar.getTimeInMillis());
		
		
		PreparedStatement pstmtSaldo = null;
     	ResultSet rsSaldo = null;
			try {
				pstmtSaldo = DB.prepareStatement(SQLAccountDetect.toString(), get_TrxName());
				pstmtSaldo.setInt(1,getAD_Client_ID());	
				pstmtSaldo.setInt(2,p_ad_org_id);	
				
				rsSaldo = pstmtSaldo.executeQuery();
				while (rsSaldo.next()) {
					BigDecimal balance = Env.ZERO;

					BigDecimal saldoawal = DB.getSQLValueBDEx(get_TrxName(), SQLSaldoAwal.toString(), new Object[] {getAD_Client_ID(),p_ad_org_id,rsSaldo.getString(1)});
					
					if(saldoawal == null) {
						saldoawal = Env.ZERO;
					}
					
					balance = saldoawal;
					
					X_T_Report_Ledger ledgerSaldoAwal = new X_T_Report_Ledger(getCtx(), null, get_TrxName());
					ledgerSaldoAwal.setAD_Org_ID(p_ad_org_id);
					ledgerSaldoAwal.setAD_PInstance_ID(getAD_PInstance_ID());
					ledgerSaldoAwal.setorg_name(org.getName());
					ledgerSaldoAwal.setDocumentNo("");
					ledgerSaldoAwal.set_ValueNoCheck("Account",rsSaldo.getString(1));
					ledgerSaldoAwal.setDateAcct(newtime);
					ledgerSaldoAwal.setDescription("SALDO AWAL");
					ledgerSaldoAwal.setAmtAcctCr(Env.ZERO);
					ledgerSaldoAwal.setAmtAcctDr(Env.ZERO);
					ledgerSaldoAwal.setBalance(saldoawal);
					ledgerSaldoAwal.set_ValueNoCheck("Account_ID", rsSaldo.getInt(2));
					ledgerSaldoAwal.set_ValueNoCheck("ElementName", rsSaldo.getString(3));
					ledgerSaldoAwal.saveEx();
					
//					System.out.println("Element Name Saldo:"+ rsSaldo.getString(3));
					
					PreparedStatement pstmt = null;
			     	ResultSet rs = null;
						try {
							pstmt = DB.prepareStatement(SQLTrx.toString(), get_TrxName());
							pstmt.setInt(1,getAD_Client_ID());	
							pstmt.setInt(2,p_ad_org_id);	
							pstmt.setInt(3,rsSaldo.getInt(2));


							rs = pstmt.executeQuery();
							while (rs.next()) {

								//System.out.println(rs.getInt(1));
								int line_id = rs.getInt(12);
								
								X_T_Report_Ledger ledger = new X_T_Report_Ledger(getCtx(), null, get_TrxName());
								ledger.setAD_Org_ID(p_ad_org_id);
								ledger.setAD_PInstance_ID(getAD_PInstance_ID());
								ledger.setorg_name(rs.getString(2));
								ledger.setDateAcct(rs.getTimestamp(10));
								
								
								StringBuilder SQLGetDocNo = new StringBuilder();
								if(rs.getString(3).contentEquals(MBankStatement.Table_Name)) {
									SQLGetDocNo.append("SELECT Description");
								}else {
									SQLGetDocNo.append("SELECT documentno");
								}
								
								SQLGetDocNo.append(" FROM "+rs.getString(3));
								SQLGetDocNo.append(" WHERE ad_client_id = "+getAD_Client_ID());
								SQLGetDocNo.append(" AND "+rs.getString(3)+"_ID =" + rs.getInt(4));
								
								
								StringBuilder SQLGetDesc = new StringBuilder();
								SQLGetDesc.append("SELECT Description");
								
								if(line_id > 0) {
									SQLGetDesc.append(" FROM "+rs.getString(3)+"Line");

								}else {
									SQLGetDesc.append(" FROM "+rs.getString(3));

								}
								
								SQLGetDesc.append(" WHERE ad_client_id = "+getAD_Client_ID());
								
								if(line_id > 0) {
									SQLGetDesc.append(" AND "+rs.getString(3)+"Line_ID =" + rs.getInt(12));
								}else {
									SQLGetDesc.append(" AND "+rs.getString(3)+"_ID =" + rs.getInt(4));

								}
								//System.out.println("SQL -->"+ SQLGetDocNo.toString());
								String documentno = DB.getSQLValueStringEx(get_TrxName(), SQLGetDocNo.toString());	
								String description = DB.getSQLValueStringEx(get_TrxName(), SQLGetDesc.toString());	
								
								if(description == null) {
									description = "-";
								}

								ledger.setDocumentNo(documentno);
								ledger.set_ValueNoCheck("Account",rs.getString(5));
								ledger.setDescription(description);
								ledger.setAmtAcctCr(rs.getBigDecimal(7));
								ledger.setAmtAcctDr(rs.getBigDecimal(8));
								ledger.set_ValueNoCheck("Account_ID", rs.getInt(9));
								ledger.set_ValueNoCheck("ElementName", rs.getString(6));

//								System.out.println("Element Name trx:"+ rs.getString(6));

								//System.out.println("Saldo :"+balance);
								if(rs.getBigDecimal(7).compareTo(Env.ZERO) > 0) {
									
									//System.out.println("CR :"+rs.getBigDecimal(7));

									balance = balance.subtract(rs.getBigDecimal(7));
									ledger.setBalance(balance);
									//System.out.println("saldo akhir : "+balance);

								}else if(rs.getBigDecimal(8).compareTo(Env.ZERO) > 0) {
									
									//System.out.println("DR :"+rs.getBigDecimal(8));

									balance = balance.add(rs.getBigDecimal(8));
									ledger.setBalance(balance);
						
									//System.out.println("saldo akhir : "+balance);
								}
								
								ledger.saveEx();
								
							}

						} catch (SQLException err) {
							
							log.log(Level.SEVERE, SQLTrx.toString(), err);
							rollback();
							
						} finally {
							
							DB.close(rs, pstmt);
							rs = null;
							pstmt = null;
							
						}	
					
								
				}

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLAccountDetect.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rsSaldo, pstmtSaldo);
				rsSaldo = null;
				pstmtSaldo = null;
				
			}	
		
		

	
		return "";
	}

	
	
	
}
