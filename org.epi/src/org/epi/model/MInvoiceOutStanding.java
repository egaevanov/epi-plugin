package org.epi.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Util;

public class MInvoiceOutStanding extends X_C_Invoice_OutStanding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public MInvoiceOutStanding(Properties ctx, int C_Invoice_OutStanding_ID, String trxName) {
		super(ctx, C_Invoice_OutStanding_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MInvoiceOutStanding(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	//getLine
	protected X_C_Invoice_OutStandingLine[] 	m_lines = null;

	public X_C_Invoice_OutStandingLine[] getLines(){
		return getLines(false, null);
	}

	public X_C_Invoice_OutStandingLine[] getLines (boolean requery, String orderBy)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_lines = getLines(null, orderClause);
		return m_lines;
	}	//	getLines
		
	public X_C_Invoice_OutStandingLine[] getLines (String whereClause, String orderClause)
	{

		StringBuilder whereClauseFinal = new StringBuilder(X_C_Invoice_OutStandingLine.COLUMNNAME_C_Invoice_OutStanding_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = X_C_Invoice_OutStandingLine.COLUMNNAME_Line;
			//
		List<X_C_Invoice_OutStandingLine> list = new Query(getCtx(), I_C_Invoice_OutStandingLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
			
		return list.toArray(new X_C_Invoice_OutStandingLine[list.size()]);		
	}	//	getLines
		

	
}
