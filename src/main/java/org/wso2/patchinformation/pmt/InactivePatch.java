package org.wso2.patchinformation.pmt;

import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.email.HtmlTableRow;

/**
 * A patch which is onhold, broken, in regression, has no entry in the pmt, has an entry in PATCH_QUEUE table but not
 * in PATCH_ETA (Patches with "state" set to [reason for not going forward with the Patch]
 */
public class InactivePatch extends Patch implements HtmlTableRow {

    private String jiraCreateDate;
    private String jiraState;

    InactivePatch(String jiraLink, String name, String productName, String assignee,
                  String patchLCState, String jiraCreateDate, String jiraState) {
        super(jiraLink, name, productName, assignee, Constants.State.INACTIVE, patchLCState);
        this.jiraCreateDate = jiraCreateDate;
        this.jiraState = jiraState;
    }


    public String getJiraCreateDate() {
        return jiraCreateDate;
    }

    /**
     * Builds the patch data as a HTML table row.
     *
     * @param backgroundColor of table row.
     * @return Returns the HTML code for a table row.
     */
    @Override
    public String objectToHTML(String backgroundColor) {
        return "<tr><td width=\"" + "15%" + "\" align=\"left\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px;" +
                " font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getJiraLink()  + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400;  line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getProductName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getAssignee() + "<td width=\"" + "7%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400;  line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiraState + "<td width=\"" + "7%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getPatchLCState() + "<td width=\"" + "7%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiraCreateDate;
    }
}
