package org.epi.validator;

import java.math.BigDecimal;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.epi.model.X_ISM_Budget_Line;
import org.epi.process.EPICalculateSaldoBudget;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIOrderLineValidator {
	
	
	public static CLogger log = CLogger.getCLogger(EPIOrderLineValidator.class);

	public static String executeOrderLine(Event event, PO po) {
		
		String msgInv= "";
		MOrderLine ordLine = (MOrderLine) po;
		
		MOrg org = new MOrg(ordLine.getCtx(), ordLine.getAD_Org_ID(), null);
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)||event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE)) {		
				msgInv = beforeSaveEPI(ordLine,event);
			}			
		}
		
	return msgInv;

	}
	
	
	private static String beforeSaveEPI(MOrderLine OrdLine,Event event) {
		
		String rslt = "";
		MOrder ord = new MOrder(Env.getCtx(), OrdLine.getC_Order_ID(), null);
		BigDecimal OrderSaldo = Env.ZERO;
		BigDecimal CurrentLineSaldo = Env.ZERO;
		BigDecimal openSaldo = EPICalculateSaldoBudget.CalculateBudgetSaldo(OrdLine.getAD_Client_ID(), OrdLine.get_ValueAsInt("ISM_Budget_Line_ID"), OrdLine.getC_Order_ID(), OrdLine.getDateOrdered());
		X_ISM_Budget_Line budgetLine = new X_ISM_Budget_Line(Env.getCtx(), OrdLine.get_ValueAsInt("ISM_Budget_Line_ID"), null);
		
		
		
		CurrentLineSaldo = OrdLine.getLineNetAmt();
		
		if(!ord.isSOTrx()) {		
			
		
			MOrderLine lines[] = ord.getLines();
			
			for(MOrderLine line : lines) {
				
				OrderSaldo = OrderSaldo.add(line.getLineNetAmt());
				
			}
			
			if(event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE)){
				
				OrderSaldo = OrderSaldo.subtract((BigDecimal)OrdLine.get_ValueOld("LineNetAmt"));
				
			}
			
			
			BigDecimal Open = openSaldo.subtract(OrderSaldo);
			
			
			if(Open.compareTo(CurrentLineSaldo) < 0) {
				
				return rslt = "Transaksi sudah melebihi saldo budget. Saldo budget "+budgetLine.getBudget_Code()+"  saat ini adalah " + Open;
			}
			
					
		}
		
		return rslt;
		
	}

}
