package org.epi.validator;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_M_InOutLineDtl;
import org.epi.process.EPIInOutSupport;
import org.epi.process.EPIParameterSupport;
import org.osgi.service.event.Event;

public class EPIInOutValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIInOutValidator.class);

	public static String executeCreateInternalUse(Event event, PO po) {
		
		String msgInOut = "";
		MInOut InOut = (MInOut) po;
		if (event.getTopic().equals(IEventTopics.DOC_BEFORE_COMPLETE)) {
			
			msgInOut = InOutBeforeComplete(InOut);
			
		}else if(event.getTopic().equals(IEventTopics.DOC_BEFORE_REVERSECORRECT)) {
			
			msgInOut = InOutBeforeReverseCorrect(InOut);
			
		}
		
	return msgInOut;

	}

	
	private static String InOutBeforeComplete(MInOut InOut) {
		String rslt = "";
		
		
		if(!InOut.isReversal() && InOut.isSOTrx()) {
			
			//Auto Create Physical Inventory if negative inven
			int C_Order_ID = InOut.getC_Order_ID();
			
			MOrder ord = new MOrder(InOut.getCtx(), C_Order_ID, null);
			MDocType DocOrd = new MDocType(ord.getCtx(), ord.getC_DocTypeTarget_ID(), null);
			
			//Check Qty Available
			
			if(DocOrd.getName().toLowerCase().contentEquals("full package order")) {
				
				HashMap<Integer, BigDecimal> todo = EPIInOutSupport.getInOutDtlNegativeInven(InOut);
				
				if(todo.size() > 0) {
					
					MInventory inventory = new MInventory (InOut.getCtx(), 0, InOut.get_TrxName());
					
					Integer p_C_DocTypePI_ID = EPIParameterSupport.getDocType(InOut.getAD_Client_ID(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, MDocType.DOCSUBTYPEINV_PhysicalInventory, "N");	

					inventory.setC_DocType_ID(p_C_DocTypePI_ID);
					inventory.setClientOrg(InOut.getAD_Client_ID(), InOut.getAD_Org_ID());
					inventory.setDescription("Adjustment QTY - Shipment : "+ InOut.getDocumentNo());					
					inventory.setM_Warehouse_ID(InOut.getM_Warehouse_ID());
					inventory.setMovementDate(InOut.getMovementDate());
					inventory.set_CustomColumnReturningBoolean("IsQtyAdjustment", true);
					inventory.saveEx();
					
					for (Integer key : todo.keySet()) {
						
						BigDecimal diffQty = todo.get(key);
						X_M_InOutLineDtl dtl = new X_M_InOutLineDtl(Env.getCtx(),key, null);

						MInventoryLine line = new MInventoryLine (inventory, dtl.getM_Locator_ID(), dtl.getM_Product_ID(), 0 , Env.ZERO , diffQty, Env.ZERO);
						line.setDescription("Shipment Line Detail :"  +dtl.getLineNo());
						line.setInventoryType(MInventoryLine.INVENTORYTYPE_InventoryDifference);
						line.saveEx();
						
						if(line.save()) {
							
							MProduct prod = new MProduct(dtl.getCtx(), line.getM_Product_ID(), null);
							EPIInOutSupport.updateCosting(inventory, prod, line);
							
						}	
						
					}
					
					if (inventory != null) {		
							if (!inventory.processIt("CO")) {
								log.warning("Inventory Process Failed: " + inventory + " - " + inventory.getProcessMsg());
								throw new IllegalStateException("Inventory Process Failed: " + inventory + " - " + inventory.getProcessMsg());

							}
							inventory.saveEx();
						}

				}
	
			}
			//end auto update stock if negative inven

			StringBuilder SQLGetSOLineDetail = new StringBuilder();
			StringBuilder SQLGetMRLineDetail = new StringBuilder();
	
			MOrderLine ordLine = null;
			MInOutLine[] lines = InOut.getLines();
			
			for(MInOutLine line : lines) {
				
				Integer M_Product_ID = line.getM_Product_ID();
				
				if(M_Product_ID > 0 ) {
					
					MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);
					
					if(!product.get_ValueAsBoolean("IsAutoShipment")) {
						
						int C_OrderLine_ID  = line.getC_OrderLine_ID();
						if(C_OrderLine_ID > 0) {
							
							ordLine = new MOrderLine(InOut.getCtx(), C_OrderLine_ID, InOut.get_TrxName());
						}
			
						SQLGetSOLineDetail.append("SELECT C_OrderLineDtl_ID ");
						SQLGetSOLineDetail.append(" FROM C_OrderLineDtl ");
						SQLGetSOLineDetail.append(" WHERE AD_Client_ID = ?");
						SQLGetSOLineDetail.append(" AND C_OrderLine_ID = ?");
						
						Integer C_SOLineDtl_ID = DB.getSQLValue(null, SQLGetSOLineDetail.toString(), new Object[] {line.getAD_Client_ID(),ordLine.getC_OrderLine_ID()});
						
						if(C_SOLineDtl_ID  > 0) {
						
							MInventory internalUse = new MInventory(InOut.getCtx(), 0, InOut.get_TrxName());
							Integer C_DocTypeIU_ID = EPIParameterSupport.getDocType(line.getAD_Client_ID(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, MDocType.DOCSUBTYPEINV_InternalUseInventory, "N");	
							int lineCount = 0;
							
							internalUse.setC_DocType_ID(C_DocTypeIU_ID);
							internalUse.setAD_Org_ID(line.getAD_Org_ID());
							MLocator loc = new MLocator(line.getCtx(), line.getM_Locator_ID(), line.get_TrxName());
							internalUse.setM_Warehouse_ID(loc.getM_Warehouse_ID());
							internalUse.setMovementDate(InOut.getMovementDate());
							internalUse.saveEx();
							
							line.set_CustomColumn("M_Inventory_ID", internalUse.getM_Inventory_ID());
							line.saveEx();	
							
							SQLGetMRLineDetail.append("SELECT M_InOutLineDtl_ID ");
							SQLGetMRLineDetail.append(" FROM M_InOutLineDtl ");
							SQLGetMRLineDetail.append(" WHERE AD_Client_ID = ?");
							SQLGetMRLineDetail.append(" AND M_InOutLine_ID = ?");
							
							
							PreparedStatement pstmt = null;
					     	ResultSet rs = null;
								try {
									pstmt = DB.prepareStatement(SQLGetMRLineDetail.toString(), null);
									pstmt.setInt(1,line.getAD_Client_ID());	
									pstmt.setInt(2,line.getM_InOutLine_ID());	
				
									rs = pstmt.executeQuery();
									while (rs.next()) {
										
										X_M_InOutLineDtl LineDtl = new X_M_InOutLineDtl(line.getCtx(), rs.getInt(1), line.get_TrxName());
				
										MInventoryLine internalUseLine = new MInventoryLine (internalUse, LineDtl.getM_Locator_ID(), LineDtl.getM_Product_ID(), 0 , Env.ZERO , Env.ZERO, LineDtl.getQtyInternalUse());
													
										System.out.println(LineDtl.getM_Locator_ID());
										
										internalUseLine.setDescription(LineDtl.getDescription());
										internalUseLine.setC_Charge_ID(1000007);
										internalUseLine.set_CustomColumn("M_SaveInv_ID", LineDtl.get_ValueAsInt("M_SaveInv_ID"));
										internalUseLine.saveEx();	
										lineCount++;
										
										LineDtl.set_CustomColumn("M_Inventory_ID", internalUseLine.getM_Inventory_ID());
										LineDtl.set_CustomColumn("M_InventoryLine_ID", internalUseLine.getM_InventoryLine_ID());
										LineDtl.saveEx();
									
									}
				
								} catch (SQLException err) {
									
									log.log(Level.SEVERE, SQLGetMRLineDetail.toString(), err);
									
									
								} finally {
									
									DB.close(rs, pstmt);
									rs = null;
									pstmt = null;
									
								}	
						
							if(internalUse!= null && lineCount > 0) {
								internalUse.processIt(MInventory.DOCACTION_Complete);
								internalUse.saveEx();
							}
							
						}
						
					}
					
					
				}
				

						
			}
		
		}
	return rslt;
		
	}
	
	private static String InOutBeforeReverseCorrect(MInOut InOut) {
		
		String rslt = "";
		
		MInOutLine[] lines = InOut.getLines();
		Integer M_Inventory_ID = 0;
		
		for(MInOutLine line : lines) {
			
			M_Inventory_ID = line.get_ValueAsInt("M_Inventory_ID");
			
			if(M_Inventory_ID > 0) {
				MInventory inv = new MInventory(InOut.getCtx(), M_Inventory_ID, null);
				inv.processIt(MInventory.ACTION_Reverse_Correct);
				inv.saveEx();
			}
				
				
		}
			
		return rslt;
		
	}
	
}
