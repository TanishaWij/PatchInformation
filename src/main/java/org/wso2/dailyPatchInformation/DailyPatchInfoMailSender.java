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
package org.wso2.dailyPatchInformation;

import org.apache.log4j.Logger;
import org.wso2.dailyPatchInformation.email.EmailBodyCreator;
import org.wso2.dailyPatchInformation.email.EmailSender;
import org.wso2.dailyPatchInformation.exceptions.EmailExceptions.EmailException;
import org.wso2.dailyPatchInformation.exceptions.JIRAExceptions.JIRAException;
import org.wso2.dailyPatchInformation.exceptions.PatchInformtionException;
import org.wso2.dailyPatchInformation.exceptions.PmtExceptions.PmtException;
import org.wso2.dailyPatchInformation.JIRAData.JIRAAccessor;
import org.wso2.dailyPatchInformation.JIRAData.JIRAIssue;
import org.wso2.dailyPatchInformation.pmtData.Patch;
import org.wso2.dailyPatchInformation.pmtData.PmtAccessor;
import org.wso2.dailyPatchInformation.propertyValues.PropertyValues;

import java.io.IOException;
import java.util.ArrayList;

import static org.wso2.dailyPatchInformation.constants.EmailConstants.EMAIL_HTML_HEADER_CUSTOMER_RELATED;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.EMAIL_HTML_HEADER_INTERNAL;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.EMAIL_SUBJECT_CUSTOMER_RELATED;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.EMAIL_SUBJECT_INTERNAL;

public class DailyPatchInfoMailSender {

    private final static Logger logger = Logger.getLogger(DailyPatchInfoMailSender.class);

    public static void main(String[] args) {

        try {
            logger.info("Executing process to send email on Internal JIRA issues.");
            executeEmailSendingProcess(false);
            logger.info("Execution completed successfully.\n");
        } catch (PatchInformtionException e) {
            logger.error("Execution failed, process was not completed\n", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            logger.info("Executing process to send email on Customer related JIRA issues.");
            executeEmailSendingProcess(true);
            logger.info("Execution completed successfully.\n");
        } catch (PatchInformtionException e) {
            logger.error("Execution failed, process was not completed\n", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The process gets internal JIRA issues or customer related JIRA issues, and sends an email containing information
     * on them and their associated patches.
     *
     * @param isMailOnCustomerReportedIssues boolean to determine whether it is the internal or customer related mail being sent.
     * @throws Exception The process execution has halted
     */
    private static void executeEmailSendingProcess(boolean isMailOnCustomerReportedIssues) throws PatchInformtionException, IOException {

        PropertyValues propertyValues = PropertyValues.getPropertyValues();
        String urlToJIRAIssues;
        String emailSubject;
        String htmlEmailHeader;

        if (isMailOnCustomerReportedIssues) {
            urlToJIRAIssues = propertyValues.getUrlToCustomerIssuesFilter();
            emailSubject = EMAIL_SUBJECT_CUSTOMER_RELATED;
            htmlEmailHeader = EMAIL_HTML_HEADER_CUSTOMER_RELATED;

        } else {
            urlToJIRAIssues = propertyValues.getUrlToInternalIssuesFilter();
            emailSubject = EMAIL_SUBJECT_INTERNAL;
            htmlEmailHeader = EMAIL_HTML_HEADER_INTERNAL;
        }

        ArrayList<JIRAIssue> JIRAIssues;
        try {
            JIRAIssues = new ArrayList<JIRAIssue>(JIRAAccessor.getJIRAs(urlToJIRAIssues, propertyValues.getJIRABasicAuth()));
            logger.info("Successfully extracted JIRA issue information from JIRA.");
        } catch (JIRAException e) {
            String errorMessage = "Failed to extract JIRA issues from JIRA.";
            logger.error(errorMessage, e);
            throw new PatchInformtionException(errorMessage, e);
        }

        String emailBody;
        try {
            String pmtConnection = propertyValues.getPmtConnection();
            String pmtUserName = propertyValues.getDbUser();
            String pmtUserPassword = propertyValues.getDbPassword();
            ArrayList<JIRAIssue> JIRATicketsInPmtAndJIRA = new ArrayList<>(PmtAccessor.filterJIRAIssues(JIRAIssues, pmtConnection, pmtUserName, pmtUserPassword));
            ArrayList<Patch> patches = new ArrayList<>(PmtAccessor.getPatchInformation(JIRATicketsInPmtAndJIRA, pmtConnection, pmtUserName, pmtUserPassword));
            logger.info("Successfully extracted patch information from the pmt.");
            emailBody = EmailBodyCreator.getEmailBody(patches, JIRATicketsInPmtAndJIRA, htmlEmailHeader);
        } catch (PmtException e) {
            String errorMessage = "Failed to extract Patch information from the pmt.";
            logger.error(errorMessage, e);
            throw new PatchInformtionException(errorMessage, e);
        }

        try {
            EmailSender.sendMessage(emailBody, emailSubject, propertyValues.getEmailUser(), propertyValues.getToList(), propertyValues.getCcList());
            logger.info("Successfully sent email with patch information.");
        } catch (EmailException e) {
            String errorMessage = "Failed to send email.";
            logger.error(errorMessage, e);
            throw new PatchInformtionException(errorMessage, e);
        }

    }

}
