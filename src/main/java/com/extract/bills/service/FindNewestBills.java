package com.extract.bills.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.extract.bills.bill.APIAccess;
import com.extract.bills.bill.Bill;
import com.extract.bills.db.DBHandler;
import com.extract.bills.ingest.JsonHandler;


public class FindNewestBills {
    public static String defaultBill;
    public static DBHandler db;//db: bills; tables: billsinfo, billsdetail
    public static JsonHandler jsh;
    public ResultSet rs;

    public FindNewestBills() {
        defaultBill = APIAccess.getUrlDefault();
        jsh = new JsonHandler(defaultBill);
        checkUpdates(jsh);
    }

    private DBHandler openConnection(String DBname) {
        return new DBHandler(DBname);
    }

    public boolean shouldUpdate(ResultSet rs, Bill bill) throws SQLException {
        java.sql.Timestamp dbLmd = rs.getTimestamp("lmd");
        if(bill.getUpdateDateIncludingText() != null){
            java.sql.Timestamp currentLmd = Timestamp.from(bill.getUpdateDateIncludingText());
            return !dbLmd.equals(currentLmd);
        } else {
            return !dbLmd.equals(Timestamp.from(bill.getUpdateDate()));
        }
    }

    //look for potential bill that requires an update in db
    public boolean checkUpdates(JsonHandler inputHander) {
        if (inputHander.getbJsn() == null || inputHander.getbJsn().getBills() == null) {
            System.out.println("No bills found in the JSON handler.");
            return false;
        }

        List<Bill> newBills = new ArrayList<>();
        List<Bill> updateBills = new ArrayList<>();
        ResultSet rs; // new result set
        db = openConnection("bills_db"); // Ensure DB connection is opened
        if (db == null) {
            System.out.println("Failed to connect to the database.");
            return false;
        }
        
        try {
            for (Bill e : inputHander.getbJsn().getBills()) {
                try {
                    String queryString = String.format("SELECT * FROM bills_info WHERE (type = '%s' AND number = %d)", e.getType(), e.getNumber());
                    rs = db.query(queryString);

                    if (!rs.next()) { // Not found in DB
                        newBills.add(e);// Collect for batch insert
                        //add notify here
                    } else { // Compare and update
                        if (shouldUpdate(rs, e)) {
                            updateBills.add(e); // Collect for batch update
                            System.out.printf("Bill %s-%d requires update in database.\n", e.getType(), e.getNumber());
                            //add notify here
                        }
                    }
                    rs.close(); //close result set 
                } catch (SQLException e1) {
                    e1.printStackTrace(); //Log but don't close shared connection
                }
            }
            if (!newBills.isEmpty()){
                addToDB(newBills);
            }
            if (!updateBills.isEmpty()) {
                //updateToDB(updateBills);
            }
        } finally {
            db.connClose(); //Close connection once after loop
        }
    return false;
    }

    //insert new row into db
    public void addToDB(List<Bill> bills) {
        db.insertToInfo(bills);
        List<Bill> detailedBills = new ArrayList<>();

        // Use a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(10, bills.size()));
        List<Future<Bill>> futures = new ArrayList<>();

        for (Bill bill : bills) {
            futures.add(executor.submit(() -> {
                JsonHandler handler = new JsonHandler(APIAccess.getUrlspecific("118", bill.getType(), String.valueOf(bill.getNumber())));
                if (handler.getbJsnSpecific() != null && handler.getbJsnSpecific().getBill() != null) {
                    return handler.getbJsnSpecific().getBill();
                } else {
                    System.out.println("No detailed bill found for " + bill.getType() + " " + bill.getNumber());
                    return null;
                }
            }));
        }

        // Collect results
        for (Future<Bill> future : futures) {
            try {
                Bill detailed = future.get();
                if (detailed != null) {
                    detailedBills.add(detailed);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        db.insertToDetail(detailedBills);
    }

    public void updateToDB(List<Bill> bills) {
        db.auditAndUpdate(bills);
    }
}
