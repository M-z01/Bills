package com.extract.bills.bill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
public class DBHandler {
	private String url;
	private String username;
	private String password;
	private Connection conn;
	private String DBName;
	
	public DBHandler(String DBName) {
		this.url = "jdbc:mysql://localhost:3306/" + DBName + "?serverTimezone=UTC";
		this.username = "root";
		this.password = PasswordReader.getPassword();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conn = connectToDB(DBName);
			this.DBName = DBName;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Connection connectToDB(String DBName) throws SQLException {
		Connection conn = DriverManager.getConnection(this.url, this.username, this.password);
		System.out.println("Connected to " + DBName + ".");
		return conn;
	}
	
	public ResultSet query(String queryString) throws SQLException {
		Statement st = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = st.executeQuery(queryString);
		//rs.next();
		return rs;
	}

	public int insert(String insertString) throws SQLException {
		PreparedStatement pst = this.conn.prepareStatement(insertString);
		int rowAffected = pst.executeUpdate();
		pst.close();
		return rowAffected;
	}

	public int insert(Bill bill) throws SQLException {
		String sql = "INSERT INTO bills_info (type, number, lmd) VALUES (?, ?, ?)";
		try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
			pst.setString(1, bill.getType());
			pst.setInt(2, bill.getNumber());
			if (bill.getUpdateDateIncludingText() != null) {
				pst.setTimestamp(3, Timestamp.from(bill.getUpdateDateIncludingText()));
			} else {
				pst.setTimestamp(3, Timestamp.from(bill.getUpdateDate()));
			}
			return pst.executeUpdate();
		}
	}

	public int update(String updateString) throws SQLException {
		PreparedStatement pst = this.conn.prepareStatement(updateString);
		int rowAffected = pst.executeUpdate();
		pst.close();
		return rowAffected;
	}
	
	public Connection getConn() {
		return this.conn;
	}
	
	public void connClose() {
		try {
			this.conn.close();
			System.out.println("Connection to " + this.DBName + " is now closed.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
