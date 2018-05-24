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

import org.wso2.patchinformation.comparators.DateCompatator;
import org.wso2.patchinformation.comparators.PatchChainedComparator;
import org.wso2.patchinformation.comparators.ProductNameComparator;
import org.wso2.patchinformation.comparators.ReleasedReportDateComparator;
import org.wso2.patchinformation.comparators.ReportDateComparator;
import org.wso2.patchinformation.comparators.StateNameComparator;
import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.jira.JIRAIssue;
import org.wso2.patchinformation.pmt.InactivePatch;
import org.wso2.patchinformation.pmt.OpenPatch;
import org.wso2.patchinformation.pmt.Patch;

import java.util.ArrayList;

import static org.wso2.patchinformation.constants.EmailConstants.COLUMN_NAMES;
import static org.wso2.patchinformation.constants.EmailConstants.COLUMN_NAMES_DEV;
import static org.wso2.patchinformation.constants.EmailConstants.COLUMN_NAMES_INACTIVE;
import static org.wso2.patchinformation.constants.EmailConstants.COLUMN_NAMES_RELEASED;
import static org.wso2.patchinformation.constants.EmailConstants.COLUMN_NAMES_SUMMARY;
import static org.wso2.patchinformation.constants.EmailConstants.DEV_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_FOOTER;
import static org.wso2.patchinformation.constants.EmailConstants.INACTIVE_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.IN_QUEUE_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.IN_SIGNING_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.RELEASED_SECTION_HEADER;
import static org.wso2.patchinformation.constants.EmailConstants.SUMMARY_SECTION_HEADER;

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
     * @param jiraIssues  all JIRA issues.
     * @param emailHeader email header dependent on if its the internal or customer related email.
     * @return the email body.
     */
    public String getEmailBody(ArrayList<JIRAIssue> jiraIssues, String emailHeader) {

        String emailBody = emailHeader;
        emailBody += getSummeryTable(jiraIssues);
        ArrayList<Patch> inactivePatches = new ArrayList<>(getAllInactivePatches(jiraIssues));
        emailBody += getStateTable(INACTIVE_SECTION_HEADER, COLUMN_NAMES_INACTIVE, "Jira Create Date",
                inactivePatches);

        ArrayList<Patch> patches = new ArrayList<>(getAllPatches(jiraIssues));
        ArrayList<Patch> patchesInQueue = new ArrayList<>();
        ArrayList<Patch> patchesInDev = new ArrayList<>();
        ArrayList<Patch> patchesInSigning = new ArrayList<>();
        assignOpenPatchesToStates(patches, patchesInQueue, patchesInSigning, patchesInDev);
        emailBody += getStateTable(IN_QUEUE_SECTION_HEADER, COLUMN_NAMES, "Work Days In Queue",
                patchesInQueue);
        emailBody += getStateTable(DEV_SECTION_HEADER, COLUMN_NAMES_DEV, "Work Days In Dev",
                patchesInDev);
        emailBody += getStateTable(IN_SIGNING_SECTION_HEADER, COLUMN_NAMES, "Work Days In Signing",
                patchesInSigning);
        emailBody += getReleasedTable(jiraIssues);
        emailBody += EMAIL_FOOTER;
        return emailBody;
    }

    /**
     * Builds the html string corresponding to the Summary table which holds information on all unique JIRA issues
     * with open patches.
     *
     * @param jiraIssues arraylist of all JIRA issues
     * @return the html code for the table
     */
    private String getSummeryTable(ArrayList<JIRAIssue> jiraIssues) {

        String table = SUMMARY_SECTION_HEADER;
        table += COLUMN_NAMES_SUMMARY;
        jiraIssues.sort(new ReportDateComparator());
        ArrayList<HtmlTableRow> jirasToHtml = new ArrayList<>(jiraIssues);
        table += getTableRows(jirasToHtml);
        table += "</table>";
        return table;
    }


    /**
     * returns an arraylist of all inactive patches
     *
     * @param jiraIssues arraylist of JIRA issues
     * @return all patches
     */
    private ArrayList<InactivePatch> getAllInactivePatches(ArrayList<JIRAIssue> jiraIssues) {

        ArrayList<InactivePatch> inactivePatches = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            if (jiraIssue.getOpenPatchesInJIRA().isEmpty()) {
                inactivePatches.addAll(jiraIssue.getInactivePatches());
            }
        }
        inactivePatches.sort(new DateCompatator());
        return inactivePatches;
    }

    /**
     * returns an arraylist of all patches
     *
     * @param jiraIssues arraylist of JIRA issues
     * @return all patches
     */
    private ArrayList<OpenPatch> getAllPatches(ArrayList<JIRAIssue> jiraIssues) {

        ArrayList<OpenPatch> openPatches = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            openPatches.addAll(jiraIssue.getOpenPatchesInJIRA());
        }
        return openPatches;
    }

    /**
     * Add the patches to an arraylist dependent on which state it is in.
     *
     * @param openPatches arraylist of all patches to be recorded.
     */
    private void assignOpenPatchesToStates(ArrayList<Patch> openPatches, ArrayList<Patch> patchesInQueue,
                                           ArrayList<Patch> patchesInSigning, ArrayList<Patch> patchesInDevelopment) {

        for (Patch openPatch : openPatches) {
            switch (openPatch.getState()) {
                case IN_DEV:
                    patchesInDevelopment.add(openPatch);
                    break;
                case IN_PATCH_QUEUE:
                    patchesInQueue.add(openPatch);
                    break;
                case IN_SIGNING:
                    patchesInSigning.add(openPatch);
                    break;
            }
        }
    }

    /**
     * Builds the html string corresponding to each of the 4 states table and the corresponding JIRA issues.
     *
     * @param header         email header
     * @param dateColumnName table attribute name for "date" coloumn on table
     * @param patches        arraylist of patches
     * @return html code showing the state data in a table
     */
    private String getStateTable(String header, String columnNames, String dateColumnName, ArrayList<Patch> patches) {

        String table = header + columnNames + dateColumnName + "</td></tr>";
        if (!INACTIVE_SECTION_HEADER.equals(header)) {
            patches.sort(new PatchChainedComparator(new ProductNameComparator(), new StateNameComparator()));
        }
        ArrayList<HtmlTableRow> patchesToHtml = new ArrayList<>(patches);
        table += getTableRows(patchesToHtml);
        table += "</table>";
        return table;
    }

    /**
     * Builds the table body where each row corresponds to a unique patch.
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
            htmlRows.append(row.objectToHTML(backgroundColor));
        }
        return htmlRows.toString();
    }

    private String getReleasedTable(ArrayList<JIRAIssue> jiraIssues) {

        String table = RELEASED_SECTION_HEADER;
        table += COLUMN_NAMES_RELEASED;
        ArrayList<JIRAIssue> releasedJiras = getJirasWithReleasedPatches(jiraIssues);
        releasedJiras.sort(new ReleasedReportDateComparator());
        table += getReleasedTableRows(releasedJiras);
        table += "</table>";
        return table;
    }

    private ArrayList<JIRAIssue> getJirasWithReleasedPatches(ArrayList<JIRAIssue> jiraIssues) {

        ArrayList<JIRAIssue> releasedJiras = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            ArrayList<OpenPatch> releasedPatches = new ArrayList<>(jiraIssue.getReleasedPatches());
            if (!releasedPatches.isEmpty()) {
                releasedJiras.add(jiraIssue);
            }
        }
        return releasedJiras;
    }

    /**
     * Builds the table body where each row corresponds to a unique patch.
     *
     * @param jiraIssues Arraylist of all issues or patches
     * @return html code for row vaues of table
     */
    private String getReleasedTableRows(ArrayList<JIRAIssue> jiraIssues) {

        StringBuilder htmlRows = new StringBuilder();
        boolean toggleFlag = true;
        String backgroundColor;
        //set background colour
        for (JIRAIssue jiraIssue : jiraIssues) {
            if (toggleFlag) {
                backgroundColor = Constants.BACKGROUND_COLOR_WHITE;
                toggleFlag = false;
            } else {
                backgroundColor = Constants.BACKGROUND_COLOR_GRAY;
                toggleFlag = true;
            }
            htmlRows.append(jiraIssue.devPatchesToHTML(backgroundColor));
        }
        return htmlRows.toString();
    }

}





