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
package org.wso2.dailyPatchInformation.email;

import org.wso2.dailyPatchInformation.JIRAData.JIRAIssue;
import org.wso2.dailyPatchInformation.constants.Constants;
import org.wso2.dailyPatchInformation.pmtData.Comparators.PatchChainedComparator;
import org.wso2.dailyPatchInformation.pmtData.Comparators.ProductNameComparator;
import org.wso2.dailyPatchInformation.pmtData.Comparators.StateNameComparator;
import org.wso2.dailyPatchInformation.pmtData.Patch;

import java.util.ArrayList;

import static org.wso2.dailyPatchInformation.constants.EmailConstants.DEV_STATE_TABLE_COLUMNS_START;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.EMAIL_FOOTER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.IN_DEVELOPMENT_SECTION_HEADER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.IN_QUEUE_SECTION_HEADER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.IN_SIGNING_SECTION_HEADER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.RELEASED_SECTION_HEADER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.STATE_TABLE_COLUMNS_END;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.STATE_TABLE_COLUMNS_START;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.SUMMARY_SECTION_HEADER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.TABLE_HEADER_SUMMARY;

/**
 * Creates and returns the body of the email. The body is broken up into a summary table, and a table for each of
 * the four States.
 */
public class EmailBodyCreator {

    private static EmailBodyCreator EMAIL_BODY_CREATOR;

    private EmailBodyCreator() {

    }

    public static EmailBodyCreator getEmailBodyCreator() {

        if (EMAIL_BODY_CREATOR == null) {
            EMAIL_BODY_CREATOR = new EmailBodyCreator();
        }
        return EMAIL_BODY_CREATOR;
    }

    /**
     * Returns the body of the email.
     *
     * @param patches     all patches to be assigned to states and their details to be recorded in the email.
     * @param JIRAIssues  all JIRA issues containing details to be recorded in the email.
     * @param emailHeader email header dependent on if its the internal or customer related email.
     * @return the email body.
     */
    public String getEmailBody(ArrayList<JIRAIssue> JIRAIssues, String emailHeader) {

        ArrayList<Patch> patchesInQueue = new ArrayList<>();
        ArrayList<Patch> patchesInDevelopment = new ArrayList<>();
        ArrayList<Patch> patchesInSigning = new ArrayList<>();
        ArrayList<Patch> patchesReleased = new ArrayList<>();
        String emailBody = emailHeader;
        emailBody += getSummeryHtmlTable(JIRAIssues);
        assignPatchesToStates(getAllPatches(JIRAIssues), patchesInQueue, patchesInSigning, patchesInDevelopment, patchesReleased);
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

    private ArrayList<Patch> getAllPatches(ArrayList<JIRAIssue> JIRAIssues) {

        ArrayList<Patch> patches = new ArrayList<>();
        for (JIRAIssue jiraIssue : JIRAIssues) {
            patches.addAll(jiraIssue.getPatchesInJIRA());
        }
        return patches;
    }

    /**
     * Builds the html string corresponding to the Summary table which holds information on all unique JIRA issues.
     *
     * @param JIRAIssues arraylist of all JIRA issues
     * @return the html code for the table
     */
    private String getSummeryHtmlTable(ArrayList<JIRAIssue> JIRAIssues) {

        String summaryTable = SUMMARY_SECTION_HEADER;
        summaryTable += TABLE_HEADER_SUMMARY;
        ArrayList<HtmlTableRow> JIRAsToHtml = new ArrayList<>(JIRAIssues);
        summaryTable += getTableRows(JIRAsToHtml);
        summaryTable += "</table>";
        return summaryTable;
    }

    private String getStateHtmlTable(String stateHeader, String dateColumnName, ArrayList<Patch> patchesInState) {

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





