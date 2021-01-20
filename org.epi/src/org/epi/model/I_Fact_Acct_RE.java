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
package org.epi.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for Fact_Acct_RE
 *  @author iDempiere (generated) 
 *  @version Release 6.2
 */
@SuppressWarnings("all")
public interface I_Fact_Acct_RE 
{

    /** TableName=Fact_Acct_RE */
    public static final String Table_Name = "Fact_Acct_RE";

    /** AD_Table_ID=1000032 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AccountFrom_ID */
    public static final String COLUMNNAME_AccountFrom_ID = "AccountFrom_ID";

	/** Set Account From	  */
	public void setAccountFrom_ID (int AccountFrom_ID);

	/** Get Account From	  */
	public int getAccountFrom_ID();

	public org.compiere.model.I_C_ElementValue getAccountFrom() throws RuntimeException;

    /** Column name AccountTo_ID */
    public static final String COLUMNNAME_AccountTo_ID = "AccountTo_ID";

	/** Set Account To	  */
	public void setAccountTo_ID (int AccountTo_ID);

	/** Get Account To	  */
	public int getAccountTo_ID();

	public org.compiere.model.I_C_ElementValue getAccountTo() throws RuntimeException;

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name Fact_Acct_RE_ID */
    public static final String COLUMNNAME_Fact_Acct_RE_ID = "Fact_Acct_RE_ID";

	/** Set Retained Earning	  */
	public void setFact_Acct_RE_ID (int Fact_Acct_RE_ID);

	/** Get Retained Earning	  */
	public int getFact_Acct_RE_ID();

    /** Column name Fact_Acct_RE_UU */
    public static final String COLUMNNAME_Fact_Acct_RE_UU = "Fact_Acct_RE_UU";

	/** Set Fact_Acct_RE_UU	  */
	public void setFact_Acct_RE_UU (String Fact_Acct_RE_UU);

	/** Get Fact_Acct_RE_UU	  */
	public String getFact_Acct_RE_UU();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsMonthly */
    public static final String COLUMNNAME_IsMonthly = "IsMonthly";

	/** Set Monthly Calculation Period	  */
	public void setIsMonthly (boolean IsMonthly);

	/** Get Monthly Calculation Period	  */
	public boolean isMonthly();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Rg_AccountPost_Acct */
    public static final String COLUMNNAME_Rg_AccountPost_Acct = "Rg_AccountPost_Acct";

	/** Set Account Post	  */
	public void setRg_AccountPost_Acct (int Rg_AccountPost_Acct);

	/** Get Account Post	  */
	public int getRg_AccountPost_Acct();

	public I_C_ValidCombination getRg_AccountPost_A() throws RuntimeException;

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();
}
