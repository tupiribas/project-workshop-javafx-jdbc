package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl"); 
				conn = DriverManager.getConnection(url, props);
			} 
			catch (SQLException e) {
				throw new DbException("FALHA NA CONEXÃO AO BANCO DE DADOS cod.:02>>> " + e.getMessage());
			}
		}
		return conn;
	}
	
	private static Properties loadProperties() {
		try (FileInputStream fis = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fis);
			return props;	
		} 
		catch (IOException e) {
			throw new DbException("FAILED TO LOAD PROPERTIES cod.:01>>> " + e.getMessage());
		}
	}
	
	public static void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			throw new DbException("FAILED TO CLOSE CONNECTION cod.:03>>> " + e.getMessage());
		}
	}
	
	public static void closeConnection(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new DbException("FAILED TO CLOSE CONNECTION cod.:03>>> " + e.getMessage());
		}
	}
	
	public static void closeConnection(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}
		catch (SQLException e) {
			throw new DbException("FAILED TO CLOSE CONNECTION cod.:03>>> " + e.getMessage());
		}
	}
	
	public static void closeConnection(PreparedStatement stmt, ResultSet rs) {
		try {
			if (stmt != null && rs != null) {
				stmt.close();
				rs.close();
			}
		} catch (SQLException e) {
			throw new DbException("FAILED TO CLOSE CONNECTION cod.:03>>> " + e.getMessage());
		}
	}
}
