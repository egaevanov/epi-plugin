package org.epi.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrg;
import org.compiere.util.Env;
import org.epi.utils.FinalVariableGlobal;

public class CallOutInvoice extends CalloutEngine implements IColumnCallout  {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID == null) {
			AD_Org_ID = 0;
		}
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {

			if(mField.getColumnName().equals("C_InvoiceDP_ID")){
				
				return InvoiceDPChangeEPI(ctx, WindowNo, mTab, mField, value);
			}
		
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
			
			/*
			 * TODO
			 */		
		}
		
		return "";
	}

	
public String InvoiceDPChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer C_InvoiveDP_ID = (Integer)mTab.getValue("C_InvoiceDP_ID");
		
		if(C_InvoiveDP_ID == null) {
			C_InvoiveDP_ID = 0;
		}
		
		
		if(C_InvoiveDP_ID <= 0 ) {
			
			mTab.setValue(MInvoice.COLUMNNAME_C_BPartner_ID, null);
			mTab.setValue(MInvoice.COLUMNNAME_C_BPartner_Location_ID,null);
			mTab.setValue("C_BankAccount_ID", null);
			mTab.setValue(MInvoice.COLUMNNAME_M_PriceList_ID, null);
			mTab.setValue(MInvoice.COLUMNNAME_POReference, null);
			mTab.setValue(MInvoice.COLUMNNAME_SalesRep_ID, null);
			mTab.setValue(MInvoice.COLUMNNAME_Description, null);

			return"";
		}
		
		
		MInvoice invDP = new MInvoice(ctx, C_InvoiveDP_ID, null);
		MDocType docType = new MDocType(ctx, invDP.getC_DocType_ID(), null);
		
		if(!docType.getDescription().equals("ARIDP")) {
			
			return "Tipe Dokumen Invoice First Payment Yang Di Input Tidak Sesuai";
		}
		
		
		mTab.setValue(MInvoice.COLUMNNAME_AD_Org_ID, invDP.getAD_Org_ID());
		mTab.setValue(MInvoice.COLUMNNAME_C_BPartner_ID, invDP.getC_BPartner_ID());
		mTab.setValue(MInvoice.COLUMNNAME_C_BPartner_Location_ID, invDP.getC_BPartner_Location_ID());
		mTab.setValue("C_BankAccount_ID", invDP.get_Value("C_BankAccount_ID"));
		mTab.setValue(MInvoice.COLUMNNAME_M_PriceList_ID, invDP.getM_PriceList_ID());
		mTab.setValue(MInvoice.COLUMNNAME_POReference, invDP.getPOReference());
		mTab.setValue(MInvoice.COLUMNNAME_SalesRep_ID, invDP.getSalesRep_ID());
		mTab.setValue(MInvoice.COLUMNNAME_Description, "Invoice First Payment : "+ invDP.getDocumentNo());

	
	return"";
	}

}
