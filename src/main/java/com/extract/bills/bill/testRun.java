package com.extract.bills.bill;
public class testRun {
	@SuppressWarnings("unused")
	public static void main(String args[]) {
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
		String detailedBill = APIAccess.getUrlspecific("118", "s", "1");
		String byCongress = APIAccess.getUrlByCongress("118");
		String byType = APIAccess.getUrlByType("118", "s");
		//JsonHandler Jsh3 = new JsonHandler(byCongress);
		//JsonHandler Jsh4 = new JsonHandler(byType);
		JsonHandler JsH = new JsonHandler(defaultBills);
		//JsonHandler JsH2 = new JsonHandler(detailedBill);

		FindNewestBills f = new FindNewestBills();
		System.out.println("success");
		
		// JsH.getbJsn().getBills().forEach((e) -> {
		// 	System.out.println(e.getOriginChamber());
		// 	System.out.println(e.getNumber());
		// 	System.out.println(e.getUpdateDateIncludingText());
		// });
	}
}
