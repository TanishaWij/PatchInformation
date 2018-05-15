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
package org.wso2.OpenPatchInformation.PmtData;

import org.apache.log4j.Logger;
import org.wso2.OpenPatchInformation.Exceptions.PmtExceptions.AccessingPmtException;
import org.wso2.OpenPatchInformation.Exceptions.PmtExceptions.ExtractingFromResultsetException;
import org.wso2.OpenPatchInformation.Exceptions.PmtExceptions.PmtException;
import org.wso2.OpenPatchInformation.JiraData.JiraIssue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.wso2.OpenPatchInformation.ConfiguredProperties.getValueOf;
import static org.wso2.OpenPatchInformation.Constants.Constants.QUERY_PER_PATCH;
import static org.wso2.OpenPatchInformation.Constants.Constants.SELECT_SUPPORT_JIRAS;
import static org.wso2.OpenPatchInformation.Constants.Constants.SUPPORT_JIRA_URL;

public class AccessPMT {
    private final Logger logger = Logger.getLogger(AccessPMT.class);

    public ArrayList<JiraIssue> getJiraIssuesInBothPMTAndJira(ArrayList<JiraIssue> jiraIssues) throws PmtException {
        logger.info("Getting Jira issues recorded in both the PmtDb and Jira");
        String url = getValueOf("pmtConnection");
        String user = getValueOf("dbUser");
        String password = getValueOf("dbPassword");
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(SELECT_SUPPORT_JIRAS);
             ResultSet result = pst.executeQuery()) {
            ArrayList<String> allJirasInPmt = new ArrayList<>();
            try {
                while (result.next()) {
                    String jiraName = (result.getString(SUPPORT_JIRA_URL)).substring(37);
                    allJirasInPmt.add(jiraName);
                }
            } catch (SQLException e) {
                logger.error("Failed to extract data from returned ResultSet", e);
                throw new ExtractingFromResultsetException("Error extracting data from returned ResultSet", e);
            }
            //get Jira issues recorded in both the PMT and Jira
            ArrayList<JiraIssue> jiraIssuesInPmtAndJira = new ArrayList<>();
            for (JiraIssue jiraIssue : jiraIssues) {
                if (allJirasInPmt.contains(jiraIssue.getName())) {
                    jiraIssuesInPmtAndJira.add(jiraIssue);
                }
            }
            logger.info("Jira issues recorded in both the PmtDb and Jira extracted successfully");
            return jiraIssuesInPmtAndJira;
        } catch (SQLException e) {
            logger.error("Failed to connect to the Pmtdb", e);
            throw new AccessingPmtException("Failed to connect to the Pmtdb", e);
        }
    }


    public ArrayList<Patch> getPatchesAssociatedWith(ArrayList<JiraIssue> jiraIssuesInPmtAndJira) throws PmtException {
        logger.info("Getting Patch information associated with the Jira issues");
        ArrayList<Patch> allPatches = new ArrayList<>();
        for (JiraIssue jiraIssue : jiraIssuesInPmtAndJira) {
            String query = QUERY_PER_PATCH + jiraIssue.getName() + "';";
            String url = getValueOf("pmtConnection");
            String user = getValueOf("dbUser");
            String password = getValueOf("dbPassword");
            try (Connection con = DriverManager.getConnection(url, user, password);
                 PreparedStatement pst = con.prepareStatement(query);
                 ResultSet result = pst.executeQuery()) {
                try {
                    allPatches.addAll(PatchesCreator.getPatchesIn(result, jiraIssue));
                } catch (SQLException e) {
                    logger.error("Failed to extract data from returned ResultSet for: " + jiraIssue.getName(), e);
                    throw new ExtractingFromResultsetException("Failed to extract data from returned ResultSet for: " + jiraIssue.getName(), e);
                }
            } catch (SQLException e) {
                logger.error("Failed to connect to the Pmtdb", e);
                throw new AccessingPmtException("Failed to connect to the Pmtdb", e);
            }
        }
        logger.info("Patch information associated with the Jira issues extracted successfully.");
        return allPatches;

    }
}