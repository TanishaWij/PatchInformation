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
package org.wso2.dailyPatchInformation.pmtData;

import org.apache.log4j.Logger;
import org.wso2.dailyPatchInformation.exceptions.PmtExceptions.AccessingPmtException;
import org.wso2.dailyPatchInformation.exceptions.PmtExceptions.ExtractingFromResultsetException;
import org.wso2.dailyPatchInformation.exceptions.PmtExceptions.PmtException;
import org.wso2.dailyPatchInformation.JIRAData.JIRAIssue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.wso2.dailyPatchInformation.constants.Constants.JIRA_URL_PREFIX_LENGTH;
import static org.wso2.dailyPatchInformation.constants.Constants.QUERY_PER_PATCH;
import static org.wso2.dailyPatchInformation.constants.Constants.SELECT_SUPPORT_JIRAS;
import static org.wso2.dailyPatchInformation.constants.Constants.SUPPORT_JIRA_URL_FIELD;

/**
 * Accesses the pmtdb and queries it to get the JIRA issues that have a corresponding entry in the pmt and then gets the
 * patch information for each of the JIRA issues
 */
public class PmtAccessor {
//todo :
    private static final Logger logger = Logger.getLogger(PmtAccessor.class);

    public static ArrayList<JIRAIssue> filterJIRAIssues(ArrayList<JIRAIssue> JIRAIssues, String url, String user, String password) throws PmtException {

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(SELECT_SUPPORT_JIRAS);
             ResultSet result = pst.executeQuery()) {
            ArrayList<String> allJIRANamesInPmt = new ArrayList<>();
            try {
                while (result.next()) {
                    String JIRAUrl = result.getString(SUPPORT_JIRA_URL_FIELD);
                    String JIRAName = "";
                    if (JIRAUrl.length() >= JIRA_URL_PREFIX_LENGTH) {
                        JIRAName = JIRAUrl.substring(JIRA_URL_PREFIX_LENGTH);
                    }
                    allJIRANamesInPmt.add(JIRAName);
                }
            } catch (SQLException e) {
                String errorMessage = "Failed to extract data from returned ResultSet";
                logger.error(errorMessage, e);
                throw new ExtractingFromResultsetException(errorMessage, e);
            }
            //todo: method
            //get JIRA issues recorded in both the PMT and JIRA
            ArrayList<JIRAIssue> JIRAIssuesInPmtAndJIRA = new ArrayList<>();
            for (JIRAIssue JIRAIssue : JIRAIssues) {
                if (allJIRANamesInPmt.contains(JIRAIssue.getName())) {
                    JIRAIssuesInPmtAndJIRA.add(JIRAIssue);
                }
            }
            return JIRAIssuesInPmtAndJIRA;
        } catch (SQLException e) {
            String errorMessage = "Failed to connect to the Pmt db";
            logger.error(errorMessage, e);
            throw new AccessingPmtException(errorMessage, e);
        }
    }

    public static ArrayList<Patch> getPatchInformation(ArrayList<JIRAIssue> JIRAIssuesInPmtAndJIRA,String url, String user, String password) throws PmtException {

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            ArrayList<Patch> allPatches = new ArrayList<>();
            for (JIRAIssue JIRAIssue : JIRAIssuesInPmtAndJIRA) {
                String query = QUERY_PER_PATCH + JIRAIssue.getName() + "';";
                try (PreparedStatement pst = con.prepareStatement(query); ResultSet result = pst.executeQuery()) {
                    //TODO: dont return anything
                    allPatches.addAll(PatchesCreato.populatePatches(result, JIRAIssue));
                } catch (SQLException e) {
                    String errorMessage = "Failed to extract data from returned ResultSet for: " + JIRAIssue.getName();
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