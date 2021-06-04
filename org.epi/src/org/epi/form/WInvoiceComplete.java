package org.epi.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import org.adempiere.util.ProcessUtil;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;

public class WInvoiceComplete extends InvoiceComplete implements IFormController,EventListener<Event>, WTableModelListener, ValueChangeListener  {
	
	
	// CustomForm
	private CustomForm form = new CustomForm();
	
	// BorderLayout
	private Borderlayout mainLayout = new Borderlayout();
	private Borderlayout infoLayout = new Borderlayout();
	private Borderlayout actionLayout = new Borderlayout();

	// Panel
	private Panel parameterPanel = new Panel();
	private Panel infoPanel = new Panel();
	private Panel southPanel = new Panel();

	// Grid
	private Grid actionGrid = GridFactory.newGridLayout();
	private Grid parameterGrid = GridFactory.newGridLayout();	
	
	// Parameter Component
	private Label OrgLabel = new Label("Organization");;
	private Label DateOrderedLabel = new Label("Date Order");
	private Label DateSeparateLabel = new Label(" To ");
	private Label BPartnerLabel = new Label("Bussiness Partner");
	private Label DocStatusLabel = new Label("Document Status");
	
	private WSearchEditor BPartner = null;
	private WDateEditor DateTrx1 = new WDateEditor();
	private WDateEditor DateTrx2 = new WDateEditor();
	private WTableDirEditor OrgSearch = null;
	private Checkbox IsReceipt = new Checkbox();

//	private Combobox BPartner = new Combobox();
	private Combobox DocStatusBox = new Combobox();

	
	private Hbox HboxDate= new Hbox();
	
	private Button SearchButton = new Button();
//	private Button ReportButton = new Button();
	private Button ProcessCompleteButton = new Button();
	
	private Properties ctx = Env.getCtx();

	private WListbox TransactionTable = ListboxFactory.newDataTable();
	
	private Button ZoomButton = new Button();
	
	ArrayList<KeyNamePair> TabList = null;
	int ISM_ViewTransactionConf_ID = 0;

	public WInvoiceComplete() {	
		if(DataInitialization()){
			UIInitialization();
		}else {
			return;
		}
	}
		
	private void UIInitialization() {
		
		form.appendChild(mainLayout);
		
		form.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>(){
	
			@Override
			public void onEvent(Event arg0) throws Exception {
				form.dispose();
			}
				
		});
		
		//mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");
		
		North north = new North();
		mainLayout.appendChild(north);
		
		String grid = "border: 1px solid #C0C0C0; border-radius:5px;";
		north.appendChild(parameterPanel);
		north.setStyle(grid);
		
		parameterPanel.appendChild(parameterGrid);
		//parameterGrid.setWidth("100%");
		parameterGrid.setStyle("Height:28%;");
	
		Rows rows = null;
		Row row = null;
	
		rows = parameterGrid.newRows();
		
		//Show Paramenter Component
		row = rows.newRow();
		row.appendCellChild(OrgLabel.rightAlign(), 1);
		row.appendCellChild(OrgSearch.getComponent(), 1);
		
		row.appendCellChild(BPartnerLabel.rightAlign(), 1);
		row.appendCellChild(BPartner.getComponent(), 1);
		BPartner.getComponent().setHflex("true");
		
		row = rows.newRow();
		row.appendCellChild(DateOrderedLabel.rightAlign(), 1);

		row.appendCellChild(HboxDate);
		
		HboxDate.appendChild(DateTrx1.getComponent());
		//row.appendCellChild(DateTrx1.getComponent());
		DateTrx1.getComponent().setPlaceholder("Date From");
		HboxDate.appendChild(DateSeparateLabel);

		HboxDate.appendChild(DateTrx2.getComponent());
//		row.appendCellChild(DateTrx2.getComponent());
		DateTrx2.getComponent().setPlaceholder("Date To");

		row.appendCellChild(DocStatusLabel.rightAlign(),1);
		row.appendCellChild(DocStatusBox, 1);
		DocStatusBox.setHflex("true");
		
		row = rows.newRow();
		row.appendCellChild(new Space(),1);	
		row.appendCellChild(IsReceipt, 1);
		IsReceipt.setSelected(true);
		IsReceipt.setLabel("Invoice AR");
		
		row = rows.newRow();
		row.appendCellChild(new Space(),1);	
		
		row = rows.newRow();
		row.appendCellChild(new Space(),1);	
		row.appendCellChild(SearchButton, 1);
		SearchButton.setHflex("true");
		SearchButton.setLabel("Search Data");
		SearchButton.setStyle("font-weight:bold");
		SearchButton.addActionListener(this);
		
		row.appendCellChild(ZoomButton, 1);
		ZoomButton.setHflex("true");
		ZoomButton.setLabel("Zoom Data");
		ZoomButton.setStyle("font-weight:bold");
		ZoomButton.addActionListener(this);
		
		row.appendCellChild(ProcessCompleteButton, 1);
		ProcessCompleteButton.setHflex("true");
		ProcessCompleteButton.setLabel("Process Complete");
		ProcessCompleteButton.setStyle("font-weight:bold");
		ProcessCompleteButton.addActionListener(this);
				
		row.appendCellChild(new Space(),1);	

		// SouthPanel
		South south = new South();
		mainLayout.appendChild(south);
		south.setStyle(grid);
		south.appendChild(southPanel);
		southPanel.appendChild(actionGrid);
		
		Rows southRows = null;
		Row southRow = null;
	
		southRows = actionGrid.newRows();
		actionGrid.setStyle("Height:50%;");
	
		southRow = southRows.newRow();
		
		//Show Action Component
		Hbox southHBox = new Hbox();
		southRow.appendCellChild(southHBox , 1);
		southHBox.setAlign("right");
		
		south = new South();
		infoPanel.appendChild(actionLayout);
		actionLayout.appendChild(south);
		//infoPanel.setWidth("100%");
		infoPanel.setHeight("100%");
		//actionLayout.setWidth("100%");
		actionLayout.setHeight("100%");
		
		Center center = new Center();
		actionLayout.appendChild(center);
		center.appendChild(TransactionTable);
		//TransactionTable.setWidth("100%");
		center.setStyle(grid);
	
		center = new Center();
		mainLayout.appendChild(center);
		center.appendChild(infoLayout);
		//infoLayout.setWidth("100%");
		infoLayout.setHeight("100%");
	
		north = new North();
		north.setHeight("100%");
		infoLayout.appendChild(north);
		north.appendChild(infoPanel);
		north.setSplittable(true);
		center = new Center();
		infoLayout.appendChild(center);
		
	}
	
	private Boolean DataInitialization() {
		Boolean OK = true;
		
		int AD_Org_ID = Env.getAD_Org_ID(ctx);
		
		if(AD_Org_ID <= 0) {
			OK = false;
		}
		
		//Lookup Org
		MLookup OrgLookup = MLookupFactory.get(ctx, form.getWindowNo(), 0, 528,DisplayType.TableDir);
		OrgSearch = new WTableDirEditor("AD_Org_ID", true, false, true,OrgLookup);
		OrgSearch.addValueChangeListener(this);
		OrgSearch.setMandatory(true);
		OrgSearch.setValue(AD_Org_ID);	
		OrgSearch.setReadWrite(false);
	
		//Lookup BP
		MLookup lookupBP = MLookupFactory.get(ctx, form.getWindowNo(),0, 2893, DisplayType.Search);
		BPartner = new WSearchEditor("C_BPartner_ID", true, false, true,lookupBP);
		BPartner.addValueChangeListener(this);
		BPartner.setMandatory(true);
		
		HashMap<String, String> DocStatusMap = new HashMap<String, String>();
		DocStatusMap.put("DR", "Draft");
		DocStatusMap.put("RE", "Reverse");
		DocStatusMap.put("VO", "Void");
		DocStatusMap.put("CO", "Complete");
		DocStatusMap.put("IP", "In Progress");

		DocStatusBox.removeAllItems();
		
		for(String key : DocStatusMap.keySet()) {
			DocStatusBox.appendItem(DocStatusMap.get(key),key);
		}
		
		DocStatusBox.setSelectedIndex(4);

		return OK;
	}
	

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onEvent(Event e) throws Exception {

		if(e.getTarget().equals(SearchButton)) {
			
			search();
			
		}else if(e.getTarget().equals(ZoomButton)) {
			
			ArrayList<Integer> records_id = new ArrayList<Integer>();

			for(int i = 0 ; i < TransactionTable.getRowCount() ; i++) {
				boolean isSelected = (boolean) TransactionTable.getValueAt(i, 0);
				if(isSelected) {
					KeyNamePair docNo =  (KeyNamePair) TransactionTable.getValueAt(i, 3);
					Integer getID = docNo.getKey();
					records_id.add(getID);
				}
					
			}
			
			Integer AD_Table_ID = 318;

			if(records_id.size() < 1 ) {
				
				FDialog.info(form.getWindowNo(), null, "", "Silahkan Pilih Data Terlebih Dahulu");
				return;
			}
			
			for(int i = 0 ; i < records_id.size() ; i++) {
				zoom(AD_Table_ID,records_id.get(i));
			}
			
		}else if(e.getTarget().equals(ProcessCompleteButton)) {
			
			StringBuilder rs = new StringBuilder();
			
			ArrayList<Integer> records_id = new ArrayList<Integer>();

			for(int i = 0 ; i < TransactionTable.getRowCount() ; i++) {
				boolean isSelected = (boolean) TransactionTable.getValueAt(i, 0);
				if(isSelected) {
					KeyNamePair docNo =  (KeyNamePair) TransactionTable.getValueAt(i, 3);
					Integer getID = docNo.getKey();
					records_id.add(getID);
				}
					
			}
			

			if(records_id.size() < 1 ) {
				
				FDialog.info(form.getWindowNo(), null, "", "Silahkan Pilih Data Terlebih Dahulu","Info");
				return;
			}
			
			
			for (int i = 0 ; i < TransactionTable.getRowCount(); i++) {
				
				boolean isSelected = (boolean) TransactionTable.getValueAt(i, 0);
				if(isSelected) {
					KeyNamePair docNo =  (KeyNamePair) TransactionTable.getValueAt(i, 3);
					Integer C_Invoice_ID = docNo.getKey();
					
					MInvoice inv = new MInvoice(ctx, C_Invoice_ID, null);
					
					String DocStatus = inv.getDocStatus();
					
					if(DocStatus.toUpperCase().equals(MInvoice.DOCSTATUS_Drafted.toUpperCase())||
							DocStatus.toUpperCase().equals(MInvoice.DOCSTATUS_InProgress.toUpperCase())) {
						
							inv.processIt(MInvoice.DOCACTION_Complete);
							if(inv.save()) {
								
								if(inv.getDocStatus().equals(MInvoice.DOCSTATUS_Completed)) {
									rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Success");
									rs.append("\n");
								
								}else {
									rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Failed (Please Check Details Of Invoice)");
									rs.append("\n");
								}
								
							}else {
								
								rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Failed (Please Check Details Of Invoice)");
								rs.append("\n");

							}
						
					}else if(DocStatus.toUpperCase().equals(MInvoice.DOCSTATUS_Invalid.toUpperCase())) {
						
						MInvoiceLine[] lines = inv.getLines();
						
						if(lines.length == 0) {
							
							rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Failed (Status Invalid, Please Check Details Of Invoice)");
							rs.append("\n");
							
						}else {
							
							rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Cant Process (Status Document Invalid, Please Manual Complete on Invoice Window )");
							rs.append("\n");
											
						}
							
						
						
					}else if(DocStatus.toUpperCase().equals(MInvoice.DOCSTATUS_Voided.toUpperCase())) {
						
						rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Failed (Document Void Cant Complete)");
						rs.append("\n");
						
					}else if(DocStatus.toUpperCase().equals(MInvoice.DOCSTATUS_Reversed.toUpperCase())) {
						
						rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Failed (Document Reverse Cant Complete)");
						rs.append("\n");
						
					}else {
						
						rs.append("Process Complete Invoice "+ inv.getDocumentNo() + " Failed (Document Already Completed)");
						rs.append("\n");
						
					}				
					
				}
				
			}
			
			FDialog.info(form.getWindowNo(), null, null, rs.toString(), "Info");

			
		}
//		else if(e.getTarget().equals(ReportButton)) {
//			
//			
//			for (int i = 0; i < TransactionTable.getRowCount(); i++) {
//				
//				boolean IsSelected = (boolean) TransactionTable.getValueAt(i, 0);
//				
//				if(IsSelected) {
//					
//					KeyNamePair kpID = (KeyNamePair) TransactionTable.getValueAt(i, 1);
//					Integer C_DocType_ID = getIDFromComboBox(DocType,MDocType.Table_Name, MDocType.COLUMNNAME_Name);
//
//					StringBuilder SQLGetProces = new StringBuilder();
//					SQLGetProces.append("SELECT AD_Process_ID");
//					SQLGetProces.append(" FROM ISM_ViewTransactionConfLine ");
//					SQLGetProces.append(" WHERE AD_Client_ID =  "+Env.getAD_Client_ID(ctx));
//					SQLGetProces.append(" AND AD_Org_ID =  "+Env.getAD_Org_ID(ctx));
//					SQLGetProces.append(" AND ISM_ViewTransactionConf_ID =  "+ISM_ViewTransactionConf_ID);
//					SQLGetProces.append(" AND C_DocType_ID =  "+C_DocType_ID);
//
//					Integer AD_Process_ID = DB.getSQLValueEx(null, SQLGetProces.toString());
//					
//					if(AD_Process_ID > 0 && kpID.getKey() > 0) {
//					
//						printReport(kpID.getKey(),AD_Process_ID);
//
//					}else {
//						
//						FDialog.info(form.getWindowNo(), null, null, "No Data for Print Report", "Info");
//						return;
//						
//					}
//				}else {
//					
//					FDialog.info(form.getWindowNo(), null, null, "No Data for Print Report", "Info");
//					return;
//					
//				}
//				
//				
//			}
//			
//			
//		}		
	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	public void search() {
		
		Integer C_BPartner_ID = (Integer) BPartner.getValue();
		if(C_BPartner_ID == null) {
			C_BPartner_ID = 0;
		
		}
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Integer AD_Org_ID = (Integer) OrgSearch.getValue();
		
		Timestamp dateFrom = (Timestamp) DateTrx1.getValue();
		Timestamp dateTo = (Timestamp) DateTrx2.getValue();
		
		boolean IsAR = IsReceipt.isChecked();
		
		String DocStatus = DocStatusBox.getSelectedItem().getValue();
			
		data = getDataInvoice(Env.getAD_Client_ID(ctx), AD_Org_ID, dateFrom, dateTo, C_BPartner_ID,DocStatus,IsAR, TransactionTable);
		Vector<String> columnNames = getOISColumnNamesInvoice();

		TransactionTable.clear();

		// Set Model
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		TransactionTable.setData(modelP, columnNames);
		configureMiniTableInvoice(TransactionTable);
		
	}
	
	protected Vector<String> getOISColumnNamesInvoice() {
		
		// Header Info
		Vector<String> columnNames = new Vector<String>(5);
		columnNames.add(Msg.getMsg(ctx, "Select"));
		
		columnNames.add("Date Accounting");
		columnNames.add("Date Invoiced");
		columnNames.add("No Voucher");
		columnNames.add("No PO");
		if(IsReceipt.isChecked()) {
			columnNames.add("Customer");
		}else {
			columnNames.add("Vendor");
		}
		columnNames.add("Description");
		columnNames.add("Project");
		columnNames.add("Amount");
		columnNames.add("Nomor Faktur");
		columnNames.add("Document Status");
		return columnNames;
		
	}
	
	protected void configureMiniTableInvoice(IMiniTable miniTable) {
		
		miniTable.setColumnClass(0, Boolean.class, false); 
		miniTable.setColumnClass(1, Timestamp.class, true); 	//DateAcct
		miniTable.setColumnClass(2, Timestamp.class, true); 	//DateInvoiced
		miniTable.setColumnClass(3, KeyNamePair.class, true); 	//DocumentNo-C_Invoice_ID
		miniTable.setColumnClass(4, String.class, true); 		//POReference
		miniTable.setColumnClass(5, KeyNamePair.class, true); 	//Name-C_BPartner_ID
		miniTable.setColumnClass(6, String.class, true); 		//Description
		miniTable.setColumnClass(7, KeyNamePair.class, true); 	//Name-C_Project_ID
		miniTable.setColumnClass(8, BigDecimal.class, true); 	//GrandTotal
		miniTable.setColumnClass(9, String.class, true); 		//TaxReference
		miniTable.setColumnClass(10, String.class, true); 		//DocStatus

		miniTable.autoSize();

	}	
	public void printReport(int Record_ID,int AD_Process_ID) {
		
		 String trxName = Trx.createTrxName("Print");
		// String url = "/home/idempiere/idempiere.gtk.linux.x86_64/idempiere-server/reports/";
		//String url = "D:\\SourceCode\\iDempiereBase\\reports\\";
		 
		 MProcess proc = new MProcess(Env.getCtx(), AD_Process_ID, trxName);
		 MPInstance instance = new MPInstance(proc,proc.getAD_Process_ID());
		 ProcessInfo pi = new ProcessInfo("Print", AD_Process_ID);
		 pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
		 ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		 list.add(new ProcessInfoParameter("Record_ID", Record_ID, null,null, null));
		// list.add(new ProcessInfoParameter("url_path",url, null,null, null));
		 ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
		 list.toArray(pars);
		 pi.setParameter(pars);
		 //
		 Trx trx = Trx.get(trxName, true);
		 trx.commit();
		
		 ProcessUtil.startJavaProcess(Env.getCtx(), pi, Trx.get(trxName,true));
		
	}
	
	public void zoom(int AD_Table_ID, int Record_ID){
		
		AEnv.zoom(AD_Table_ID, Record_ID);
		
	}   //  zoom

	
	
}
