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

/** Generated Model for Fact_Acct_RE
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_Fact_Acct_RE extends PO implements I_Fact_Acct_RE, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200914L;

    /** Standard Constructor */
    public X_Fact_Acct_RE (Properties ctx, int Fact_Acct_RE_ID, String trxName)
    {
      super (ctx, Fact_Acct_RE_ID, trxName);
      /** if (Fact_Acct_RE_ID == 0)
        {
			setAccountFrom_ID (0);
			setAccountTo_ID (0);
			setFact_Acct_RE_ID (0);
			setRg_AccountPost_Acct (0);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_Fact_Acct_RE (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Fact_Acct_RE[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_ElementValue getAccountFrom() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getAccountFrom_ID(), get_TrxName());	}

	/** Set Account From.
		@param AccountFrom_ID Account From	  */
	public void setAccountFrom_ID (int AccountFrom_ID)
	{
		if (AccountFrom_ID < 1) 
			set_Value (COLUMNNAME_AccountFrom_ID, null);
		else 
			set_Value (COLUMNNAME_AccountFrom_ID, Integer.valueOf(AccountFrom_ID));
	}

	/** Get Account From.
		@return Account From	  */
	public int getAccountFrom_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AccountFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getAccountTo() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getAccountTo_ID(), get_TrxName());	}

	/** Set Account To.
		@param AccountTo_ID Account To	  */
	public void setAccountTo_ID (int AccountTo_ID)
	{
		if (AccountTo_ID < 1) 
			set_Value (COLUMNNAME_AccountTo_ID, null);
		else 
			set_Value (COLUMNNAME_AccountTo_ID, Integer.valueOf(AccountTo_ID));
	}

	/** Get Account To.
		@return Account To	  */
	public int getAccountTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AccountTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

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

	/** Set Fact_Acct_RE_UU.
		@param Fact_Acct_RE_UU Fact_Acct_RE_UU	  */
	public void setFact_Acct_RE_UU (String Fact_Acct_RE_UU)
	{
		set_ValueNoCheck (COLUMNNAME_Fact_Acct_RE_UU, Fact_Acct_RE_UU);
	}

	/** Get Fact_Acct_RE_UU.
		@return Fact_Acct_RE_UU	  */
	public String getFact_Acct_RE_UU () 
	{
		return (String)get_Value(COLUMNNAME_Fact_Acct_RE_UU);
	}

	/** Set Monthly Calculation Period.
		@param IsMonthly Monthly Calculation Period	  */
	public void setIsMonthly (boolean IsMonthly)
	{
		set_Value (COLUMNNAME_IsMonthly, Boolean.valueOf(IsMonthly));
	}

	/** Get Monthly Calculation Period.
		@return Monthly Calculation Period	  */
	public boolean isMonthly () 
	{
		Object oo = get_Value(COLUMNNAME_IsMonthly);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	public I_C_ValidCombination getRg_AccountPost_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getRg_AccountPost_Acct(), get_TrxName());	}

	/** Set Account Post.
		@param Rg_AccountPost_Acct Account Post	  */
	public void setRg_AccountPost_Acct (int Rg_AccountPost_Acct)
	{
		set_Value (COLUMNNAME_Rg_AccountPost_Acct, Integer.valueOf(Rg_AccountPost_Acct));
	}

	/** Get Account Post.
		@return Account Post	  */
	public int getRg_AccountPost_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Rg_AccountPost_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_ValueNoCheck (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}