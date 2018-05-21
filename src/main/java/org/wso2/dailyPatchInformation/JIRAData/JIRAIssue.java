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
package org.wso2.dailyPatchInformation.JIRAData;

import org.wso2.dailyPatchInformation.email.HtmlTableRow;
import org.wso2.dailyPatchInformation.pmtData.Patch;

import java.util.ArrayList;

/**
 * Represents each of the unique JIRAs returned by the JIRAIssue Filter.
 */
public class JIRAIssue implements HtmlTableRow {

    private String name;
    private String assigneeName;
    private ArrayList<Patch> patches;

    private String oldestPatchReportDate;
    private String JIRALink;

    JIRAIssue(String JIRAName, String assignee) {
        this.name = JIRAName;
        this.assigneeName = assignee;
        this.patches = new ArrayList<>();
    }

    public void addPatchToJIRA(Patch newPatch) {

        this.patches.add(newPatch);
    }

    public ArrayList<Patch> getPatchesInJIRA() {

        return this.patches;
    }

    public String getName() {

        return name;
    }

    public String getAssigneeName() {

        return this.assigneeName;

    }

    public void setJIRALink(String JIRALink) {

        this.JIRALink = JIRALink;

    }

    public void setPatchReportDate(String oldestPatchReportDate) {

        this.oldestPatchReportDate = oldestPatchReportDate;

    }

    public String objectToHtml(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                JIRALink + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assigneeName + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                patches.size() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                oldestPatchReportDate;
    }
}
