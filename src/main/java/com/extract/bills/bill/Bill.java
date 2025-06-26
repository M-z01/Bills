package com.extract.bills.bill;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bill {
	public static String[] chambers = {"/HR/", "/S/", "/HJRES/", "/SJRES/", "/HCONRES/", "/SCONRES/", "/HRES/", "/SRES/"};
	private Actions actions;
    private Amendments amendments;
    private List<CboCostEstimate> cboCostEstimates;
    private List<CommitteeReport> committeeReports;
    private Committees committees;
    private int congress;
    private String constitutionalAuthorityStatementText;
    private Cosponsors cosponsors;
    private Instant introducedDate;
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
    private Instant updateDate;
    private Instant updateDateIncludingText;
    private String name;
    private String overview;
    private String status;
    private String url;
    
	public Bill() {
		//default constructor
	}

	public Bill(ResultSet primAndWrapperFieldsRs, ResultSet CboCostEstimateRs, 
	ResultSet committeeReportRs, ResultSet lawRs, ResultSet sponsorRs) {
		importFromDB(primAndWrapperFieldsRs, CboCostEstimateRs, committeeReportRs, lawRs, sponsorRs);
	}

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
	public Instant getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		if (updateDate.contains("T")){
			// Ensure full format
			if(updateDate.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}Z$")){
				updateDate = updateDate.replace("Z", ":00Z");
			}
			this.updateDate = Instant.parse(updateDate);
		} else {
			//convert from date to instant at start of day UTC
			this.updateDate = Instant.parse(updateDate + "T00:00:00Z");
		}
	}
	public void setUpdateDate(Instant instant) {
		this.updateDate = instant;
	}
	public Instant getUpdateDateIncludingText() {
		return updateDateIncludingText;
	}
	public void setUpdateDateIncludingText(String updateDateIncludingText) {
		if (updateDateIncludingText.contains("T")){
			// Ensure full format
			if(updateDateIncludingText.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}Z$")){
				updateDateIncludingText = updateDateIncludingText.replace("Z", ":00Z");
			}
			this.updateDateIncludingText = Instant.parse(updateDateIncludingText);
		} else {
			//convert from date to instant at start of day UTC
			this.updateDateIncludingText = Instant.parse(updateDateIncludingText + "T00:00:00Z");
		}
	}
	public void setUpdateDateIncludingText(Instant instant) {
		this.updateDateIncludingText = instant;
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
	public Instant getIntroducedDate() {
		return introducedDate;
	}
	public void setIntroducedDate(Instant introducedDate) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Bill bill = (Bill) o;
		return number == bill.number &&
				congress == bill.congress &&
				Objects.equals(type, bill.type) &&
				Objects.equals(title, bill.title) &&
				Objects.equals(originChamber, bill.originChamber) &&
				Objects.equals(actions, bill.actions) &&
				Objects.equals(amendments, bill.amendments) &&
				Objects.equals(cboCostEstimates, bill.cboCostEstimates) &&
				Objects.equals(committeeReports, bill.committeeReports) &&
				Objects.equals(committees, bill.committees) &&
				Objects.equals(constitutionalAuthorityStatementText, bill.constitutionalAuthorityStatementText) &&
				Objects.equals(cosponsors, bill.cosponsors) &&
				Objects.equals(introducedDate, bill.introducedDate) &&
				Objects.equals(latestAction, bill.latestAction) &&
				Objects.equals(laws, bill.laws) &&
				Objects.equals(policyArea, bill.policyArea) &&
				Objects.equals(relatedBills, bill.relatedBills) &&
				Objects.equals(sponsors, bill.sponsors) &&
				Objects.equals(subjects, bill.subjects) &&
				Objects.equals(summaries, bill.summaries) &&
				Objects.equals(textVersions, bill.textVersions) &&
				Objects.equals(titles, bill.titles) &&
				Objects.equals(updateDate, bill.updateDate) &&
				Objects.equals(updateDateIncludingText, bill.updateDateIncludingText) &&
				Objects.equals(name, bill.name) &&
				Objects.equals(overview, bill.overview) &&
				Objects.equals(status, bill.status) &&
				Objects.equals(url, bill.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			number, congress, type, title, originChamber, actions, amendments, cboCostEstimates,
			committeeReports, committees, constitutionalAuthorityStatementText, cosponsors,
			introducedDate, latestAction, laws, policyArea, relatedBills, sponsors, subjects,
			summaries, textVersions, titles, updateDate, updateDateIncludingText, name, overview, status, url
		);
	}

	public void importFromDB(ResultSet primAndWrapperFieldsRs, ResultSet cboCostEstimateRs, 
		ResultSet committeeReportRs, ResultSet lawRs, ResultSet sponsorRs) {
		try {
			setCongress(118);// Hardcoded for now, should be set dynamically if needed
			setType(primAndWrapperFieldsRs.getString("type"));
			setNumber(primAndWrapperFieldsRs.getInt("number"));
			setTitle(primAndWrapperFieldsRs.getString("title"));
			setOriginChamber(primAndWrapperFieldsRs.getString("origin_chamber"));
			setConstitutionalAuthorityStatementText(primAndWrapperFieldsRs.getString("constitutional_authority_statement_text"));
			String introducedDateStr = primAndWrapperFieldsRs.getString("introduced_date");
			if (introducedDateStr != null) {
				// If the string does not contain 'T', it's just a date
				if (!introducedDateStr.contains("T")) {
					introducedDateStr = introducedDateStr + "T00:00:00Z";
				}
				setIntroducedDate(Instant.parse(introducedDateStr));
			} else {
				setIntroducedDate(null);
			}
			setUpdateDate(primAndWrapperFieldsRs.getTimestamp("update_date") != null ?
				primAndWrapperFieldsRs.getTimestamp("update_date").toInstant() : (Instant) null);
			setUpdateDateIncludingText(primAndWrapperFieldsRs.getTimestamp("update_date_including_text") != null ?
				primAndWrapperFieldsRs.getTimestamp("update_date_including_text").toInstant() : (Instant) null);
			
			if (primAndWrapperFieldsRs.getInt("bill_actions_count") != 0) {
				setActions(new Actions(primAndWrapperFieldsRs.getInt("bill_actions_count"), 
					primAndWrapperFieldsRs.getString("bill_actions_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_amendments_count") != 0){
				setAmendments(new Amendments(primAndWrapperFieldsRs.getInt("bill_amendments_count"), 
					primAndWrapperFieldsRs.getString("bill_amendments_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_committees_count") != 0) {
				setCommittees(new Committees(primAndWrapperFieldsRs.getInt("bill_committees_count"), 
					primAndWrapperFieldsRs.getString("bill_committees_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_cosponsors_count") != 0) {
				setCosponsors(new Cosponsors(primAndWrapperFieldsRs.getInt("bill_cosponsors_count"), 
					primAndWrapperFieldsRs.getInt("bill_cosponsors_count_including_withdrawn"),
					primAndWrapperFieldsRs.getString("bill_cosponsors_url")));
			}
			
			if (primAndWrapperFieldsRs.getString("bill_latest_action_text") != null) {
				setLatestAction(new LatestAction(
					primAndWrapperFieldsRs.getDate("bill_latest_action_date") != null ? 
					primAndWrapperFieldsRs.getDate("bill_latest_action_date").toString() : null, 
					primAndWrapperFieldsRs.getString("bill_latest_action_text")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_related_bills_count") != 0) {
				setRelatedBills(new RelatedBills(primAndWrapperFieldsRs.getInt("bill_related_bills_count"), 
					primAndWrapperFieldsRs.getString("bill_related_bills_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_subjects_count") != 0) {
				setSubjects(new Subjects(primAndWrapperFieldsRs.getInt("bill_subjects_count"), 
					primAndWrapperFieldsRs.getString("bill_subjects_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_summaries_count") != 0) {
				setSummaries(new Summaries(primAndWrapperFieldsRs.getInt("bill_summaries_count"), 
					primAndWrapperFieldsRs.getString("bill_summaries_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_text_versions_count") != 0) {
				setTextVersions(new TextVersions(primAndWrapperFieldsRs.getInt("bill_text_versions_count"), 
					primAndWrapperFieldsRs.getString("bill_text_versions_url")));
			}
			
			if (primAndWrapperFieldsRs.getInt("bill_titles_count") != 0) {
				setTitles(new Titles(primAndWrapperFieldsRs.getInt("bill_titles_count"), 
					primAndWrapperFieldsRs.getString("bill_titles_url")));
			}

			if(primAndWrapperFieldsRs.getString("bill_policy_area_name") != null) {
				setPolicyArea(new PolicyArea(primAndWrapperFieldsRs.getString("bill_policy_area_name")));
			}
			
			while(cboCostEstimateRs.next()) {
				if (cboCostEstimates == null) {
					cboCostEstimates = new ArrayList<>();
				}
				cboCostEstimates.add(new CboCostEstimate(
					cboCostEstimateRs.getString("bill_cbo_cost_estimate_description"),
					cboCostEstimateRs.getTimestamp("bill_cbo_cost_estimate_pub_date") != null ? 
						cboCostEstimateRs.getTimestamp("bill_cbo_cost_estimate_pub_date").toString() : null,
					cboCostEstimateRs.getString("bill_cbo_cost_estimate_title"),
					cboCostEstimateRs.getString("bill_cbo_cost_estimate_url")));
			}
			
			while(committeeReportRs.next()) {
				if (committeeReports == null) {
					committeeReports = new ArrayList<>();
				}
				committeeReports.add(new CommitteeReport(
					committeeReportRs.getString("bill_committee_report_citation"),
					committeeReportRs.getString("bill_committee_report_url")));
			}

			while(lawRs.next()) {
				if (laws == null) {
					laws = new ArrayList<>();
				}
				laws.add(new Law(
					lawRs.getString("bill_law_number"),
					lawRs.getString("bill_law_type")));
			}

			while(sponsorRs.next()) {
				if (sponsors == null) {
					sponsors = new ArrayList<>();
				}
				sponsors.add(new Sponsor(
					sponsorRs.getString("bill_sponsor_bioguide_id"),
					sponsorRs.getInt("bill_sponsor_district"),
					sponsorRs.getString("bill_sponsor_first_name"),
					sponsorRs.getString("bill_sponsor_full_name"),
					sponsorRs.getString("bill_sponsor_is_by_request"),
					sponsorRs.getString("bill_sponsor_last_name"),
					sponsorRs.getString("bill_sponsor_middle_name"),
					sponsorRs.getString("bill_sponsor_party"),
					sponsorRs.getString("bill_sponsor_state"),
					sponsorRs.getString("bill_sponsor_url")));
			}

		} catch (Exception e) {
			System.out.println("Error importing Bill from DB: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
