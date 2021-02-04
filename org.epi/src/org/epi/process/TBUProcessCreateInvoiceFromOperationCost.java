package org.epi.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MCharge;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_TBU_BAOperation;
import org.epi.model.X_TBU_OperationService;


public class TBUProcessCreateInvoiceFromOperationCost extends SvrProcess{
	
	
	
	private int p_C_BPartner_ID = 0;
	private Timestamp p_StartDate = null;
	private Timestamp p_EndDate = null;
	private int p_C_Activity_ID = 0;
	private int p_C_Invoice_ID = 0;
	private int p_C_Project_ID = 0;
	private int p_ISM_Department_ID = 0;
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_BPartner_ID"))
				p_C_BPartner_ID  = (int)para[i].getParameterAsInt();
			else if(name.equals("StartDate"))
				p_StartDate  = (Timestamp)para[i].getParameterAsTimestamp();
			else if(name.equals("EndDate"))
				p_EndDate  = (Timestamp)para[i].getParameterAsTimestamp();
			else if(name.equals("C_Activity_ID"))
				p_C_Activity_ID = (int)para[i].getParameterAsInt();
			else if(name.equals("C_Project_ID"))
				p_C_Project_ID= (int)para[i].getParameterAsInt();
			else if(name.equals("ISM_Department_ID"))
				p_ISM_Department_ID = (int)para[i].getParameterAsInt();
		
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_C_Invoice_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		
		StringBuilder SQLgetDoctype = new StringBuilder();
		
		SQLgetDoctype.append("SELECT C_DocType_ID ");
		SQLgetDoctype.append(" FROM C_DocType ");
		SQLgetDoctype.append(" WHERE AD_Client_ID = "+getAD_Client_ID());
		SQLgetDoctype.append(" AND Name = 'Operation Cost' ");
		int C_DocType_ID = DB.getSQLValueEx(get_TrxName(), SQLgetDoctype.toString());


		StringBuilder SQLGetData = new StringBuilder();
		SQLGetData.append("SELECT TBU_BAOperation_ID ");
		SQLGetData.append(" FROM TBU_BAOperation ");
		SQLGetData.append(" WHERE C_BPartner_ID = ?");
		SQLGetData.append(" AND DocStatus = 'CO'");
		SQLGetData.append(" AND IsInvoiced = 'N'");
		SQLGetData.append(" AND C_Invoice_ID IS NULL");
		SQLGetData.append(" AND DateOperation BETWEEN '"+p_StartDate+"' AND '"+p_EndDate+"'");
		SQLGetData.append(" AND C_DocType_ID = ?");
		if(p_C_Activity_ID > 0) {
			SQLGetData.append(" AND C_Activity_ID = ?");
		}
		
		if(p_C_Project_ID > 0) {
			SQLGetData.append(" AND C_Project_ID = ?");
		}

		if(p_ISM_Department_ID > 0) {
			SQLGetData.append(" AND ISM_Department_ID = ?");
		}

		
		BigDecimal SumGrandTotal = Env.ZERO;
		MInvoice invoice = new MInvoice(getCtx(), p_C_Invoice_ID, get_TrxName());

		
		StringBuilder SQLGetDataLine = new StringBuilder();
		SQLGetDataLine.append("SELECT DISTINCT (TBU_OperationService_ID)");
		SQLGetDataLine.append(" FROM TBU_OperationLine");
		SQLGetDataLine.append(" WHERE TBU_BAOperation_ID IN (");
		
		String IDs = "";
		String SQLGetDataLineStr = "";
		
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLGetData.toString(), null);
				pstmt.setInt(1,p_C_BPartner_ID);	
				pstmt.setInt(2,C_DocType_ID);	
				if(p_C_Activity_ID > 0) {
					pstmt.setInt(3,p_C_Activity_ID);	
				}
				if(p_C_Project_ID > 0) {
					pstmt.setInt(4,p_C_Project_ID);	
				}
				if(p_ISM_Department_ID > 0) {
					pstmt.setInt(5,p_ISM_Department_ID);	
				}
				rs = pstmt.executeQuery();
				int rowCountIndex = 0;
				while (rs.next()) {
					rowCountIndex++;
					
					X_TBU_BAOperation BAOp = new X_TBU_BAOperation(getCtx(), rs.getInt(1), get_TrxName());
					BAOp.setC_Invoice_ID(invoice.getC_Invoice_ID());
					BAOp.saveEx();
					
					SumGrandTotal = SumGrandTotal.add(BAOp.getGrandTotal());
					IDs = IDs+BAOp.getTBU_BAOperation_ID()+",";
					
					
					invoice.setAD_Org_ID(BAOp.getAD_Org_ID());
					invoice.setC_BPartner_ID(BAOp.getC_BPartner_ID());
					invoice.setC_Project_ID(BAOp.getC_Project_ID());
					invoice.setC_Activity_ID(BAOp.getC_Activity_ID());
					invoice.set_CustomColumn("ISM_Department_ID", BAOp.getISM_Department_ID());
														
				}

				
			if(rowCountIndex == 0) {
				
				return "Tidak Ada Data BA Operation yang Tergenerate";
			}	
			invoice.setGrandTotal(SumGrandTotal);	
			invoice.saveEx();
			
			
			 
				
			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetData.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
			
			
			if (IDs != null && IDs.length() > 0 && IDs.charAt(IDs.length() - 1) == ',') {
			        SQLGetDataLineStr = IDs.substring(0, IDs.length() - 1)+")"; 
			}
			 
			
			StringBuilder SQLGetLineBAOperation = new StringBuilder();
			SQLGetLineBAOperation.append("SELECT SUM(Qty*Price)");
			SQLGetLineBAOperation.append(" FROM TBU_OperationLine ");
			SQLGetLineBAOperation.append(" WHERE TBU_OperationService_ID = ?");
			SQLGetLineBAOperation.append(" AND TBU_BAOperation_ID IN (");
			

			
			int lineNo = 10;
			
			PreparedStatement pstmtLine = null;
	     	ResultSet rsLine = null;
				try {
					pstmtLine = DB.prepareStatement(SQLGetDataLine+SQLGetDataLineStr, null);
					
					rsLine = pstmtLine.executeQuery();
					while (rsLine.next()) {
						
						
						X_TBU_OperationService serv = new X_TBU_OperationService(getCtx(), rsLine.getInt(1), get_TrxName());
						
						MInvoiceLine invLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
						invLine.setAD_Org_ID(invoice.getAD_Org_ID());
						invLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
						invLine.setC_Charge_ID(serv.getC_Charge_ID());
						invLine.setLine(lineNo);
						
						MCharge charge = new MCharge(getCtx(), serv.getC_Charge_ID(), get_TrxName());
					
						StringBuilder getTax = new StringBuilder();
						getTax.append("SELECT C_Tax_ID ");
						getTax.append(" FROM  C_Tax ");
						getTax.append(" WHERE C_TaxCategory_ID = "+ charge.getC_TaxCategory_ID());
						getTax.append(" AND AD_Client_ID =  "+invoice.getAD_Client_ID());
						Integer C_Tax_ID = DB.getSQLValueEx(get_TrxName(), getTax.toString());
						
						invLine.setC_Tax_ID(C_Tax_ID);
						invLine.setQtyEntered(Env.ONE);
						invLine.setQty(Env.ONE);
						invLine.setQtyInvoiced(Env.ONE);
						
						BigDecimal price = DB.getSQLValueBD(get_TrxName(), SQLGetLineBAOperation.toString()+SQLGetDataLineStr, new Object[] {serv.getTBU_OperationService_ID()});
						
						invLine.setPriceEntered(price);
						invLine.setPriceActual(price);
						invLine.saveEx();
												
						lineNo = lineNo+10;
					}

				
				} catch (SQLException err) {
					
					log.log(Level.SEVERE,SQLGetDataLine+SQLGetDataLineStr, err);
					rollback();
					
				} finally {
					
					DB.close(rsLine, pstmtLine);
					rsLine = null;
					pstmtLine = null;
					
				}	
			
			
		
		return null;
	}

}
