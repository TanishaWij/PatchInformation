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
package org.wso2.PatchInformation;

import org.apache.log4j.Logger;
import org.wso2.PatchInformation.Email.EmailBodyCreator;
import org.wso2.PatchInformation.Email.EmailSender;
import org.wso2.PatchInformation.JiraData.AccessJira;
import org.wso2.PatchInformation.JiraData.JiraIssue;
import org.wso2.PatchInformation.PmtData.AccessPMT;
import org.wso2.PatchInformation.PmtData.Patch;

import java.util.ArrayList;

import static org.wso2.PatchInformation.Constants.EmailConstants.EMAIL_HTML_HEADER_CUSTOMER_RELATED;
import static org.wso2.PatchInformation.Constants.EmailConstants.EMAIL_HTML_HEADER_INTERNAL;
import static org.wso2.PatchInformation.Constants.EmailConstants.EMAIL_SUBJECT_CUSTOMER_RELATED;
import static org.wso2.PatchInformation.Constants.EmailConstants.EMAIL_SUBJECT_INTERNAL;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            executeProcess(false);
        } catch (Exception e) {
            logger.error("Mail on internal Jira issues not sent successfully\n");
        }
        try {
            executeProcess(true);
        } catch (Exception e) {
            logger.error("Mail on customer related Jira issues not sent successfully\n");
        }
    }

    /**
     * The process gets internal Jira issues or customer related jira issues, and sends an email containing information
     * on them and their associated patches.
     *
     * @param isMailOnCustomerReportedIssues boolean to determine whether it is the internal or customer related mail being sent.
     * @throws Exception The process execution has halted
     */
    private static void executeProcess(boolean isMailOnCustomerReportedIssues) throws Exception {

        String urlToJiraIssues;
        String emailSubject;
        String htmlEmailHeader;

        if (isMailOnCustomerReportedIssues) {
            urlToJiraIssues = ConfiguredProperties.getValueOf("UrlToCustomerIssues");
            emailSubject = EMAIL_SUBJECT_CUSTOMER_RELATED;
            htmlEmailHeader = EMAIL_HTML_HEADER_CUSTOMER_RELATED;
            logger.info("Executing process for Customer related Jira issues.");
        } else {
            urlToJiraIssues = ConfiguredProperties.getValueOf("UrlToInternalIssues");
            emailSubject = EMAIL_SUBJECT_INTERNAL;
            htmlEmailHeader = EMAIL_HTML_HEADER_INTERNAL;
            logger.info("Executing process for Internal Jira issues.");
        }

        ArrayList<JiraIssue> jiraIssues = new ArrayList<>(AccessJira.getJirasReturnedBy(urlToJiraIssues));

        ArrayList<JiraIssue> jiraTicketsInPmtAndJira = new ArrayList<>(AccessPMT.getJiraIssuesInBothPMTAndJira(jiraIssues));

        ArrayList<Patch> patches = new ArrayList<>(AccessPMT.getPatchesAssociatedWith(jiraTicketsInPmtAndJira));

        String emailBody = EmailBodyCreator.getEmailBody(patches, jiraTicketsInPmtAndJira, htmlEmailHeader);

        EmailSender.sendMessage(emailBody,emailSubject);

    }

}
