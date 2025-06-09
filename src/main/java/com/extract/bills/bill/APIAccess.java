package com.extract.bills.bill;


public class APIAccess {
	public static final String key = "c1G9h5kf29Il0Zbtlwz8YZkOOgKizSxmwg1SU9CG";
	public static final String defaultBill = "https://api.congress.gov/v3/bill";
	private static final String listFormat = "?format=json&offset=0&limit=20&sort=updateDate+desc&api_key=";
	private static final String singleFormat = "?format=json&api_key=";
	
	public static String getUrlDefault() {
		return defaultBill + listFormat + key;
	}
	
	public static String getUrlByCongress(String congress) {
		return defaultBill + "/" + congress + listFormat + key;
	}
	
	public static String getUrlByType(String congress, String type) {
		return defaultBill + "/" + congress + "/" + type + listFormat + key;
	}
	
	public static String getUrlspecific(String congress, String type, String billIndex) {
		return defaultBill + "/" + congress + "/" + type + "/" + billIndex + singleFormat + key;
	}
}
