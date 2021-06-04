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

/** Generated Model for C_Invoice_OutStandingLine
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_C_Invoice_OutStandingLine extends PO implements I_C_Invoice_OutStandingLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210324L;

    /** Standard Constructor */
    public X_C_Invoice_OutStandingLine (Properties ctx, int C_Invoice_OutStandingLine_ID, String trxName)
    {
      super (ctx, C_Invoice_OutStandingLine_ID, trxName);
      /** if (C_Invoice_OutStandingLine_ID == 0)
        {
			setC_Invoice_OutStanding_ID (0);
			setC_Invoice_OutStandingLine_ID (0);
			setLine (0);
			setPriceEntered (Env.ZERO);
			setQtyEntered (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_C_Invoice_OutStandingLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_C_Invoice_OutStandingLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getC_Charge_ID(), get_TrxName());	}

	/** Set Charge.
		@param C_Charge_ID 
		Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Invoice_OutStanding getC_Invoice_OutStanding() throws RuntimeException
    {
		return (I_C_Invoice_OutStanding)MTable.get(getCtx(), I_C_Invoice_OutStanding.Table_Name)
			.getPO(getC_Invoice_OutStanding_ID(), get_TrxName());	}

	/** Set Invoice Outstanding.
		@param C_Invoice_OutStanding_ID Invoice Outstanding	  */
	public void setC_Invoice_OutStanding_ID (int C_Invoice_OutStanding_ID)
	{
		if (C_Invoice_OutStanding_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_OutStanding_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_OutStanding_ID, Integer.valueOf(C_Invoice_OutStanding_ID));
	}

	/** Get Invoice Outstanding.
		@return Invoice Outstanding	  */
	public int getC_Invoice_OutStanding_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_OutStanding_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Invoice Outstanding Line.
		@param C_Invoice_OutStandingLine_ID Invoice Outstanding Line	  */
	public void setC_Invoice_OutStandingLine_ID (int C_Invoice_OutStandingLine_ID)
	{
		if (C_Invoice_OutStandingLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_OutStandingLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_OutStandingLine_ID, Integer.valueOf(C_Invoice_OutStandingLine_ID));
	}

	/** Get Invoice Outstanding Line.
		@return Invoice Outstanding Line	  */
	public int getC_Invoice_OutStandingLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_OutStandingLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_Invoice_OutStandingLine_UU.
		@param C_Invoice_OutStandingLine_UU C_Invoice_OutStandingLine_UU	  */
	public void setC_Invoice_OutStandingLine_UU (String C_Invoice_OutStandingLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_C_Invoice_OutStandingLine_UU, C_Invoice_OutStandingLine_UU);
	}

	/** Get C_Invoice_OutStandingLine_UU.
		@return C_Invoice_OutStandingLine_UU	  */
	public String getC_Invoice_OutStandingLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_C_Invoice_OutStandingLine_UU);
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

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Price.
		@param PriceEntered 
		Price Entered - the price based on the selected/base UoM
	  */
	public void setPriceEntered (BigDecimal PriceEntered)
	{
		set_ValueNoCheck (COLUMNNAME_PriceEntered, PriceEntered);
	}

	/** Get Price.
		@return Price Entered - the price based on the selected/base UoM
	  */
	public BigDecimal getPriceEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity.
		@param QtyEntered 
		The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_ValueNoCheck (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}