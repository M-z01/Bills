package com.extract.bills.bill;

import java.util.Objects;


public class CboCostEstimate {
    private String description;
    private String pubDate;
    private String title;
    private String url;
    
	public CboCostEstimate() {
		//default constructor
	}

	public CboCostEstimate(String description, String pubDate, String title, String url) {
		this.description = description;
		this.pubDate = pubDate;
		this.title = title;
		this.url = url;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
		CboCostEstimate cboCostEstimate = (CboCostEstimate) o;
		return Objects.equals(description, cboCostEstimate.description) &&
				Objects.equals(pubDate, cboCostEstimate.pubDate) &&
				Objects.equals(title, cboCostEstimate.title) &&
				Objects.equals(url, cboCostEstimate.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, pubDate, title, url);
	}

}
