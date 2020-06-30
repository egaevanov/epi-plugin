package org.epi.component;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.I_M_RequisitionLine;
import org.epi.callout.CallOutInOutLine;
import org.epi.callout.CallOutInvoice;
import org.epi.callout.CallOutMaterialSave;
import org.epi.callout.CallOutOrderLine;
import org.epi.callout.CallOutOrderLineDetail;
import org.epi.callout.CallOutRequisitionLine;
import org.epi.callout.CallOutInvoiceLineDetail;
import org.epi.callout.CallOutShipmentLineDetail;
import org.epi.model.I_C_InvoiceLineDtl;
import org.epi.model.I_C_OrderlineDtl;
import org.epi.model.I_M_InOutLineDtl;
import org.epi.model.I_M_SaveInv;

/**
 * 
 * @author Tegar N
 *
 */

public class EPICallOutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName,
			String columnName) {

		List<IColumnCallout> list = new ArrayList<IColumnCallout>();
		
		if (tableName.equals(I_M_SaveInv.Table_Name)){
			list.add (new CallOutMaterialSave());
		}else if (tableName.equals(I_C_OrderlineDtl.Table_Name)){
			list.add (new CallOutOrderLineDetail());
		}else if (tableName.equals(I_M_InOutLine.Table_Name)){
			list.add (new CallOutInOutLine());
		}else if(tableName.equals(I_M_InOutLineDtl.Table_Name)){
			list.add(new CallOutShipmentLineDetail());
		}else if(tableName.equals(I_C_InvoiceLineDtl.Table_Name)){
			list.add(new CallOutInvoiceLineDetail());
		}else if(tableName.equals(I_M_RequisitionLine.Table_Name)) {
			list.add(new CallOutRequisitionLine());
		}else if(tableName.equals(I_C_OrderLine.Table_Name)) {
			list.add(new CallOutOrderLine());
		}else if(tableName.equals(I_C_Invoice.Table_Name)) {
			list.add(new CallOutInvoice());
		}

		
		return list != null ? list.toArray(new IColumnCallout[0])
				: new IColumnCallout[0];
	}

}
