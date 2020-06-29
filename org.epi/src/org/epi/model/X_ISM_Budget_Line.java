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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for ISM_Budget_Line
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_ISM_Budget_Line extends PO implements I_ISM_Budget_Line, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200511L;

    /** Standard Constructor */
    public X_ISM_Budget_Line (Properties ctx, int ISM_Budget_Line_ID, String trxName)
    {
      super (ctx, ISM_Budget_Line_ID, trxName);
      /** if (ISM_Budget_Line_ID == 0)
        {
			setApr (Env.ZERO);
			setAug (Env.ZERO);
			setBudget_Acct (0);
			setBudget_Code (null);
			setDec (Env.ZERO);
			setFeb (Env.ZERO);
			setISM_Activity_ID (0);
			setISM_Budget_Line_ID (0);
			setISM_Budget_Line_UU (null);
			setISM_Budget_Planning_ID (0);
			setJan (Env.ZERO);
			setJul (Env.ZERO);
			setJun (Env.ZERO);
			setMar (Env.ZERO);
			setMay (Env.ZERO);
			setNov (Env.ZERO);
			setOct (Env.ZERO);
			setSep (Env.ZERO);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_ISM_Budget_Line (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_ISM_Budget_Line[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Apr.
		@param Apr Apr	  */
	public void setApr (BigDecimal Apr)
	{
		set_Value (COLUMNNAME_Apr, Apr);
	}

	/** Get Apr.
		@return Apr	  */
	public BigDecimal getApr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Apr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Aug.
		@param Aug Aug	  */
	public void setAug (BigDecimal Aug)
	{
		set_Value (COLUMNNAME_Aug, Aug);
	}

	/** Get Aug.
		@return Aug	  */
	public BigDecimal getAug () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Aug);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_C_ValidCombination getBudget_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getBudget_Acct(), get_TrxName());	}

	/** Set Budget Acct.
		@param Budget_Acct Budget Acct	  */
	public void setBudget_Acct (int Budget_Acct)
	{
		set_Value (COLUMNNAME_Budget_Acct, Integer.valueOf(Budget_Acct));
	}

	/** Get Budget Acct.
		@return Budget Acct	  */
	public int getBudget_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Budget_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Budget Code.
		@param Budget_Code Budget Code	  */
	public void setBudget_Code (String Budget_Code)
	{
		set_Value (COLUMNNAME_Budget_Code, Budget_Code);
	}

	/** Get Budget Code.
		@return Budget Code	  */
	public String getBudget_Code () 
	{
		return (String)get_Value(COLUMNNAME_Budget_Code);
	}

	/** Set Dec.
		@param Dec Dec	  */
	public void setDec (BigDecimal Dec)
	{
		set_Value (COLUMNNAME_Dec, Dec);
	}

	/** Get Dec.
		@return Dec	  */
	public BigDecimal getDec () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Dec);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Feb.
		@param Feb Feb	  */
	public void setFeb (BigDecimal Feb)
	{
		set_Value (COLUMNNAME_Feb, Feb);
	}

	/** Get Feb.
		@return Feb	  */
	public BigDecimal getFeb () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Feb);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_ISM_Activity getISM_Activity() throws RuntimeException
    {
		return (I_ISM_Activity)MTable.get(getCtx(), I_ISM_Activity.Table_Name)
			.getPO(getISM_Activity_ID(), get_TrxName());	}

	/** Set Activity.
		@param ISM_Activity_ID Activity	  */
	public void setISM_Activity_ID (int ISM_Activity_ID)
	{
		if (ISM_Activity_ID < 1) 
			set_Value (COLUMNNAME_ISM_Activity_ID, null);
		else 
			set_Value (COLUMNNAME_ISM_Activity_ID, Integer.valueOf(ISM_Activity_ID));
	}

	/** Get Activity.
		@return Activity	  */
	public int getISM_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ISM_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Budget Line.
		@param ISM_Budget_Line_ID Budget Line	  */
	public void setISM_Budget_Line_ID (int ISM_Budget_Line_ID)
	{
		if (ISM_Budget_Line_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ISM_Budget_Line_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ISM_Budget_Line_ID, Integer.valueOf(ISM_Budget_Line_ID));
	}

	/** Get Budget Line.
		@return Budget Line	  */
	public int getISM_Budget_Line_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ISM_Budget_Line_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ISM_Budget_Line_UU.
		@param ISM_Budget_Line_UU ISM_Budget_Line_UU	  */
	public void setISM_Budget_Line_UU (String ISM_Budget_Line_UU)
	{
		set_ValueNoCheck (COLUMNNAME_ISM_Budget_Line_UU, ISM_Budget_Line_UU);
	}

	/** Get ISM_Budget_Line_UU.
		@return ISM_Budget_Line_UU	  */
	public String getISM_Budget_Line_UU () 
	{
		return (String)get_Value(COLUMNNAME_ISM_Budget_Line_UU);
	}

	public I_ISM_Budget_Planning getISM_Budget_Planning() throws RuntimeException
    {
		return (I_ISM_Budget_Planning)MTable.get(getCtx(), I_ISM_Budget_Planning.Table_Name)
			.getPO(getISM_Budget_Planning_ID(), get_TrxName());	}

	/** Set Budget Planning.
		@param ISM_Budget_Planning_ID Budget Planning	  */
	public void setISM_Budget_Planning_ID (int ISM_Budget_Planning_ID)
	{
		if (ISM_Budget_Planning_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ISM_Budget_Planning_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ISM_Budget_Planning_ID, Integer.valueOf(ISM_Budget_Planning_ID));
	}

	/** Get Budget Planning.
		@return Budget Planning	  */
	public int getISM_Budget_Planning_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ISM_Budget_Planning_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Jan.
		@param Jan Jan	  */
	public void setJan (BigDecimal Jan)
	{
		set_Value (COLUMNNAME_Jan, Jan);
	}

	/** Get Jan.
		@return Jan	  */
	public BigDecimal getJan () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Jan);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Jul.
		@param Jul Jul	  */
	public void setJul (BigDecimal Jul)
	{
		set_Value (COLUMNNAME_Jul, Jul);
	}

	/** Get Jul.
		@return Jul	  */
	public BigDecimal getJul () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Jul);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Jun.
		@param Jun Jun	  */
	public void setJun (BigDecimal Jun)
	{
		set_Value (COLUMNNAME_Jun, Jun);
	}

	/** Get Jun.
		@return Jun	  */
	public BigDecimal getJun () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Jun);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Mar.
		@param Mar Mar	  */
	public void setMar (BigDecimal Mar)
	{
		set_Value (COLUMNNAME_Mar, Mar);
	}

	/** Get Mar.
		@return Mar	  */
	public BigDecimal getMar () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Mar);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set May.
		@param May May	  */
	public void setMay (BigDecimal May)
	{
		set_Value (COLUMNNAME_May, May);
	}

	/** Get May.
		@return May	  */
	public BigDecimal getMay () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_May);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Nov.
		@param Nov Nov	  */
	public void setNov (BigDecimal Nov)
	{
		set_Value (COLUMNNAME_Nov, Nov);
	}

	/** Get Nov.
		@return Nov	  */
	public BigDecimal getNov () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Nov);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Oct.
		@param Oct Oct	  */
	public void setOct (BigDecimal Oct)
	{
		set_Value (COLUMNNAME_Oct, Oct);
	}

	/** Get Oct.
		@return Oct	  */
	public BigDecimal getOct () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Oct);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Sep.
		@param Sep Sep	  */
	public void setSep (BigDecimal Sep)
	{
		set_Value (COLUMNNAME_Sep, Sep);
	}

	/** Get Sep.
		@return Sep	  */
	public BigDecimal getSep () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Sep);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}