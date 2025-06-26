package com.extract.bills.bill;

import java.util.Objects;


public class Cosponsors {
    private int count;
    private int countIncludingWithdrawnCosponsors;
    private String url;
    
	public Cosponsors() {
		//default constructor
	}

	public Cosponsors(int count, int countIncludingWithdrawnCosponsors, String url) {
		this.count = count;
		this.countIncludingWithdrawnCosponsors = countIncludingWithdrawnCosponsors;
		this.url = url;
	}

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
    
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Cosponsors cosponsors = (Cosponsors) o;
		return count == cosponsors.count && 
		       countIncludingWithdrawnCosponsors == cosponsors.countIncludingWithdrawnCosponsors &&
		       Objects.equals(url, cosponsors.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, countIncludingWithdrawnCosponsors, url);
	}
}
