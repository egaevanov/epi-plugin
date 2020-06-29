package org.epi.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.model.I_C_DocType;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategoryAcct;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.epi.model.X_M_InOutLineDtl;

public class EPIInOutSupport {
	
	protected static CLogger	log = CLogger.getCLogger(EPIInOutSupport.class);
	public static HashMap<Integer, BigDecimal> getInOutDtlNegativeInven(MInOut InOut){
		
		HashMap<Integer, BigDecimal> rslt = new HashMap<Integer, BigDecimal>();
		
		MInOutLine[] lines = InOut.getLines();
		for(MInOutLine line : lines) {
			
			StringBuilder SQLGetMRLineDtl = new StringBuilder();
			SQLGetMRLineDtl.append("SELECT M_InOutLineDtl_ID ");
			SQLGetMRLineDtl.append(" FROM M_InOutLineDtl ");
			SQLGetMRLineDtl.append(" WHERE AD_Client_ID = ?");
			SQLGetMRLineDtl.append(" AND M_InOutLine_ID = ?");
			
			
			PreparedStatement pstmt = null;
	     	ResultSet rs = null;
				try {
					pstmt = DB.prepareStatement(SQLGetMRLineDtl.toString(), null);
					pstmt.setInt(1,line.getAD_Client_ID());	
					pstmt.setInt(2,line.getM_InOutLine_ID());	

					rs = pstmt.executeQuery();
					while (rs.next()) {
					
						X_M_InOutLineDtl dtl = new X_M_InOutLineDtl(Env.getCtx(), rs.getInt(1), null);
						BigDecimal AvailableCoalQty = EPIQtyValidatorGlobalOH.getAvailableCoalQtyPerLocator(dtl.getM_Product_ID(),dtl.getM_Locator_ID(),0,dtl.get_TrxName());
						BigDecimal QtyInternalUse = dtl.getQtyInternalUse();
						
						if(QtyInternalUse.compareTo(AvailableCoalQty) > 0) {
							
							BigDecimal diff = QtyInternalUse.subtract(AvailableCoalQty);
							
							rslt.put(dtl.getM_InOutLineDtl_ID(), diff);
						}
					
					}

				} catch (SQLException err) {
					
					log.log(Level.SEVERE, SQLGetMRLineDtl.toString(), err);
					
					
				} finally {
					
					DB.close(rs, pstmt);
					rs = null;
					pstmt = null;
					
				}
			
			
			
		}
		
		
		return rslt;
		
	}
	
	public static void updateCosting(MInventory inv, MProduct product,MInventoryLine line) {
		String costingLevel = null;
		
		MInventory 		costingDoc = null;
		int p_C_AcctSchema_ID = EPIParameterSupport.getID(line.getAD_Client_ID(), MAcctSchema.Table_Name, MAcctSchema.COLUMNNAME_CostingMethod, 0, MAcctSchema.COSTINGMETHOD_AveragePO);
//		int p_M_CostType_ID = ISMParameterSupport.getID(line.getAD_Client_ID(), MCostType.Table_Name, "", 0, "");
		int p_M_CostElement_ID = EPIParameterSupport.getID(line.getAD_Client_ID(), MCostElement.Table_Name, MCostElement.COLUMNNAME_CostingMethod, 0, MCostElement.COSTINGMETHOD_AveragePO);
		int p_C_DocTypeCA_ID = EPIParameterSupport.getDocType(line.getAD_Client_ID(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, MDocType.DOCSUBTYPEINV_CostAdjustment, "N");	
		
		int p_AD_OrgTrx_ID = 0;
		
		MAcctSchema acctSchema = null;
		
		if(product.getM_Product_Category_ID() > 0){
			MProductCategoryAcct pca = MProductCategoryAcct.get(line.getCtx(), product.getM_Product_Category_ID(), p_C_AcctSchema_ID, line.get_TrxName());
			costingLevel = pca.getCostingLevel();
			
			 acctSchema = MAcctSchema.get(line.getCtx(), p_C_AcctSchema_ID, line.get_TrxName());

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
		MCost cost = MCost.get (product, costASI, acctSchema, costOrgID, p_M_CostElement_ID, line.get_TrxName());
		if (cost.is_new())
			cost.saveEx();
		if (costingDoc == null) {
			costingDoc = new MInventory(line.getCtx(), 0, line.get_TrxName());
			costingDoc.setC_DocType_ID(p_C_DocTypeCA_ID);
			costingDoc.setCostingMethod(cost.getM_CostElement().getCostingMethod());
			costingDoc.setAD_Org_ID(line.getAD_Org_ID());
			costingDoc.setDocAction(DocAction.ACTION_Complete);
			costingDoc.saveEx();
		}
		
		MInventoryLine costingLine = new MInventoryLine(line.getCtx(), 0, line.get_TrxName());
		costingLine.setM_Inventory_ID(costingDoc.getM_Inventory_ID());
		costingLine.setM_Product_ID(cost.getM_Product_ID());
		costingLine.setCurrentCostPrice(cost.getCurrentCostPrice());
		costingLine.setNewCostPrice(cost.getCurrentCostPrice());
		costingLine.setM_Locator_ID(0);
		costingLine.setAD_Org_ID(line.getAD_Org_ID());
		costingLine.setM_AttributeSetInstance_ID(costASI);
		costingLine.saveEx();
		
		
		
		
		if (costingDoc != null) {
			if (!DocumentEngine.processIt(costingDoc, DocAction.ACTION_Complete)) 
			{
				StringBuilder msg = new StringBuilder();
				I_C_DocType docType = costingDoc.getC_DocType();
				msg.append(Msg.getMsg(line.getCtx(), "ProcessFailed")).append(": ");
				if (Env.isBaseLanguage(line.getCtx(), I_C_DocType.Table_Name))
					msg.append(docType.getName());
				else
					msg.append(((PO)docType).get_Translation(I_C_DocType.COLUMNNAME_Name));
				throw new AdempiereUserError(msg.toString());
			}
			costingDoc.saveEx();
		}
		
	}
	


}
