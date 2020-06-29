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

/** Generated Model for ISM_Department
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_ISM_Department extends PO implements I_ISM_Department, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200511L;

    /** Standard Constructor */
    public X_ISM_Department (Properties ctx, int ISM_Department_ID, String trxName)
    {
      super (ctx, ISM_Department_ID, trxName);
      /** if (ISM_Department_ID == 0)
        {
			setISM_Department_ID (0);
			setISM_Department_UU (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_ISM_Department (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_ISM_Department[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Department.
		@param ISM_Department_ID Department	  */
	public void setISM_Department_ID (int ISM_Department_ID)
	{
		if (ISM_Department_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ISM_Department_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ISM_Department_ID, Integer.valueOf(ISM_Department_ID));
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

	/** Set ISM_Department_UU.
		@param ISM_Department_UU ISM_Department_UU	  */
	public void setISM_Department_UU (String ISM_Department_UU)
	{
		set_ValueNoCheck (COLUMNNAME_ISM_Department_UU, ISM_Department_UU);
	}

	/** Get ISM_Department_UU.
		@return ISM_Department_UU	  */
	public String getISM_Department_UU () 
	{
		return (String)get_Value(COLUMNNAME_ISM_Department_UU);
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
}