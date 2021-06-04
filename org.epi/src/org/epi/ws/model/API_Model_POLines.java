package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_POLines {
	

	public String IsActive;
	public Integer C_Charge_ID;
	public Integer C_UOM_ID;
	public BigDecimal QtyOrdered;
	public BigDecimal PriceList;
	public BigDecimal PriceEntered;
	public BigDecimal PriceActual;
	public BigDecimal LineNetAmt;
	public Integer M_Locator_ID; 
	public BigDecimal DiscountAmt;
	public Integer C_Order_ID;
	
	//wise
	public String Wise_Material_Stock;
	public String Wise_Material_type;
	public String Wise_Part_Number;
	public String Wise_Description;
	public BigDecimal Wise_Qty;
	public String Wise_UOM;
	public String Wise_Amount;
	public String Wise_DeliveryDate;
	public String Wise_Asset_Number;
	public String Wise_Jasa;

	
	
	public API_Model_POLines(String IsActive, Integer C_Charge_ID,
			Integer C_UOM_ID, BigDecimal QtyOrdered, BigDecimal PriceList,
			BigDecimal PriceEntered, BigDecimal PriceActual,
			Integer M_AttributeSetInstance_ID, BigDecimal LineNetAmt, Integer M_Locator_ID, BigDecimal DiscountAmt,	Integer C_Order_ID,String Wise_Material_Stock,
			String Wise_Material_type,
			String Wise_Part_Number,
			String Wise_Description,
			BigDecimal Wise_Qty,
			String Wise_UOM,
			String Wise_Amount,
			String Wise_DeliveryDate,
			String Wise_Asset_Number,
			String Wise_Jasa) {

		this.IsActive = IsActive;
		this.C_Charge_ID = C_Charge_ID;
		this.C_UOM_ID = C_UOM_ID;
		this.QtyOrdered = QtyOrdered;
		this.PriceList = PriceList;
		this.PriceEntered = PriceEntered;
		this.PriceActual = PriceActual;
		this.LineNetAmt = LineNetAmt;
		this.M_Locator_ID = M_Locator_ID;
		this.DiscountAmt = DiscountAmt;
		this.C_Order_ID = C_Order_ID;
		
		this.Wise_Material_Stock = Wise_Material_Stock;
		this.Wise_Material_type = Wise_Material_type;
		this.Wise_Part_Number = Wise_Part_Number;
		this.Wise_Description = Wise_Description;
		this.Wise_Qty = Wise_Qty;
		this.Wise_UOM = Wise_UOM;
		this.Wise_Amount = Wise_Amount;
		this.Wise_DeliveryDate = Wise_DeliveryDate;
		this.Wise_Asset_Number = Wise_Asset_Number;
		this.Wise_Jasa = Wise_Jasa;
			
	}
	

}
