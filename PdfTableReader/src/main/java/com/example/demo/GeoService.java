package com.example.demo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GeoService {
	
	DaoRepository dao = new DaoRepository();
	
	CommonFunctions cf = new CommonFunctions();
    
    public void geoCodeInsertionInDB(XSSFWorkbook wb) throws ClassNotFoundException, SQLException {
        try {
        	Sheet sheet = wb.getSheetAt(0);
        	int totalRows = sheet.getLastRowNum() + 1;
        	
        	//For zila GEO code
        	Row forZilaRow = sheet.getRow(1);
        	String zilaName = forZilaRow.getCell(14).toString();
        	Cell zilaGEOcode = forZilaRow.getCell(2);
        	String zilaGEOcodeString = cf.cellValueOfSheetRow(zilaGEOcode);
        	int zilaGEO = Integer.parseInt(zilaGEOcodeString);
        	System.out.println("Zila Name="+zilaName+" and GEO="+zilaGEO);
        	dao.updateZilaGEOcode(zilaGEO,zilaName);
        	int rowIndex = 0;
        	
        	List<String> upzilas = dao.getUpazilaFromDB(zilaName);
        	
        	//For upazila GEO code
        	for (String dbUpazilaName: upzilas) {
        		int upazilaGEO = 0;
        		for (int i = 0; i < totalRows; i++) {
        			Row upazilaRow = sheet.getRow(i);
	        		String upazilaName = upazilaRow.getCell(14).toString();
	        		
	        		if (!upazilaName.equalsIgnoreCase(dbUpazilaName) && upazilaName.contains(" ")) {
	        			upazilaName = upazilaName.replaceAll("\\s", "");//remove white space between string.exmple:"Hello World" ans: HelloWorld.
					}
	        		if(!upazilaName.equalsIgnoreCase(dbUpazilaName) && (upazilaName.contains("(") || upazilaName.contains(")"))) {
	        			upazilaName = upazilaName.replaceAll("\\((.*?)\\)","");//remove word within bracket of string. example: "Apple Mango (fruit)" ans: Apple Mango.
	        		}
	        		
	        		if (upazilaName.equalsIgnoreCase(dbUpazilaName) && upazilaGEO == 0) {
	        			Cell upazilaGEOcode = upazilaRow.getCell(4);
		        		String upazilaGEOcodeString = cf.cellValueOfSheetRow(upazilaGEOcode);
		        		
		        		if (!upazilaGEOcodeString.isEmpty() && cf.cellValueOfSheetRow(upazilaRow.getCell(7)).equalsIgnoreCase("")) {
		        			upazilaGEO = Integer.parseInt(upazilaGEOcodeString);//select upazila geo when union geo is blanck.
		        			rowIndex = i;
						}
		        		if (upazilaGEO != 0) {
		        			System.out.println("Upazila name="+upazilaName+" and GEO="+upazilaGEO);
		        			dao.updateUpazilaGEOcode(dbUpazilaName, upazilaGEO, zilaName);
		        			
		        			unionGEOinsertionIntoDB(sheet, rowIndex, totalRows, zilaName, dbUpazilaName, upazilaName, upazilaGEO);
						}
					}
				}
        		if (upazilaGEO == 0) {// if upazila geo found and union geo found in same row then upazila geo inserted by this way.
        			for (int i = 0; i < totalRows; i++) {
            			Row upazilaRow = sheet.getRow(i);
    	        		String upazilaName = upazilaRow.getCell(14).toString();
    	        		
    	        		if (!upazilaName.equalsIgnoreCase(dbUpazilaName) && upazilaName.contains(" ")) {
    	        			upazilaName = upazilaName.replaceAll("\\s", "");
    					}
    	        		if(!upazilaName.equalsIgnoreCase(dbUpazilaName) && (upazilaName.contains("(") || upazilaName.contains(")"))) {
    	        			upazilaName = upazilaName.replaceAll("\\((.*?)\\)","");
    	        		}
    	        		
    	        		if (upazilaName.equalsIgnoreCase(dbUpazilaName) && upazilaGEO == 0) {
    	        			Cell upazilaGEOcode = upazilaRow.getCell(4);
    		        		String upazilaGEOcodeString = cf.cellValueOfSheetRow(upazilaGEOcode);
    		        		
    		        		if (upazilaGEO == 0 && !upazilaGEOcodeString.isEmpty()) {
    		        			upazilaGEO = Integer.parseInt(upazilaGEOcodeString);
    						}
    		        		if (upazilaGEO != 0) {
    		        			System.out.println("Upazila name="+upazilaName+" and GEO="+upazilaGEO);
    		        			dao.updateUpazilaGEOcode(dbUpazilaName, upazilaGEO, zilaName);
    		        			
    		        			unionGEOinsertionIntoDB(sheet, rowIndex, totalRows, zilaName, dbUpazilaName, upazilaName, upazilaGEO);
    						}
    					}
    				}
				}
            }
        	//For upazila GEO code end
	    }catch (Exception e) {
			e.printStackTrace();
		}
        
        cf.getDbConnection().close();
        
        System.out.println("Insertion completed !");
    }
    
    public void unionGEOinsertionIntoDB(Sheet sheet, int rowIndex, int totalRow, String zilaName, String dbUpazilaName, String upazilaName, int upazilaGEO) {
    	try {
    		List<String> dbUnions = dao.getUnionsFromDB(dbUpazilaName, zilaName);
			for (String dbUnionName: dbUnions) {
				int unionGEO = 0;
				for (int j = rowIndex; j < totalRow; j++) {
					Row unionRow = sheet.getRow(j);
					String  unionName = unionRow.getCell(14).toString();
					
					findUnionName(unionName, dbUnionName, unionGEO);
					
					if (unionName.equalsIgnoreCase(dbUnionName)) {
						Cell unionGEOcode = unionRow.getCell(7);
			    		String unionGEOcodeString = cf.cellValueOfSheetRow(unionGEOcode);
			    		
			    		if (Integer.parseInt(cf.cellValueOfSheetRow(unionRow.getCell(4))) == upazilaGEO && !unionGEOcodeString.equalsIgnoreCase("") 
			    				&& unionGEOcodeString != null && !unionGEOcodeString.isEmpty()) 
			    		{
			    			unionGEO = Integer.parseInt(unionGEOcodeString);
		        			System.out.println("Upazila name="+upazilaName+", Union name="+unionName+" and GEO="+unionGEO);
		        			dao.updateUnionsGEOcode(dbUnionName, unionGEO, dbUpazilaName, zilaName);
		        			break;
			    		}
					} 
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    public String findUnionName(String unionName, String dbUnionName, int unionGEO) {
    	String baseUnionName = unionName;
    	if (dbUnionName.equalsIgnoreCase("Charfaradi") && unionName.equalsIgnoreCase("CHAR FARADI")) {
			System.out.println(dbUnionName);
		}
    	
    	if (!unionName.equalsIgnoreCase(dbUnionName) && unionName.contains(" ")) {
			unionName = unionName.replaceAll("\\s", "");
		}
		if(!unionName.equalsIgnoreCase(dbUnionName) && (unionName.contains("(") || unionName.contains(")"))) {
			unionName = unionName.replaceAll("\\((.*?)\\)","");
		}
		
		unionName = baseUnionName(unionName, dbUnionName, baseUnionName);
		if (!unionName.equalsIgnoreCase(dbUnionName) && unionGEO == 0) {
			if (unionName.length()>5 && unionName.contains("CHAR") || unionName.contains("CHOR")) {
				if (unionName.length()>5 && unionName.contains("CHAR")) {
					int i = unionName.indexOf("CHAR");
					if(i>-1){
						unionName = unionName.replace("CHAR", "CHOR");
					}
				}
				else if (unionName.length()>5 && unionName.contains("CHOR")) {
					int i = unionName.indexOf("CHOR");
					if(i>-1){
						unionName = unionName.replace("CHOR", "CHAR");
					}
				}
				unionName = baseUnionName(unionName, dbUnionName, baseUnionName);
			}
			
			if (!unionName.equalsIgnoreCase(dbUnionName) && unionName.contains("HA")
					&& unionName.contains("A")) {
				if (!unionName.equalsIgnoreCase(dbUnionName) && unionName.contains("HA")) {
					int i = unionName.indexOf("HA");
					if(i>-1){
						unionName = unionName.replace("HA", "A");
					}
				}
				else if (unionName.contains("A")) {
					int i = unionName.indexOf("A");
					if(i>-1){
						unionName = unionName.replace("A", "HA");
					}
				}
				unionName = baseUnionName(unionName, dbUnionName, baseUnionName);
			}
			
			if (!unionName.equalsIgnoreCase(dbUnionName) && unionName.length()>5 && unionName.contains("SH")
					&& unionName.contains("S")) {
				if (!unionName.equalsIgnoreCase(dbUnionName) && unionName.length()>5 && unionName.contains("SH")) {
					int i = unionName.indexOf("SH");
					if(i>-1){
						unionName = unionName.replace("SH", "S");
					}
				}
				else if (unionName.length()>5 && unionName.contains("S")) {
					int i = unionName.indexOf("S");
					if(i>-1){
						unionName = unionName.replace("S", "SH");
					}
				}
				unionName = baseUnionName(unionName, dbUnionName, baseUnionName);
			}
			
			if (!unionName.equalsIgnoreCase(dbUnionName) && (unionName.contains("A") || unionName.contains("O") || unionName.contains("U"))) {
				if (unionName.contains("O")) {
					unionName=unionName.replace('O','A');
					if (!unionName.equalsIgnoreCase(dbUnionName)) {
						unionName=unionName.replace('O','U');
					}
				}
				else if (unionName.contains("U")) {
					unionName=unionName.replace('U','O');
					if (!unionName.equalsIgnoreCase(dbUnionName)) {
						unionName=unionName.replace('U','O');
					}
				}
				else if (unionName.contains("A")) {
					unionName=unionName.replace('A','O');
				}
				unionName = baseUnionName(unionName, dbUnionName, baseUnionName);
			}
			
		}
		return unionName;
    }
    
    public String baseUnionName(String unionName, String dbUnionName, String baseUnionName) {
    	if (!unionName.equalsIgnoreCase(dbUnionName)) {
    		unionName = baseUnionName;
		}
    	return unionName;
    }
    
    
}
