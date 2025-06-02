package com.extract.bills.bill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class test {
	public static void main(String args[]) {
		List<String> gay = new ArrayList<String>();
		String[] gay2 = {"/hr/", "/s/", "/hjres/", "/sjres/", "/hconres/", "/sconres/", "/hres/", "/sres/"};
		String testString2 = "https://api.congress.gov/v3/bill/117/hr/3076?api_key=c1G9h5kf29Il0Zbtlwz8YZkOOgKizSxmwg1SU9CG";
		String testString1 = "https://api.congress.gov/v3/bill?format=json&limit=1&sort=updateDate+desc&api_key=c1G9h5kf29Il0Zbtlwz8YZkOOgKizSxmwg1SU9CG";
		
		gay.add("/hr/");
		gay.add("/s/");
		gay.add("/hjres/");
		gay.add("/sjres/");
		gay.add("/hconres/");
		gay.add("/sconres/");
		gay.add("/hres/");
		gay.add("/sres/");
		
		System.out.println(stringContainsItemFromList(testString1, gay));
		System.out.println(stringContainsItemFromList(testString2, gay));
		System.out.println(stringContainsItemFromList(testString1, gay2));
		System.out.println(stringContainsItemFromList(testString2, gay2));
		
	}
	
	public static boolean stringContainsItemFromList(String inputStr, List<String> items) {
	    return items.stream().anyMatch(inputStr::contains);
	}
	
	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).anyMatch(inputStr::contains);
	}
}
