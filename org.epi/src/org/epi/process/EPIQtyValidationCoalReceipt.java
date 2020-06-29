package org.epi.process;
//package org.ism.process;
//
//import java.math.BigDecimal;
//
//import org.compiere.model.MDocType;
//import org.compiere.util.CLogger;
//import org.compiere.util.DB;
//import org.compiere.util.Env;
//import org.ism.model.X_M_SaveInv;
//
//public class ISMQtyValidationCoalReceipt {
//	
//	protected static CLogger	log = CLogger.getCLogger(ISMQtyValidationCoalReceipt.class);
//	
//	
//	public static BigDecimal getAvailableCoalQty(int AD_Client_ID, X_M_SaveInv X_M_SaveInv){
//		
//		BigDecimal rs = Env.ZERO;
//		
//		BigDecimal QtyCoalReceipt = X_M_SaveInv.getQtyEntered();
//		BigDecimal QtyInternalUse = getQtyInternalUse(AD_Client_ID, X_M_SaveInv.getM_SaveInv_ID());
//		BigDecimal QtyOnGoingShip = getQtyOnGoingShip(AD_Client_ID, X_M_SaveInv.getM_SaveInv_ID());
//
//		rs = QtyCoalReceipt.subtract(QtyInternalUse).subtract(QtyOnGoingShip);
//		
//		return rs;
//		
//	}
//	
//
//	public static BigDecimal getQtyInternalUse(int AD_Client_ID, int M_SaveInv_ID){
//		
//		BigDecimal rs = Env.ZERO;
//		
//		StringBuilder getDocType = new StringBuilder();
//		getDocType.append("SELECT C_DocType_ID");
//		getDocType.append(" FROM  C_DocType");
//		getDocType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
//		getDocType.append(" AND DocBaseType ='"+MDocType.DOCBASETYPE_MaterialPhysicalInventory+"'");
//		getDocType.append(" AND DocSubTypeInv ='"+MDocType.DOCSUBTYPEINV_InternalUseInventory+"'");
//
//		int C_DocType_ID = DB.getSQLValueEx(null, getDocType.toString());
//
//
//		StringBuilder SQLGetQtyInternalUse = new StringBuilder();
//		SQLGetQtyInternalUse.append("SELECT COALESCE(SUM(invLine.QtyInternalUse),0)  ");
//		SQLGetQtyInternalUse.append(" FROM M_InventoryLine invLine ");
//		SQLGetQtyInternalUse.append(" LEFT JOIN M_Inventory inv ON inv.M_Inventory_ID = invLine.M_Inventory_ID");
//		SQLGetQtyInternalUse.append(" WHERE invLine.AD_Client_ID = "+ AD_Client_ID );	
//		SQLGetQtyInternalUse.append(" AND invLine.M_SaveInv_ID = "+ M_SaveInv_ID );
//		SQLGetQtyInternalUse.append(" AND inv.C_DocType_ID = "+ C_DocType_ID );
//
//		
//		rs = DB.getSQLValueBDEx(null, SQLGetQtyInternalUse.toString());
//
//		return rs;
//		
//	}
//	
//	
//	public static BigDecimal getQtyOnGoingShip(int AD_Client_ID, int M_SaveInv_ID){
//		
//		BigDecimal rs = Env.ZERO;
//		
//		StringBuilder SQLGetQtyInternalUse = new StringBuilder();
//		SQLGetQtyInternalUse.append("SELECT COALESCE(SUM(QtyInternalUse),0)  ");
//		SQLGetQtyInternalUse.append(" FROM M_InOutLineDtl sldt ");
//		SQLGetQtyInternalUse.append(" LEFT JOIN M_InOutLine sl ON sl.M_InOutLine_ID = sldt.M_InOutLine_ID ");
//		SQLGetQtyInternalUse.append(" LEFT JOIN M_Product mp ON mp.M_Product_ID = sl.M_Product_ID ");
//		SQLGetQtyInternalUse.append(" WHERE sldt.AD_Client_ID = "+ AD_Client_ID );	
//		SQLGetQtyInternalUse.append(" AND sldt.M_Inventory_ID Is NULL" );
//		SQLGetQtyInternalUse.append(" AND sldt.M_InventoryLine_ID Is NULL" );
//		SQLGetQtyInternalUse.append(" AND mp.IsAutoShipment = 'N'" );
//		SQLGetQtyInternalUse.append(" AND sldt.M_SaveInv_ID = "+ M_SaveInv_ID);
//		
//		rs = DB.getSQLValueBDEx(null, SQLGetQtyInternalUse.toString());
//
//		return rs;
//		
//	}
//	
//	
//	
//	
//}
