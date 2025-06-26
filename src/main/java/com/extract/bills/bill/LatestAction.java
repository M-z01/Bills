package com.extract.bills.bill;

import java.util.Objects;


public class LatestAction {
	private String actionDate;
    private String text;
    
	public LatestAction() {
		//default constructor
	}

	public LatestAction(String actionDate, String text) {
		this.actionDate = actionDate;
		this.text = text;
	}
	
	public String getActionDate() {
		return actionDate;
	}
	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LatestAction latestAction = (LatestAction) o;
		return Objects.equals(actionDate, latestAction.actionDate) &&
		       Objects.equals(text, latestAction.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(actionDate, text);
	}
}
