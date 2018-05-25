package org.wso2.patchinformation.pmt;

import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.email.HtmlTableRow;

/**
 * A patch associated with a JIRA issue
 */
public class Patch implements HtmlTableRow {

    private String jiraLink;
    private String name;
    private String productName;
    private String assignee;
    private Constants.State state;
    private String patchLCState;

    Patch(String jiraLink, String name, String productName, String assignee, Constants.State state,
          String patchLCState) {
        this.jiraLink = jiraLink;
        this.name = name;
        this.productName = productName;
        this.assignee = assignee;
        this.state = state;
        this.patchLCState = patchLCState;
    }

    String getName() {
        return name;
    }

    public Constants.State getState() {
        return state;
    }

    public String getPatchLCState() {
        return this.patchLCState;
    }

    String getJiraLink() {
        return jiraLink;
    }

    public String getProductName() {
        return productName;
    }

    String getAssignee() {
        return assignee;
    }

    @Override
    public String objectToHTML(String backgroundColor) {
        return null;
    }
}
