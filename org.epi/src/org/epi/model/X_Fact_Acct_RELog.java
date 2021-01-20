/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.epi.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for Fact_Acct_RELog
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_Fact_Acct_RELog extends PO implements I_Fact_Acct_RELog, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20201028L;

    /** Standard Constructor */
    public X_Fact_Acct_RELog (Properties ctx, int Fact_Acct_RELog_ID, String trxName)
    {
      super (ctx, Fact_Acct_RELog_ID, trxName);
      /** if (Fact_Acct_RELog_ID == 0)
        {
			setFact_Acct_RE_ID (0);
			setFact_Acct_RELog_ID (0);
			setGL_Journal_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Fact_Acct_RELog (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_Fact_Acct_RELog[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Period getC_Period() throws RuntimeException
    {
		return (org.compiere.model.I_C_Period)MTable.get(getCtx(), org.compiere.model.I_C_Period.Table_Name)
			.getPO(getC_Period_ID(), get_TrxName());	}

	/** Set Period.
		@param C_Period_ID 
		Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Period_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	public int getC_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_Fact_Acct_RE getFact_Acct_RE() throws RuntimeException
    {
		return (I_Fact_Acct_RE)MTable.get(getCtx(), I_Fact_Acct_RE.Table_Name)
			.getPO(getFact_Acct_RE_ID(), get_TrxName());	}

	/** Set Retained Earning.
		@param Fact_Acct_RE_ID Retained Earning	  */
	public void setFact_Acct_RE_ID (int Fact_Acct_RE_ID)
	{
		if (Fact_Acct_RE_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_RE_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_RE_ID, Integer.valueOf(Fact_Acct_RE_ID));
	}

	/** Get Retained Earning.
		@return Retained Earning	  */
	public int getFact_Acct_RE_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Fact_Acct_RE_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Retained Earning Logs.
		@param Fact_Acct_RELog_ID Retained Earning Logs	  */
	public void setFact_Acct_RELog_ID (int Fact_Acct_RELog_ID)
	{
		if (Fact_Acct_RELog_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_RELog_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_RELog_ID, Integer.valueOf(Fact_Acct_RELog_ID));
	}

	/** Get Retained Earning Logs.
		@return Retained Earning Logs	  */
	public int getFact_Acct_RELog_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Fact_Acct_RELog_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Fact_Acct_RELog_UU.
		@param Fact_Acct_RELog_UU Fact_Acct_RELog_UU	  */
	public void setFact_Acct_RELog_UU (String Fact_Acct_RELog_UU)
	{
		set_ValueNoCheck (COLUMNNAME_Fact_Acct_RELog_UU, Fact_Acct_RELog_UU);
	}

	/** Get Fact_Acct_RELog_UU.
		@return Fact_Acct_RELog_UU	  */
	public String getFact_Acct_RELog_UU () 
	{
		return (String)get_Value(COLUMNNAME_Fact_Acct_RELog_UU);
	}

	public org.compiere.model.I_GL_Journal getGL_Journal() throws RuntimeException
    {
		return (org.compiere.model.I_GL_Journal)MTable.get(getCtx(), org.compiere.model.I_GL_Journal.Table_Name)
			.getPO(getGL_Journal_ID(), get_TrxName());	}

	/** Set Journal.
		@param GL_Journal_ID 
		General Ledger Journal
	  */
	public void setGL_Journal_ID (int GL_Journal_ID)
	{
		if (GL_Journal_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_GL_Journal_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_GL_Journal_ID, Integer.valueOf(GL_Journal_ID));
	}

	/** Get Journal.
		@return General Ledger Journal
	  */
	public int getGL_Journal_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Journal_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}