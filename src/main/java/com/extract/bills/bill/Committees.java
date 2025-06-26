package com.extract.bills.bill;

import java.util.Objects;


public class Committees {
    private int count;
    private String url;
    
	public Committees() {
		//default constructor
	}

	public Committees(int count, String url) {
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
		Committees committees = (Committees) o;
		return count == committees.count && 
		       Objects.equals(url, committees.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, url);
	}
}
