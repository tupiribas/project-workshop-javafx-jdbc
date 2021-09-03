package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDAO {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {this.conn = conn;}

	@Override
	public void insert(Department obj) {
		PreparedStatement stmt = null;
		try {
			String sql = "INSERT INTO department (Name) VALUE (?)";
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, obj.getName());
			
			int rows = stmt.executeUpdate();
			
			if (rows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				while (rs.next()) {
					obj.setId(rs.getInt(1));
				}
				DB.closeConnection(rs);
			}
			else {
				throw new DbException("UNEXPECTED ERROR! NO ROWS AFFECTED! cod.:015>>> ");
			}
		} 
		catch (Exception e) {
			throw new DbException("FAILED TO CATCH THE DEPARTMENT cod.:014>>> " + e.getMessage());
		}
		finally {
			DB.closeConnection(stmt);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement stmt = null;
		try {
			String sql = "UPDATE department SET Name = ? WHERE Id = ?";
			stmt = conn.prepareStatement(sql);
			
			stmt.setString(1, obj.getName());
			stmt.setInt(2, obj.getId());
			
			stmt.executeUpdate();
		} 
		catch (SQLException e) {
			throw new DbException("DATA UPDATE FAILED cod.:016>>> " + e.getMessage());
		}
		finally {
			DB.closeConnection(stmt);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		try {
			String sql = "DELETE FROM department WHERE Id = ?";
			stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, id);
			
			int rows = stmt.executeUpdate();
			
			if (rows == 0) {
				throw new DbException("ERROR: ID NON-EXISTENT cod.:017");
			}
		} 
		catch (SQLException e) {
			throw new DbException("FAILED TO DELETE THE DATA cod.:016>>> " + e.getMessage());
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM department WHERE Id = ?";
			stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				return instantiateDepartment(rs);
			}
			return null;
		} 
		catch (SQLException e) {
			throw new DbException("FAILED TO SHOW DEPARTMENT DATA cod.:012>>> " + e.getMessage());
		} 
		finally {
			DB.closeConnection(stmt, rs);
		}
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;
	} 

	@Override
	public List<Department> findAll() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM department";
			stmt = conn.prepareStatement(sql);
			
			rs = stmt.executeQuery();
			
			List<Department> listDepartments = new ArrayList<>();
			
			while (rs.next()) {
				listDepartments.add(instantiateDepartment(rs));
			}
			return listDepartments;
		} 
		catch (SQLException e) {
			throw new DbException("FAILED TO SHOW SELLER DATA cod.:013>>> " + e.getMessage());
		}
		finally {
			DB.closeConnection(stmt, rs);
		}
	}

}
