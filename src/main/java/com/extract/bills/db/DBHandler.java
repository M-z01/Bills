package com.extract.bills.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import com.extract.bills.bill.Bill;
import com.extract.bills.bill.CboCostEstimate;
import com.extract.bills.bill.CommitteeReport;
import com.extract.bills.bill.Law;
import com.extract.bills.bill.Sponsor;

public class DBHandler {
    private String url;
    private String username;
    private String password;
    private Connection conn;
    private String DBName;

    public DBHandler(String DBName) {
        this.url = "jdbc:mysql://localhost:3306/" + DBName + "?serverTimezone=UTC";
        this.username = "root";
        this.password = com.extract.bills.util.PasswordReader.getPassword();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = connectToDB(DBName);
            this.DBName = DBName;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connectToDB(String DBName) throws SQLException {
        Connection conn = DriverManager.getConnection(this.url, this.username, this.password);
        System.out.println("Connected to " + DBName + ".");
        return conn;
    }

    public ResultSet query(String queryString) throws SQLException {
        Statement st = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return st.executeQuery(queryString);
    }

    public int insert(String insertString) throws SQLException {
        PreparedStatement pst = this.conn.prepareStatement(insertString);
        int rowAffected = pst.executeUpdate();
        pst.close();
        return rowAffected;
    }

    public void insertToInfo(List<Bill> bills) {
        String sql = "INSERT INTO bills_info (type, number, lmd) VALUES (?, ?, ?)";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                Instant lmd = bill.getUpdateDateIncludingText() != null ? bill.getUpdateDateIncludingText() : bill.getUpdateDate();
                pst.setTimestamp(3, lmd != null ? Timestamp.from(lmd) : null);
                pst.addBatch();
                System.out.printf("New Bill %s-%d detected and added to database.\n", bill.getType(), bill.getNumber());
            }
            pst.executeBatch();
            System.out.println("Inserted " + bills.size() + " new bills into bills_info.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertToDetail(List<Bill> bills) {
        String sql = """
                INSERT INTO bills_detail (type, number, title, origin_chamber, 
                constitutional_authority_statement_text, introduced_date, 
                latest_action_text, latest_action_date, update_date, 
                update_date_including_text, name, overview, status, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.setString(3, bill.getTitle());
                pst.setString(4, bill.getOriginChamber());
                pst.setString(5, bill.getConstitutionalAuthorityStatementText());
                if (bill.getIntroducedDate() != null) {
                    pst.setDate(6, Date.valueOf(bill.getIntroducedDate().atZone(ZoneOffset.UTC).toLocalDate()));
                } else {
                    pst.setNull(6, java.sql.Types.DATE);
                }
                // latestAction
                if (bill.getLatestAction() != null) {
                    pst.setString(7, bill.getLatestAction().getText());
                    pst.setDate(8, bill.getLatestAction().getActionDate() != null ?
                            Date.valueOf(java.time.LocalDate.parse(bill.getLatestAction().getActionDate())) : null);
                } else {
                    pst.setNull(7, java.sql.Types.VARCHAR);
                    pst.setNull(8, java.sql.Types.DATE);
                }
                pst.setTimestamp(9, bill.getUpdateDate() != null ? Timestamp.from(bill.getUpdateDate()) : null);
                pst.setTimestamp(10, bill.getUpdateDateIncludingText() != null ?
                        Timestamp.from(bill.getUpdateDateIncludingText()) :
                        (bill.getUpdateDate() != null ? Timestamp.from(bill.getUpdateDate()) : null));
                pst.setString(11, bill.getName());
                pst.setString(12, bill.getOverview());
                pst.setString(13, bill.getStatus());
                pst.setString(14, bill.getUrl());
                pst.addBatch();
            }
            pst.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bills_detail.");
        try {
            insertToChildTables(bills);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertToChildTables(List<Bill> bills) throws SQLException {
        insertToActions(bills);
        insertToAmendments(bills);
        insertToCboCostEstimates(bills);
        insertToCommitteeReports(bills);
        insertToCommittees(bills);
        insertToCosponsors(bills);
        insertToLaws(bills);
        insertToSponsors(bills);
        insertToSubjects(bills);
        insertToSummaries(bills);
        insertToTextVersions(bills);
        insertToTitles(bills);
        insertToRelatedBills(bills);
        insertToLatestActions(bills);
        insertToPolicyArea(bills);
    }

    private void insertToActions(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_actions (type, number, bill_actions_count, bill_actions_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer count = bill.getActions() != null ? bill.getActions().getCount() : null;
                String url = bill.getActions() != null ? bill.getActions().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (count != null) {
                    pst.setInt(3, count);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_actions.");
    }

    private void insertToAmendments(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_amendments (type, number, bill_amendments_count, bill_amendments_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer amendmentCount = bill.getAmendments() != null ? bill.getAmendments().getCount() : null;
                String url = bill.getAmendments() != null ? bill.getAmendments().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (amendmentCount != null) {
                    pst.setInt(3, amendmentCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_amendments.");
    }

    private void insertToCboCostEstimates(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_cbo_cost_estimates (type, number, bill_cbo_cost_estimate_description, bill_cbo_cost_estimate_pub_date, 
                bill_cbo_cost_estimate_title, bill_cbo_cost_estimate_url) VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                List<CboCostEstimate> estimates = bill.getCboCostEstimates();
                if (estimates != null) {
                    for (CboCostEstimate cboCostEstimate : estimates) {
                        pst.setString(1, bill.getType());
                        pst.setInt(2, bill.getNumber());
                        pst.setString(3, cboCostEstimate.getDescription());
                        pst.setTimestamp(4, cboCostEstimate.getPubDate() != null ?
                                Timestamp.from(Instant.parse(cboCostEstimate.getPubDate())) : null);
                        pst.setString(5, cboCostEstimate.getTitle());
                        pst.setString(6, cboCostEstimate.getUrl());
                        pst.addBatch();
                    }
                }
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_cbo_cost_estimates.");
    }

    private void insertToCommitteeReports(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_committee_reports (type, number, bill_committee_report_citation, bill_committee_report_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                List<CommitteeReport> reports = bill.getCommitteeReports();
                if (reports != null) {
                    for (CommitteeReport report : reports) {
                        pst.setString(1, bill.getType());
                        pst.setInt(2, bill.getNumber());
                        pst.setString(3, report.getCitation());
                        pst.setString(4, report.getUrl());
                        pst.addBatch();
                    }
                }
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_committee_reports.");
    }

    private void insertToCommittees(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_committees (type, number, bill_committees_count, bill_committees_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer committeeCount = bill.getCommittees() != null ? bill.getCommittees().getCount() : null;
                String url = bill.getCommittees() != null ? bill.getCommittees().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (committeeCount != null) {
                    pst.setInt(3, committeeCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_committees.");
    }

    private void insertToCosponsors(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_cosponsors (type, number, bill_cosponsors_count, bill_cosponsors_count_including_withdrawn, bill_cosponsors_url) VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer cosponsorCount = bill.getCosponsors() != null ? bill.getCosponsors().getCount() : null;
                Integer cosponsorCountIncludingWithdrawn = bill.getCosponsors() != null ? bill.getCosponsors().getCountIncludingWithdrawnCosponsors() : null;
                String url = bill.getCosponsors() != null ? bill.getCosponsors().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (cosponsorCount != null) {
                    pst.setInt(3, cosponsorCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                if (cosponsorCountIncludingWithdrawn != null) {
                    pst.setInt(4, cosponsorCountIncludingWithdrawn);
                } else {
                    pst.setNull(4, java.sql.Types.INTEGER);
                }
                pst.setString(5, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_cosponsors.");
    }

    private void insertToLaws(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_laws (type, number, bill_law_number, bill_law_type) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                List<Law> laws = bill.getLaws();
                if (laws != null) {
                    for (Law law : laws) {
                        pst.setString(1, bill.getType());
                        pst.setInt(2, bill.getNumber());
                        pst.setString(3, law.getNumber());
                        pst.setString(4, law.getType());
                        pst.addBatch();
                    }
                }
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_laws.");
    }

    private void insertToSponsors(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_sponsors (type, number, bill_sponsor_bioguide_id, bill_sponsor_district, bill_sponsor_first_name, 
                bill_sponsor_last_name, bill_sponsor_middle_name, bill_sponsor_full_name,
                bill_sponsor_is_by_request, bill_sponsor_party, bill_sponsor_state, bill_sponsor_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                List<Sponsor> sponsors = bill.getSponsors();
                if (sponsors != null) {
                    for (Sponsor sponsor : sponsors) {
                        pst.setString(1, bill.getType());
                        pst.setInt(2, bill.getNumber());
                        pst.setString(3, sponsor.getBioguideId());
                        if (sponsor.getDistrict() != -1) {
                            pst.setInt(4, sponsor.getDistrict());
                        } else {
                            pst.setNull(4, java.sql.Types.INTEGER);
                        }
                        pst.setString(5, sponsor.getFirstName());
                        pst.setString(6, sponsor.getLastName());
                        pst.setString(7, sponsor.getMiddleName());
                        pst.setString(8, sponsor.getFullName());
                        pst.setString(9, sponsor.getIsByRequest());
                        pst.setString(10, sponsor.getParty());
                        pst.setString(11, sponsor.getState());
                        pst.setString(12, sponsor.getUrl());
                        pst.addBatch();
                    }
                }
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_sponsors.");
    }

    private void insertToSubjects(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_subjects (type, number, bill_subjects_count, bill_subjects_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer subjectCount = bill.getSubjects() != null ? bill.getSubjects().getCount() : null;
                String url = bill.getSubjects() != null ? bill.getSubjects().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (subjectCount != null) {
                    pst.setInt(3, subjectCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_subjects.");
    }

    private void insertToSummaries(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_summaries (type, number, bill_summaries_count, bill_summaries_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer summaryCount = bill.getSummaries() != null ? bill.getSummaries().getCount() : null;
                String url = bill.getSummaries() != null ? bill.getSummaries().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (summaryCount != null) {
                    pst.setInt(3, summaryCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_summaries.");
    }

    private void insertToTextVersions(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_text_versions (type, number, bill_text_versions_count, bill_text_versions_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer textVersionCount = bill.getTextVersions() != null ? bill.getTextVersions().getCount() : null;
                String url = bill.getTextVersions() != null ? bill.getTextVersions().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (textVersionCount != null) {
                    pst.setInt(3, textVersionCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_text_versions.");
    }

    private void insertToTitles(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_titles (type, number, bill_titles_count, bill_titles_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer titleCount = bill.getTitles() != null ? bill.getTitles().getCount() : null;
                String url = bill.getTitles() != null ? bill.getTitles().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (titleCount != null) {
                    pst.setInt(3, titleCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_titles.");
    }

    private void insertToRelatedBills(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_related_bills (type, number, bill_related_bills_count, bill_related_bills_url) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                Integer relatedBillCount = bill.getRelatedBills() != null ? bill.getRelatedBills().getCount() : null;
                String url = bill.getRelatedBills() != null ? bill.getRelatedBills().getUrl() : null;
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                if (relatedBillCount != null) {
                    pst.setInt(3, relatedBillCount);
                } else {
                    pst.setNull(3, java.sql.Types.INTEGER);
                }
                pst.setString(4, url);
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_related_bills.");
    }

    private void insertToLatestActions(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_latest_actions (type, number, bill_latest_action_date, bill_latest_action_text) VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                if (bill.getLatestAction() != null) {
                    pst.setString(1, bill.getType());
                    pst.setInt(2, bill.getNumber());
                    pst.setDate(3, bill.getLatestAction().getActionDate() != null ?
                            Date.valueOf(java.time.LocalDate.parse(bill.getLatestAction().getActionDate())) : null);
                    pst.setString(4, bill.getLatestAction().getText());
                    pst.addBatch();
                }
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_latest_actions.");
    }

    private void insertToPolicyArea(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_policy_area (type, number, bill_policy_area_name) VALUES (?, ?, ?)
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                if (bill.getPolicyArea() != null) {
                    pst.setString(1, bill.getType());
                    pst.setInt(2, bill.getNumber());
                    pst.setString(3, bill.getPolicyArea().getName());
                    pst.addBatch();
                }
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " new bills into bill_policy_area.");
    }

    public int update(String updateString) throws SQLException {
        PreparedStatement pst = this.conn.prepareStatement(updateString);
        int rowAffected = pst.executeUpdate();
        pst.close();
        return rowAffected;
    }

    //wip

    public void updateToInfo(List<Bill> bills) {
        //TODO
    }

    public void updateToDetail(List<Bill> bills) {
    String sql = """
        UPDATE bills_detail SET
            title = ?, origin_chamber = ?, constitutional_authority_statement_text = ?, introduced_date = ?,
            latest_action_text = ?, latest_action_date = ?, update_date = ?, update_date_including_text = ?,
            name = ?, overview = ?, status = ?, url = ?
        WHERE type = ? AND number = ?
        """;
    try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
        for (Bill bill : bills) {
            pst.setString(1, bill.getTitle());
            pst.setString(2, bill.getOriginChamber());
            pst.setString(3, bill.getConstitutionalAuthorityStatementText());
            if (bill.getIntroducedDate() != null) {
                pst.setDate(4, Date.valueOf(bill.getIntroducedDate().atZone(ZoneOffset.UTC).toLocalDate()));
            } else {
                pst.setNull(4, java.sql.Types.DATE);
            }
            if (bill.getLatestAction() != null) {
                pst.setString(5, bill.getLatestAction().getText());
                pst.setDate(6, bill.getLatestAction().getActionDate() != null ?
                        Date.valueOf(java.time.LocalDate.parse(bill.getLatestAction().getActionDate())) : null);
            } else {
                pst.setNull(5, java.sql.Types.VARCHAR);
                pst.setNull(6, java.sql.Types.DATE);
            }
            pst.setTimestamp(7, bill.getUpdateDate() != null ? Timestamp.from(bill.getUpdateDate()) : null);
            pst.setTimestamp(8, bill.getUpdateDateIncludingText() != null ?
                    Timestamp.from(bill.getUpdateDateIncludingText()) :
                    (bill.getUpdateDate() != null ? Timestamp.from(bill.getUpdateDate()) : null));
            pst.setString(9, bill.getName());
            pst.setString(10, bill.getOverview());
            pst.setString(11, bill.getStatus());
            pst.setString(12, bill.getUrl());
            pst.setString(13, bill.getType());
            pst.setInt(14, bill.getNumber());
            pst.addBatch();
        }
            pst.executeBatch();
    } catch (SQLException e) {
        e.printStackTrace();
    }
        System.out.println("Updated " + bills.size() + " bills in bills_detail.");
    }




    public void auditAndUpdate(List<Bill> bills) {
        try {
            //TODO: compare bill to db bill and only insert if different
            insertToBillsInfoHistory(bills);
            insertToBillsDetailHistory(bills);
            insertToBillActionsHistory(bills);
            insertToBillAmendmentsHistory(bills);
            insertToBillCboCostEstimatesHistory(bills);
            insertToBillCommitteeReportsHistory(bills);
            insertToBillCommitteesHistory(bills);
            insertToBillCosponsorsHistory(bills);
            insertToBillLawsHistory(bills);
            insertToBillSponsorsHistory(bills);
            insertToBillSubjectsHistory(bills);
            insertToBillSummariesHistory(bills);
            insertToBillTextVersionsHistory(bills);
            insertToBillTitlesHistory(bills);
            insertToBillRelatedBillsHistory(bills);
            insertToBillLatestActionsHistory(bills);
            insertToBillPolicyAreaHistory(bills);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertToBillsInfoHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bills_info_history (type, number, lmd, operation, operation_timestamp)
                SELECT type, number, lmd, 'UPDATE', NOW()
                FROM bills_info
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bills_info_history.");
    }

    private void insertToBillsDetailHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bills_detail_history (type, number, title, origin_chamber, constitutional_authority_statement_text,
                introduced_date, latest_action_text, latest_action_date, update_date, update_date_including_text,
                name, overview, status, url, operation, operation_timestamp)
                SELECT type, number, title, origin_chamber, constitutional_authority_statement_text,
                introduced_date, latest_action_text, latest_action_date, update_date, update_date_including_text,
                name, overview, status, url, 'UPDATE', NOW()
                FROM bills_detail
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bills_detail_history.");
    }

    private void insertToBillActionsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_actions_history (type, number, bill_actions_count, bill_actions_url, operation, operation_timestamp)
                SELECT type, number, bill_actions_count, bill_actions_url, 'UPDATE', NOW()
                FROM bill_actions
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_actions_history.");
    }

    private void insertToBillAmendmentsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_amendments_history (type, number, bill_amendments_count, bill_amendments_url, operation, operation_timestamp)
                SELECT type, number, bill_amendments_count, bill_amendments_url, 'UPDATE', NOW()
                FROM bill_amendments
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_amendments_history.");
    }

    private void insertToBillCboCostEstimatesHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_cbo_cost_estimates_history (id, type, number, bill_cbo_cost_estimate_description, 
                bill_cbo_cost_estimate_pub_date, bill_cbo_cost_estimate_title, bill_cbo_cost_estimate_url, operation, operation_timestamp)
                SELECT id, type, number, bill_cbo_cost_estimate_description,
                bill_cbo_cost_estimate_pub_date, bill_cbo_cost_estimate_title, bill_cbo_cost_estimate_url, 'UPDATE', NOW()
                FROM bill_cbo_cost_estimates
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_cbo_cost_estimates_history.");
    }

    private void insertToBillCommitteeReportsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_committee_reports_history (id, type, number, bill_committee_report_citation, 
                bill_committee_report_url, operation, operation_timestamp)
                SELECT id, type, number, bill_committee_report_citation,
                bill_committee_report_url, 'UPDATE', NOW()
                FROM bill_committee_reports
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_committee_reports_history.");
    }

    public void insertToBillCommitteesHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_committees_history (type, number, bill_committees_count, bill_committees_url, operation, operation_timestamp)
                SELECT type, number, bill_committees_count, bill_committees_url, 'UPDATE', NOW()
                FROM bill_committees
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_committees_history.");
    }

    public void insertToBillCosponsorsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_cosponsors_history (type, number, bill_cosponsors_count, bill_cosponsors_count_including_withdrawn, bill_cosponsors_url, operation, operation_timestamp)
                SELECT type, number, bill_cosponsors_count, bill_cosponsors_count_including_withdrawn, bill_cosponsors_url, 'UPDATE', NOW()
                FROM bill_cosponsors
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_cosponsors_history.");
    }

    public void insertToBillLawsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_laws_history (id, type, number, bill_law_number, bill_law_type, operation, operation_timestamp)
                SELECT id, type, number, bill_law_number, bill_law_type, 'UPDATE', NOW()
                FROM bill_laws
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_laws_history.");
    }

    public void insertToBillSponsorsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_sponsors_history (id, type, number, bill_sponsor_bioguide_id, bill_sponsor_district, 
                bill_sponsor_first_name, bill_sponsor_last_name, bill_sponsor_middle_name, bill_sponsor_full_name,
                bill_sponsor_is_by_request, bill_sponsor_party, bill_sponsor_state, bill_sponsor_url, operation, operation_timestamp)
                SELECT id, type, number, bill_sponsor_bioguide_id, bill_sponsor_district,
                bill_sponsor_first_name, bill_sponsor_last_name, bill_sponsor_middle_name, bill_sponsor_full_name,
                bill_sponsor_is_by_request, bill_sponsor_party, bill_sponsor_state, bill_sponsor_url,
                'UPDATE', NOW()
                FROM bill_sponsors
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_sponsors_history.");
    }

    public void insertToBillSubjectsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_subjects_history (type, number, bill_subjects_count, bill_subjects_url, operation, operation_timestamp)
                SELECT type, number, bill_subjects_count, bill_subjects_url, 'UPDATE', NOW()
                FROM bill_subjects
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_subjects_history.");
    }

    public void insertToBillSummariesHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_summaries_history (type, number, bill_summaries_count, bill_summaries_url, operation, operation_timestamp)
                SELECT type, number, bill_summaries_count, bill_summaries_url, 'UPDATE', NOW()
                FROM bill_summaries
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_summaries_history.");
    }

    public void insertToBillTextVersionsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_text_versions_history (type, number, bill_text_versions_count, bill_text_versions_url, operation, operation_timestamp)
                SELECT type, number, bill_text_versions_count, bill_text_versions_url, 'UPDATE', NOW()
                FROM bill_text_versions
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_text_versions_history.");
    }

    public void insertToBillTitlesHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_titles_history (type, number, bill_titles_count, bill_titles_url, operation, operation_timestamp)
                SELECT type, number, bill_titles_count, bill_titles_url, 'UPDATE', NOW()
                FROM bill_titles
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_titles_history.");
    }

    public void insertToBillRelatedBillsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_related_bills_history (type, number, bill_related_bills_count, bill_related_bills_url, operation, operation_timestamp)
                SELECT type, number, bill_related_bills_count, bill_related_bills_url, 'UPDATE', NOW()
                FROM bill_related_bills
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_related_bills_history.");
    }

    public void insertToBillLatestActionsHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_latest_actions_history (type, number, bill_latest_action_date, bill_latest_action_text, operation, operation_timestamp)
                SELECT type, number, bill_latest_action_date, bill_latest_action_text, 'UPDATE', NOW()
                FROM bill_latest_actions
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_latest_actions_history.");
    }

    public void insertToBillPolicyAreaHistory(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_policy_area_history (type, number, bill_policy_area_name, operation, operation_timestamp)
                SELECT type, number, bill_policy_area_name, 'UPDATE', NOW()
                FROM bill_policy_area
                WHERE type = ? AND number = ?
                """;
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            for (Bill bill : bills) {
                pst.setString(1, bill.getType());
                pst.setInt(2, bill.getNumber());
                pst.addBatch();
            }
            pst.executeBatch();
        }
        System.out.println("Inserted " + bills.size() + " records into bill_policy_area_history.");
    }

    public Bill getBillFromDB(String type, int number) throws SQLException {
        String sqlPrimWrapper = """
                SELECT 
                    bi.type,
                    bi.number,
                    bi.lmd,
                    bd.title,
                    bd.origin_chamber,
                    bd.constitutional_authority_statement_text,
                    bd.introduced_date,
                    bd.latest_action_date,
                    bd.latest_action_text,
                    bd.update_date,
                    bd.update_date_including_text,
                    bd.name,
                    bd.overview,
                    bd.status,
                    bd.url,
                    ba.bill_actions_count,
                    ba.bill_actions_url,
                    bam.bill_amendments_count,
                    bam.bill_amendments_url,
                    bc.bill_committees_count,
                    bc.bill_committees_url,
                    bco.bill_cosponsors_count,
                    bco.bill_cosponsors_count_including_withdrawn,
                    bco.bill_cosponsors_url,
                    bla.bill_latest_action_date,
                    bla.bill_latest_action_text,
                    brb.bill_related_bills_count,
                    brb.bill_related_bills_url,
                    bs.bill_subjects_count,
                    bs.bill_subjects_url,
                    bsm.bill_summaries_count,
                    bsm.bill_summaries_url,
                    btv.bill_text_versions_count,
                    btv.bill_text_versions_url,
                    bt.bill_titles_count,
                    bt.bill_titles_url,
                    bpa.bill_policy_area_name
                FROM bills_info AS bi
                LEFT JOIN bills_detail AS bd ON bi.type = bd.type AND bi.number = bd.number
                LEFT JOIN bill_actions AS ba ON bi.type = ba.type AND bi.number = ba.number
                LEFT JOIN bill_amendments AS bam ON bi.type = bam.type AND bi.number = bam.number
                LEFT JOIN bill_committees AS bc ON bi.type = bc.type AND bi.number = bc.number
                LEFT JOIN bill_cosponsors AS bco ON bi.type = bco.type AND bi.number = bco.number
                LEFT JOIN bill_latest_actions AS bla ON bi.type = bla.type AND bi.number = bla.number
                LEFT JOIN bill_related_bills AS brb ON bi.type = brb.type AND bi.number = brb.number
                LEFT JOIN bill_subjects AS bs ON bi.type = bs.type AND bi.number = bs.number
                LEFT JOIN bill_summaries AS bsm ON bi.type = bsm.type AND bi.number = bsm.number
                LEFT JOIN bill_text_versions AS btv ON bi.type = btv.type AND bi.number = btv.number
                LEFT JOIN bill_titles AS bt ON bi.type = bt.type AND bi.number = bt.number
                LEFT JOIN bill_policy_area AS bpa ON bi.type = bpa.type AND bi.number = bpa.number
                WHERE bi.type = ? AND bi.number = ?;
                """;
        String sqlCboCostEstimate = """
                SELECT * FROM bill_cbo_cost_estimates WHERE type = ? AND number = ?;
                """;
        String sqlCommitteeReports = """
                SELECT * FROM bill_committee_reports WHERE type = ? AND number = ?;
                """;
        String sqlLaws = """
                SELECT * FROM bill_laws WHERE type = ? AND number = ?;
                """;
        String sqlSponsors = """
                SELECT * FROM bill_sponsors WHERE type = ? AND number = ?;
                """;
        try (
            PreparedStatement primAndWrapperPst = this.conn.prepareStatement(sqlPrimWrapper);
            PreparedStatement cboCostEstimatePst = this.conn.prepareStatement(sqlCboCostEstimate);
            PreparedStatement committeeReportsPst = this.conn.prepareStatement(sqlCommitteeReports);
            PreparedStatement lawsPst = this.conn.prepareStatement(sqlLaws);
            PreparedStatement sponsorsPst = this.conn.prepareStatement(sqlSponsors)
        ) {
            fetchingPstHelper(primAndWrapperPst, type, number);
            fetchingPstHelper(cboCostEstimatePst, type, number);
            fetchingPstHelper(committeeReportsPst, type, number);
            fetchingPstHelper(lawsPst, type, number);
            fetchingPstHelper(sponsorsPst, type, number);

            try (
                ResultSet primAndWrapperRs = primAndWrapperPst.executeQuery();
                ResultSet cboCostEstimateRs = cboCostEstimatePst.executeQuery();
                ResultSet committeeReportsRs = committeeReportsPst.executeQuery();
                ResultSet lawsRs = lawsPst.executeQuery();
                ResultSet sponsorsRs = sponsorsPst.executeQuery()
            ) {
                if (primAndWrapperRs.next()) {
                    return new Bill(primAndWrapperRs, cboCostEstimateRs, committeeReportsRs, lawsRs, sponsorsRs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fetchingPstHelper (PreparedStatement pst, String type, int number) throws SQLException {
        pst.setString(1, type.replace("/", "").toLowerCase());
        pst.setInt(2, number);
    }

    public Connection getConn() {
        return this.conn;
    }

    public void connClose() {
        try {
            this.conn.close();
            System.out.println("Connection to " + this.DBName + " is now closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
