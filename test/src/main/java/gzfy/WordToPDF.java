package gzfy;

import jp.ne.so_net.ga2.no_ji.jcom.IDispatch;
import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;

public class WordToPDF {

	   
	        public void createPDF(String officePath,String pdfPath) throws Exception {  
	                ReleaseManager rm = null;  
	                IDispatch app = null;  
	                try {  
	                        rm=new ReleaseManager();  
	                        app = new IDispatch(rm, "PDFMakerAPI.PDFMakerApp");  
	                        app.method("CreatePDF",new Object[]{officePath,pdfPath});  
	                } catch (Exception e) {  
	                        throw e;  
	                } finally {  
	                        try {  
	                                app=null;  
	                                rm.release();  
	                                rm = null;  
	                        } catch (Exception e) {  
	                                throw e;  
	                        }  
	                }  
	        }  
	   
	        public static void main(String[] args) throws Exception {  
	        	WordToPDF one=new WordToPDF();  
//	                one.createPDF("E:\\codigg.ppt","E:\\codigg-ppt.pdf");  
	                one.createPDF("G:\\t.doc","G:\\codigg-doc.pdf");  
//	                one.createPDF("E:\\codigg.xls","E:\\codigg-xls.pdf");  
	        }  
	}
