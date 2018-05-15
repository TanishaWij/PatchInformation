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
package org.wso2.OpenPatchInformation;
//import org.wso2.OpenPatchInformation.PmtData.AccessPmt;

import org.apache.log4j.Logger;
import org.wso2.OpenPatchInformation.Email.EmailBodyCreator;
import org.wso2.OpenPatchInformation.Email.EmailSender;
import org.wso2.OpenPatchInformation.JiraData.AccessJira;
import org.wso2.OpenPatchInformation.JiraData.JiraIssue;
import org.wso2.OpenPatchInformation.PmtData.AccessPMT;
import org.wso2.OpenPatchInformation.PmtData.Patch;

import java.util.ArrayList;

import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_HEADER_EXTERNAL;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_HEADER_INTERNAL;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_SUBJECT_CUSTOMER;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_SUBJECT_INTERNAL;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args)  {
        //TODO: ask what to do with final exception

        try {
            executeProcess(false);
            logger.info("Execution complete");
        } catch (Exception e) {
            logger.error("Mail on internal Jira issues not sent successfully", e);
        }
        try {
            executeProcess(true);
            logger.info("Execution complete");
        } catch (Exception e) {
            logger.error("mail on customer Jira issues not sent successfully", e);
        }
    }

    /**
     * Gets the Jira tickets returned by a Jira filter and sends an email containing information about those Jira
     * ticket and the associated patches
     *
     * @param isMailOnCustomerReportedIssues boolean to determine whether its the internal or external mail been sent
     * @throws Exception the process has not completed in full
     */
    private static void executeProcess(boolean isMailOnCustomerReportedIssues) throws Exception {
        String urlToJiraIssues;
        String emailSubject;
        String emailHeader;
        if (isMailOnCustomerReportedIssues) {
            urlToJiraIssues = ConfiguredProperties.getValueOf("UrlToCustomerIssues");
            emailSubject = EMAIL_SUBJECT_CUSTOMER;
            emailHeader = EMAIL_HEADER_EXTERNAL;
            logger.info("Executing - Sending of mail containing Customer Jira issues and corresponding patch Information.");
        } else {
            urlToJiraIssues = ConfiguredProperties.getValueOf("UrlToInternalIssues");
            emailSubject = EMAIL_SUBJECT_INTERNAL;
            emailHeader = EMAIL_HEADER_INTERNAL;
            logger.info("Executing - Sending of mail containing Internal Jira issues and corresponding patch Information.");
        }

        //Get arraylist of jira tickets from Jira filter
        ArrayList<JiraIssue> jiraIssues = new ArrayList<>(new AccessJira().getJirasReturnedBy(urlToJiraIssues));

        //get arraylist of Jira tickets that have a corresponding entry in the pmt
        ArrayList<JiraIssue> jiraTicketsInPmtAndJira = new ArrayList<>(new AccessPMT().getJiraIssuesInBothPMTAndJira(jiraIssues));
        //Get an arraylist of all patches associated with the jira tickets
        ArrayList<Patch> patches = new ArrayList<>(new AccessPMT().getPatchesAssociatedWith(jiraTicketsInPmtAndJira));

        //get email body
        String emailBody = new EmailBodyCreator().getEmailBody(patches, jiraTicketsInPmtAndJira, emailHeader);
        //Send email
       new EmailSender().sendEmail(emailBody, emailSubject);

    }

}
