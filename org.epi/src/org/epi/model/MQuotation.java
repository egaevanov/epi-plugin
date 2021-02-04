package org.epi.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Util;

public class MQuotation extends X_C_Quotation{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MQuotation(Properties ctx, int C_Quotation_ID, String trxName) {
		super(ctx, C_Quotation_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	
	public MQuotation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	//getLine
	protected X_C_QuotationLine[] 	m_lines = null;

	public X_C_QuotationLine[] getLines(){
			return getLines(false, null);
		}

	public X_C_QuotationLine[] getLines (boolean requery, String orderBy){
			
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
		
	public X_C_QuotationLine[] getLines (String whereClause, String orderClause){

		StringBuilder whereClauseFinal = new StringBuilder(X_C_QuotationLine.COLUMNNAME_C_Quotation_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = X_C_QuotationLine.COLUMNNAME_Line;
		//
		List<X_C_QuotationLine> list = new Query(getCtx(), I_C_QuotationLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
			
		return list.toArray(new X_C_QuotationLine[list.size()]);		
	}	//	getLines
		

	

}
