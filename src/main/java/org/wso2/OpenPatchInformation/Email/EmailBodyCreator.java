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
package org.wso2.OpenPatchInformation.Email;

import org.wso2.OpenPatchInformation.Constants.Constants;
import org.wso2.OpenPatchInformation.JiraData.JiraIssue;
import org.wso2.OpenPatchInformation.PmtData.Comparators.PatchChainedComparator;
import org.wso2.OpenPatchInformation.PmtData.Comparators.ProductNameComparator;
import org.wso2.OpenPatchInformation.PmtData.Comparators.StateNameComparator;
import org.wso2.OpenPatchInformation.PmtData.Patch;

import java.util.ArrayList;

import static org.wso2.OpenPatchInformation.Constants.MailConstants.DEV_STATE_TABLE_COLUMNS_START;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.EMAIL_FOOTER;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.IN_DEVELOPMENT_SECTION_HEADER;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.IN_QUEUE_SECTION_HEADER;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.IN_SIGNING_SECTION_HEADER;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.RELEASED_SECTION_HEADER;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.STATE_TABLE_COLUMNS_END;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.STATE_TABLE_COLUMNS_START;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.SUMMARY_SECTION_HEADER;
import static org.wso2.OpenPatchInformation.Constants.MailConstants.TABLE_HEADER_SUMMARY;

/**
 * Creates and returns the body of the email. The body is broken up into a summary table, and a table for each of
 * the four States.
 */
public class EmailBodyCreator {

    private static ArrayList<Patch> patchesInQueue = new ArrayList<>();
    private static ArrayList<Patch> patchesInDevelopment = new ArrayList<>();
    private static ArrayList<Patch> patchesInSigning = new ArrayList<>();
    private static ArrayList<Patch> patchesReleased = new ArrayList<>();

    /**
     * Returns the body of the email.
     *
     * @param patches     all patches to be assigned to states and their details to be recorded in the email.
     * @param jiraIssues  all jira issues containing details to be recorded in the email.
     * @param emailHeader email header dependent on if its the internal or customer related email.
     * @return the email body.
     */
    public static String getEmailBody(ArrayList<Patch> patches, ArrayList<JiraIssue> jiraIssues, String emailHeader) {

        String emailBody = emailHeader;
        emailBody += getSummeryHtmlTable(jiraIssues);
        assignPatchesToStates(patches);
        //get patch queue table
        emailBody += getStateHtmlTable(IN_QUEUE_SECTION_HEADER, "WORK DAYS IN QUEUE", patchesInQueue);
        emailBody += getStateHtmlTable(IN_DEVELOPMENT_SECTION_HEADER, "WORK DAYS IN DEV", patchesInDevelopment);
        emailBody += getStateHtmlTable(IN_SIGNING_SECTION_HEADER, "WORK DAYS IN SIGNING", patchesInSigning);
        emailBody += getStateHtmlTable(RELEASED_SECTION_HEADER, "DATE RELEASED", patchesReleased);
        emailBody += EMAIL_FOOTER;
        return emailBody;
    }

    /**
     * Add the patches to an arraylist dependent on which state it is in.
     *
     * @param patches arraylist of all patches to be recorded.
     */
    private static void assignPatchesToStates(ArrayList<Patch> patches) {

        for (Patch patch : patches) {
            switch (patch.getState()) {
                case IN_DEV:
                    patchesInDevelopment.add(patch);
                    break;

                case IN_PATCH_QUEUE:
                    patchesInQueue.add(patch);
                    break;

                case IN_SIGNING:
                    patchesInSigning.add(patch);
                    break;

                case REALEASED:
                    patchesReleased.add(patch);
                    break;
            }
        }
    }

    /**
     * Builds the html string corresponding to the Summary table which holds information on all unique Jira issues.
     *
     * @param jiraIssues arraylist of all Jira issues
     * @return the html code for the table
     */
    private static String getSummeryHtmlTable(ArrayList<JiraIssue> jiraIssues) {

        String summaryTable = SUMMARY_SECTION_HEADER;
        summaryTable += TABLE_HEADER_SUMMARY;
        ArrayList<HtmlTableRow> jirasToHtml = new ArrayList<>(jiraIssues);
        summaryTable += getTableRows(jirasToHtml);
        summaryTable += "</table>";
        return summaryTable;
    }

    private static String getStateHtmlTable(String stateHeader, String dateColumnName, ArrayList<Patch> patchesInState) {

        String table = stateHeader;
        if (IN_DEVELOPMENT_SECTION_HEADER.equals(stateHeader)) {
            table += DEV_STATE_TABLE_COLUMNS_START;
        } else {
            table += STATE_TABLE_COLUMNS_START;
        }
        table += dateColumnName + STATE_TABLE_COLUMNS_END;
        patchesInState.sort(new PatchChainedComparator(new ProductNameComparator(), new StateNameComparator()));
        ArrayList<HtmlTableRow> patchesToHtml = new ArrayList<>(patchesInState);
        table += getTableRows(patchesToHtml);
        table += "</table>";
        return table;
    }

    /**
     * Builds the table body where each row corresponds to a unique Jira
     *
     * @param rows Arraylist of all issues or patches
     * @return html code for row vaues of table
     */
    private static String getTableRows(ArrayList<HtmlTableRow> rows) {

        StringBuilder htmlRows = new StringBuilder();
        boolean toggleFlag = true;
        String backgroundColor;
        //set background colour
        for (HtmlTableRow row : rows) {
            if (toggleFlag) {
                backgroundColor = Constants.BACKGROUND_COLOR_WHITE;
                toggleFlag = false;
            } else {
                backgroundColor = Constants.BACKGROUND_COLOR_GRAY;
                toggleFlag = true;
            }
            htmlRows.append(row.objectToHtml(backgroundColor));
        }
        return htmlRows.toString();
    }

}





