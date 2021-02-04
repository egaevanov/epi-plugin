package org.epi.model;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.Msg;
import org.compiere.util.Util;

public class MBAOperation extends X_TBU_BAOperation implements DocAction, DocOptions{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	
	
	
	
	/**	Process Message 			*/
	protected String		m_processMsg = null;
	
	public MBAOperation(Properties ctx, int TBU_BAOperation_ID, String trxName) {
		super(ctx, TBU_BAOperation_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBAOperation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	public boolean processIt(String action) throws Exception {
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (action, getDocAction());
	}


	
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
		
		
		setProcessed(true);	
		setDocAction(DOCACTION_Close);
		
		
		X_TBU_OperationLine[] lines = getLines();
		
		for(X_TBU_OperationLine line : lines) {
			
			line.setProcessed(true);
			line.saveEx();
			
		}
		
		
		return DocAction.STATUS_Completed;
	}

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

		setDescription(Msg.getMsg(getCtx(), "Voided"));
		setProcessed(true);
		setDocStatus(DOCSTATUS_Voided);
		setDocAction(DOCACTION_None);
		
		X_TBU_OperationLine[] lines = getLines();
		
		for(X_TBU_OperationLine line : lines) {
			
		line.setProcessed(true);
		line.saveEx();
			
		}
				
		return true;
	}

	@Override
	public boolean closeIt() {
		
		X_TBU_OperationLine[] lines = getLines();
		
		for(X_TBU_OperationLine line : lines) {
			
			line.setProcessed(false);
			line.saveEx();
			
		}
		
		setDocAction(DOCACTION_Close);
		
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
		setDocAction(DOCACTION_None);
		
		X_TBU_OperationLine[] lines = getLines();
		
		for(X_TBU_OperationLine line : lines) {
			
			line.setProcessed(false);
			line.saveEx();
			
		}
		
		return true;
	}

	@Override
	public String getSummary() {
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
	public int getC_Currency_ID() {
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

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {
		
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
			
		}
		
		return index;
		
	}

	public boolean unlockIt() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	
	//getLine
	protected X_TBU_OperationLine[] 	m_lines = null;

	public X_TBU_OperationLine[] getLines(){
		return getLines(false, null);
	}

	public X_TBU_OperationLine[] getLines (boolean requery, String orderBy)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "LineNo";
		m_lines = getLines(null, orderClause);
		return m_lines;
	}	//	getLines
	
	public X_TBU_OperationLine[] getLines (String whereClause, String orderClause)
	{

		StringBuilder whereClauseFinal = new StringBuilder(X_TBU_OperationLine.COLUMNNAME_TBU_BAOperation_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = X_TBU_OperationLine.COLUMNNAME_LineNo;
		//
		List<X_TBU_OperationLine> list = new Query(getCtx(), I_TBU_OperationLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		
		return list.toArray(new X_TBU_OperationLine[list.size()]);		
	}	//	getLines
	

}
