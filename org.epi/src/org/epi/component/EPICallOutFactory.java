package org.epi.component;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.I_M_RequisitionLine;
import org.epi.callout.EPICallOutInOutLine;
import org.epi.callout.EPICallOutInvoice;
import org.epi.callout.EPICallOutMaterialSave;
import org.epi.callout.EPICallOutOrderLine;
import org.epi.callout.EPICallOutOrderLineDetail;
import org.epi.callout.EPICallOutRequisitionLine;
import org.epi.callout.EPICalloutInvoiceLineDetail;
import org.epi.callout.EPICalloutShipmentLineDetail;
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
			list.add (new EPICallOutMaterialSave());
		}else if (tableName.equals(I_C_OrderlineDtl.Table_Name)){
			list.add (new EPICallOutOrderLineDetail());
		}else if (tableName.equals(I_M_InOutLine.Table_Name)){
			list.add (new EPICallOutInOutLine());
		}else if(tableName.equals(I_M_InOutLineDtl.Table_Name)){
			list.add(new EPICalloutShipmentLineDetail());
		}else if(tableName.equals(I_C_InvoiceLineDtl.Table_Name)){
			list.add(new EPICalloutInvoiceLineDetail());
		}else if(tableName.equals(I_M_RequisitionLine.Table_Name)) {
			list.add(new EPICallOutRequisitionLine());
		}else if(tableName.equals(I_C_OrderLine.Table_Name)) {
			list.add(new EPICallOutOrderLine());
		}else if(tableName.equals(I_C_Invoice.Table_Name)) {
			list.add(new EPICallOutInvoice());
		}

		
		return list != null ? list.toArray(new IColumnCallout[0])
				: new IColumnCallout[0];
	}

}
