package com.example.demo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class CommonFunctions {
	public Connection getDbConnection() throws SQLException, ClassNotFoundException {
    	Connection connection = null ;
    	try {
			Class.forName("com.oracle.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		}
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.70.55:1521/APEXDB.GIGATECHLTD.COM?user=foodprod&password=foodprod");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return connection;
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
	
	public String ifStringContainWhiteSpace(String zilaName, String upazilaName){
		String sql = "";
		if (zilaName.contains(" ") || upazilaName.contains(" ")) {
        	if (upazilaName.contains(" ")) {
        		upazilaName = upazilaName.replaceAll("\\s", "");//for removing white space between two words;
			}else if (zilaName.contains(" ") && upazilaName.contains(" ") ) {
				zilaName = zilaName.replaceAll("\\s", "");
        		upazilaName = upazilaName.replaceAll("\\s", "");
			}else {
				zilaName = zilaName.replaceAll("\\s", "");
			}
        }
		sql = selectZilaAndUpazila(zilaName, upazilaName);
		return sql;
	}
	
	public String selectZilaAndUpazila(String zilaName, String upazilaName){
		String sql = "select upa.id as upazila_id, upa.district_id as zila_id from t_upazila upa "
				+ " join t_districts dis on dis.id = upa.district_id" 
				+ " where dis.name = '"+zilaName+"' and upa.name = '"+upazilaName
				+ "' order by upa.name asc";
		return sql;
	}
	
}
