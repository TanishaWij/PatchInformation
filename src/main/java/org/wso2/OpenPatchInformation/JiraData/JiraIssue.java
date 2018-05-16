package org.wso2.OpenPatchInformation.JiraData;//
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

import org.wso2.OpenPatchInformation.Email.HtmlTableRow;
import org.wso2.OpenPatchInformation.PmtData.Patch;

import java.util.ArrayList;

/**
 * Represents each of the unique Jiras returned by the JiraIssue Filter.
 */
public class JiraIssue implements HtmlTableRow {

    private String name;
    private String assigneeName;
    private ArrayList<Patch> patches;
    private int numPatches;
    private String oldestPatchReportDate;
    private String jiraLink;

    public JiraIssue(String jiraName, String assignee) {

        this.name = jiraName;
        this.assigneeName = assignee;
        this.patches = new ArrayList<>();
        this.numPatches = 0;
    }

    public void addPatchToJira(Patch newPatch) {

        this.patches.add(newPatch);
        numPatches++;
    }

    public ArrayList<Patch> getPatchesInJira() {

        return this.patches;
    }

    public String getName() {

        return name;
    }

    public String getAssigneeName() {

        return this.assigneeName;

    }

    public void setJiraLink(String jiraLink) {

        this.jiraLink = jiraLink;

    }

    public void setPatchReportDate(String oldestPatchReportDate) {

        this.oldestPatchReportDate = oldestPatchReportDate;

    }

    public String objectToHtml(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiraLink + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assigneeName + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                numPatches + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                oldestPatchReportDate;
    }
}
