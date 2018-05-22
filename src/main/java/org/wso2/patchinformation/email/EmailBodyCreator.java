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
package org.wso2.patchinformation.email;

import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.jira.JIRAIssue;
import org.wso2.patchinformation.pmt.Patch;
import org.wso2.patchinformation.pmt.comparators.PatchChainedComparator;
import org.wso2.patchinformation.pmt.comparators.ProductNameComparator;
import org.wso2.patchinformation.pmt.comparators.StateNameComparator;

import java.util.ArrayList;

import static org.wso2.patchinformation.constants.EmailConstants.DEV_STATE_TABLE_COLUMNS_START;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_FOOTER;
import static org.wso2.patchinformation.constants.EmailConstants.IN_DEV_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.IN_QUEUE_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.IN_SIGNING_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.RELEASED_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.STATE_TABLE_COLUMNS_END;
import static org.wso2.patchinformation.constants.EmailConstants.STATE_TABLE_COLUMNS_START;
import static org.wso2.patchinformation.constants.EmailConstants.SUMMARY_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.TABLE_HEADER_SUMMARY;

/**
 * Creates and returns the body of the email. The body is broken up into a summary table, and a table for each of
 * the four States.
 */
public class EmailBodyCreator {

    private static EmailBodyCreator emailBodyCreator;

    private EmailBodyCreator() {
    }

    public static EmailBodyCreator getEmailBodyCreator() {
        if (emailBodyCreator == null) {
            emailBodyCreator = new EmailBodyCreator();
        }
        return emailBodyCreator;
    }

    /**
     * Returns the body of the email.
     *
     * @param jiraIssues  all JIRA issues containing details to be recorded in the email.
     * @param emailHeader email header dependent on if its the internal or customer related email.
     * @return the email body.
     */
    public String getEmailBody(ArrayList<JIRAIssue> jiraIssues, String emailHeader) {
        ArrayList<Patch> patchesInQueue = new ArrayList<>();
        ArrayList<Patch> patchesInDev = new ArrayList<>();
        ArrayList<Patch> patchesInSigning = new ArrayList<>();
        ArrayList<Patch> patchesReleased = new ArrayList<>();
        String emailBody = emailHeader;
        emailBody += getSummeryTable(jiraIssues);
        assignPatchesToStates(getAllPatches(jiraIssues), patchesInQueue, patchesInSigning, patchesInDev,
                patchesReleased);
        emailBody += getStateTable(IN_QUEUE_SECTION_HEADER, "WORK DAYS IN QUEUE", patchesInQueue);
        emailBody += getStateTable(IN_DEV_SECTION_HEADER, "WORK DAYS IN DEV",
                patchesInDev);
        emailBody += getStateTable(IN_SIGNING_SECTION_HEADER, "WORK DAYS IN SIGNING",
                patchesInSigning);
        emailBody += getStateTable(RELEASED_SECTION_HEADER, "DATE RELEASED", patchesReleased);
        emailBody += EMAIL_FOOTER;
        return emailBody;
    }

    /**
     * Add the patches to an arraylist dependent on which state it is in.
     *
     * @param patches arraylist of all patches to be recorded.
     */
    private void assignPatchesToStates(ArrayList<Patch> patches, ArrayList<Patch> patchesInQueue,
                                       ArrayList<Patch> patchesInSigning, ArrayList<Patch> patchesInDevelopment,
                                       ArrayList<Patch> patchesReleased) {
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
     * returns an arraylist of all patches
     *
     * @param jiraIssues arraylist of JIRA issues
     * @return all patches
     */
    private ArrayList<Patch> getAllPatches(ArrayList<JIRAIssue> jiraIssues) {
        ArrayList<Patch> patches = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            patches.addAll(jiraIssue.getPatchesInJIRA());
        }
        return patches;
    }

    /**
     * Builds the html string corresponding to the Summary table which holds information on all unique JIRA issues.
     *
     * @param jiraIssues arraylist of all JIRA issues
     * @return the html code for the table
     */
    private String getSummeryTable(ArrayList<JIRAIssue> jiraIssues) {
        String summaryTable = SUMMARY_SECTION_HEADER;
        summaryTable += TABLE_HEADER_SUMMARY;
        ArrayList<HtmlTableRow> jirasToHtml = new ArrayList<>(jiraIssues);
        summaryTable += getTableRows(jirasToHtml);
        summaryTable += "</table>";
        return summaryTable;
    }

    /**
     * Builds the html string corresponding to each of the 4 states table and the corresponding JIRA issues.
     *
     * @param header         email header
     * @param dateColumnName table attribute name for "date" coloumn on table
     * @param patches        arraylist of patches
     * @return html code showing the state data in a table
     */
    private String getStateTable(String header, String dateColumnName, ArrayList<Patch> patches) {
        String table = header;
        if (IN_DEV_SECTION_HEADER.equals(header)) {
            table += DEV_STATE_TABLE_COLUMNS_START;
        } else {
            table += STATE_TABLE_COLUMNS_START;
        }
        table += dateColumnName + STATE_TABLE_COLUMNS_END;
        patches.sort(new PatchChainedComparator(new ProductNameComparator(), new StateNameComparator()));
        ArrayList<HtmlTableRow> patchesToHtml = new ArrayList<>(patches);
        table += getTableRows(patchesToHtml);
        table += "</table>";
        return table;
    }

    /**
     * Builds the table body where each row corresponds to a unique JIRA
     *
     * @param rows Arraylist of all issues or patches
     * @return html code for row vaues of table
     */
    private String getTableRows(ArrayList<HtmlTableRow> rows) {
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





