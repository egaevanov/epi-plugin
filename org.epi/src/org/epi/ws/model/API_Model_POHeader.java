package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_POHeader {
	
	public String OrderReference;
	public Integer C_BPartner_ID;
	public Integer C_BPartner_Location_ID;
	public String DateOrdered;
	public Integer M_Warehouse_ID;
	public Integer SalesRep_ID;
	public Integer M_PriceList_ID;
	public Integer PaymentRule;
	public Integer C_Currency_ID;
	public Integer C_PaymentTerm_ID;
	public BigDecimal Grandtotal;
	public BigDecimal TotaLines;
	public String DeliveryViaRule;// Pickup & Non Pickup
	public String Description;
	public String IsPickUP;
	public Integer C_TaxCategory_ID;
	public Integer C_BankAccount_ID;
	public BigDecimal DiscountAmt;
	public String POReference;
	public Integer Wise_Order_ID;
	public String Wise_PR_Number;
	public String Wise_PO_Number;
	public String Wise_Title;
	public String Wise_PO_Type;
	public String Wise_Unit_Type;
	public String Wise_Delivery_Point;
	public String Wise_Remark;
	
	
	public API_Model_POHeader(
			String OrderReference,
			Integer C_BPartner_ID, 
			Integer C_BPartner_Location_ID,
			String DateOrdered, 
			Integer M_Warehouse_ID,
			Integer SalesRep_ID,
			Integer M_PriceList_ID, 
			Integer PaymentRule, 
			Integer C_Currency_ID,
			Integer C_PaymentTerm_ID, 
			BigDecimal Grandtotal,
			BigDecimal TotalLines,
			String DeliveryViaRule, 
			String Description,
			Integer C_TaxCategory_ID, 
			BigDecimal DiscountAmt,
			String POReference,
			Integer Wise_Order_ID,
			String Wise_PR_Number,
			String Wise_PO_Number,
			String Wise_Title,
			String Wise_PO_Type,
			String Wise_Unit_Type,
			String Wise_Delivery_Point,
			String Wise_Remark) {
		
		this.OrderReference = OrderReference;
		this.C_BPartner_ID = C_BPartner_ID;
		this.C_BPartner_Location_ID = C_BPartner_Location_ID;
		this.DateOrdered = DateOrdered;
		this.M_Warehouse_ID = M_Warehouse_ID;
		this.SalesRep_ID = SalesRep_ID;
		this.M_PriceList_ID = M_PriceList_ID;
		this.PaymentRule = PaymentRule;
		this.C_Currency_ID = C_Currency_ID;
		this.C_PaymentTerm_ID = C_PaymentTerm_ID;
		this.Grandtotal = Grandtotal;
		this.TotaLines = TotalLines;
		this.DeliveryViaRule = DeliveryViaRule;
		this.Description = Description;
		this.C_TaxCategory_ID = C_TaxCategory_ID;
		this.DiscountAmt = DiscountAmt;
		this.POReference = POReference;
		this.Wise_Order_ID = Wise_Order_ID;
		this.Wise_PR_Number = Wise_PR_Number;
		this.Wise_PO_Number = Wise_PO_Number;
		this.Wise_Title = Wise_Title;
		this.Wise_PO_Type = Wise_PO_Type;
		this.Wise_Unit_Type = Wise_Unit_Type;
		this.Wise_Delivery_Point = Wise_Delivery_Point;
		this.Wise_Remark = Wise_Remark;
		
	}
	
}
