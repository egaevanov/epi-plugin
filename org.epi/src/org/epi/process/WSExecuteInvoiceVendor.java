package org.epi.process;

import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPriceList;
import org.compiere.util.DB;
import org.epi.ws.model.API_Model_InvoiceVendor;
import org.epi.ws.model.API_Model_InvoiceVendorLines;

public class WSExecuteInvoiceVendor {

public static Integer CreateInvoiceVendor(int AD_Client_ID, int AD_Org_ID, API_Model_InvoiceVendor dataHeader, API_Model_InvoiceVendorLines[] dataDetails,Properties ctx , String trxName) {
		
		Integer rs = 0;
		
		try {		
						
			StringBuilder SQLGetDocType = new StringBuilder();
			SQLGetDocType.append("SELECT C_DocType_ID");
			SQLGetDocType.append(" FROM C_DocType ");
			SQLGetDocType.append(" WHERE DocBaseType = 'API'");
			SQLGetDocType.append(" AND AD_Client_ID = " + AD_Client_ID);
			SQLGetDocType.append(" AND Name = 'AP Invoice'");

			Integer C_DocType_ID = DB.getSQLValueEx(trxName, SQLGetDocType.toString());
			MInvoice Inv = new MInvoice(ctx, 0, trxName);

			Inv.setAD_Org_ID(AD_Org_ID);
			Inv.setC_DocType_ID(C_DocType_ID);
//			Inv.setC_Order_ID(p_C_Order_ID);
			
			StringBuilder SQLGetBP = new StringBuilder();
			SQLGetBP.append("SELECT C_BPartner_ID");
			SQLGetBP.append(" FROM C_BPartner ");
			SQLGetBP.append(" WHERE Name2 = '"+dataHeader.vendor_id+"'");
			SQLGetBP.append(" AND AD_Client_ID = " + AD_Client_ID);
			Integer vendor_id = DB.getSQLValueEx(trxName, SQLGetBP.toString());
			
			StringBuilder SQLGetBPLoc = new StringBuilder();
			SQLGetBPLoc.append("SELECT C_BPartner_Location_ID");
			SQLGetBPLoc.append(" FROM C_BPartner_Location ");
			SQLGetBPLoc.append(" WHERE AD_Client_ID = " + AD_Client_ID);
			SQLGetBPLoc.append(" AND C_BPartner_ID = " + vendor_id);

			Integer C_BPartner_Location_ID = DB.getSQLValueEx(trxName, SQLGetBPLoc.toString());

			
			Inv.setC_BPartner_ID(vendor_id);
			Inv.setC_BPartner_Location_ID(C_BPartner_Location_ID);
			Inv.setM_PriceList_ID(1000016);
			Inv.setC_Currency_ID(303);
			Inv.setPaymentRule("P");		
			Inv.setC_PaymentTerm_ID(1000021);
			Inv.setIsSOTrx(false);

			MPriceList priceList = new MPriceList(ctx, 1000016, trxName);
			Inv.setIsTaxIncluded(priceList.isTaxIncluded());
			Inv.setDocAction("CO");
			Inv.setPOReference(dataHeader.invoice_reff_no);
			Timestamp DateInvoiced = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.invoice_date);
			Inv.setDateInvoiced(DateInvoiced);
			Inv.set_CustomColumn("URL_PO", dataHeader.url_po_attachment);
			Inv.set_CustomColumn("URL_GR", dataHeader.url_gr_attachment);


			Inv.saveEx();
			
			int noLine = 0;
			if(Inv.save()) {
				
				for (API_Model_InvoiceVendorLines DataDetail : dataDetails) {
					noLine = noLine+1;
					MInvoiceLine invLine = new MInvoiceLine(ctx, 0, trxName);

					invLine.setAD_Org_ID(AD_Org_ID);
					invLine.setC_Invoice_ID(Inv.getC_Invoice_ID());

					invLine.setC_Charge_ID(DataDetail.charge_id);
					invLine.setC_Tax_ID(DataDetail.c_tax_id);


					invLine.setLine(DataDetail.line);

					invLine.setQtyEntered(DataDetail.qty);
					invLine.setQty(DataDetail.qty);
					invLine.setQtyInvoiced(DataDetail.qty);

					invLine.setPriceEntered(DataDetail.price);
					invLine.setPriceActual(DataDetail.price);
					invLine.saveEx();
						
				}
							
			}
			
			if(noLine > 0) {
				Inv.setDocAction(MInvoice.DOCACTION_Complete);
//				Inv.processIt(MOrder.ACTION_Complete);
				
				if(Inv.save()) {
					
					rs = Inv.getC_Invoice_ID();
					
				}
				
			}
		
		} catch (Exception e) {

			rs = 0;
		
		}
		
		return rs;
		
		
	}



}
