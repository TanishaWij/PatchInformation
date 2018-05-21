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
import org.wso2.PatchInformation.JiraData.JiraIssue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_DEVELOPMENT;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_FAILED_QA;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_ONHOLD;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_PREQA;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_READY_FOR_QA;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_RELEASED;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_RELEASED_NOT_AUTOMATED;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_RELEASED_NOT_IN_PUBLIC_SVN;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_STAGING;
import static org.wso2.PatchInformation.Constants.Constants.LC_STATE_TESTING;
import static org.wso2.PatchInformation.Constants.Constants.NOT_SET;
import static org.wso2.PatchInformation.Constants.Constants.NOT_SPECIFIED;
import static org.wso2.PatchInformation.Constants.Constants.STILL_IN_QUEUE;
import static org.wso2.PatchInformation.Constants.Constants.State;
import static org.wso2.PatchInformation.Constants.Constants.TAKEN_OFF_QUEUE;

class PatchesCreator {

    /**
     * Creates and returns the patches associated with a particular Jira issue
     * @param result from querying the pmtdb
     * @param jiraIssue associated with the pmt query
     * @return An arrylist of patches
     * @throws SQLException
     */
    static ArrayList<Patch> getPatchesIn(ResultSet result, JiraIssue jiraIssue) throws SQLException {

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
            jiraIssue.setJiraLink(jiraLink);
            String active = result.getString("ACTIVE");
            String productName = result.getString("PRODUCT_NAME");
            String daysSincePatchWasReported = result.getString("DAYS_SINCE_REPORT");
            String assignee = jiraIssue.getAssigneeName();

            if (TAKEN_OFF_QUEUE.equals(active)) {
                String lcState = result.getString("LC_STATE");
                String patchName = result.getString("PATCH_NAME");
                String signRequestSentOn = result.getString("SIGN_REQUEST_SENT_ON");
                //check if patch is in signing
                if (LC_STATE_ONHOLD.equals(lcState) ){
                    //ignore and do nothing

                } else if (((LC_STATE_STAGING.equals(lcState)) && (signRequestSentOn != null) ||
                        (LC_STATE_TESTING.equals(lcState)) && (signRequestSentOn != null))) {
                    String daysInSigning = result.getString("DAYS_IN_SIGNING");
                    jiraIssue.addPatchToJira(new Patch(jiraLink, patchName, productName, assignee, State.IN_SIGNING,
                            "InSigning", daysInSigning));

                    //check if patch is in development
                } else if (LC_STATE_STAGING.equals(lcState) || LC_STATE_DEVELOPMENT.equals(lcState) ||
                        LC_STATE_ONHOLD.equals(lcState) || LC_STATE_TESTING.equals(lcState) ||
                        LC_STATE_PREQA.equals(lcState) || LC_STATE_FAILED_QA.equals(lcState) ||
                        LC_STATE_READY_FOR_QA.equals(lcState)) {

                    jiraIssue.addPatchToJira(new DevPatch(jiraLink, patchName, productName, assignee, State.IN_DEV, lcState,
                            curReportDate,
                            daysSincePatchWasReported));

                    //if patch has been released
                } else if (LC_STATE_RELEASED.equals(lcState)) {
                    jiraIssue.addPatchToJira(new Patch(jiraLink, patchName, productName, assignee, State.REALEASED, lcState,
                            getDate(result.getString("RELEASED_ON"))));
                } else if (LC_STATE_RELEASED_NOT_AUTOMATED.equals(lcState)) {
                    jiraIssue.addPatchToJira(new Patch(jiraLink, patchName, productName, assignee, State.REALEASED, lcState,
                            getDate(result.getString("RELEASED_NOT_AUTOMATED_ON"))));
                } else if (LC_STATE_RELEASED_NOT_IN_PUBLIC_SVN.equals(lcState)) {
                    jiraIssue.addPatchToJira(new Patch(jiraLink, patchName, productName, assignee, State.REALEASED, lcState,
                            getDate(result.getString("RELEASED_NOT_IN_PUBLIC_SVN_ON"))));
                }

            } else if (STILL_IN_QUEUE.equals(active)) {
                jiraIssue.addPatchToJira(new Patch(jiraLink, "Patch not created yet", productName,
                        assignee, State.IN_PATCH_QUEUE, "InQueue", daysSincePatchWasReported));
            }
        }
        return jiraIssue.getPatchesInJira();
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
}
