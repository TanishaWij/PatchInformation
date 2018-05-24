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
package org.wso2.patchinformation.jira;

import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.email.HtmlTableRow;
import org.wso2.patchinformation.pmt.Patch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.wso2.patchinformation.constants.Constants.NOT_IN_PMT;
import static org.wso2.patchinformation.constants.Constants.NOT_SET;

/**
 * Represents each of the unique JIRAs returned by the JIRAIssue Filter.
 */
public class JIRAIssue implements HtmlTableRow {

    private String name;
    private String assigneeName;
    private ArrayList<Patch> patches;
    private ArrayList<Patch> releasedPatches;
    private String reportDate;
    private String reportDateReleasedPatches;
    private String jiralink;

    public String getReportDate() {
        return reportDate;
    }

    public String getReportDateReleasedPatches() {

        return reportDateReleasedPatches;
    }


    JIRAIssue(String jiraName, String assignee) {
        this.name = jiraName;
        this.assigneeName = assignee;
        this.patches = new ArrayList<>();
        this.releasedPatches = new ArrayList<>();
        this.reportDate = NOT_SET;
        this.reportDateReleasedPatches = NOT_SET;
    }

    public ArrayList<Patch> getReleasedPatches() {

        return releasedPatches;
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

    public void setJiralink(String jiralink) {
        this.jiralink = jiralink;
    }


    /**
     * Returns the oldest of two date
     *
     * @param currentDateStr the date val currently recorded
     * @param newDateStr     new date
     * @return the oldest date
     */
    private static String dateCompare(String currentDateStr, String newDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date currentDate = sdf.parse(currentDateStr);
            Date newDate = sdf.parse(newDateStr);
            if (currentDate.before(newDate)) {
                return currentDateStr;
            } else {
                return newDateStr;
            }
        } catch (ParseException e) {
            return currentDateStr;
        }
    }

    public void addPatchToJIRA(Patch patch, String currentReportDate) {
        this.patches.add(patch);
        if (patch.getState().equals(Constants.State.REALEASED)) {
            addReleasedPatches(patch, currentReportDate);
        }
    }

    public void setReportDate(String currentReportDate) {
        if (NOT_SET.equals(reportDate)) {
            reportDate = currentReportDate;
        } else {
            reportDate = dateCompare(reportDate, currentReportDate);
        }
    }

    private void addReleasedPatches(Patch patch, String currentReportDate) {
        this.releasedPatches.add(patch);
        if (NOT_SET.equals(reportDateReleasedPatches)) {
            reportDateReleasedPatches = currentReportDate;
        } else {
            reportDateReleasedPatches = dateCompare(reportDateReleasedPatches, currentReportDate);
        }
    }
    public String objectToHTML(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiralink + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assigneeName + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                patches.size() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                reportDate;
    }

    public String devPatchesToHTML(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiralink + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assigneeName + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                releasedPatches.size() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                reportDateReleasedPatches;
    }

    public void setAsNotInPMT() {
        this.reportDate = NOT_IN_PMT;
    }
}

