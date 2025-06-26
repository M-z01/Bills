package com.extract.bills.bill;

import java.util.Objects;


public class Law {
    private String number;
    private String type;
    
	public Law() {
		//default constructor
	}

	public Law(String number, String type) {
		this.number = number;
		this.type = type;
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Law law = (Law) o;
		return Objects.equals(number, law.number) && 
		       Objects.equals(type, law.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(number, type);
	}
}
