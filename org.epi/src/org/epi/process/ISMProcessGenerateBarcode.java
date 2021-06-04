package org.epi.process;

import java.io.FileOutputStream;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfWriter;

public class ISMProcessGenerateBarcode extends SvrProcess {
				
		String p_barcode = "";
		int p_M_Product_ID = 0;
		
		@Override
		protected void prepare() {
			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				
				if (para[i].getParameter() == null);
//				else if (name.equals("M_Product_ID"))
//					p_M_Product_ID = (int)para[i].getParameterAsInt();				
				else if (name.equals("code"))		
					p_barcode = (String)para[i].getParameterAsString();					
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
			
		
		@Override
		protected String doIt() throws Exception {
			
			
			String cekUPC = validation();
			
			if(cekUPC != null && !cekUPC.isEmpty()){
				
				return cekUPC;
				
			}
//			
//			if(p_M_Product_ID <= 0){
//				
//				return "Product Tidak Boleh Kosong";
//				
//			}
			
			
//			Barcode barcode = BarcodeFactory.createCode128("asdasdsa");
//			barcode.setDrawingText(false);
//			barcode.setBarHeight(200);
//			barcode.setBarWidth(5);
			
			Document doc = new Document(new Rectangle(PageSize.POSTCARD));
			PdfWriter pdfW = PdfWriter.getInstance(doc, new FileOutputStream("D://test.pdf"));
			doc.open();
			Barcode128 barcode = new Barcode128();
			barcode.setCode(p_barcode);
			barcode.setAltText("");
			barcode.setTextAlignment(Element.ALIGN_CENTER);
			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));
//			doc.add(barcode.createImageWithBarcode(pdfW.getDirectContent(), null, null));

			doc.close();			
			
			
//			MProduct prod = new MProduct(getCtx(), p_M_Product_ID, get_TrxName());
//			prod.setUPC(p_barcode);
//			prod.saveEx();
			
			return "Generate Barcode Berhasil";
			
		}
		
		
		private String validation(){
			
			String rs = "";
			
			StringBuilder SQLGetUPC = new StringBuilder(); 
			
			
			SQLGetUPC.append("SELECT M_Product_ID ");
			SQLGetUPC.append(" FROM M_Product ");
			SQLGetUPC.append(" WHERE AD_Client_ID = ? ");
			SQLGetUPC.append(" AND M_Product_ID = ? ");
			SQLGetUPC.append(" AND UPC = '"+p_barcode+"'");
			
			int upc =  DB.getSQLValueEx(get_TrxName(), SQLGetUPC.toString(), new Object[]{getAD_Client_ID(),p_M_Product_ID});

			
			if(upc > 0){
				
				return rs = "Kode Barcode Yang Diinput Sudah Terdaftar";
			}
			
			
			return rs;
		}
	
	}
	
	
