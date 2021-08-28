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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for A_Depreciation_Exp_Cus
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_A_Depreciation_Exp_Cus extends PO implements I_A_Depreciation_Exp_Cus, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210727L;

    /** Standard Constructor */
    public X_A_Depreciation_Exp_Cus (Properties ctx, int A_Depreciation_Exp_Cus_ID, String trxName)
    {
      super (ctx, A_Depreciation_Exp_Cus_ID, trxName);
      /** if (A_Depreciation_Exp_Cus_ID == 0)
        {
			setA_Asset_ID (0);
			setA_Depreciation_Exp_Cus_ID (0);
			setDescription (null);
			setExpense (Env.ZERO);
// 0
			setProcessed (false);
        } */
    }

    /** Load Constructor */
    public X_A_Depreciation_Exp_Cus (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_A_Depreciation_Exp_Cus[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_A_Asset getA_Asset() throws RuntimeException
    {
		return (org.compiere.model.I_A_Asset)MTable.get(getCtx(), org.compiere.model.I_A_Asset.Table_Name)
			.getPO(getA_Asset_ID(), get_TrxName());	}

	/** Set Asset.
		@param A_Asset_ID 
		Asset used internally or by customers
	  */
	public void setA_Asset_ID (int A_Asset_ID)
	{
		if (A_Asset_ID < 1) 
			set_Value (COLUMNNAME_A_Asset_ID, null);
		else 
			set_Value (COLUMNNAME_A_Asset_ID, Integer.valueOf(A_Asset_ID));
	}

	/** Get Asset.
		@return Asset used internally or by customers
	  */
	public int getA_Asset_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Depreciation Expense Custom.
		@param A_Depreciation_Exp_Cus_ID Depreciation Expense Custom	  */
	public void setA_Depreciation_Exp_Cus_ID (int A_Depreciation_Exp_Cus_ID)
	{
		if (A_Depreciation_Exp_Cus_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_A_Depreciation_Exp_Cus_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_A_Depreciation_Exp_Cus_ID, Integer.valueOf(A_Depreciation_Exp_Cus_ID));
	}

	/** Get Depreciation Expense Custom.
		@return Depreciation Expense Custom	  */
	public int getA_Depreciation_Exp_Cus_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Depreciation_Exp_Cus_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set A_Depreciation_Exp_Cus_UU.
		@param A_Depreciation_Exp_Cus_UU A_Depreciation_Exp_Cus_UU	  */
	public void setA_Depreciation_Exp_Cus_UU (String A_Depreciation_Exp_Cus_UU)
	{
		set_ValueNoCheck (COLUMNNAME_A_Depreciation_Exp_Cus_UU, A_Depreciation_Exp_Cus_UU);
	}

	/** Get A_Depreciation_Exp_Cus_UU.
		@return A_Depreciation_Exp_Cus_UU	  */
	public String getA_Depreciation_Exp_Cus_UU () 
	{
		return (String)get_Value(COLUMNNAME_A_Depreciation_Exp_Cus_UU);
	}

	public org.compiere.model.I_C_ElementValue getCR_Account() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getCR_Account_ID(), get_TrxName());	}

	/** Set Account (Credit).
		@param CR_Account_ID 
		Account used
	  */
	public void setCR_Account_ID (int CR_Account_ID)
	{
		if (CR_Account_ID < 1) 
			set_Value (COLUMNNAME_CR_Account_ID, null);
		else 
			set_Value (COLUMNNAME_CR_Account_ID, Integer.valueOf(CR_Account_ID));
	}

	/** Get Account (Credit).
		@return Account used
	  */
	public int getCR_Account_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_CR_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_ValueNoCheck (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
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

	public org.compiere.model.I_C_ElementValue getDR_Account() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getDR_Account_ID(), get_TrxName());	}

	/** Set Account (Debit).
		@param DR_Account_ID 
		Account used
	  */
	public void setDR_Account_ID (int DR_Account_ID)
	{
		if (DR_Account_ID < 1) 
			set_Value (COLUMNNAME_DR_Account_ID, null);
		else 
			set_Value (COLUMNNAME_DR_Account_ID, Integer.valueOf(DR_Account_ID));
	}

	/** Get Account (Debit).
		@return Account used
	  */
	public int getDR_Account_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DR_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Expense.
		@param Expense Expense	  */
	public void setExpense (BigDecimal Expense)
	{
		set_Value (COLUMNNAME_Expense, Expense);
	}

	/** Get Expense.
		@return Expense	  */
	public BigDecimal getExpense () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Expense);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
}