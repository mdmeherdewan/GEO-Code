package com.example.demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DaoRepository {
	CommonFunctions cf = new CommonFunctions();
	
	public List<String> getUpazilaFromDB(String zilaName) throws SQLException, ClassNotFoundException{
		List<String> upazilas = new ArrayList<String>();
		int zilaId = 0;
		ResultSet rs = null;
		Statement stmt = cf.getDataSource().createStatement();
		
		zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
		String sql = "select id from t_districts where name = '"+zilaName+"'";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
        	zilaId =  rs.getInt("id");
        }
        
        if (rs.next() == false && zilaName.contains(" ")) {
        	zilaName = zilaName.replaceAll("\\s", "");//for removing white space between two words;
        	sql = "select id from t_districts where name = '"+zilaName+"'";
	        rs = stmt.executeQuery(sql);
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
		String sql = cf.selectZilaAndUpazila(zilaName, upazilaName);
		Statement stmt = cf.getDataSource().createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
        	upazilaId =  rs.getInt("upazila_id");
        	zilaId =  rs.getInt("zila_id");
        }
        
        if (rs.next() == false && (zilaName.contains(" ") || upazilaName.contains(" ") )) {
        	sql = cf.ifStringContainWhiteSpace(zilaName, upazilaName);
	        rs = stmt.executeQuery(sql);
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
		Statement stmt = cf.getDataSource().createStatement();
        String sql = "update t_districts set geo_code = "+zilaGEOcode+" where name = '"+ zilaName + "'";
        
        stmt.executeUpdate(sql);
       
	}
	
	public void updateUpazilaGEOcode(String upazilaName, int upazilaGEOcode, String zilaName) throws SQLException, ClassNotFoundException{
		int upazilaId = 0;
		int zilaId = 0;
		ResultSet rs = null;
		zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
		String sql = cf.selectZilaAndUpazila(zilaName, upazilaName);
		Statement stmt = cf.getDataSource().createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
        	upazilaId =  rs.getInt("upazila_id");
        	zilaId =  rs.getInt("zila_id");
        }
        if (rs.next() == false && (zilaName.contains(" ") || upazilaName.contains(" ") )) {
        	sql = cf.ifStringContainWhiteSpace(zilaName, upazilaName);
	        rs = stmt.executeQuery(sql);
        }
        
        String sql2 = "update t_upazila set geo_code = "+upazilaGEOcode+" where id = "+ upazilaId +" and district_id = '"+zilaId+"'";
        stmt.executeUpdate(sql2);
       
	}
	
	public void updateUnionsGEOcode(String unionName, int unionGEOcode, String upazilaName, String zilaName) throws SQLException, ClassNotFoundException{
		int upazilaId = 0;
		String sql = "";
		ResultSet rs = null;
		zilaName = zilaName.substring(0,1)+zilaName.substring(1).toLowerCase();
		sql = cf.selectZilaAndUpazila(zilaName, upazilaName);
		
		Statement stmt = cf.getDataSource().createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
        	upazilaId =  rs.getInt("upazila_id");
        }
        
        if (rs.next() == false && (zilaName.contains(" ") || upazilaName.contains(" ") )) {
        	sql = cf.ifStringContainWhiteSpace(zilaName, upazilaName);
	        rs = stmt.executeQuery(sql);
        }
        
        String sql2 = "update t_unions set geo_code = "+unionGEOcode+" where upazila_id = "+ upazilaId + " and name = '"+unionName+"'";
        stmt.executeUpdate(sql2);
       
	}

}
