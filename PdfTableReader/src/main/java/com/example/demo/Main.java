package com.example.demo;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	DaoRepository daoRepository = new DaoRepository();

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
    	Main m2 = new Main();
    	
    	File file = m2.getFilePathAndName();
    	String filePath = file.getAbsolutePath();
    	Workbook workbook = new XSSFWorkbook();
        FileInputStream fis=new FileInputStream(new File(filePath));
        
        XSSFWorkbook wb=new XSSFWorkbook(fis); // for input file xlsx format
       // HSSFWorkbook wb=new HSSFWorkbook(fis); // for input file xls format 
        
        m2.geoCodeInsertionInDB(wb);
        
    }
    
    public String cellValueOfSheetRow (Cell cell) {
    	String cellValue="0";
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        cellValue = cell.getStringCellValue();
                        break;

                    case Cell.CELL_TYPE_FORMULA:
                        cellValue = cell.getCellFormula();
                        break;

                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            cellValue = cell.getDateCellValue().toString();
                        } else {
                            BigDecimal b = new BigDecimal(cell.getNumericCellValue(), MathContext.DECIMAL64);
                            cellValue = String.valueOf(b);
                        }
                        break;

                    case Cell.CELL_TYPE_BLANK:
                        cellValue = "";
                        break;

                    case Cell.CELL_TYPE_BOOLEAN:
                        cellValue = Boolean.toString(cell.getBooleanCellValue());
                        break;
                }
                
    	  return cellValue;
    }
    
    public void geoCodeInsertionInDB(XSSFWorkbook wb) {
        try {
        	Sheet sheet = wb.getSheetAt(0);
        	int rowCount = sheet.getLastRowNum() + 1;
        	
        	//For zila GEO code
        	Row forZilaRow = sheet.getRow(1);
        	String zilaName = forZilaRow.getCell(14).toString();
        	Cell zilaGEOcode = forZilaRow.getCell(2);
        	String zilaGEOcodeString = cellValueOfSheetRow(zilaGEOcode);
        	int zilaGEO = Integer.parseInt(zilaGEOcodeString);
        	System.out.println("Zila geo = "+zilaGEO);
        	
        	List<String> upzilas = daoRepository.getUpazilaFromDB("Kishoreganj");
        	//For upazila GEO code
        	for (String dbUpazilaName: upzilas) {
        		int upzilaGEO = 0;
        		for (int i = 0; i < rowCount; i++) {
        			Row upazilaRow = sheet.getRow(i);
	        		Cell upazilaName = upazilaRow.getCell(14);
	        		
	        		if (upazilaName.toString().equalsIgnoreCase(dbUpazilaName) && upzilaGEO == 0) {
	        			Cell upazilaGEOcode = upazilaRow.getCell(4);
		        		String upazilaGEOcodeString = cellValueOfSheetRow(upazilaGEOcode);
		        		if (upazilaGEOcodeString != null && !upazilaGEOcodeString.isEmpty()) {
		        			upzilaGEO = Integer.parseInt(upazilaGEOcodeString);
		        			System.out.println("Upazila Name="+upazilaName.toString()+" and Upazila GEO="+upzilaGEO);
		        			daoRepository.updateUpazilaGEOcode(dbUpazilaName, upzilaGEO, zilaName);
		        			//For union GEO code start
		    	        	for (String dbUnionName: daoRepository.getUnionsFromDB(dbUpazilaName, zilaName)) {
		    	        		for (int j = 0; j < rowCount; j++) {
		    	        			Row unionRow = sheet.getRow(j);
		    		        		Cell unionName = unionRow.getCell(14);
		    		        		if (unionName.toString().equalsIgnoreCase(dbUnionName)) {
		    		        			Cell unionGEOcode = unionRow.getCell(7);
		    			        		String unionGEOcodeString = cellValueOfSheetRow(unionGEOcode);
		    			        		
		    			        		if (Integer.parseInt(cellValueOfSheetRow(unionRow.getCell(4))) == upzilaGEO && unionGEOcodeString != null && !unionGEOcodeString.isEmpty()) {
		    			        			int unionGEO = Integer.parseInt(unionGEOcodeString);
		    			        			System.out.println("Upazila Name="+upazilaName.toString()+" and Union Name="+unionName.toString()+" and Union GEO="+unionGEO);
		    			        			daoRepository.updateUnionsGEOcode(dbUnionName, unionGEO, dbUpazilaName, zilaName);
		    			        			break;
		    							}
		    						}
		    		        		
		    					}
		    	            }
		    	        	//For union GEO code start
		    	        	
						}
					}
	        		
				}
            }
        	//For upazila GEO code
        	
           
    }catch (Exception e) {
		e.printStackTrace();
	}
 
}
	    
	    
	    
	    
	    
	    
}
