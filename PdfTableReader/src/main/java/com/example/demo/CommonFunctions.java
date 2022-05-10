package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CommonFunctions {
	public Connection getDataSource() throws SQLException, ClassNotFoundException {
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
