package com.example.demo;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.*;

public class Main {
	
	static GeoService geoService= new GeoService();
	
	static File currDir = new File(".");
    static String path = currDir.getAbsolutePath();
    static String fileFolder = path.substring(0, path.length() - 1);
	
	public File getFilePathAndName(){
		 	File selectedFile = null;
	        JFileChooser jFileChooser = new JFileChooser();
	        jFileChooser.setCurrentDirectory(new File("/User/alvinreyes"));
	         
	        int result = jFileChooser.showOpenDialog(new JFrame());
	        
	        if (result == JFileChooser.APPROVE_OPTION) {
	            selectedFile = jFileChooser.getSelectedFile();
	        }
	        
	        return selectedFile;
	    }

    public static void main(String[] args) throws Exception {
    	Main thisClass = new Main();
    	
    	File file = thisClass.getFilePathAndName();
    	String filePath = file.getAbsolutePath();
    	Workbook workbook = new XSSFWorkbook();
        FileInputStream fis=new FileInputStream(new File(filePath));
        
        XSSFWorkbook wb=new XSSFWorkbook(fis); // for input file xlsx format
//        HSSFWorkbook wb=new HSSFWorkbook(fis); // for input file xls format 
        
        geoService.geoCodeInsertionInDB(wb);
            
        
    }  
	    
	    
}
