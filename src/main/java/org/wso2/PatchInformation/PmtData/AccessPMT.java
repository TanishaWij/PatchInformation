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
package org.wso2.PatchInformation.PmtData;

import org.apache.log4j.Logger;
import org.wso2.PatchInformation.Exceptions.PmtExceptions.AccessingPmtException;
import org.wso2.PatchInformation.Exceptions.PmtExceptions.ExtractingFromResultsetException;
import org.wso2.PatchInformation.Exceptions.PmtExceptions.PmtException;
import org.wso2.PatchInformation.JiraData.JiraIssue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.wso2.PatchInformation.ConfiguredProperties.getValueOf;
import static org.wso2.PatchInformation.Constants.Constants.JIRA_URL_PREFIX_LENGTH;
import static org.wso2.PatchInformation.Constants.Constants.QUERY_PER_PATCH;
import static org.wso2.PatchInformation.Constants.Constants.SELECT_SUPPORT_JIRAS;
import static org.wso2.PatchInformation.Constants.Constants.SUPPORT_JIRA_URL;

/**
 * Accesses the pmtdb and queries it to get the Jira issues that have a corresponding entry in the pmt and then gets the
 * patch information for each of the Jira issues
 */
public class AccessPMT {

    private static final Logger logger = Logger.getLogger(AccessPMT.class);
    private static String url = getValueOf("pmtConnection");
    private static String user = getValueOf("dbUser");
    private static String password = getValueOf("dbPassword");

    public static ArrayList<JiraIssue> getJiraIssuesInBothPMTAndJira(ArrayList<JiraIssue> jiraIssues) throws PmtException {

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(SELECT_SUPPORT_JIRAS);
             ResultSet result = pst.executeQuery()) {
            ArrayList<String> allJiraNamesInPmt = new ArrayList<>();
            try {
                while (result.next()) {
                    String jiraUrl = (result.getString(SUPPORT_JIRA_URL));
                    String jiraName = "";
                    if (jiraUrl.length() >= JIRA_URL_PREFIX_LENGTH) {
                        jiraName = jiraUrl.substring(JIRA_URL_PREFIX_LENGTH);
                    }
                    allJiraNamesInPmt.add(jiraName);
                }
            } catch (SQLException e) {
                String errorMessage = "Failed to extract data from returned ResultSet";
                logger.error(errorMessage, e);
                throw new ExtractingFromResultsetException(errorMessage, e);
            }
            //get Jira issues recorded in both the PMT and Jira
            ArrayList<JiraIssue> jiraIssuesInPmtAndJira = new ArrayList<>();
            for (JiraIssue jiraIssue : jiraIssues) {
                if (allJiraNamesInPmt.contains(jiraIssue.getName())) {
                    jiraIssuesInPmtAndJira.add(jiraIssue);
                }
            }
            return jiraIssuesInPmtAndJira;
        } catch (SQLException e) {
            String errorMessage = "Failed to connect to the Pmt db";
            logger.error(errorMessage, e);
            throw new AccessingPmtException(errorMessage, e);
        }
    }

    public static ArrayList<Patch> getPatchesAssociatedWith(ArrayList<JiraIssue> jiraIssuesInPmtAndJira) throws PmtException {

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            ArrayList<Patch> allPatches = new ArrayList<>();
            for (JiraIssue jiraIssue : jiraIssuesInPmtAndJira) {
                String query = QUERY_PER_PATCH + jiraIssue.getName() + "';";
                try (PreparedStatement pst = con.prepareStatement(query); ResultSet result = pst.executeQuery()) {
                        allPatches.addAll(PatchesCreator.getPatchesIn(result, jiraIssue));
                    } catch (SQLException e) {
                        String errorMessage = "Failed to extract data from returned ResultSet for: " + jiraIssue.getName();
                        logger.error(errorMessage, e);
                        throw new ExtractingFromResultsetException(errorMessage, e);
                }
            }
            return allPatches;
        } catch (SQLException e) {
            String errorMessage = "Failed to connect to the Pmt db";
            logger.error(errorMessage, e);
            throw new AccessingPmtException(errorMessage, e);
        }
    }
}