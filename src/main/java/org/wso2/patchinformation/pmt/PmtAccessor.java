//
// Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//
package org.wso2.patchinformation.pmt;

import org.apache.log4j.Logger;
import org.wso2.patchinformation.exceptions.pmt.PmtConnectionException;
import org.wso2.patchinformation.exceptions.pmt.PmtContentException;
import org.wso2.patchinformation.exceptions.pmt.PmtException;
import org.wso2.patchinformation.jira.JIRAIssue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.wso2.patchinformation.constants.Constants.JIRA_URL_PREFIX_LENGTH;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_DEVELOPMENT;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_FAILED_QA;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_ONHOLD;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_PREQA;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_READY_FOR_QA;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_RELEASED;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_RELEASED_NOT_AUTOMATED;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_RELEASED_NOT_IN_PUBLIC_SVN;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_STAGING;
import static org.wso2.patchinformation.constants.Constants.LC_STATE_TESTING;
import static org.wso2.patchinformation.constants.Constants.NOT_SET;
import static org.wso2.patchinformation.constants.Constants.NOT_SPECIFIED;
import static org.wso2.patchinformation.constants.Constants.OFF_QUEUE;
import static org.wso2.patchinformation.constants.Constants.QUERY_PER_PATCH;
import static org.wso2.patchinformation.constants.Constants.SELECT_SUPPORT_JIRAS;
import static org.wso2.patchinformation.constants.Constants.STILL_IN_QUEUE;
import static org.wso2.patchinformation.constants.Constants.SUPPORT_JIRA_URL_FIELD;
import static org.wso2.patchinformation.constants.Constants.State;

/**
 * Accesses the pmtdb and queries it to get the JIRA issues that have a corresponding entry in the pmt and then gets the
 * patch information for each of the JIRA issues.
 */
public class PmtAccessor {

    private static final Logger LOGGER = Logger.getLogger(PmtAccessor.class);
    private static PmtAccessor pmtAccessor;

    private PmtAccessor() {

    }

    public static PmtAccessor getPmtAccessor() {

        if (pmtAccessor == null) {
            pmtAccessor = new PmtAccessor();
        }
        return pmtAccessor;
    }

    private static String getDate(String dateAndTime) {

        if (dateAndTime == null || !(dateAndTime.contains(" "))) {
            return NOT_SPECIFIED;
        } else {
            String[] dateSplit = dateAndTime.split(" ");
            return dateSplit[0];
        }
    }

    /**
     * Returns the oldest of two date
     *
     * @param currentDateStr the date val currently recorded
     * @param newDateStr     new date
     * @return the oldest date
     */
    private static String dateCompare(String currentDateStr, String newDateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date currentDate = sdf.parse(currentDateStr);
            Date newDate = sdf.parse(newDateStr);
            if (currentDate.before(newDate)) {
                return currentDateStr;
            } else {
                return newDateStr;
            }
        } catch (ParseException e) {
            return currentDateStr;
        }
    }

    public ArrayList<JIRAIssue> filterJIRAIssues(ArrayList<JIRAIssue> jiraIssues, String url, String user,
                                                 String password) throws PmtException {

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(SELECT_SUPPORT_JIRAS);
             ResultSet result = pst.executeQuery()) {
            ArrayList<String> allJIRANamesInPmt = new ArrayList<>();
            try {
                while (result.next()) {
                    String jiraUrl = result.getString(SUPPORT_JIRA_URL_FIELD);
                    String jiraName = "";
                    if (jiraUrl.length() >= JIRA_URL_PREFIX_LENGTH) {
                        jiraName = jiraUrl.substring(JIRA_URL_PREFIX_LENGTH);
                    }
                    allJIRANamesInPmt.add(jiraName);
                }
            } catch (SQLException e) {
                String errorMessage = "Failed to extract data from returned pmt ResultSet";
                LOGGER.error(errorMessage, e);
                throw new PmtContentException(errorMessage, e);
            }
            return getJIRAIssuesInPmtAndJIRA(jiraIssues, allJIRANamesInPmt);
        } catch (SQLException e) {
            String errorMessage = "Failed to connect to the Pmt db";
            LOGGER.error(errorMessage, e);
            throw new PmtConnectionException(errorMessage, e);
        }
    }

    private ArrayList<JIRAIssue> getJIRAIssuesInPmtAndJIRA(ArrayList<JIRAIssue> jiraIssues,
                                                           ArrayList<String> allJIRANamesInPmt) {

        ArrayList<JIRAIssue> jiraIssuesInPmtAndJira = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            if (allJIRANamesInPmt.contains(jiraIssue.getName())) {
                jiraIssuesInPmtAndJira.add(jiraIssue);
            }
        }
        return jiraIssuesInPmtAndJira;
    }

    public void populatePatches(ArrayList<JIRAIssue> jiraIssuesInPmtAndJira, String url, String user, String password)
            throws PmtException {

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            for (JIRAIssue jiraIssue : jiraIssuesInPmtAndJira) {
                String query = QUERY_PER_PATCH + jiraIssue.getName() + "';";
                try (PreparedStatement pst = con.prepareStatement(query); ResultSet result = pst.executeQuery()) {
                    populatePatchesFromResultSet(result, jiraIssue);
                } catch (SQLException e) {
                    String errorMessage = "Failed to extract data from returned ResultSet for: " + jiraIssue.getName();
                    LOGGER.error(errorMessage, e);
                    throw new PmtContentException(errorMessage, e);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to connect to the Pmt db";
            LOGGER.error(errorMessage, e);
            throw new PmtConnectionException(errorMessage, e);
        }
    }

    private void populatePatchesFromResultSet(ResultSet result, JIRAIssue jiraIssue) throws SQLException {

        String oldestPatchReportDate = NOT_SET;
        String curReportDate;
        //iterate through SQl response
        while (result.next()) {
            curReportDate = result.getString("REPORT_DATE");
            if (NOT_SET.equals(oldestPatchReportDate)) {
                oldestPatchReportDate = curReportDate;
            } else {
                oldestPatchReportDate = dateCompare(oldestPatchReportDate, curReportDate);
            }
            jiraIssue.setPatchReportDate(oldestPatchReportDate);
            String jiraLink = result.getString("SUPPORT_JIRA");
            jiraIssue.setJiralink(jiraLink);
            String active = result.getString("ACTIVE");
            String productName = result.getString("PRODUCT_NAME");
            String daysSincePatchWasReported = result.getString("DAYS_SINCE_REPORT");
            String assignee = jiraIssue.getAssigneeName();

            if (OFF_QUEUE.equals(active)) {
                String lcState = result.getString("LC_STATE");
                String patchName = result.getString("PATCH_NAME");
                String signRequestSentOn = result.getString("SIGN_REQUEST_SENT_ON");
                //check if patch is in signing
                if (!LC_STATE_ONHOLD.equals(lcState)) {
                    if (((LC_STATE_STAGING.equals(lcState)) && (signRequestSentOn != null) ||
                            (LC_STATE_TESTING.equals(lcState)) && (signRequestSentOn != null))) {
                        String daysInSigning = result.getString("DAYS_IN_SIGNING");
                        jiraIssue.addPatchToJIRA(new Patch(jiraLink, patchName, productName, assignee, State.IN_SIGNING,
                                "InSigning", daysInSigning));
                        //check if patch is in development
                    } else if (LC_STATE_STAGING.equals(lcState) || LC_STATE_DEVELOPMENT.equals(lcState) ||
                            LC_STATE_ONHOLD.equals(lcState) || LC_STATE_TESTING.equals(lcState) ||
                            LC_STATE_PREQA.equals(lcState) || LC_STATE_FAILED_QA.equals(lcState) ||
                            LC_STATE_READY_FOR_QA.equals(lcState)) {

                        jiraIssue.addPatchToJIRA(new DevPatch(jiraLink, patchName, productName, assignee,
                                State.IN_DEV, lcState, curReportDate, daysSincePatchWasReported));
                        //if patch has been released
                    } else if (LC_STATE_RELEASED.equals(lcState)) {
                        jiraIssue.addPatchToJIRA(new Patch(jiraLink, patchName, productName, assignee, State.REALEASED,
                                lcState, getDate(result.getString("RELEASED_ON"))));
                    } else if (LC_STATE_RELEASED_NOT_AUTOMATED.equals(lcState)) {
                        jiraIssue.addPatchToJIRA(new Patch(jiraLink, patchName, productName, assignee, State.REALEASED,
                                lcState, getDate(result.getString("RELEASED_NOT_AUTOMATED_ON"))));
                    } else if (LC_STATE_RELEASED_NOT_IN_PUBLIC_SVN.equals(lcState)) {
                        jiraIssue.addPatchToJIRA(new Patch(jiraLink, patchName, productName, assignee, State.REALEASED,
                                lcState, getDate(result.getString("RELEASED_NOT_IN_PUBLIC_SVN_ON"))));
                    }
                }
            } else if (STILL_IN_QUEUE.equals(active)) {
                jiraIssue.addPatchToJIRA(new Patch(jiraLink, "Patch name not created", productName,
                        assignee, State.IN_PATCH_QUEUE, "InQueue", daysSincePatchWasReported));
            }
        }
    }
}

