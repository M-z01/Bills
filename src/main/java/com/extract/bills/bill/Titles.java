package com.extract.bills.bill;

import java.util.Objects;


public class Titles {
    private int count;
    private String url;
    
	public Titles() {
		//default constructor
	}

	public Titles(int count, String url) {
		this.count = count;
		this.url = url;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Titles titles = (Titles) o;
		return count == titles.count && 
		       Objects.equals(url, titles.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, url);
	}
}
