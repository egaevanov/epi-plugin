package org.epi.component;

import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoice;
import org.compiere.util.CLogger;
import org.epi.createfrom.WEPICreateFromInvoiceUI;

public class EPICreateFromFactory implements ICreateFromFactory  {
	
	protected CLogger log = CLogger.getCLogger(getClass());

	
	@Override
	public ICreateFrom create(GridTab mTab) {

		String tableName = mTab.getTableName();
		
		if (tableName.equals(MInvoice.Table_Name))
			return new WEPICreateFromInvoiceUI(mTab);
			
			
		return null;
		
		
	}
	

}
