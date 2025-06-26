package com.extract.bills.bill;

import java.util.Objects;


public class Sponsor {
    private String bioguideId;
    private int district;
    private String firstName;
    private String fullName;
    private String isByRequest;
    private String lastName;
    private String middleName;
    private String party;
    private String state;
    private String url;
    
	public Sponsor() {
		//default constructor
	}

	public Sponsor(String bioguideId, int district, String firstName, String fullName, String isByRequest,
			String lastName, String middleName, String party, String state, String url) {
		this.bioguideId = bioguideId;
		this.district = district;
		this.firstName = firstName;
		this.fullName = fullName;
		this.isByRequest = isByRequest;
		this.lastName = lastName;
		this.middleName = middleName;
		this.party = party;
		this.state = state;
		this.url = url;
	}
	
	public String getBioguideId() {
		return bioguideId;
	}
	public void setBioguideId(String bioguideId) {
		this.bioguideId = bioguideId;
	}
	public int getDistrict() {
		return district;
	}
	public void setDistrict(int district) {
		this.district = district;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getIsByRequest() {
		return isByRequest;
	}
	public void setIsByRequest(String isByRequest) {
		this.isByRequest = isByRequest;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getParty() {
		return party;
	}
	public void setParty(String party) {
		this.party = party;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
		Sponsor sponsor = (Sponsor) o;
		return district == sponsor.district &&
				Objects.equals(bioguideId, sponsor.bioguideId) &&
				Objects.equals(firstName, sponsor.firstName) &&
				Objects.equals(fullName, sponsor.fullName) &&
				Objects.equals(isByRequest, sponsor.isByRequest) &&
				Objects.equals(lastName, sponsor.lastName) &&
				Objects.equals(middleName, sponsor.middleName) &&
				Objects.equals(party, sponsor.party) &&
				Objects.equals(state, sponsor.state) &&
				Objects.equals(url, sponsor.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bioguideId, district, firstName, fullName, isByRequest, lastName, middleName, party, state, url);
	}
}
