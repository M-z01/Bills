package com.extract.bills.bill;


public class Cosponsors {
    private int count;
    private int countIncludingWithdrawnCosponsors;
    private String url;
    
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCountIncludingWithdrawnCosponsors() {
		return countIncludingWithdrawnCosponsors;
	}
	public void setCountIncludingWithdrawnCosponsors(int countIncludingWithdrawnCosponsors) {
		this.countIncludingWithdrawnCosponsors = countIncludingWithdrawnCosponsors;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
    
}
