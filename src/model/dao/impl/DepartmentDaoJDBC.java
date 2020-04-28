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
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO department " + "(Name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
				}
			} else {
				throw new DbException("Unexpected error! No rows affected.");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE department " + " SET NAME = ? " + "WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");

			st.setInt(1, id);
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Department findById(Integer id) {
		Department department = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM department " + "WHERE Id = ?");

			st.setInt(1, id);

			rs = st.executeQuery();
			if (rs.next()) {
				department = new Department();
				department.setId(rs.getInt("Id"));
				department.setName(rs.getString("Name"));
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		return department;
	}

	@Override
	public List<Department> findAll() {
		List<Department> list = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM department");

			rs = st.executeQuery();
			while (rs.next()) {
				Department department = new Department();
				department.setId(rs.getInt("Id"));
				department.setName(rs.getString("Name"));
				list.add(department);
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		return list;
	}

}
