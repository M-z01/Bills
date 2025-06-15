package com.extract.bills.db;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import com.extract.bills.bill.*;

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
    }

    public void insertToActions(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_actions (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToAmendments(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_amendments (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToCboCostEstimates(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_cbo_cost_estimates (type, number, description, pub_date, title, url) VALUES (?, ?, ?, ?, ?, ?)
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

    public void insertToCommitteeReports(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_committee_reports (type, number, citation, url) VALUES (?, ?, ?, ?)
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

    public void insertToCommittees(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_committees (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToCosponsors(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_cosponsors (type, number, count, count_including_withdrawn, url) VALUES (?, ?, ?, ?, ?)
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

    public void insertToLaws(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_laws (type, number, number_law, type_law) VALUES (?, ?, ?, ?)
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

    public void insertToSponsors(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_sponsors (type, number, bioguide_id, district, first_name, last_name, middle_name, full_name,
                is_by_request, party, state, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

    public void insertToSubjects(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_subjects (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToSummaries(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_summaries (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToTextVersions(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_text_versions (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToTitles(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_titles (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToRelatedBills(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_related_bills (type, number, count, url) VALUES (?, ?, ?, ?)
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

    public void insertToLatestActions(List<Bill> bills) throws SQLException {
        String sql = """
                INSERT INTO bill_latest_actions (type, number, action_date, text) VALUES (?, ?, ?, ?)
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

    public int update(String updateString) throws SQLException {
        PreparedStatement pst = this.conn.prepareStatement(updateString);
        int rowAffected = pst.executeUpdate();
        pst.close();
        return rowAffected;
    }

    //wip
    public void updateToDetail(List<Bill> bills) throws SQLException {
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
    }
    System.out.println("Updated " + bills.size() + " bills in bills_detail.");
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
