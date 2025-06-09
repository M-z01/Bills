package com.extract.bills.bill;

import java.util.List;

public class Bill {
	public static String[] chambers = {"/hr/", "/s/", "/hjres/", "/sjres/", "/hconres/", "/sconres/", "/hres/", "/sres/"};
	private Actions actions;
    private Amendments amendments;
    private List<CboCostEstimate> cboCostEstimates;
    private List<CommitteeReport> committeeReports;
    private Committees committees;
    private int congress;
    private String constitutionalAuthorityStatementText;
    private Cosponsors cosponsors;
    private String introducedDate;
    private LatestAction latestAction;
    private List<Law> laws;
    private int number;
    private String originChamber;
    private PolicyArea policyArea;
    private RelatedBills relatedBills;
    private List<Sponsor> sponsors;
    private Subjects subjects;
    private Summaries summaries;
    private TextVersions textVersions;
    private String title;
    private Titles titles;
    private String type;
    private String updateDate;
    private String updateDateIncludingText;
    private String name;
    private String overview;
    private String status;
    private String url;
    
	public int getCongress() {
		return congress;
	}
	public void setCongress(int congress) {
		this.congress = congress;
	}
	public LatestAction getLatestAction() {
		return latestAction;
	}
	public void setLatestAction(LatestAction latestAction) {
		this.latestAction = latestAction;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getOriginChamber() {
		return originChamber;
	}
	public void setOriginChamber(String originChamber) {
		this.originChamber = originChamber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateDateIncludingText() {
		return updateDateIncludingText;
	}
	public void setUpdateDateIncludingText(String updateDateIncludingText) {
		this.updateDateIncludingText = updateDateIncludingText;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Actions getActions() {
		return actions;
	}
	public void setActions(Actions actions) {
		this.actions = actions;
	}
	public Amendments getAmendments() {
		return amendments;
	}
	public void setAmendments(Amendments amendments) {
		this.amendments = amendments;
	}
	public List<CboCostEstimate> getCboCostEstimates() {
		return cboCostEstimates;
	}
	public void setCboCostEstimates(List<CboCostEstimate> cboCostEstimates) {
		this.cboCostEstimates = cboCostEstimates;
	}
	public List<CommitteeReport> getCommitteeReports() {
		return committeeReports;
	}
	public void setCommitteeReports(List<CommitteeReport> committeeReports) {
		this.committeeReports = committeeReports;
	}
	public Committees getCommittees() {
		return committees;
	}
	public void setCommittees(Committees committees) {
		this.committees = committees;
	}
	public String getConstitutionalAuthorityStatementText() {
		return constitutionalAuthorityStatementText;
	}
	public void setConstitutionalAuthorityStatementText(String constitutionalAuthorityStatementText) {
		this.constitutionalAuthorityStatementText = constitutionalAuthorityStatementText;
	}
	public Cosponsors getCosponsors() {
		return cosponsors;
	}
	public void setCosponsors(Cosponsors cosponsors) {
		this.cosponsors = cosponsors;
	}
	public String getIntroducedDate() {
		return introducedDate;
	}
	public void setIntroducedDate(String introducedDate) {
		this.introducedDate = introducedDate;
	}
	public List<Law> getLaws() {
		return laws;
	}
	public void setLaws(List<Law> laws) {
		this.laws = laws;
	}
	public PolicyArea getPolicyArea() {
		return policyArea;
	}
	public void setPolicyArea(PolicyArea policyArea) {
		this.policyArea = policyArea;
	}
	public RelatedBills getRelatedBills() {
		return relatedBills;
	}
	public void setRelatedBills(RelatedBills relatedBills) {
		this.relatedBills = relatedBills;
	}
	public List<Sponsor> getSponsors() {
		return sponsors;
	}
	public void setSponsors(List<Sponsor> sponsors) {
		this.sponsors = sponsors;
	}
	public Subjects getSubjects() {
		return subjects;
	}
	public void setSubjects(Subjects subjects) {
		this.subjects = subjects;
	}
	public Summaries getSummaries() {
		return summaries;
	}
	public void setSummaries(Summaries summaries) {
		this.summaries = summaries;
	}
	public TextVersions getTextVersions() {
		return textVersions;
	}
	public void setTextVersions(TextVersions textVersions) {
		this.textVersions = textVersions;
	}
	public Titles getTitles() {
		return titles;
	}
	public void setTitles(Titles titles) {
		this.titles = titles;
	}
}
