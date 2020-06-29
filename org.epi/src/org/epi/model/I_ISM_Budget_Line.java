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

/** Generated Interface for ISM_Budget_Line
 *  @author iDempiere (generated) 
 *  @version Release 6.2
 */
@SuppressWarnings("all")
public interface I_ISM_Budget_Line 
{

    /** TableName=ISM_Budget_Line */
    public static final String Table_Name = "ISM_Budget_Line";

    /** AD_Table_ID=1000019 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

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

    /** Column name Apr */
    public static final String COLUMNNAME_Apr = "Apr";

	/** Set Apr	  */
	public void setApr (BigDecimal Apr);

	/** Get Apr	  */
	public BigDecimal getApr();

    /** Column name Aug */
    public static final String COLUMNNAME_Aug = "Aug";

	/** Set Aug	  */
	public void setAug (BigDecimal Aug);

	/** Get Aug	  */
	public BigDecimal getAug();

    /** Column name Budget_Acct */
    public static final String COLUMNNAME_Budget_Acct = "Budget_Acct";

	/** Set Budget Acct	  */
	public void setBudget_Acct (int Budget_Acct);

	/** Get Budget Acct	  */
	public int getBudget_Acct();

	public I_C_ValidCombination getBudget_A() throws RuntimeException;

    /** Column name Budget_Code */
    public static final String COLUMNNAME_Budget_Code = "Budget_Code";

	/** Set Budget Code	  */
	public void setBudget_Code (String Budget_Code);

	/** Get Budget Code	  */
	public String getBudget_Code();

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

    /** Column name Dec */
    public static final String COLUMNNAME_Dec = "Dec";

	/** Set Dec	  */
	public void setDec (BigDecimal Dec);

	/** Get Dec	  */
	public BigDecimal getDec();

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

    /** Column name Feb */
    public static final String COLUMNNAME_Feb = "Feb";

	/** Set Feb	  */
	public void setFeb (BigDecimal Feb);

	/** Get Feb	  */
	public BigDecimal getFeb();

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

    /** Column name ISM_Activity_ID */
    public static final String COLUMNNAME_ISM_Activity_ID = "ISM_Activity_ID";

	/** Set Activity	  */
	public void setISM_Activity_ID (int ISM_Activity_ID);

	/** Get Activity	  */
	public int getISM_Activity_ID();

	public I_ISM_Activity getISM_Activity() throws RuntimeException;

    /** Column name ISM_Budget_Line_ID */
    public static final String COLUMNNAME_ISM_Budget_Line_ID = "ISM_Budget_Line_ID";

	/** Set Budget Line	  */
	public void setISM_Budget_Line_ID (int ISM_Budget_Line_ID);

	/** Get Budget Line	  */
	public int getISM_Budget_Line_ID();

    /** Column name ISM_Budget_Line_UU */
    public static final String COLUMNNAME_ISM_Budget_Line_UU = "ISM_Budget_Line_UU";

	/** Set ISM_Budget_Line_UU	  */
	public void setISM_Budget_Line_UU (String ISM_Budget_Line_UU);

	/** Get ISM_Budget_Line_UU	  */
	public String getISM_Budget_Line_UU();

    /** Column name ISM_Budget_Planning_ID */
    public static final String COLUMNNAME_ISM_Budget_Planning_ID = "ISM_Budget_Planning_ID";

	/** Set Budget Planning	  */
	public void setISM_Budget_Planning_ID (int ISM_Budget_Planning_ID);

	/** Get Budget Planning	  */
	public int getISM_Budget_Planning_ID();

	public I_ISM_Budget_Planning getISM_Budget_Planning() throws RuntimeException;

    /** Column name Jan */
    public static final String COLUMNNAME_Jan = "Jan";

	/** Set Jan	  */
	public void setJan (BigDecimal Jan);

	/** Get Jan	  */
	public BigDecimal getJan();

    /** Column name Jul */
    public static final String COLUMNNAME_Jul = "Jul";

	/** Set Jul	  */
	public void setJul (BigDecimal Jul);

	/** Get Jul	  */
	public BigDecimal getJul();

    /** Column name Jun */
    public static final String COLUMNNAME_Jun = "Jun";

	/** Set Jun	  */
	public void setJun (BigDecimal Jun);

	/** Get Jun	  */
	public BigDecimal getJun();

    /** Column name Mar */
    public static final String COLUMNNAME_Mar = "Mar";

	/** Set Mar	  */
	public void setMar (BigDecimal Mar);

	/** Get Mar	  */
	public BigDecimal getMar();

    /** Column name May */
    public static final String COLUMNNAME_May = "May";

	/** Set May	  */
	public void setMay (BigDecimal May);

	/** Get May	  */
	public BigDecimal getMay();

    /** Column name Nov */
    public static final String COLUMNNAME_Nov = "Nov";

	/** Set Nov	  */
	public void setNov (BigDecimal Nov);

	/** Get Nov	  */
	public BigDecimal getNov();

    /** Column name Oct */
    public static final String COLUMNNAME_Oct = "Oct";

	/** Set Oct	  */
	public void setOct (BigDecimal Oct);

	/** Get Oct	  */
	public BigDecimal getOct();

    /** Column name Sep */
    public static final String COLUMNNAME_Sep = "Sep";

	/** Set Sep	  */
	public void setSep (BigDecimal Sep);

	/** Get Sep	  */
	public BigDecimal getSep();

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
