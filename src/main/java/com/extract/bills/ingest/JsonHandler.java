package com.extract.bills.ingest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.extract.bills.bill.Bill;
import com.extract.bills.bill.BillJson;
import com.extract.bills.bill.BillJsonSpecific;


public class JsonHandler {
	private BillJson bJsn;
	private BillJsonSpecific bJsnSpecific;
	public JsonHandler (String link) {
		try {
			URL url = new URL(link);
			InputStreamReader reader = new InputStreamReader(url.openStream());
			Gson gson = new GsonBuilder().registerTypeAdapter(Bill.class, new BillDeserializer()).create();
			if(stringContainsItemFromList(link, Bill.chambers)) {//specific bill
				bJsnSpecific = gson.fromJson(reader, BillJsonSpecific.class);
			} else {
				bJsn = gson.fromJson(reader, BillJson.class);//list of bills
			}
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public BillJson getbJsn() {
		return bJsn;
	}
	public void setbJsn(BillJson bJsn) {
		this.bJsn = bJsn;
	}
	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).anyMatch(inputStr::contains);
	}
}
