package org.epi.model;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MOrder;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.Msg;
import org.compiere.util.Util;

public class MQuotation extends X_C_Quotation implements DocAction, DocOptions{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**	Process Message 			*/
	protected String		m_processMsg = null;
	
	public MQuotation(Properties ctx, int C_Quotation_ID, String trxName) {
		super(ctx, C_Quotation_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	
	public MQuotation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	//getLine
	protected X_C_QuotationLine[] 	m_lines = null;

	public X_C_QuotationLine[] getLines(){
			return getLines(false, null);
		}

	public X_C_QuotationLine[] getLines (boolean requery, String orderBy){
			
		if (m_lines != null && !requery) {
				set_TrxName(m_lines, get_TrxName());
				return m_lines;
				}
			//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_lines = getLines(null, orderClause);
		return m_lines;
	}	//	getLines
		
	public X_C_QuotationLine[] getLines (String whereClause, String orderClause){

		StringBuilder whereClauseFinal = new StringBuilder(X_C_QuotationLine.COLUMNNAME_C_Quotation_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = X_C_QuotationLine.COLUMNNAME_Line;
		//
		List<X_C_QuotationLine> list = new Query(getCtx(), I_C_QuotationLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
			
		return list.toArray(new X_C_QuotationLine[list.size()]);		
	}	//	getLines


	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,int AD_Table_ID, String[] docAction, String[] options, int index) {
		
		for(int i=0;i<options.length;i++){
			options[i]=null;
		}		
		index = 0;
		
		if(docStatus.equals(DocAction.STATUS_Drafted)){
			options[index++] = DocAction.ACTION_Complete;
			options[index++] = DocAction.ACTION_Void;
			
		}else if(docStatus.equals(DocAction.STATUS_Completed)){
			options[index++] = DocAction.ACTION_Close;
			options[index++] = DocAction.ACTION_ReActivate;
			options[index++] = DocAction.ACTION_Void;

		}else if(docStatus.equals(DocAction.STATUS_InProgress)){
			options[index++] = DocAction.ACTION_Complete;
			options[index++] = DocAction.ACTION_Void;
			
		}else if(docStatus.equals(DocAction.STATUS_Invalid)){
			options[index++] = DocAction.ACTION_Complete;
			options[index++] = DocAction.ACTION_Void;
			
		}
		
		
		return index;
		
	}


	@Override
	public boolean processIt(String action) throws Exception {
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (action, getDocAction());
	}


	@Override
	public boolean unlockIt() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String prepareIt() {

		setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}


	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String completeIt() {
		if (DOCACTION_Prepare.equals(getDocAction())|| DOCACTION_Re_Activate.equals(getDocAction())){
			setProcessed(false);
			return DocAction.STATUS_InProgress;
		}else if(DOCACTION_Void.equals(getDocAction())) {
			setProcessed(true);	
			setDocStatus(DOCSTATUS_Voided);
			setDocAction(DOCACTION_Void);
			return DocAction.STATUS_Voided;
		}
			

		X_C_QuotationLine[] lines = getLines();
		
		
		if(lines.length == 0) {
			m_processMsg = "Cant Complete Document Without Details";
			return DocAction.STATUS_Invalid;
		}
			
		
		for(X_C_QuotationLine line : lines) {
			
			line.setProcessed(true);
			line.saveEx();
			
		}
		
		setProcessed(true);	
		setDocAction(DOCACTION_Close);
		m_processMsg = "Document Completed";

		
		return DocAction.STATUS_Completed;
	}


	@Override
	public boolean voidIt() {
		if (DOCSTATUS_Drafted.equals(getDocStatus())
				|| DOCSTATUS_Invalid.equals(getDocStatus())
				|| DOCSTATUS_InProgress.equals(getDocStatus())
				|| DOCSTATUS_Approved.equals(getDocStatus())
				|| DOCSTATUS_NotApproved.equals(getDocStatus())
				|| DOCSTATUS_Completed.equals(getDocStatus())){
		
		
		
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
			if (m_processMsg != null)
				return false;
				
			setDocStatus(DOCSTATUS_Voided); // need to set & save docstatus to be able to check it in MInOutConfirm.voidIt()
			saveEx();
		
		}else if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus())){
			
			
				m_processMsg = "Document Closed: " + getDocStatus();
				return false;
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		
		MOrder ord  = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
		
		if(ord.getDocStatus().equals(MOrder.DOCSTATUS_Completed)) {
			
			m_processMsg = "Cant Void Quotation - Sales Order Related Status Complete";
			return false;

		}
		
		setDescription(Msg.getMsg(getCtx(), "Voided"));
		setProcessed(true);
		setDocStatus(DOCSTATUS_Voided);
		setDocAction(DOCACTION_None);
		
		X_C_QuotationLine[] lines = getLines();
		
		for(X_C_QuotationLine line : lines) {
			
		line.setProcessed(true);
		line.saveEx();
			
		}
		
		m_processMsg = "Document Voided";

		return true;
	}


	@Override
	public boolean closeIt() {
	X_C_QuotationLine[] lines = getLines();
		
		for(X_C_QuotationLine line : lines) {
			
			line.setProcessed(false);
			line.saveEx();
			
		}
		
		setDocAction(DOCACTION_Close);
		m_processMsg = "Document Closed";
		return true;
	}


	@Override
	public boolean reverseCorrectIt() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean reverseAccrualIt() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean reActivateIt() {
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;
		
	
		setProcessed(false);
		setDocStatus(DOCSTATUS_Drafted);
		setDocAction(DOCACTION_None);
		
		X_C_QuotationLine[] lines = getLines();
		
		for(X_C_QuotationLine line : lines) {
			
			line.setProcessed(false);
			line.saveEx();
			
		}
		m_processMsg = "Document Re-Activated- Document Status become InProgress";

		return true;
	}


	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getDocumentInfo() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public File createPDF() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getProcessMsg() {
		return m_processMsg;
	}


	@Override
	public int getDoc_User_ID() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getDocAction() {	
		String docAct = (String)get_Value(COLUMNNAME_DocAction);	
		return docAct;
	}
	
	

}
