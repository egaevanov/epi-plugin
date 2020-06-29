package org.epi.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.process.ImportProcess;
import org.compiere.model.I_C_DocType;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.model.MCostType;
import org.compiere.model.MDocType;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategoryAcct;
import org.compiere.model.PO;
import org.compiere.model.X_I_Inventory;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.epi.model.X_M_SaveInv;



public class EPIProcessMaterialSaveIn extends SvrProcess implements ImportProcess
{
	private int				p_AD_Client_ID = 0;
	private boolean			p_UpdateCosting = false;
	private Integer			p_C_AcctSchema_ID = 0;
	MAcctSchema 			acctSchema 	= null;
	private Integer			p_M_CostType_ID = 0;
	private int				p_M_CostElement_ID = 0;
	private int				p_AD_OrgTrx_ID = 0;
	private String			m_docAction = null;
	private MInventory 		costingDoc = null;
	private Integer 		p_C_DocTypeCA_ID = 0;
	private int 			record_ID = 0;
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare(){
		
		record_ID = getRecord_ID();
		p_AD_Client_ID = Env.getAD_Client_ID(getCtx());	
		p_C_AcctSchema_ID = EPIParameterSupport.getID(getAD_Client_ID(), MAcctSchema.Table_Name, MAcctSchema.COLUMNNAME_CostingMethod, 0, MAcctSchema.COSTINGMETHOD_AveragePO);
		p_M_CostType_ID = EPIParameterSupport.getID(getAD_Client_ID(), MCostType.Table_Name, "", 0, "");
		p_M_CostElement_ID = EPIParameterSupport.getID(getAD_Client_ID(), MCostElement.Table_Name, MCostElement.COLUMNNAME_CostingMethod, 0, MCostElement.COSTINGMETHOD_AveragePO);
		p_AD_OrgTrx_ID = 0;
		p_C_DocTypeCA_ID = EPIParameterSupport.getDocType(getAD_Client_ID(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, MDocType.DOCSUBTYPEINV_CostAdjustment, "N");	
		p_UpdateCosting = true;
		m_docAction = MInventory.DOCACTION_Complete;
		
	}	


	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		
		X_M_SaveInv saveInv = new X_M_SaveInv(getCtx(), record_ID, get_TrxName());

		
		StringBuilder msglog = new StringBuilder("M_Locator_ID=").append(saveInv.getM_Locator_ID()).append(",MovementDate=").append(saveInv.getDateTrx());
		if (log.isLoggable(Level.INFO)) log.info(msglog.toString());
		
		if (p_UpdateCosting) {
			if (p_C_AcctSchema_ID <= 0) {
				throw new IllegalArgumentException("Accounting Schema required!");
			}
			if (p_M_CostType_ID <= 0) {
				throw new IllegalArgumentException("Cost Type required!");
			}
			if (p_M_CostElement_ID <= 0 ) {
				throw new IllegalArgumentException("Cost Element required!");
			}
			if (p_AD_OrgTrx_ID < 0 ) {
				throw new IllegalArgumentException("AD_OrgTrx required!");
			}
			if (p_C_DocTypeCA_ID <= 0 ) {
				throw new IllegalArgumentException("Cost Adjustment Document Type required!");
			}
			 acctSchema = MAcctSchema.get(getCtx(), p_C_AcctSchema_ID, get_TrxName());
		}
		
		
		MInventory inventory = null;
		
		try{
			
			Timestamp MovementDate = TimeUtil.getDay(saveInv.getDateTrx());


			inventory = new MInventory (getCtx(), 0, get_TrxName());
				
			Integer p_C_DocTypePI_ID = EPIParameterSupport.getDocType(getAD_Client_ID(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, MDocType.DOCSUBTYPEINV_PhysicalInventory, "N");	

			inventory.setC_DocType_ID(p_C_DocTypePI_ID);
			inventory.setClientOrg(saveInv.getAD_Client_ID(), saveInv.getAD_Org_ID());
			inventory.setDescription("I " + saveInv.getM_Warehouse_ID() + " " + MovementDate);
			inventory.setM_Warehouse_ID(saveInv.getM_Warehouse_ID());
			inventory.setMovementDate(MovementDate);
			inventory.saveEx();
			
			MProduct product = new MProduct(getCtx(), saveInv.getM_Product_ID(), get_TrxName());
//			int M_AttributeSetInstance_ID = generateASI(product,imp);
			
			MInventoryLine line = new MInventoryLine (inventory, saveInv.getM_Locator_ID(), saveInv.getM_Product_ID(), 0 , Env.ZERO , saveInv.getQtyEntered(), Env.ZERO);
			line.setDescription(saveInv.getDescription());
			line.setInventoryType(MInventoryLine.INVENTORYTYPE_InventoryDifference);
			line.saveEx();
			
			
			
			if(line != null) {
				
				saveInv.setM_Inventory_ID(line.getM_Inventory_ID());
				saveInv.setM_InventoryLine_ID(line.getM_InventoryLine_ID());
				
				if(saveInv.save()) {
					if (p_UpdateCosting) {
						updateCosting(saveInv, product, line);
					}
				}
				
			}
									
			if (inventory != null) {
				if (m_docAction != null && m_docAction.length() > 0) {
					if (!inventory.processIt(m_docAction)) {
						log.warning("Inventory Process Failed: " + inventory + " - " + inventory.getProcessMsg());
						throw new IllegalStateException("Inventory Process Failed: " + inventory + " - " + inventory.getProcessMsg());

					}
					inventory.saveEx();
				}
			}
			
			if (costingDoc != null) {
				if (!DocumentEngine.processIt(costingDoc, DocAction.ACTION_Complete)) 
				{
					StringBuilder msg = new StringBuilder();
					I_C_DocType docType = costingDoc.getC_DocType();
					msg.append(Msg.getMsg(getCtx(), "ProcessFailed")).append(": ");
					if (Env.isBaseLanguage(getCtx(), I_C_DocType.Table_Name))
						msg.append(docType.getName());
					else
						msg.append(((PO)docType).get_Translation(I_C_DocType.COLUMNNAME_Name));
					throw new AdempiereUserError(msg.toString());
				}
				costingDoc.saveEx();
			}
		}catch (Exception e){
			
			rollback();
			throw new AdempiereException(e);
		}
		

		return "";
	}	//	doIt

	protected int generateASI(MProduct product,X_I_Inventory imp){
		int M_AttributeSetInstance_ID = 0;
		if ((imp.getLot() != null && imp.getLot().length() > 0) || (imp.getSerNo() != null && imp.getSerNo().length() > 0))
		{
			
			if (product.isInstanceAttribute())
			{
				MAttributeSet mas = product.getAttributeSet();
				MAttributeSetInstance masi = new MAttributeSetInstance(getCtx(), 0, mas.getM_AttributeSet_ID(), get_TrxName());
				if (mas.isLot() && imp.getLot() != null)
					masi.setLot(imp.getLot(), imp.getM_Product_ID());
				if (mas.isSerNo() && imp.getSerNo() != null)
					masi.setSerNo(imp.getSerNo());
				masi.setDescription();
				masi.saveEx();
				M_AttributeSetInstance_ID = masi.getM_AttributeSetInstance_ID();
			}
		}
		return M_AttributeSetInstance_ID;
	}

	protected void updateCosting(X_M_SaveInv saveInv, MProduct product,MInventoryLine line) {
		String costingLevel = null;
		if(product.getM_Product_Category_ID() > 0){
			MProductCategoryAcct pca = MProductCategoryAcct.get(getCtx(), product.getM_Product_Category_ID(), p_C_AcctSchema_ID, get_TrxName());
			costingLevel = pca.getCostingLevel();
			if (costingLevel == null) {
				costingLevel = acctSchema.getCostingLevel();
			}

		}

		int costOrgID = p_AD_OrgTrx_ID;
		int costASI = line.getM_AttributeSetInstance_ID();
		if (MAcctSchema.COSTINGLEVEL_Client.equals(costingLevel)){
			costOrgID = 0;
			costASI = 0;
		} else if (MAcctSchema.COSTINGLEVEL_Organization.equals(costingLevel)) { 
			costASI = 0;
		} else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(costingLevel)) {
			costOrgID = 0;
		}
		MCost cost = MCost.get (product, costASI, acctSchema, costOrgID, p_M_CostElement_ID, get_TrxName());
		if (cost.is_new())
			cost.saveEx();
		if (costingDoc == null) {
			costingDoc = new MInventory(getCtx(), 0, get_TrxName());
			costingDoc.setC_DocType_ID(p_C_DocTypeCA_ID);
			costingDoc.setCostingMethod(cost.getM_CostElement().getCostingMethod());
			costingDoc.setAD_Org_ID(saveInv.getAD_Org_ID());
			costingDoc.setDocAction(DocAction.ACTION_Complete);
			costingDoc.saveEx();
		}
		
		MInventoryLine costingLine = new MInventoryLine(getCtx(), 0, get_TrxName());
		costingLine.setM_Inventory_ID(costingDoc.getM_Inventory_ID());
		costingLine.setM_Product_ID(cost.getM_Product_ID());
		costingLine.setCurrentCostPrice(Env.ZERO);
		costingLine.setNewCostPrice(Env.ZERO);
		costingLine.setM_Locator_ID(0);
		costingLine.setAD_Org_ID(saveInv.getAD_Org_ID());
		costingLine.setM_AttributeSetInstance_ID(costASI);
		costingLine.saveEx();
		
		saveInv.setM_CostingLine_ID(costingLine.getM_InventoryLine_ID());
		saveInv.setDocStatus("CO");
		saveInv.setProcessed(true);
		saveInv.saveEx();
	}


	@Override
	public String getImportTableName() {
		return X_I_Inventory.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(p_AD_Client_ID);
		return msgreturn.toString();
	}
}	//	ImportInventory