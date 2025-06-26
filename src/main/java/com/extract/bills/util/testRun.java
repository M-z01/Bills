package com.extract.bills.util;

import java.sql.SQLException;

import com.extract.bills.bill.APIAccess;
import com.extract.bills.bill.Bill;
import com.extract.bills.db.DBHandler;
import com.extract.bills.ingest.JsonHandler;

public class testRun {
	@SuppressWarnings("unused")
	public static void main(String args[]) throws SQLException {
//		DBHandler billsinfo = new DBHandler("bills");
//		try {
//			ResultSet rs = billsinfo.query("Select * from billsinfo");
//			//ResultSet rs = billsinfo.query("Select * from billsdetail");
//			String type = rs.getString("type");
//			System.out.println(type);
//			rs.close();
//			billsinfo.connClose();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
		System.out.println();
		String defaultBills = APIAccess.getUrlDefault();
		String detailedBill = APIAccess.getUrlspecific("118", "/S/", "254");
		//String byCongress = APIAccess.getUrlByCongress("118");
		//String byType = APIAccess.getUrlByType("118", "s");
		//JsonHandler Jsh3 = new JsonHandler(byCongress);
		//JsonHandler Jsh4 = new JsonHandler(byType);

		//JsonHandler JsH = new JsonHandler(defaultBills);
		JsonHandler JsH2 = new JsonHandler(detailedBill);
		DBHandler db = new DBHandler("bills_db");

		//FindNewestBills f = new FindNewestBills();
		//System.out.println("success");
	
		Bill bdb = db.getBillFromDB("/S/", 254);
		Bill bjs = JsH2.getbJsnSpecific().getBill();
		Boolean test = bdb.equals(bjs);
		System.out.println("test: " + test);
	}
}
