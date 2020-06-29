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

/** Generated Model for ISM_Budget_Transaction
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_ISM_Budget_Transaction extends PO implements I_ISM_Budget_Transaction, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200616L;

    /** Standard Constructor */
    public X_ISM_Budget_Transaction (Properties ctx, int ISM_Budget_Transaction_ID, String trxName)
    {
      super (ctx, ISM_Budget_Transaction_ID, trxName);
      /** if (ISM_Budget_Transaction_ID == 0)
        {
			setBudgetAmt (Env.ZERO);
			setDateInvoiced (new Timestamp( System.currentTimeMillis() ));
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
			setISM_Budget_Line_ID (0);
			setISM_Budget_Transaction_ID (0);
			setISM_Budget_Transaction_UU (null);
        } */
    }

    /** Load Constructor */
    public X_ISM_Budget_Transaction (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_ISM_Budget_Transaction[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Booked = BO */
	public static final String BUDGET_STATUS_Booked = "BO";
	/** Voided = VO */
	public static final String BUDGET_STATUS_Voided = "VO";
	/** Allocated = AL */
	public static final String BUDGET_STATUS_Allocated = "AL";
	/** Set Budget Status.
		@param Budget_Status Budget Status	  */
	public void setBudget_Status (String Budget_Status)
	{

		set_Value (COLUMNNAME_Budget_Status, Budget_Status);
	}

	/** Get Budget Status.
		@return Budget Status	  */
	public String getBudget_Status () 
	{
		return (String)get_Value(COLUMNNAME_Budget_Status);
	}

	/** Set Budget Amount.
		@param BudgetAmt Budget Amount	  */
	public void setBudgetAmt (BigDecimal BudgetAmt)
	{
		set_Value (COLUMNNAME_BudgetAmt, BudgetAmt);
	}

	/** Get Budget Amount.
		@return Budget Amount	  */
	public BigDecimal getBudgetAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BudgetAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_InvoiceLine)MTable.get(getCtx(), org.compiere.model.I_C_InvoiceLine.Table_Name)
			.getPO(getC_InvoiceLine_ID(), get_TrxName());	}

	/** Set Invoice Line.
		@param C_InvoiceLine_ID 
		Invoice Detail Line
	  */
	public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
	{
		if (C_InvoiceLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_InvoiceLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
	}

	/** Get Invoice Line.
		@return Invoice Detail Line
	  */
	public int getC_InvoiceLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_InvoiceLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException
    {
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_Name)
			.getPO(getC_Order_ID(), get_TrxName());	}

	/** Set Order.
		@param C_Order_ID 
		Order
	  */
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_OrderLine)MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_Name)
			.getPO(getC_OrderLine_ID(), get_TrxName());	}

	/** Set Sales Order Line.
		@param C_OrderLine_ID 
		Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID)
	{
		if (C_OrderLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
	}

	/** Get Sales Order Line.
		@return Sales Order Line
	  */
	public int getC_OrderLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date Invoiced.
		@param DateInvoiced 
		Date printed on Invoice
	  */
	public void setDateInvoiced (Timestamp DateInvoiced)
	{
		set_ValueNoCheck (COLUMNNAME_DateInvoiced, DateInvoiced);
	}

	/** Get Date Invoiced.
		@return Date printed on Invoice
	  */
	public Timestamp getDateInvoiced () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateInvoiced);
	}

	/** Set Date Ordered.
		@param DateOrdered 
		Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_ValueNoCheck (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	public I_ISM_Budget_Line getISM_Budget_Line() throws RuntimeException
    {
		return (I_ISM_Budget_Line)MTable.get(getCtx(), I_ISM_Budget_Line.Table_Name)
			.getPO(getISM_Budget_Line_ID(), get_TrxName());	}

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

	/** Set Budget Transaction.
		@param ISM_Budget_Transaction_ID Budget Transaction	  */
	public void setISM_Budget_Transaction_ID (int ISM_Budget_Transaction_ID)
	{
		if (ISM_Budget_Transaction_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ISM_Budget_Transaction_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ISM_Budget_Transaction_ID, Integer.valueOf(ISM_Budget_Transaction_ID));
	}

	/** Get Budget Transaction.
		@return Budget Transaction	  */
	public int getISM_Budget_Transaction_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ISM_Budget_Transaction_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ISM_Budget_Transaction_UU.
		@param ISM_Budget_Transaction_UU ISM_Budget_Transaction_UU	  */
	public void setISM_Budget_Transaction_UU (String ISM_Budget_Transaction_UU)
	{
		set_ValueNoCheck (COLUMNNAME_ISM_Budget_Transaction_UU, ISM_Budget_Transaction_UU);
	}

	/** Get ISM_Budget_Transaction_UU.
		@return ISM_Budget_Transaction_UU	  */
	public String getISM_Budget_Transaction_UU () 
	{
		return (String)get_Value(COLUMNNAME_ISM_Budget_Transaction_UU);
	}
}