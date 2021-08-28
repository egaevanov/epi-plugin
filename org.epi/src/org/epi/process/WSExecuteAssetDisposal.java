package org.epi.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MAsset;
import org.compiere.model.MDocType;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.ws.model.API_Model_AssetDisposal;

public class WSExecuteAssetDisposal {
	
	
	
		public static Integer CreateAssetDisposal(int AD_Client_ID, int AD_Org_ID, API_Model_AssetDisposal dataHeader, Properties ctx , String trxName){
			
			Integer result = 0;
			
			try {
				
			
				
				StringBuilder SQLGetAcctSchema = new StringBuilder();
				SQLGetAcctSchema.append("SELECT C_AcctSchema1_ID ");
				SQLGetAcctSchema.append(" FROM AD_ClientInfo");
				SQLGetAcctSchema.append(" WHERE AD_Client_ID= "+Env.getAD_Client_ID(Env.getCtx()));
				
				Integer C_AcctSchema_ID = DB.getSQLValue(trxName, SQLGetAcctSchema.toString());
				
				
				StringBuilder SQLGetDocType = new StringBuilder();
				SQLGetDocType.append("SELECT C_DocType_ID ");
				SQLGetDocType.append(" FROM C_DocType");
				SQLGetDocType.append(" WHERE AD_Client_ID = "+Env.getAD_Client_ID(Env.getCtx()));
				SQLGetDocType.append(" AND DocBaseType =  '"+MDocType.DOCBASETYPE_GLJournal+"'");				
				
				Integer C_DocType_ID = DB.getSQLValue(trxName, SQLGetDocType.toString());
				
				
				StringBuilder SQLGetGLCategory = new StringBuilder();
				SQLGetGLCategory.append("SELECT GL_Category_ID ");
				SQLGetGLCategory.append(" FROM GL_Category");
				SQLGetGLCategory.append(" WHERE AD_Client_ID = "+Env.getAD_Client_ID(Env.getCtx()));
				SQLGetGLCategory.append(" AND AD_Org_ID =  "+AD_Org_ID);
				SQLGetGLCategory.append(" AND CategoryCode = 'DIS'");	
	
				Integer GL_Category_ID = DB.getSQLValue(trxName, SQLGetGLCategory.toString());
				
				
				Timestamp DateAcct = org.epi.utils.DataSetupValidation.convertStringToTimeStamp(dataHeader.disposal_date);
	
				Calendar calendar = Calendar.getInstance();
			    calendar.setTime(DateAcct);
			    calendar.add(Calendar.DAY_OF_WEEK, -1);

			    
			    @SuppressWarnings("static-access")
				Integer month = calendar.getInstance().get(Calendar.MONTH)+1;
			    @SuppressWarnings("static-access")
				Integer year = calendar.getInstance().get(Calendar.YEAR);
			    
			    
				StringBuilder SQLGetPeriod = new StringBuilder();
				SQLGetPeriod.append("SELECT C_Period_ID ");
				SQLGetPeriod.append(" FROM C_Period cp");
				SQLGetPeriod.append(" LEFT JOIN C_Year cy on cy.C_Year_ID = cp.C_Year_ID ");
				SQLGetPeriod.append(" WHERE cy.fiscalyear = '"+year+"'");
				SQLGetPeriod.append(" AND cp.periodno = '"+month+"'");

				Integer C_Period_ID = DB.getSQLValue(trxName, SQLGetPeriod.toString());
				
				
				StringBuilder SQLGetAsset = new StringBuilder();
				SQLGetAsset.append("SELECT A_Asset_ID ");
				SQLGetAsset.append(" FROM A_Asset ");
				SQLGetAsset.append(" WHERE AD_Org_ID ="+AD_Org_ID );
				SQLGetAsset.append(" AND InventoryNo ='"+dataHeader.asset_id+"'");
				
				Integer A_Asset_ID = DB.getSQLValueEx(trxName, SQLGetAsset.toString());
				
				if(A_Asset_ID <= 0)
					return 0;
				
			    MAsset asset = new MAsset(ctx, A_Asset_ID, trxName);
			    
			    
				MAcctSchema acctSchema = new MAcctSchema(ctx, C_AcctSchema_ID, trxName);
				
				MJournal journal = new MJournal(ctx, 0, trxName);
				journal.setAD_Org_ID(AD_Org_ID);
				journal.setC_AcctSchema_ID(C_AcctSchema_ID);
				journal.setDescription(dataHeader.description);
				journal.setPostingType("A");
				journal.setDateAcct(DateAcct);
				journal.setC_Period_ID(C_Period_ID);
				journal.setDateDoc(DateAcct);
				journal.setC_Currency_ID(acctSchema.getC_Currency_ID());
				journal.setC_DocType_ID(C_DocType_ID);
				journal.setGL_Category_ID(GL_Category_ID);
				journal.setC_ConversionType_ID(114);
				journal.set_ValueOfColumn("ReferenceNo", dataHeader.disposal_no);
				journal.set_ValueOfColumn("A_Disposed_Reason", dataHeader.disposal_reason);
				journal.set_ValueOfColumn("A_Disposed_Method", dataHeader.disposal_method);

				

				if(journal.save()) {
					
						
						if(dataHeader.acc_depre.compareTo(dataHeader.acc_depre_change)==0) {
							
							
							for (int i = 0 ; i < 2 ;i++) {
								Integer lineNo = i +10;
								
								
								
								MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
								
								journalLine.setLine(lineNo);
								journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
								journalLine.setAD_Org_ID(AD_Org_ID);
								journalLine.setDescription(dataHeader.description);
								journalLine.setA_Asset_ID(asset.getA_Asset_ID());
								journalLine.setC_Currency_ID(journal.getC_Currency_ID());
								journalLine.setC_Project_ID(dataHeader.project_id);
								journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
								journalLine.setDateAcct(journal.getDateAcct());


								if(i == 0) {
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Accumdepreciation_Acct");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.acc_depre);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.acc_depre, Env.ZERO);
									
									
								}else if ( i==1) {
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Asset_Acct ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(Env.ZERO);
									journalLine.setAmtSourceCr(dataHeader.acc_depre);
									journalLine.setAmtAcct(Env.ZERO,dataHeader.acc_depre);
														
								}
								
								journalLine.saveEx();

								
								
							}
							
							
							
						}else if(dataHeader.acc_depre.compareTo(dataHeader.acc_depre_change)>0) {
							
							for (int i = 0 ; i < 3 ;i++) {
								Integer lineNo = i +10;

								MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
								
								journalLine.setLine(lineNo);
								journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
								journalLine.setAD_Org_ID(AD_Org_ID);
								journalLine.setDescription(dataHeader.description);
								journalLine.setA_Asset_ID(asset.getA_Asset_ID());
								journalLine.setC_Currency_ID(journal.getC_Currency_ID());
								journalLine.setC_Project_ID(dataHeader.project_id);
								journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
								journalLine.setDateAcct(journal.getDateAcct());
								
								if(i==0) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Accumdepreciation_Acct");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.acc_depre);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.acc_depre, Env.ZERO);
									
								}else if(i==1) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Disposal_Loss_Acct  ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(Env.ZERO);
									journalLine.setAmtSourceCr(dataHeader.acc_depre.subtract(dataHeader.acc_depre_change));
									journalLine.setAmtAcct(Env.ZERO,dataHeader.acc_depre.subtract(dataHeader.acc_depre_change));
									
								}else if(i==2) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Asset_Acct ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(Env.ZERO);
									journalLine.setAmtSourceCr(dataHeader.acc_depre_change);
									journalLine.setAmtAcct(Env.ZERO,dataHeader.acc_depre_change);
									
								}
								
								journalLine.saveEx();
								
							}
							
						}else if(dataHeader.sold_amount.compareTo(((BigDecimal)asset.get_Value("AssetValue")).subtract(dataHeader.acc_depre))==0) {
							
							for (int i = 0 ; i < 3 ;i++) {
								
								Integer lineNo = i +10;

								MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
								
								journalLine.setLine(lineNo);
								journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
								journalLine.setAD_Org_ID(AD_Org_ID);
								journalLine.setDescription(dataHeader.description);
								journalLine.setA_Asset_ID(asset.getA_Asset_ID());
								journalLine.setC_Currency_ID(journal.getC_Currency_ID());
								journalLine.setC_Project_ID(dataHeader.project_id);
								journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
								journalLine.setDateAcct(journal.getDateAcct());
								
								if(i==0) {
									
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM C_BankAccount_Acct cbaa   ");
									SQLGetAcct.append(" INNER JOIN C_BankAccount cba ON cba.C_BankAccount_ID = cbaa.C_BankAccount_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = cbaa.B_Asset_Acct");
									SQLGetAcct.append(" WHERE cba.C_BankAccount_ID = "+dataHeader.bankaccount_id);
									
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.sold_amount);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.sold_amount, Env.ZERO);
									
								}else if(i==1) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Accumdepreciation_Acct");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.acc_depre);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.acc_depre, Env.ZERO);
									
								}else if(i==2) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Asset_Acct ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(Env.ZERO);
									journalLine.setAmtSourceCr((BigDecimal) asset.get_Value("AssetValue"));
									journalLine.setAmtAcct(Env.ZERO,(BigDecimal) asset.get_Value("AssetValue"));
									
								}
								
								journalLine.saveEx();
								
							}
							
						}else if(dataHeader.sold_amount.compareTo(((BigDecimal)asset.get_Value("AssetValue")).subtract(dataHeader.acc_depre))<0) {
							
							for (int i = 0 ; i < 4 ;i++) {
								
								Integer lineNo = i +10;

								MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
								
								journalLine.setLine(lineNo);
								journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
								journalLine.setAD_Org_ID(AD_Org_ID);
								journalLine.setDescription(dataHeader.description);
								journalLine.setA_Asset_ID(asset.getA_Asset_ID());
								journalLine.setC_Currency_ID(journal.getC_Currency_ID());
								journalLine.setC_Project_ID(dataHeader.project_id);
								journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
								journalLine.setDateAcct(journal.getDateAcct());
								
								
								if(i==0) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM C_BankAccount_Acct cbaa   ");
									SQLGetAcct.append(" INNER JOIN C_BankAccount cba ON cba.C_BankAccount_ID = cbaa.C_BankAccount_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = cbaa.B_Asset_Acct");
									SQLGetAcct.append(" WHERE cba.C_BankAccount_ID = "+dataHeader.bankaccount_id);
									
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.sold_amount);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.sold_amount, Env.ZERO);
									
								}else if(i==1){
								
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Accumdepreciation_Acct");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.acc_depre);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.acc_depre, Env.ZERO);
								
								}else if(i==2){
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Disposal_Loss_Acct  ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									BigDecimal amt = (((BigDecimal)asset.get_Value("AssetValue")).subtract(dataHeader.acc_depre)).subtract(dataHeader.sold_amount);
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(amt);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(amt,Env.ZERO);
							
								}else if(i==3){
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Asset_Acct ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(Env.ZERO);
									journalLine.setAmtSourceCr((BigDecimal) asset.get_Value("AssetValue"));
									journalLine.setAmtAcct(Env.ZERO,(BigDecimal) asset.get_Value("AssetValue"));
								}
								
								journalLine.saveEx();
								
							}
							
						}else if(dataHeader.sold_amount.compareTo(((BigDecimal)asset.get_Value("AssetValue")).subtract(dataHeader.acc_depre))>0) {
							
							for (int i = 0 ; i < 4 ;i++) {
								
								Integer lineNo = i +10;

								MJournalLine journalLine = new MJournalLine(ctx, 0, trxName);
								
								journalLine.setLine(lineNo);
								journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
								journalLine.setAD_Org_ID(AD_Org_ID);
								journalLine.setDescription(dataHeader.description);
								journalLine.setA_Asset_ID(asset.getA_Asset_ID());
								journalLine.setC_Currency_ID(journal.getC_Currency_ID());
								journalLine.setC_Project_ID(dataHeader.project_id);
								journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
								journalLine.setDateAcct(journal.getDateAcct());
								
								
								if(i==0) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM C_BankAccount_Acct cbaa   ");
									SQLGetAcct.append(" INNER JOIN C_BankAccount cba ON cba.C_BankAccount_ID = cbaa.C_BankAccount_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = cbaa.B_Asset_Acct");
									SQLGetAcct.append(" WHERE cba.C_BankAccount_ID = "+dataHeader.bankaccount_id);
									
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.sold_amount);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.sold_amount, Env.ZERO);
									
									
								}else if(i==1) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Accumdepreciation_Acct");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(dataHeader.acc_depre);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(dataHeader.acc_depre, Env.ZERO);
									
								}else if(i==2) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Disposal_Revenue_Acct ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									BigDecimal amt = dataHeader.sold_amount.subtract(((BigDecimal)asset.get_Value("AssetValue")).subtract(dataHeader.acc_depre));

									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(amt);
									journalLine.setAmtSourceCr(Env.ZERO);
									journalLine.setAmtAcct(amt, Env.ZERO);
									
								}else if(i==3) {
									
									StringBuilder SQLGetAcct = new StringBuilder();
									SQLGetAcct.append("SELECT cvc.Account_ID  ");
									SQLGetAcct.append(" FROM A_Asset_Group_Acct aagc  ");
									SQLGetAcct.append(" INNER JOIN A_Asset_Group aag ON aag.A_Asset_Group_ID = aagc.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN A_Asset aa ON aa.A_Asset_Group_ID = aag.A_Asset_Group_ID");
									SQLGetAcct.append(" INNER JOIN C_ValidCombination cvc ON cvc.C_ValidCombination_ID = aagc.A_Asset_Acct ");
									SQLGetAcct.append(" WHERE aa.A_Asset_ID = "+asset.getA_Asset_ID());
									
									Integer Account_ID = DB.getSQLValueEx(trxName, SQLGetAcct.toString());
									
									journalLine.setAccount_ID(Account_ID);
									journalLine.setAmtSourceDr(Env.ZERO);
									journalLine.setAmtSourceCr((BigDecimal) asset.get_Value("AssetValue"));
									journalLine.setAmtAcct(Env.ZERO,(BigDecimal) asset.get_Value("AssetValue"));
									
								}
								
								journalLine.saveEx();
								
							}
						
						}
						
				}
				
				
				journal.processIt(MJournal.ACTION_Complete);
				if(journal.save()) {
					
					StringBuilder updateData = new StringBuilder();
					updateData.append("UPDATE A_Asset ");
					updateData.append(" SET IsDisposed = 'Y' , ");
					if(dataHeader.disposal_reason.toUpperCase().equals("S1")) {
						updateData.append("A_Asset_Status ='SO',");

					}else {
						updateData.append("A_Asset_Status ='DI',");
						
					}
					updateData.append("AssetStatus = null,");
					updateData.append("AssetDisposalDate ='"+journal.getDateAcct()+"'");
					updateData.append(" WHERE A_Asset_ID = "+asset.getA_Asset_ID());
					
					DB.executeUpdateEx(updateData.toString(), trxName);
					result = journal.getGL_Journal_ID();
				}
		
			} catch (Exception e) {
				
				
			}
			return result;
			
			
		} 

}
