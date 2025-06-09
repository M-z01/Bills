package com.extract.bills.bill;

import java.sql.ResultSet;
import java.sql.SQLException;

public class findNewestBills {
    public static String defaultBill;
    public static DBHandler bills;//db: bills; tables: billsinfo, billsdetail
    public static JsonHandler jsh;
    public ResultSet rs;

    public findNewestBills() {
        defaultBill = APIAccess.getUrlDefault();
        bills = openConnection("bills_db");
        jsh = new JsonHandler(defaultBill);
        checkUpdates(jsh);
    }

    private DBHandler openConnection(String DBname) {
        return new DBHandler(DBname);
    }

    public boolean shouldUpdate(ResultSet rs, Bill bill) throws SQLException {
        String dbLmd = rs.getString("lmd");
        String currentLmd = bill.getUpdateDateIncludingText();
        return !dbLmd.equals(currentLmd);
    }
    //look for potential bill that requires an update in db
    public boolean checkUpdates(JsonHandler inputHander) {
    try {
        for (Bill e : inputHander.getbJsn().getBills()) {
            try {
                String queryString = String.format("SELECT * FROM bills_info WHERE (type = '%s' AND number = %d)", e.getType(), e.getNumber());
                rs = bills.query(queryString);

                if (!rs.next()) { // Not found in DB
                    addToDB(e);
                    //add notify here
                } else { // Compare and update
                    if (shouldUpdate(rs, e)) {
                        //updateDB(e, rs);
                        //add notify here
                    }
                    
                }

                rs.close(); //close result set 
            } catch (SQLException e1) {
                e1.printStackTrace(); //Log but don't close shared connection
            }
        }
    } finally {
        bills.connClose(); //Close connection once after loop
    }
    return false;
}

    
    //update row in db
    /*
    public void updateDB(Bill bill, ResultSet rs) throws SQLException {
        Timestamp dbLmd = rs.getTimestamp("lmd");
        Timestamp newLmd = Timestamp.valueOf(bill.getUpdateDateIncludingText());

        if (!dbLmd.equals(newLmd)) {
            rs.updateTimestamp("lmd", newLmd);
            rs.updateRow();
            System.out.printf("Updated bill: %s-%d with new lmd: %s%n", bill.getType(), bill.getNumber(), newLmd);
        } else {
            System.out.println("No update needed for bill: " + bill.getType() + "-" + bill.getNumber());
        }
    }
     */

    //insert new row into db
    public void addToDB(Bill bill) throws SQLException {
        String insertString = String.format("INSERT INTO bills_info (type, number, lmd) VALUES ('%s', %d, '%s')", bill.getType(), bill.getNumber(), bill.getUpdateDateIncludingText());
        int rowsAffected = bills.insert(insertString);
        System.out.println("Rows affected: " + rowsAffected);
    }

    public void addDetailToDB(Bill bill) throws SQLException {
        //add billsdetail to database
    }
    
}
