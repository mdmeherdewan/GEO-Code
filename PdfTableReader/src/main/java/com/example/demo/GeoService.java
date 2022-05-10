package com.example.demo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GeoService {
	
	DaoRepository dao = new DaoRepository();
	
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
        	System.out.println(zilaName+" Zila GEO="+zilaGEO);
        	dao.updateZilaGEOcode(zilaGEO,zilaName);
        	
        	List<String> upzilas = dao.getUpazilaFromDB(zilaName);
        	
        	//For upazila GEO code
        	for (String dbUpazilaName: upzilas) {
        		int upzilaGEO = 0;
        		for (int i = 0; i < rowCount; i++) {
        			Row upazilaRow = sheet.getRow(i);
	        		Cell upazilaName = upazilaRow.getCell(14);
	        		
	        		if (upazilaName.toString().equalsIgnoreCase(dbUpazilaName) && upzilaGEO == 0) {
	        			Cell upazilaGEOcode = upazilaRow.getCell(4);
		        		String upazilaGEOcodeString = cellValueOfSheetRow(upazilaGEOcode);
		        		
		        		if (!upazilaGEOcodeString.isEmpty() && cellValueOfSheetRow(upazilaRow.getCell(7)).equalsIgnoreCase("")) {
		        			upzilaGEO = Integer.parseInt(upazilaGEOcodeString);
		        			System.out.println("Upazila name="+upazilaName.toString()+" and GEO="+upzilaGEO);
		        			dao.updateUpazilaGEOcode(dbUpazilaName, upzilaGEO, zilaName);
		        			
		        			//For union GEO code start
		    	        	for (String dbUnionName: dao.getUnionsFromDB(dbUpazilaName, zilaName)) {
		    	        		for (int j = 0; j < rowCount; j++) {
		    	        			Row unionRow = sheet.getRow(j);
		    		        		String  unionName = unionRow.getCell(14).toString();
		    		        		
		    		        		if (!unionName.equalsIgnoreCase(dbUnionName) && unionName.contains(" ")) {
		    		        			unionName = unionName.replaceAll("\\s", "");
		    						}

		    		        		if (unionName.equalsIgnoreCase(dbUnionName)) {
		    		        			Cell unionGEOcode = unionRow.getCell(7);
		    			        		String unionGEOcodeString = cellValueOfSheetRow(unionGEOcode);
		    			        		
		    			        		if (unionGEOcodeString.equalsIgnoreCase("") || unionGEOcodeString == null || unionGEOcodeString.isEmpty()) {
		    			        			j++;
		    			        		}
		    			        		else {
		    			        			if (Integer.parseInt(cellValueOfSheetRow(unionRow.getCell(4))) == upzilaGEO) {
			    			        			int unionGEO = Integer.parseInt(unionGEOcodeString);
			    			        			System.out.println("Upazila name="+upazilaName.toString()+", Union name="+unionName+" and GEO="+unionGEO);
			    			        			dao.updateUnionsGEOcode(dbUnionName, unionGEO, dbUpazilaName, zilaName);
			    			        			break;
			    							}
										}
		    						}
		    					}
		    	            }
		    	        	//For union GEO code end
						}
					}
				}
            }
        	//For upazila GEO code end
	    }catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("Insertion completed !");
    }
    
    
}
