package org.epi.acct;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MInvoice;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

public class EPIDoc {
	
	protected static CLogger log = CLogger.getCLogger(EPIDoc.class);
	
	/**	Account Type - Invoice - AR Service  */
	public static final int 	ACCTTYPE_C_Invoice_DP   = 5;

	public static int getISMValidCombination_ID (int AcctType, MAcctSchema as , MInvoice inv)
	{
		int para_1 = 0;     //  first parameter (second is always AcctSchema)
		String sql = null;

		if (AcctType == ACCTTYPE_C_Invoice_DP)
		{
			sql = "SELECT C_Prepayment_Acct FROM C_BP_Customer_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
			para_1 = inv.getC_BPartner_ID();
			
		}else{
			log.severe ("Not found AcctType=" + AcctType);
			return 0;
		}
		//  Do we have sql & Parameter
		if (sql == null || para_1 == 0)
		{
			log.severe ("No Parameter for AcctType=" + AcctType + " - SQL=" + sql);
			return 0;
		}

		//  Get Acct
		int Account_ID = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			if (para_1 == -1)   //  GL Accounts
				pstmt.setInt (1, as.getC_AcctSchema_ID());
			else
			{
				pstmt.setInt (1, para_1);
				pstmt.setInt (2, as.getC_AcctSchema_ID());
			}
			rs = pstmt.executeQuery();
			if (rs.next())
				Account_ID = rs.getInt(1);
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, "AcctType=" + AcctType + " - SQL=" + sql, e);
			return 0;
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//	No account
		if (Account_ID == 0)
		{
			log.severe ("NO account Type="+ AcctType );
			return 0;
		}
		return Account_ID;
	}	//	getAccount_ID

}
