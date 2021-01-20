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

/** Generated Model for GL_Category_SeqNo
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_GL_Category_SeqNo extends PO implements I_GL_Category_SeqNo, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200811L;

    /** Standard Constructor */
    public X_GL_Category_SeqNo (Properties ctx, int GL_Category_SeqNo_ID, String trxName)
    {
      super (ctx, GL_Category_SeqNo_ID, trxName);
      /** if (GL_Category_SeqNo_ID == 0)
        {
			setC_Period_ID (0);
			setCalendarYearMonth (null);
			setCurrentNext (0);
// 100
			setGL_Category_ID (0);
			setGL_Category_SeqNo_ID (0);
        } */
    }

    /** Load Constructor */
    public X_GL_Category_SeqNo (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_GL_Category_SeqNo[")
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

	/** Set YearMonth.
		@param CalendarYearMonth 
		YYYYMM
	  */
	public void setCalendarYearMonth (String CalendarYearMonth)
	{
		set_ValueNoCheck (COLUMNNAME_CalendarYearMonth, CalendarYearMonth);
	}

	/** Get YearMonth.
		@return YYYYMM
	  */
	public String getCalendarYearMonth () 
	{
		return (String)get_Value(COLUMNNAME_CalendarYearMonth);
	}

	/** Set Current Next.
		@param CurrentNext 
		The next number to be used
	  */
	public void setCurrentNext (int CurrentNext)
	{
		set_Value (COLUMNNAME_CurrentNext, Integer.valueOf(CurrentNext));
	}

	/** Get Current Next.
		@return The next number to be used
	  */
	public int getCurrentNext () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_CurrentNext);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException
    {
		return (org.compiere.model.I_GL_Category)MTable.get(getCtx(), org.compiere.model.I_GL_Category.Table_Name)
			.getPO(getGL_Category_ID(), get_TrxName());	}

	/** Set GL Category.
		@param GL_Category_ID 
		General Ledger Category
	  */
	public void setGL_Category_ID (int GL_Category_ID)
	{
		if (GL_Category_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_GL_Category_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_GL_Category_ID, Integer.valueOf(GL_Category_ID));
	}

	/** Get GL Category.
		@return General Ledger Category
	  */
	public int getGL_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set GL Category Sequence No.
		@param GL_Category_SeqNo_ID GL Category Sequence No	  */
	public void setGL_Category_SeqNo_ID (int GL_Category_SeqNo_ID)
	{
		if (GL_Category_SeqNo_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_GL_Category_SeqNo_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_GL_Category_SeqNo_ID, Integer.valueOf(GL_Category_SeqNo_ID));
	}

	/** Get GL Category Sequence No.
		@return GL Category Sequence No	  */
	public int getGL_Category_SeqNo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Category_SeqNo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set GL_Category_SeqNo_UU.
		@param GL_Category_SeqNo_UU GL_Category_SeqNo_UU	  */
	public void setGL_Category_SeqNo_UU (String GL_Category_SeqNo_UU)
	{
		set_ValueNoCheck (COLUMNNAME_GL_Category_SeqNo_UU, GL_Category_SeqNo_UU);
	}

	/** Get GL_Category_SeqNo_UU.
		@return GL_Category_SeqNo_UU	  */
	public String getGL_Category_SeqNo_UU () 
	{
		return (String)get_Value(COLUMNNAME_GL_Category_SeqNo_UU);
	}
}