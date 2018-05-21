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

package org.wso2.dailyPatchInformation.pmtData;

import org.wso2.dailyPatchInformation.constants.Constants;
import org.wso2.dailyPatchInformation.email.HtmlTableRow;

/**
 * Extended from the more general Patch class. Same functionality, more class attributes.
 */
public class DevPatch extends Patch implements HtmlTableRow {

    private String reportDate;

    DevPatch(String url, String Name, String productName, String assignee, Constants.State state, String patchLCState,
             String reportDate, String daysInState) {

        super(url, Name, productName, assignee, state, patchLCState, daysInState);
        this.reportDate = reportDate;
    }

    /**
     * Builds the patch data as a HTML table row
     *
     * @param backgroundColor of table row
     * @return Returns the HTML code for a table row
     */
    @Override
    public String objectToHtml(String backgroundColor) {

        return "<tr><td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getUrl() + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getProductName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor
                + " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400;" +
                " line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getAssignee() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getPatchLCState() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                this.reportDate + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px;  padding: 15px 10px 5px 10px;\">" +
                getDaysInState();
    }

}