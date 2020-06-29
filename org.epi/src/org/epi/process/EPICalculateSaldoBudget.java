package org.epi.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_ISM_Budget_Line;

public class EPICalculateSaldoBudget {

protected static CLogger	log = CLogger.getCLogger(EPICalculateSaldoBudget.class);
	
	
	public static BigDecimal CalculateBudgetSaldo(int AD_Client_ID, int ISM_BudgetLine_ID, int C_Order_IDexc, Timestamp DateOrdered){
		
		BigDecimal rs = Env.ZERO;
		
		BigDecimal saldoPerMonth = Env.ZERO;
		BigDecimal saldoAllocated = Env.ZERO;
		BigDecimal saldoBooked = Env.ZERO;
		BigDecimal saldoLeft = Env.ZERO;
		
		X_ISM_Budget_Line budLine = new X_ISM_Budget_Line(Env.getCtx(), ISM_BudgetLine_ID, null);
		
		// Date set to Login Date
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateOrdered);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int month = cal.get(Calendar.MONTH)+1;
		int year = cal.get(Calendar.YEAR);
		
		
		if(month == 1 ) {
			saldoPerMonth = budLine.getJan();
		}else if(month == 2) {
			saldoPerMonth = budLine.getFeb();
		}else if(month == 3) {
			saldoPerMonth = budLine.getMar();
		}else if(month == 4) {
			saldoPerMonth = budLine.getApr();
		}else if(month == 5) {
			saldoPerMonth = budLine.getMay();
		}else if(month == 6) {
			saldoPerMonth = budLine.getJun();
		}else if(month == 7) {
			saldoPerMonth = budLine.getJul();
		}else if(month == 8) {
			saldoPerMonth = budLine.getAug();
		}else if(month == 9) {
			saldoPerMonth = budLine.getSep();
		}else if(month == 10) {
			saldoPerMonth = budLine.getOct();
		}else if(month == 11) {
			saldoPerMonth = budLine.getNov();
		}else if(month == 12) {
			saldoPerMonth = budLine.getDec();
		}
		
		
		//Saldo Allocated
		
		StringBuilder Allocated = new StringBuilder();
		Allocated.append("SELECT SUM(BudgetAmt) as Allocated");
		Allocated.append(" FROM ISM_Budget_Transaction");
		Allocated.append(" WHERE AD_Client_ID = ? ");
		Allocated.append(" AND ISM_Budget_Line_ID = ? ");
		Allocated.append(" AND date_part('year', dateordered) = '"+year+"'");
		Allocated.append(" AND date_part('month', dateordered) = '"+month+"'");
		Allocated.append(" AND budget_status = 'AL'");
		Allocated.append(" AND budgetamt > 0 ");
		
		saldoAllocated = DB.getSQLValueBDEx(null, Allocated.toString(), new Object[] {budLine.getAD_Client_ID(),budLine.getISM_Budget_Line_ID()});
		
		StringBuilder Booked = new StringBuilder();
		Booked.append("SELECT SUM(BudgetAmt) as Booked");
		Booked.append(" FROM ISM_Budget_Transaction");
		Booked.append(" WHERE AD_Client_ID = ? ");
		Booked.append(" AND ISM_Budget_Line_ID = ? ");
		Booked.append(" AND date_part('year', dateordered) = '"+year+"'");
		Booked.append(" AND date_part('month', dateordered) = '"+month+"'");
		Booked.append(" AND budget_status = 'BO'");
		Booked.append(" AND budgetamt > 0 ");
		Booked.append(" AND C_Order_ID NOT IN  ("+C_Order_IDexc+")");
		
		saldoBooked= DB.getSQLValueBDEx(null, Booked.toString(), new Object[] {budLine.getAD_Client_ID(),budLine.getISM_Budget_Line_ID()});

		if(saldoAllocated == null) {
			saldoAllocated = Env.ZERO;
		}
		
		if(saldoBooked == null) {
			saldoBooked = Env.ZERO;
		}
		
		BigDecimal total = saldoAllocated.add(saldoBooked);
		
		saldoLeft = saldoPerMonth.subtract(total);
		
		rs = saldoLeft;
		
		
		return rs;
		
	}
	
}
