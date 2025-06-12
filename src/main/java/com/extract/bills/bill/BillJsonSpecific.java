package com.extract.bills.bill;

import com.extract.bills.ingest.Request;

public class BillJsonSpecific {
	private Bill bill;
    private Request request;
    
	public Bill getBill() {
		return bill;
	}
	public void setBill(Bill bill) {
		this.bill = bill;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
    
}
