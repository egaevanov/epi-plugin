package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_InvoiceVendor {
	
	public String invoice_reff_no;
	public String invoice_date;
	public String vendor_id;
	public String url_po_attachment;
	public String url_gr_attachment;
	public ArrayList<JsonObject> detail;
	
	
	public API_Model_InvoiceVendor(String invoice_reff_no,
			String invoice_date,
			String vendor_id,
			String url_po_attachment,
			String url_gr_attachment,
			ArrayList<JsonObject> detail) {
		
		
		this.invoice_reff_no=invoice_reff_no;
		this.invoice_date=invoice_date;
		this.vendor_id=vendor_id;
		this.url_po_attachment=url_po_attachment;
		this.url_gr_attachment=url_gr_attachment;
		this.detail=detail;
			
	}

}
