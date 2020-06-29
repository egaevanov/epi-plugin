package org.epi.process;

import java.math.BigDecimal;

import org.compiere.model.MStorageOnHand;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

public class EPIQtyValidatorGlobalOH {
	protected static CLogger	log = CLogger.getCLogger(EPIQtyValidatorGlobalOH.class);
	
	public static BigDecimal getAvailableCoalQtyPerWH(int M_Product_ID,int M_Warehouse_ID, int M_AttributeSetInstance_ID, String trxName){
	
		BigDecimal rs = Env.ZERO;
		
	
		rs = MStorageOnHand.getQtyOnHand(M_Product_ID, M_Warehouse_ID, M_AttributeSetInstance_ID, trxName);
	
		
		return rs;
	
	}
	
	
	public static BigDecimal getAvailableCoalQtyPerLocator(int M_Product_ID,int M_Locator_ID, int M_AttributeSetInstance_ID, String trxName){
		
		BigDecimal rs = Env.ZERO;
		

		rs = MStorageOnHand.getQtyOnHandForLocator(M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID, trxName);

		
		return rs;
		
	}

	
}
