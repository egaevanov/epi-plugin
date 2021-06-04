package org.epi.ws.model;

public class API_Model_GL {
	
	public Integer AD_Org_ID;
	public Integer C_AcctSchema_ID;
	public String Description;
	public String PostingType;
	public String DateAcct;
	public Integer C_Period_ID;
	public String DateDoc;
	public Integer C_Currency_ID;
	public Integer C_DocType_ID;
	public Integer GL_Category_ID;
	public Integer C_ConversionType_ID;
	
	public API_Model_GL(
			Integer AD_Org_ID,
			Integer C_AcctSchema_ID,
			String Description,
			String PostingType,
			String DateAcct,
			Integer C_Period_ID,
			String DateDoc,
			Integer C_Currency_ID,
			Integer C_DocType_ID,
			Integer GL_Category_ID,
			Integer C_ConversionType_ID) {
		
		this.AD_Org_ID = AD_Org_ID;
		this.C_AcctSchema_ID = C_AcctSchema_ID;
		this.Description = Description;
		this.PostingType = PostingType;
		this.DateAcct = DateAcct;
		this.C_Period_ID = C_Period_ID;
		this.DateDoc = DateDoc;
		this.C_Currency_ID = C_Currency_ID;
		this.C_DocType_ID = C_DocType_ID;
		this.GL_Category_ID = GL_Category_ID;
		this.C_ConversionType_ID = C_ConversionType_ID;
	}

}
