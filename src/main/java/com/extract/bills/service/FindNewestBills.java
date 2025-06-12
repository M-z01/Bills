package com.extract.bills.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.extract.bills.bill.APIAccess;
import com.extract.bills.bill.Bill;
import com.extract.bills.db.DBHandler;
import com.extract.bills.ingest.JsonHandler;


public class FindNewestBills {
    public static String defaultBill;
    public static DBHandler bills;//db: bills; tables: billsinfo, billsdetail
    public static JsonHandler jsh;
    public ResultSet rs;

    public FindNewestBills() {
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
        if(bill.getUpdateDateIncludingText() != null){
            String currentLmd = bill.getUpdateDateIncludingText().toString();
            return !dbLmd.equals(currentLmd);
        } else {
            return !dbLmd.equals(bill.getUpdateDate().toString());
        }
        
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
                    System.out.printf("New Bill %s-%d detected and added to database.\n", e.getType(), e.getNumber());
                } else { // Compare and update
                    if (shouldUpdate(rs, e)) {
                        updateDB(e, rs);
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
    public void updateDB(Bill bill, ResultSet rs) throws SQLException {
        Timestamp dbLmd = rs.getTimestamp("lmd");
        Timestamp newLmd;
        if (bill.getUpdateDateIncludingText() == null){
            newLmd = Timestamp.from(bill.getUpdateDate());
        } else {
            newLmd = Timestamp.from(bill.getUpdateDateIncludingText());
        }
        

        if (!dbLmd.equals(newLmd)) {
            rs.updateTimestamp("lmd", newLmd);
            rs.updateRow();
            System.out.printf("Updated bill: %s-%d with new lmd: %s%n", bill.getType(), bill.getNumber(), newLmd);
        } else {
            //System.out.println("No update needed for bill: " + bill.getType() + "-" + bill.getNumber());
        }
    }

    //insert new row into db
    public void addToDB(Bill bill) throws SQLException {
        bills.insert(bill);
    }

    public void addDetailToDB(Bill bill) throws SQLException {
        //add billsdetail to database
    }
    
}
