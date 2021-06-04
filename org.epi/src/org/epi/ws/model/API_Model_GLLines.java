package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_GLLines {

	public Integer AD_Org_ID;
	public Integer GL_Journal_ID;
	public String Description;
	public Integer C_Currency_ID;
	public Integer Account_ID;
	public BigDecimal AmtSourceDr;
	public BigDecimal AmtSourceCr;
	public BigDecimal AmtAcct;
	public Integer C_ConversionType_ID;
	public String DateAcct;
	
	public API_Model_GLLines(
			Integer AD_Org_ID,
			Integer GL_Journal_ID,
			String Description,
			Integer C_Currency_ID,
			Integer Account_ID,
			BigDecimal AmtSourceDr,
			BigDecimal AmtSourceCr,
			BigDecimal AmtAcct,
			Integer C_ConversionType_ID,
			String DateAcct) {
		
		this.AD_Org_ID = AD_Org_ID; 
		this.GL_Journal_ID = GL_Journal_ID; 
		this.Description = Description; 
		this.C_Currency_ID = C_Currency_ID; 
		this.Account_ID = Account_ID; 
		this.AmtSourceDr = AmtSourceDr; 
		this.AmtSourceCr = AmtSourceCr; 
		this.AmtAcct = AmtAcct; 
		this.C_ConversionType_ID = C_ConversionType_ID; 
		this.DateAcct = DateAcct; 
		
	}
	
}
