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

/** Generated Model for TBU_OperationLine
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_TBU_OperationLine extends PO implements I_TBU_OperationLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210115L;

    /** Standard Constructor */
    public X_TBU_OperationLine (Properties ctx, int TBU_OperationLine_ID, String trxName)
    {
      super (ctx, TBU_OperationLine_ID, trxName);
      /** if (TBU_OperationLine_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_TBU_OperationLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_TBU_OperationLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
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

	/** Set Distance.
		@param Distance Distance	  */
	public void setDistance (BigDecimal Distance)
	{
		set_Value (COLUMNNAME_Distance, Distance);
	}

	/** Get Distance.
		@return Distance	  */
	public BigDecimal getDistance () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Distance);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Fuel Consumtion Max.
		@param FuelConsumtionMax Fuel Consumtion Max	  */
	public void setFuelConsumtionMax (BigDecimal FuelConsumtionMax)
	{
		set_Value (COLUMNNAME_FuelConsumtionMax, FuelConsumtionMax);
	}

	/** Get Fuel Consumtion Max.
		@return Fuel Consumtion Max	  */
	public BigDecimal getFuelConsumtionMax () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FuelConsumtionMax);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Line Amount.
		@param LineNetAmt 
		Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public void setLineNetAmt (BigDecimal LineNetAmt)
	{
		set_ValueNoCheck (COLUMNNAME_LineNetAmt, LineNetAmt);
	}

	/** Get Line Amount.
		@return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public BigDecimal getLineNetAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LineNetAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Line.
		@param LineNo 
		Line No
	  */
	public void setLineNo (int LineNo)
	{
		set_Value (COLUMNNAME_LineNo, Integer.valueOf(LineNo));
	}

	/** Get Line.
		@return Line No
	  */
	public int getLineNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LineNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Price.
		@param Price 
		Price
	  */
	public void setPrice (BigDecimal Price)
	{
		set_Value (COLUMNNAME_Price, Price);
	}

	/** Get Price.
		@return Price
	  */
	public BigDecimal getPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Price);
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

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_TBU_BAOperation getTBU_BAOperation() throws RuntimeException
    {
		return (I_TBU_BAOperation)MTable.get(getCtx(), I_TBU_BAOperation.Table_Name)
			.getPO(getTBU_BAOperation_ID(), get_TrxName());	}

	/** Set BA Operation.
		@param TBU_BAOperation_ID BA Operation	  */
	public void setTBU_BAOperation_ID (int TBU_BAOperation_ID)
	{
		if (TBU_BAOperation_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_TBU_BAOperation_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_TBU_BAOperation_ID, Integer.valueOf(TBU_BAOperation_ID));
	}

	/** Get BA Operation.
		@return BA Operation	  */
	public int getTBU_BAOperation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TBU_BAOperation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public I_TBU_OperationEquipmentUnit getTBU_OperationEquipmentUnit() throws RuntimeException
    {
		return (I_TBU_OperationEquipmentUnit)MTable.get(getCtx(), I_TBU_OperationEquipmentUnit.Table_Name)
			.getPO(getTBU_OperationEquipmentUnit_ID(), get_TrxName());	}

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

	/** Set Operation Line.
		@param TBU_OperationLine_ID Operation Line	  */
	public void setTBU_OperationLine_ID (int TBU_OperationLine_ID)
	{
		if (TBU_OperationLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationLine_ID, Integer.valueOf(TBU_OperationLine_ID));
	}

	/** Get Operation Line.
		@return Operation Line	  */
	public int getTBU_OperationLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TBU_OperationLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Operation Line.
		@param TBU_OperationLine_UU Operation Line	  */
	public void setTBU_OperationLine_UU (String TBU_OperationLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_TBU_OperationLine_UU, TBU_OperationLine_UU);
	}

	/** Get Operation Line.
		@return Operation Line	  */
	public String getTBU_OperationLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_TBU_OperationLine_UU);
	}

	public I_TBU_OperationService getTBU_OperationService() throws RuntimeException
    {
		return (I_TBU_OperationService)MTable.get(getCtx(), I_TBU_OperationService.Table_Name)
			.getPO(getTBU_OperationService_ID(), get_TrxName());	}

	/** Set Operation Service.
		@param TBU_OperationService_ID Operation Service	  */
	public void setTBU_OperationService_ID (int TBU_OperationService_ID)
	{
		if (TBU_OperationService_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationService_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_TBU_OperationService_ID, Integer.valueOf(TBU_OperationService_ID));
	}

	/** Get Operation Service.
		@return Operation Service	  */
	public int getTBU_OperationService_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TBU_OperationService_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}