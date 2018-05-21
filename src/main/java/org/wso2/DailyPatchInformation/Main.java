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
package org.wso2.DailyPatchInformation;

import org.apache.log4j.Logger;
import org.wso2.DailyPatchInformation.Email.EmailBodyCreator;
import org.wso2.DailyPatchInformation.Email.EmailSender;
import org.wso2.DailyPatchInformation.Exceptions.EmailExceptions.EmailException;
import org.wso2.DailyPatchInformation.Exceptions.JiraExceptions.JiraException;
import org.wso2.DailyPatchInformation.Exceptions.PatchInformtionException;
import org.wso2.DailyPatchInformation.Exceptions.PmtExceptions.PmtException;
import org.wso2.DailyPatchInformation.JiraData.AccessJira;
import org.wso2.DailyPatchInformation.JiraData.JiraIssue;
import org.wso2.DailyPatchInformation.PmtData.AccessPMT;
import org.wso2.DailyPatchInformation.PmtData.Patch;
import org.wso2.DailyPatchInformation.PropertyValues.ConfiguredProperties;

import java.util.ArrayList;

import static org.wso2.DailyPatchInformation.Constants.EmailConstants.*;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            logger.info("Executing process to send email on Internal Jira issues.");
            executeEmailSendingProcess(false);
            logger.info("Execution completed successfully.\n");
        } catch (PatchInformtionException e) {
            logger.error("Execution failed, process was not completed\n");
        }
        try {
            logger.info("Executing process to send email on Customer related Jira issues.");
            executeEmailSendingProcess(true);
            logger.info("Execution completed successfully.\n");
        } catch (PatchInformtionException e) {
            logger.error("Execution failed, process was not completed\n");
        }
    }

    /**
     * The process gets internal Jira issues or customer related jira issues, and sends an email containing information
     * on them and their associated patches.
     *
     * @param isMailOnCustomerReportedIssues boolean to determine whether it is the internal or customer related mail being sent.
     * @throws Exception The process execution has halted
     */
    private static void executeEmailSendingProcess(boolean isMailOnCustomerReportedIssues) throws PatchInformtionException {

        String urlToJiraIssues;
        String emailSubject;
        String htmlEmailHeader;

        if (isMailOnCustomerReportedIssues) {
            urlToJiraIssues = ConfiguredProperties.getValueOf("UrlToCustomerIssues");
            emailSubject = EMAIL_SUBJECT_CUSTOMER_RELATED;
            htmlEmailHeader = EMAIL_HTML_HEADER_CUSTOMER_RELATED;

        } else {
            urlToJiraIssues = ConfiguredProperties.getValueOf("UrlToInternalIssues");
            emailSubject = EMAIL_SUBJECT_INTERNAL;
            htmlEmailHeader = EMAIL_HTML_HEADER_INTERNAL;

        }

        ArrayList<JiraIssue> jiraIssues;
        try {
            jiraIssues = new ArrayList<>(AccessJira.getJirasReturnedBy(urlToJiraIssues));
            logger.info("Successfully extracted Jira issue information from Jira.");
        } catch (JiraException e) {
            String errorMessage = "Failed to extract Jira issues from Jira.";
            logger.error(errorMessage, e);
            throw new PatchInformtionException(errorMessage, e);
        }

        String emailBody;
        try {
            ArrayList<JiraIssue> jiraTicketsInPmtAndJira = new ArrayList<>(AccessPMT.getJiraIssuesInBothPMTAndJira(jiraIssues));
            ArrayList<Patch> patches = new ArrayList<>(AccessPMT.getPatchesAssociatedWith(jiraTicketsInPmtAndJira));
            logger.info("Successfully extracted patch information from the pmt.");
            emailBody = EmailBodyCreator.getEmailBody(patches, jiraTicketsInPmtAndJira, htmlEmailHeader);
        } catch (PmtException e) {
            String errorMessage = "Failed to extract Patch information from the pmt.";
            logger.error(errorMessage, e);
            throw new PatchInformtionException(errorMessage, e);
        }

        try {
            EmailSender.sendMessage(emailBody, emailSubject);
            logger.info("Successfully sent Email with patch information.");
        } catch (EmailException e) {
            String errorMessage = "Failed to send email.";
            logger.error(errorMessage, e);
            throw new PatchInformtionException(errorMessage, e);
        }

    }

}
