package org.epi.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.util.ProcessUtil;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
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
import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
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

public class WTransactionAll extends TransactionAll implements IFormController,EventListener<Event>, WTableModelListener, ValueChangeListener  {
	
	
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
	private Label TypeTrxLabel = new Label("Transaction Type");
//	private Label IsSOTrxLabel = new Label("Sales Transaction");
	private Label DateSeparateLabel = new Label(" To ");
	private Label DocTypeLabel = new Label("Document Type");
	private Label BPartnerLabel = new Label("Bussiness Partner");
	
	private WDateEditor DateTrx1 = new WDateEditor();
	private WDateEditor DateTrx2 = new WDateEditor();
	private WTableDirEditor OrgSearch = null;

	private Combobox TypeTrx = new Combobox();
	private Combobox DocType = new Combobox();
	private Combobox BPartner = new Combobox();
	
	private Hbox HboxDate= new Hbox();
	
	private Button SearchButton = new Button();
	private Button ReportButton = new Button();
	
	private Properties ctx = Env.getCtx();

	private WListbox TransactionTable = ListboxFactory.newDataTable();
	
	private Button ZoomButton = new Button();
	
	ArrayList<KeyNamePair> TabList = null;
	int ISM_ViewTransactionConf_ID = 0;

	
	public WTransactionAll() {	
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
		
		row.appendCellChild(TypeTrxLabel.rightAlign(), 1);
		row.appendCellChild(TypeTrx, 1);
		TypeTrx.setHflex("true");
		
		row = rows.newRow();
		row.appendCellChild(BPartnerLabel.rightAlign(), 1);
		row.appendCellChild(BPartner, 1);
		BPartner.setHflex("true");
		
		row.appendCellChild(DocTypeLabel.rightAlign(), 1);
		row.appendCellChild(DocType, 1);
		DocType.setHflex("true");
		
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
		
		row.appendCellChild(ReportButton, 1);
		ReportButton.setHflex("true");
		ReportButton.setLabel("Print Report");
		ReportButton.setStyle("font-weight:bold");
		ReportButton.addActionListener(this);
				
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
//		southHBox.appendChild(PostToRequisitionButton);
//		PostToRequisitionButton.setLabel("Release To Requisition");
//		PostToRequisitionButton.addActionListener(this);
//		
//		southHBox.appendChild(PostingMRButton);
//		PostingMRButton.setLabel("Release Material");
//		PostingMRButton.addActionListener(this);	
		
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
				
		TabList = loadTrx(AD_Org_ID);
		TypeTrx.removeAllItems();
		for (KeyNamePair trx : TabList)
		TypeTrx.appendItem(trx.getName());
		TypeTrx.setSelectedIndex(0);
		TypeTrx.addEventListener(0, "onChange", this);
					
		Integer AD_Tab_ID = TabList.get(TypeTrx.getSelectedIndex()).getKey();
		
		
		StringBuilder getConf_ID = new StringBuilder();
		getConf_ID.append("SELECT ISM_ViewTransactionConf_ID ");
		getConf_ID.append(" FROM ISM_ViewTransactionConf ");
		getConf_ID.append("WHERE AD_Tab_ID = "+AD_Tab_ID);

		
		ISM_ViewTransactionConf_ID = DB.getSQLValueEx(null, getConf_ID.toString());
		
		ArrayList<KeyNamePair> list = loadDocType(AD_Tab_ID);
		DocType.removeAllItems();
		for (KeyNamePair docType : list)
		DocType.appendItem(docType.getName());
		DocType.setSelectedIndex(0);
	
		ArrayList<KeyNamePair> listDoc = loadDocType(AD_Tab_ID);
		DocType.removeAllItems();
		for (KeyNamePair docType : listDoc)
		DocType.appendItem(docType.getName());
		DocType.setSelectedIndex(0);
		
		ArrayList<KeyNamePair> BPList =loadBP(AD_Org_ID);
		BPartner.removeAllItems();
		for (KeyNamePair bp : BPList)
		BPartner.appendItem(bp.getName());
		BPartner.setSelectedIndex(0);

				
	
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

		if(e.getTarget().equals(TypeTrx)) {
			
			DocType.setEnabled(true);
			
			Integer AD_Tab_ID = TabList.get(TypeTrx.getSelectedIndex()).getKey();

			ArrayList<KeyNamePair> list = loadDocType(AD_Tab_ID);
			DocType.removeAllItems();
			for (KeyNamePair docType : list)
			DocType.appendItem(docType.getName());
			DocType.setSelectedIndex(0);
			
			
			StringBuilder getConf_ID = new StringBuilder();
			getConf_ID.append("SELECT ISM_ViewTransactionConf_ID ");
			getConf_ID.append(" FROM ISM_ViewTransactionConf ");
			getConf_ID.append("WHERE AD_Tab_ID = "+AD_Tab_ID);

			
			ISM_ViewTransactionConf_ID = DB.getSQLValueEx(null, getConf_ID.toString());
			
		}else if(e.getTarget().equals(SearchButton)) {
			
			search();
		}else if(e.getTarget().equals(ZoomButton)) {
			
			ArrayList<Integer> records_id = new ArrayList<Integer>();

			for(int i = 0 ; i < TransactionTable.getRowCount() ; i++) {
				boolean isSelected = (boolean) TransactionTable.getValueAt(i, 0);
				if(isSelected) {
					KeyNamePair docNo =  (KeyNamePair) TransactionTable.getValueAt(i, 1);
					Integer getID = docNo.getKey();
					records_id.add(getID);
				}
				
		
			}
			
			Integer AD_Table_ID = getIDFromComboBox(TypeTrx,MTable.Table_Name, MTable.COLUMNNAME_Name);

			if(records_id.size() < 1 ) {
				
				FDialog.info(form.getWindowNo(), null, "", "Silahkan Pilih Data Terlebih Dahulu");
				return;
			}
			
			for(int i = 0 ; i < records_id.size() ; i++) {
				zoom(AD_Table_ID,records_id.get(i));
			}
			
		}else if(e.getTarget().equals(ReportButton)) {
			
			
			for (int i = 0; i < TransactionTable.getRowCount(); i++) {
				
				boolean IsSelected = (boolean) TransactionTable.getValueAt(i, 0);
				
				if(IsSelected) {
					
					KeyNamePair kpID = (KeyNamePair) TransactionTable.getValueAt(i, 1);
					Integer C_DocType_ID = getIDFromComboBox(DocType,MDocType.Table_Name, MDocType.COLUMNNAME_Name);

					StringBuilder SQLGetProces = new StringBuilder();
					SQLGetProces.append("SELECT AD_Process_ID");
					SQLGetProces.append(" FROM ISM_ViewTransactionConfLine ");
					SQLGetProces.append(" WHERE AD_Client_ID =  "+Env.getAD_Client_ID(ctx));
					SQLGetProces.append(" AND AD_Org_ID =  "+Env.getAD_Org_ID(ctx));
					SQLGetProces.append(" AND ISM_ViewTransactionConf_ID =  "+ISM_ViewTransactionConf_ID);
					SQLGetProces.append(" AND C_DocType_ID =  "+C_DocType_ID);

					Integer AD_Process_ID = DB.getSQLValueEx(null, SQLGetProces.toString());
					
					if(AD_Process_ID > 0 && kpID.getKey() > 0) {
					
						printReport(kpID.getKey(),AD_Process_ID);

					}else {
						
						FDialog.info(form.getWindowNo(), null, null, "No Data for Print Report", "Info");
						return;
						
					}
				}else {
					
					FDialog.info(form.getWindowNo(), null, null, "No Data for Print Report", "Info");
					return;
					
				}
				
				
			}
			
			
		}
		
		
	}

	@Override
	public ADForm getForm() {
		return form;
	}
	
	public void search() {
		
		
		Integer AD_Tab_ID = TabList.get(TypeTrx.getSelectedIndex()).getKey();		
		
		Integer C_DocType_ID = getIDFromComboBox(DocType,MDocType.Table_Name, MDocType.COLUMNNAME_Name);
		
		Integer C_BPartner_ID = getIDFromComboBox(BPartner,MBPartner.Table_Name, MBPartner.COLUMNNAME_Name);
		
		Timestamp dateFrom = (Timestamp) DateTrx1.getValue();
		Timestamp dateTo = (Timestamp) DateTrx2.getValue();
		
		StringBuilder SQLGetDateFilter = new StringBuilder();
		SQLGetDateFilter.append("SELECT FilterDate");
		SQLGetDateFilter.append(" FROM ISM_ViewTransactionConf ");
		SQLGetDateFilter.append(" WHERE AD_Client_ID =  "+Env.getAD_Client_ID(ctx));
		SQLGetDateFilter.append(" AND AD_Org_ID =  "+Env.getAD_Org_ID(ctx));
		SQLGetDateFilter.append(" AND ISM_ViewTransactionConf_ID =  "+ISM_ViewTransactionConf_ID);

		String DateFilter = DB.getSQLValueStringEx(null, SQLGetDateFilter.toString());
		

			
		Vector<Vector<Object>> data = getTrxData(AD_Tab_ID, C_DocType_ID,TypeTrx,ISM_ViewTransactionConf_ID,C_BPartner_ID,dateFrom,dateTo,DateFilter);
		Vector<String> columnNames = getOISColumnNames();

		TransactionTable.clear();

		// Set Model
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		TransactionTable.setData(modelP, columnNames);
		configureMiniTable(TransactionTable);
		
	}
	
	protected Vector<String> getOISColumnNames() {
		
		// Header Info
		Vector<String> columnNames = new Vector<String>();
		
		
		columnNames.add(Msg.getMsg(ctx, "Select"));
				
		StringBuilder SQLcolumnName = new StringBuilder();
		SQLcolumnName.append("SELECT Name,Reference");
		SQLcolumnName.append(" FROM  ISM_ViewTransactionColumn ");
		SQLcolumnName.append(" WHERE AD_Client_ID = "+AD_Client_ID);
		SQLcolumnName.append(" AND AD_Org_ID = "+ OrgSearch.getValue());
		SQLcolumnName.append(" AND ISM_ViewTransactionConf_ID = "+ISM_ViewTransactionConf_ID);
		SQLcolumnName.append(" Order By sequence asc ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(SQLcolumnName.toString(), null);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				
				columnNames.add(rs.getString(1));
				
				
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, SQLcolumnName.toString(), e);
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return columnNames;
	
	}
	
	protected void configureMiniTable(IMiniTable miniTable) {
		
		miniTable.setColumnClass(0, Boolean.class, false);
		
		StringBuilder SQLcolumnName = new StringBuilder();
		SQLcolumnName.append("SELECT Sequence,Reference ");
		SQLcolumnName.append(" FROM  ISM_ViewTransactionColumn ");
		SQLcolumnName.append(" WHERE AD_Client_ID = "+AD_Client_ID);
		SQLcolumnName.append(" AND AD_Org_ID = "+ OrgSearch.getValue());
		SQLcolumnName.append(" AND ISM_ViewTransactionConf_ID = "+ISM_ViewTransactionConf_ID);
		SQLcolumnName.append(" Order By sequence asc ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(SQLcolumnName.toString(), null);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				if(rs.getString(2).toUpperCase().equals("S")) {
					miniTable.setColumnClass((Integer)rs.getInt(1), String.class, true); 

				}else if(rs.getString(2).toUpperCase().equals("B")) {
					miniTable.setColumnClass((Integer)rs.getInt(1), BigDecimal.class, true); 

				}else if(rs.getString(2).toUpperCase().equals("N")) {
					miniTable.setColumnClass((Integer)rs.getInt(1), Integer.class, true); 

				}else if(rs.getString(2).toUpperCase().equals("D")) {
					miniTable.setColumnClass((Integer)rs.getInt(1), String.class, true); 

				} 
				
					
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, SQLcolumnName.toString(), e);
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
	
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
	
	public void zoom(int AD_Table_ID, int Record_ID)
	{
	
		AEnv.zoom(AD_Table_ID, Record_ID);
	}   //  zoom

}
