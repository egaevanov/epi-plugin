package org.epi.validator;

import java.math.BigDecimal;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.epi.model.MQuotation;
import org.epi.model.X_C_QuotationLine;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class ISMQuotationLineValidator {
	
	public static CLogger log = CLogger.getCLogger(ISMQuotationLineValidator.class);

	public static String executeISMQuotationLine(Event event, PO po) {
		
		String msgQuotLine= "";
		X_C_QuotationLine OpLine = (X_C_QuotationLine) po;
		
		MOrg org = new MOrg(OpLine.getCtx(), OpLine.getAD_Org_ID(), null);
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
			if (event.getTopic().equals(IEventTopics.PO_AFTER_NEW) ||event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE) ) {
				msgQuotLine = beforeSaveQuotationISM(OpLine,event);	
			}
		}
	return msgQuotLine;

	}
	
	
	private static String beforeSaveQuotationISM(X_C_QuotationLine QuotLine,Event event) {
		MQuotation quot = new MQuotation(QuotLine.getCtx(), QuotLine.getC_Quotation_ID(), QuotLine.get_TrxName());
		
		X_C_QuotationLine[] Lines = quot.getLines();
		BigDecimal grandTotal = Env.ZERO;
		for(X_C_QuotationLine Line : Lines) {	
			grandTotal = grandTotal.add(Line.getLineNetAmt());	
		}
		quot.setTotalLines(grandTotal);
		quot.setGrandTotal(grandTotal);
		quot.saveEx();

		
		return"";
	}
	

}
