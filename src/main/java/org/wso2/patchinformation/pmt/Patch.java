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
package org.wso2.patchinformation.pmt;

import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.email.HtmlTableRow;

/**
 * Represents a PMT Patch.
 */
public class Patch implements HtmlTableRow {

    private String url;
    private String name;
    private String productName;
    private String assignee;
    private Constants.State state;
    private String patchLCState;
    private String daysInState;

    public Patch(String jiraLink, String name, String productName, String assignee, Constants.State state,
                 String patchLCState, String daysInState) {

        this.url = jiraLink;
        this.name = name;
        this.productName = productName;
        this.assignee = assignee;
        this.state = state;
        this.patchLCState = patchLCState;
        this.daysInState = daysInState;
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

    String getUrl() {

        return url;
    }

    public String getProductName() {

        return productName;
    }

    String getAssignee() {

        return assignee;
    }

    String getDaysInState() {

        return daysInState;
    }

    public String objectToHtml(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px;" +
                " font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                url + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400;  line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                name + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                productName + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assignee + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                patchLCState + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                daysInState;
    }
}


