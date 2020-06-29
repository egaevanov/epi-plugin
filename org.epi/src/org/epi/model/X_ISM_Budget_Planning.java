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

/** Generated Model for ISM_Budget_Planning
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_ISM_Budget_Planning extends PO implements I_ISM_Budget_Planning, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200511L;

    /** Standard Constructor */
    public X_ISM_Budget_Planning (Properties ctx, int ISM_Budget_Planning_ID, String trxName)
    {
      super (ctx, ISM_Budget_Planning_ID, trxName);
      /** if (ISM_Budget_Planning_ID == 0)
        {
			setC_Year_ID (0);
			setISM_Budget_Planning_ID (0);
			setISM_Budget_Planning_UU (null);
			setISM_Department_ID (0);
			setValue (null);
			setVersion (null);
        } */
    }

    /** Load Constructor */
    public X_ISM_Budget_Planning (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_ISM_Budget_Planning[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Year getC_Year() throws RuntimeException
    {
		return (org.compiere.model.I_C_Year)MTable.get(getCtx(), org.compiere.model.I_C_Year.Table_Name)
			.getPO(getC_Year_ID(), get_TrxName());	}

	/** Set Year.
		@param C_Year_ID 
		Calendar Year
	  */
	public void setC_Year_ID (int C_Year_ID)
	{
		if (C_Year_ID < 1) 
			set_Value (COLUMNNAME_C_Year_ID, null);
		else 
			set_Value (COLUMNNAME_C_Year_ID, Integer.valueOf(C_Year_ID));
	}

	/** Get Year.
		@return Calendar Year
	  */
	public int getC_Year_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Year_ID);
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

	/** Set ISM_Budget_Planning_UU.
		@param ISM_Budget_Planning_UU ISM_Budget_Planning_UU	  */
	public void setISM_Budget_Planning_UU (String ISM_Budget_Planning_UU)
	{
		set_ValueNoCheck (COLUMNNAME_ISM_Budget_Planning_UU, ISM_Budget_Planning_UU);
	}

	/** Get ISM_Budget_Planning_UU.
		@return ISM_Budget_Planning_UU	  */
	public String getISM_Budget_Planning_UU () 
	{
		return (String)get_Value(COLUMNNAME_ISM_Budget_Planning_UU);
	}

	public I_ISM_Department getISM_Department() throws RuntimeException
    {
		return (I_ISM_Department)MTable.get(getCtx(), I_ISM_Department.Table_Name)
			.getPO(getISM_Department_ID(), get_TrxName());	}

	/** Set Department.
		@param ISM_Department_ID Department	  */
	public void setISM_Department_ID (int ISM_Department_ID)
	{
		if (ISM_Department_ID < 1) 
			set_Value (COLUMNNAME_ISM_Department_ID, null);
		else 
			set_Value (COLUMNNAME_ISM_Department_ID, Integer.valueOf(ISM_Department_ID));
	}

	/** Get Department.
		@return Department	  */
	public int getISM_Department_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ISM_Department_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Version.
		@param Version 
		Version of the table definition
	  */
	public void setVersion (String Version)
	{
		set_Value (COLUMNNAME_Version, Version);
	}

	/** Get Version.
		@return Version of the table definition
	  */
	public String getVersion () 
	{
		return (String)get_Value(COLUMNNAME_Version);
	}
}