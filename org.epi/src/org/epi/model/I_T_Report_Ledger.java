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

/** Generated Interface for T_Report_Ledger
 *  @author iDempiere (generated) 
 *  @version Release 6.2
 */
@SuppressWarnings("all")
public interface I_T_Report_Ledger 
{

    /** TableName=T_Report_Ledger */
    public static final String Table_Name = "T_Report_Ledger";

    /** AD_Table_ID=1000029 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name Account_ID */
    public static final String COLUMNNAME_Account_ID = "Account_ID";

	/** Set Account.
	  * Account used
	  */
	public void setAccount_ID (int Account_ID);

	/** Get Account.
	  * Account used
	  */
	public int getAccount_ID();

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException;

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

    /** Column name AD_PInstance_ID */
    public static final String COLUMNNAME_AD_PInstance_ID = "AD_PInstance_ID";

	/** Set Process Instance.
	  * Instance of the process
	  */
	public void setAD_PInstance_ID (int AD_PInstance_ID);

	/** Get Process Instance.
	  * Instance of the process
	  */
	public int getAD_PInstance_ID();

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException;

    /** Column name AmtAcctCr */
    public static final String COLUMNNAME_AmtAcctCr = "AmtAcctCr";

	/** Set Accounted Credit.
	  * Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr);

	/** Get Accounted Credit.
	  * Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr();

    /** Column name AmtAcctDr */
    public static final String COLUMNNAME_AmtAcctDr = "AmtAcctDr";

	/** Set Accounted Debit.
	  * Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr);

	/** Get Accounted Debit.
	  * Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr();

    /** Column name Balance */
    public static final String COLUMNNAME_Balance = "Balance";

	/** Set Balance	  */
	public void setBalance (BigDecimal Balance);

	/** Get Balance	  */
	public BigDecimal getBalance();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

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

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name ElementName */
    public static final String COLUMNNAME_ElementName = "ElementName";

	/** Set Element Name.
	  * Name of the Element
	  */
	public void setElementName (String ElementName);

	/** Get Element Name.
	  * Name of the Element
	  */
	public String getElementName();

    /** Column name org_name */
    public static final String COLUMNNAME_org_name = "org_name";

	/** Set org_name	  */
	public void setorg_name (String org_name);

	/** Get org_name	  */
	public String getorg_name();

    /** Column name T_Report_Ledger_ID */
    public static final String COLUMNNAME_T_Report_Ledger_ID = "T_Report_Ledger_ID";

	/** Set Report Ledger	  */
	public void setT_Report_Ledger_ID (int T_Report_Ledger_ID);

	/** Get Report Ledger	  */
	public int getT_Report_Ledger_ID();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();
}
