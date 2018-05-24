package org.wso2.patchinformation.comparators;

import org.wso2.patchinformation.jira.JIRAIssue;

import java.io.Serializable;
import java.util.Comparator;


/**
 * Implements the Comparator class to order objects of the JIRAIssue class by it's ReportDateReleasedPatches attribute.
 */
public class ReleasedReportDateComparator implements Comparator<JIRAIssue>, Serializable {

    public int compare(JIRAIssue jiraIssue1, JIRAIssue jiraIssue2) {
        return jiraIssue1.getReportDateReleasedPatches().compareTo(jiraIssue2.getReportDateReleasedPatches());
    }
}
