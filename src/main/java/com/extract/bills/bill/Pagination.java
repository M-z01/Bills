package com.extract.bills.bill;

import java.util.Objects;


public class Pagination {
	private int count;
    private String next;
    
	public Pagination() {
		//default constructor
	}

	public Pagination(int count, String next) {
		this.count = count;
		this.next = next;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pagination pagination = (Pagination) o;
		return count == pagination.count && 
		       Objects.equals(next, pagination.next);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, next);
	}
}
