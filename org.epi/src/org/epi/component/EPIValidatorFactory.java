package org.epi.component;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_ElementValue;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_Payment;
import org.compiere.model.I_GL_Journal;
import org.compiere.model.I_M_InOut;
import org.compiere.model.PO;
import org.compiere.model.X_C_ElementValue;
import org.compiere.model.X_C_Invoice;
import org.compiere.model.X_C_InvoiceLine;
import org.compiere.model.X_C_Order;
import org.compiere.model.X_C_OrderLine;
import org.compiere.model.X_C_Payment;
import org.compiere.model.X_GL_Journal;
import org.compiere.model.X_M_InOut;
import org.compiere.util.CLogger;
import org.epi.model.I_C_OrderlineDtl;
import org.epi.model.I_C_Quotation;
import org.epi.model.I_C_QuotationLine;
import org.epi.model.I_TBU_BAOperation;
import org.epi.model.I_TBU_OperationLine;
import org.epi.model.X_C_OrderlineDtl;
import org.epi.model.X_C_Quotation;
import org.epi.model.X_C_QuotationLine;
import org.epi.model.X_TBU_BAOperation;
import org.epi.model.X_TBU_OperationLine;
import org.epi.validator.EPIElementValueValidator;
import org.epi.validator.EPIGLJournalValidator;
import org.epi.validator.EPIInOutValidator;
import org.epi.validator.EPIInvoiceLineValidator;
import org.epi.validator.EPIInvoiceValidator;
import org.epi.validator.EPIOrdLineDtlValidator;
import org.epi.validator.EPIOrderLineValidator;
import org.epi.validator.EPIOrderValidator;
import org.epi.validator.EPIPaymentValidator;
import org.epi.validator.ISMQuotationLineValidator;
import org.epi.validator.TBUBAOperationLineValidator;
import org.epi.validator.TBUBAOperationValidator;
import org.osgi.service.event.Event;

/**
 * 
 * @author Tegar N
 *
 */

public class EPIValidatorFactory extends AbstractEventHandler{

	private CLogger log = CLogger.getCLogger(EPIValidatorFactory.class);

	
	@Override
	protected void doHandleEvent(Event event) {
		
		log.info("ISM EVENT MANAGER // INITIALIZED");
		String msg = "";
		
		if (event.getTopic().equals(IEventTopics.AFTER_LOGIN)) {
			/*
			LoginEventData eventData = getEventData(event);
			log.info(" topic="+event.getTopic()+" AD_Client_ID="+eventData.getAD_Client_ID()
					+" AD_Org_ID="+eventData.getAD_Org_ID()+" AD_Role_ID="+eventData.getAD_Role_ID()
					+" AD_User_ID="0+eventData.getAD_User_ID());
			 */
		} 

		else  {
			
			if (getPO(event).get_TableName().equals(I_M_InOut.Table_Name)) {
				msg = EPIInOutValidator.executeCreateInternalUse(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_C_OrderlineDtl.Table_Name)) {
				msg = EPIOrdLineDtlValidator.executeOrdLineDtl(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_C_Order.Table_Name)) {
				msg = EPIOrderValidator.executeOrder(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_C_Invoice.Table_Name)) {
				msg = EPIInvoiceValidator.executeInvoice(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_C_InvoiceLine.Table_Name)) {
				msg = EPIInvoiceLineValidator.executeInvoiceLine(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_C_OrderLine.Table_Name)) {
				msg = EPIOrderLineValidator.executeOrderLine(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_C_Payment.Table_Name)) {
				msg = EPIPaymentValidator.executePayment(event, getPO(event));
			}else if(getPO(event).get_TableName().equals(I_GL_Journal.Table_Name)) {
				msg = EPIGLJournalValidator.executeJournal(event, getPO(event));		
			}else if(getPO(event).get_TableName().equals(I_C_ElementValue.Table_Name)) {
				msg = EPIElementValueValidator.executeElementValue(event, getPO(event));			
			}else if(getPO(event).get_TableName().equals(I_TBU_OperationLine.Table_Name)) {
				msg = TBUBAOperationLineValidator.executeTBUOpLine(event, getPO(event));			
			}else if(getPO(event).get_TableName().equals(I_TBU_BAOperation.Table_Name)) {
				msg = TBUBAOperationValidator.executeTBUBAOperation(event, getPO(event));			
			}else if(getPO(event).get_TableName().equals(I_C_QuotationLine.Table_Name)) {
				msg = ISMQuotationLineValidator.executeISMQuotationLine(event, getPO(event));			
			}

			logEvent(event, getPO(event), msg);

		}

	}
	@Override
	protected void initialize() {
		
		registerEvent(IEventTopics.AFTER_LOGIN);
		
		//MInOutLine
		registerTableEvent(IEventTopics.DOC_BEFORE_COMPLETE, X_M_InOut.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_REVERSECORRECT, X_M_InOut.Table_Name);
		registerTableEvent(IEventTopics.DOC_AFTER_COMPLETE, X_M_InOut.Table_Name);
		
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_OrderlineDtl.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_OrderlineDtl.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_DELETE, X_C_OrderlineDtl.Table_Name);
		
		registerTableEvent(IEventTopics.DOC_BEFORE_COMPLETE, X_C_Order.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_CLOSE, X_C_Order.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_VOID, X_C_Order.Table_Name);
			
		registerTableEvent(IEventTopics.DOC_AFTER_COMPLETE, X_C_Invoice.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_REVERSECORRECT, X_C_Invoice.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_REVERSEACCRUAL, X_C_Invoice.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_VOID, X_C_Invoice.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_COMPLETE, X_C_Invoice.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_Invoice.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_Invoice.Table_Name);

		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_InvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_InvoiceLine.Table_Name);
		
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_OrderLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_OrderLine.Table_Name);
		
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_Payment.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_Payment.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, X_C_Payment.Table_Name);

		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_GL_Journal.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_GL_Journal.Table_Name);

		registerTableEvent(IEventTopics.DOC_BEFORE_COMPLETE, X_GL_Journal.Table_Name);
		
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_ElementValue.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_ElementValue.Table_Name);
		
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_TBU_OperationLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_TBU_OperationLine.Table_Name);
			
		registerTableEvent(IEventTopics.DOC_BEFORE_REACTIVATE, X_TBU_BAOperation.Table_Name);
		registerTableEvent(IEventTopics.DOC_BEFORE_VOID, X_TBU_BAOperation.Table_Name);

		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_QuotationLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_QuotationLine.Table_Name);
		
	}
	
	private void logEvent (Event event, PO po, String msg) {
		log.fine("EVENT MANAGER // "+event.getTopic()+" po="+po+" MESSAGE ="+msg);
		if (msg.length()  > 0) 
			throw new AdempiereException(msg);	
	}
	

}
