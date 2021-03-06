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

/** Generated Model for TBU_OperationEquipmentUnit
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_TBU_OperationEquipmentUnit extends PO implements I_TBU_OperationEquipmentUnit, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210115L;

    /** Standard Constructor */
    public X_TBU_OperationEquipmentUnit (Properties ctx, int TBU_OperationEquipmentUnit_ID, String trxName)
    {
      super (ctx, TBU_OperationEquipmentUnit_ID, trxName);
      /** if (TBU_OperationEquipmentUnit_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_TBU_OperationEquipmentUnit (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_TBU_OperationEquipmentUnit[")
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

	public I_TBU_OperationEquipment getTBU_OperationEquipment() throws RuntimeException
    {
		return (I_TBU_OperationEquipment)MTable.get(getCtx(), I_TBU_OperationEquipment.Table_Name)
			.getPO(getTBU_OperationEquipment_ID(), get_TrxName());	}

	/** Set Operation Equipment.
		@param TBU_OperationEquipment_ID Operation Equipment	  */
	public void setTBU_OperationEquipment_ID (int TBU_OperationEquipment_ID)
	{
		if (TBU_OperationEquipment_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationEquipment_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationEquipment_ID, Integer.valueOf(TBU_OperationEquipment_ID));
	}

	/** Get Operation Equipment.
		@return Operation Equipment	  */
	public int getTBU_OperationEquipment_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TBU_OperationEquipment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Unit.
		@param TBU_OperationEquipmentUnit_ID Unit	  */
	public void setTBU_OperationEquipmentUnit_ID (int TBU_OperationEquipmentUnit_ID)
	{
		if (TBU_OperationEquipmentUnit_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationEquipmentUnit_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationEquipmentUnit_ID, Integer.valueOf(TBU_OperationEquipmentUnit_ID));
	}

	/** Get Unit.
		@return Unit	  */
	public int getTBU_OperationEquipmentUnit_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TBU_OperationEquipmentUnit_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Unit.
		@param TBU_OperationEquipmentUnit_UU Unit	  */
	public void setTBU_OperationEquipmentUnit_UU (String TBU_OperationEquipmentUnit_UU)
	{
		set_ValueNoCheck (COLUMNNAME_TBU_OperationEquipmentUnit_UU, TBU_OperationEquipmentUnit_UU);
	}

	/** Get Unit.
		@return Unit	  */
	public String getTBU_OperationEquipmentUnit_UU () 
	{
		return (String)get_Value(COLUMNNAME_TBU_OperationEquipmentUnit_UU);
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