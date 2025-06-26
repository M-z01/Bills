package com.extract.bills.bill;

import java.util.Objects;


public class CommitteeReport {
    private String citation;
	private String url;

	public CommitteeReport() {
		//default constructor
	}

	public CommitteeReport(String citation, String url) {
		this.citation = citation;
		this.url = url;
	}

    public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
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
		CommitteeReport committeeReport = (CommitteeReport) o;
		return Objects.equals(citation, committeeReport.citation) &&
				Objects.equals(url, committeeReport.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(citation, url);
	}
}
