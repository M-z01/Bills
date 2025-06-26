package com.extract.bills.bill;

import java.util.Objects;


public class PolicyArea {
    private String name;

	public PolicyArea() {
		//default constructor
	}

	public PolicyArea(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PolicyArea policyArea = (PolicyArea) o;
		return Objects.equals(name, policyArea.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
