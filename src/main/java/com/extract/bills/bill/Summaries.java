package com.extract.bills.bill;

import java.util.Objects;


public class Summaries {
    private int count;
    private String url;
    
	public Summaries() {
		//default constructor
	}

	public Summaries(int count, String url) {
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
		Summaries summaries = (Summaries) o;
		return count == summaries.count && 
		       Objects.equals(url, summaries.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, url);
	}
}
