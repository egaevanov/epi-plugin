package org.epi.component;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.epi.model.I_C_OrderlineDtl;
import org.epi.model.I_C_Quotation;
import org.epi.model.I_C_QuotationLine;
import org.epi.model.I_ISM_Activity;
import org.epi.model.I_ISM_Budget_Line;
import org.epi.model.I_ISM_Budget_Planning;
import org.epi.model.I_ISM_Budget_Transaction;
import org.epi.model.I_ISM_Department;
import org.epi.model.I_M_InOutLineDtl;
import org.epi.model.I_M_SaveInv;
import org.epi.model.I_TBU_BAOperation;
import org.epi.model.I_TBU_OperationEquipment;
import org.epi.model.I_TBU_OperationEquipmentUnit;
import org.epi.model.I_TBU_OperationLine;
import org.epi.model.I_TBU_OperationService;
import org.epi.model.I_T_Report_Ledger;

/**
 * 
 * @author Tegar N
 *
 */

public class EPIModelFactory implements IModelFactory{

	private static HashMap<String, String> mapTableModels = new HashMap<String, String>();
	static
	{
		
		//Coal Receipt Flow - Goods Issue - Good Receipt Model
		mapTableModels.put(I_M_SaveInv.Table_Name, "org.epi.model.X_M_SaveInv");
		mapTableModels.put(I_M_InOutLineDtl.Table_Name, "org.epi.model.X_M_InOutLineDtl");
		mapTableModels.put(I_C_OrderlineDtl.Table_Name, "org.epi.model.X_C_OrderlineDtl");
		
		//Budget Planning Model
		mapTableModels.put(I_ISM_Activity.Table_Name, "org.epi.model.X_ISM_Activity");
		mapTableModels.put(I_ISM_Department.Table_Name, "org.epi.model.X_ISM_Department");
		mapTableModels.put(I_ISM_Budget_Planning.Table_Name, "org.epi.model.X_ISM_Budget_Planning");
		mapTableModels.put(I_ISM_Budget_Line.Table_Name, "org.epi.model.X_ISM_Budget_Line");
		mapTableModels.put(I_ISM_Budget_Transaction.Table_Name, "org.epi.model.X_ISM_Budget_Transaction");
		mapTableModels.put(I_T_Report_Ledger.Table_Name, "org.epi.model.X_T_Report_Ledger");
		
		//TBU Operation
		mapTableModels.put(I_TBU_OperationEquipment.Table_Name, "org.epi.model.X_TBU_OperationEquipment");
		mapTableModels.put(I_TBU_OperationEquipmentUnit.Table_Name, "org.epi.model.X_TBU_OperationEquipmentUnit");
		mapTableModels.put(I_TBU_OperationService.Table_Name, "org.epi.model.X_TBU_OperationService");
		mapTableModels.put(I_TBU_BAOperation.Table_Name, "org.epi.model.MBAOperation");
		mapTableModels.put(I_TBU_OperationLine.Table_Name, "org.epi.model.X_TBU_OperationLine");

		mapTableModels.put(I_C_Quotation.Table_Name, "org.epi.model.X_C_Quotation");
		mapTableModels.put(I_C_QuotationLine.Table_Name, "org.epi.model.X_C_QuotationLine");

	}
	
	@Override
	public Class<?> getClass(String tableName) {
		
		if (mapTableModels.containsKey(tableName)) {
			Class<?> act = null;
			try {
				act = Class.forName(mapTableModels.get(tableName));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
				return act;
		
		} else 
			return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		
		if (mapTableModels.containsKey(tableName)) {
			Class<?> clazz = null;
			Constructor<?> ctor = null;
			PO object = null;
			try {
				clazz = Class.forName(mapTableModels.get(tableName));
				ctor = clazz.getConstructor(Properties.class, int.class, String.class);
				object = (PO) ctor.newInstance(new Object[] {Env.getCtx(), Record_ID, trxName});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
				return object;
		} else 	   
		   return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
	
		if (mapTableModels.containsKey(tableName)) {
			Class<?> clazz = null;
			Constructor<?> ctor = null;
			PO object = null;
			try {
				clazz = Class.forName(mapTableModels.get(tableName));
				ctor = clazz.getConstructor(Properties.class, ResultSet.class, String.class);
				object = (PO) ctor.newInstance(new Object[] {Env.getCtx(), rs, trxName});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
				return object;
				
		} else  
			return null;
	}

	
}
