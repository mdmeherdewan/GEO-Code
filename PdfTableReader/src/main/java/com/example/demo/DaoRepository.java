package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DaoRepository {
	
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
		
		public List<String> getUpazilaFromDB(String zilaName) throws SQLException, ClassNotFoundException{
			List<String> upazilas = new ArrayList<String>();
			int zilaId = 0;
			ResultSet rs = null;
			String sql = "select id from t_districts where name = '"+zilaName+"'";
			
			Statement stmt = getDataSource().createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	zilaId =  rs.getInt("id");
	         }
	        
	        String sql2 = "select upa.name as upazila from t_upazila upa join t_districts dis on dis.id = upa.district_id "
					+ "where dis.id = "+zilaId+" order by upa.name asc";
	        rs  = stmt.executeQuery(sql2);
	        while (rs.next()) {
	        	String uz = rs.getString("upazila");
	        	upazilas.add(uz);
	         }
	        
			return upazilas;
		}
		
		public List<String> getUnionsFromDB(String upazilaName, String zilaName) throws SQLException, ClassNotFoundException{
			List<String> unions = new ArrayList<String>();
			int upazilaId = 0;
			int zilaId = 0;
			ResultSet rs = null;
			zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
			String sql = "select upa.id as upazila_id, upa.district_id as zila_id from t_upazila upa "
					+ " join t_districts dis on dis.id = upa.district_id" 
					+ " where dis.name = '"+zilaName+"' and upa.name = '"+upazilaName
					+ "' order by upa.name asc";
			
			Statement stmt = getDataSource().createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	upazilaId =  rs.getInt("upazila_id");
	        	zilaId =  rs.getInt("zila_id");
	         }
	        
	        String sql2 = "select uni.name unions from t_unions uni" 
	        		+ " join t_upazila upa on upa.id = uni.upazila_id" 
	        		+ " join t_districts dis on dis.id = upa.district_id " 
	        		+ " where upa.id = "+upazilaId+" and upa.district_id = "+zilaId+" order by uni.name asc";
	        rs  = stmt.executeQuery(sql2);
	        while (rs.next()) {
	        	String uz = rs.getString("unions");
	        	unions.add(uz);
	         }
	        
			return unions;
		}
		
		public void updateZilaGEOcode(int zilaGEOcode, String zilaName) throws SQLException, ClassNotFoundException{
			zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
			Statement stmt = getDataSource().createStatement();
	        String sql = "update t_districts set geo_code = "+zilaGEOcode+" where name = '"+ zilaName + "'";
	        
	        stmt.executeUpdate(sql);
	       
		}
		
		public void updateUpazilaGEOcode(String upazilaName, int upazilaGEOcode, String zilaName) throws SQLException, ClassNotFoundException{
			int upazilaId = 0;
			int zilaId = 0;
			ResultSet rs = null;
			zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
			String sql = "select upa.id as upazila_id, upa.district_id as zila_id from t_upazila upa "
					+ " join t_districts dis on dis.id = upa.district_id" 
					+ " where dis.name = '"+zilaName+"' and upa.name = '"+upazilaName
					+ "' order by upa.name asc";
			
			Statement stmt = getDataSource().createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	upazilaId =  rs.getInt("upazila_id");
	        	zilaId =  rs.getInt("zila_id");
	         }
	        
	        String sql2 = "update t_upazila set geo_code = "+upazilaGEOcode+" where id = "+ upazilaId +" and district_id = '"+zilaId+"'";
	        
	        stmt.executeUpdate(sql2);
	       
		}
		
		public void updateUnionsGEOcode(String unionName, int unionGEOcode, String upazilaName, String zilaName) throws SQLException, ClassNotFoundException{
			int upazilaId = 0;
			ResultSet rs = null;
			zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
			String sql = "select upa.id as upazila_id from t_upazila upa "
					+ " join t_districts dis on dis.id = upa.district_id" 
					+ " where dis.name = '"+zilaName+"' and upa.name = '"+upazilaName
					+ "' order by upa.name asc";
			
			Statement stmt = getDataSource().createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	upazilaId =  rs.getInt("upazila_id");
	         }
	        
	        String sql2 = "update t_unions set geo_code = "+unionGEOcode+" where upazila_id = "+ upazilaId + " and name = '"+unionName+"'";
	        
	        stmt.executeUpdate(sql2);
	       
		}
		
		
		

}
