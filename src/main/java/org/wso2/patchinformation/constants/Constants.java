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

package org.wso2.patchinformation.constants;

/**
 * constant values.
 */
public final class Constants {

    public static final String GRAY_BACKGROUND = "#efefef";
    public static final String WHITE_BACKGROUND = "#ffffff";
    //SQL statements
    public static final String SELECT_PATCHES_FOR_JIRA = "SELECT *, \n" +
            "(5 * (DATEDIFF(CURDATE(), REPORT_DATE) DIV 7)+ MID('0123455401234434012332340122123401101234000123450'," +
            "7 * WEEKDAY((REPORT_DATE)) + WEEKDAY(CURDATE()) + 1, 1)) AS DAYS_SINCE_REPORT,\n" +
            "(5 *(DATEDIFF(CURDATE(),SIGN_REQUEST_SENT_ON) DIV 7)+" +
            "MID('0123455401234434012332340122123401101234000123450'," +
            "7 * WEEKDAY((SIGN_REQUEST_SENT_ON)) + WEEKDAY(CURDATE()) + 1, 1)) AS DAYS_IN_SIGNING FROM PATCH_ETA e " +
            "RIGHT JOIN PATCH_QUEUE q ON e.PATCH_QUEUE_ID = q.ID\n" +
            "where SUPPORT_JIRA like '%";
    public static final String SELECT_SUPPORT_JIRAS = "SELECT SUPPORT_JIRA FROM PATCH_QUEUE\n" +
            "WHERE YEAR(REPORT_DATE) > '2017';";

    public static final String JIRA_URL_PREFIX = "https://support.wso2.com/jira/browse/";
    public static final int JIRA_URL_PREFIX_LENGTH = 37;
    public static final String NO_ENTRY_IN_PMT = "No Entry in PMT";
    public static final String NA = "N/A";
    //"active" on patch queue
    public static final String OFF_QUEUE = "No";
    public static final String IN_QUEUE = "Yes";
    //LC States
    public static final String STAGING = "Staging";
    public static final String DEVELOPMENT = "Development";
    public static final String TESTING = "Testing";
    public static final String PRE_QA = "PreQADevelopment";
    public static final String READY_FOR_QA = "ReadyForQA";
    public static final String FAILED_QA = "FailedQA";
    public static final String ON_HOLD = "OnHold";
    public static final String RELEASED_LC = "Released";
    public static final String RELEASED_NOT_AUTOMATED = "ReleasedNotAutomated";
    public static final String RELEASED_NOT_IN_PUBLIC_SVN = "ReleasedNotInPublicSVN";
    public static final String SUPPORT_JIRA_URL = "SUPPORT_JIRA";
    //jira constants
    public static final int RESULTS_PER_PAGE = 50;
    public static final int OK = 200;
    public static final String AUTH = "Authorization";
    public static final String CONTENT = "Content-Type";
    public static final String CONTENT_TYPE = "application/json; charset=UTF-8";
    public static final String GET = "GET";
    public static final String SEARCH_URL = "searchUrl";
    public static final String TOTAL = "total";
    public static final String ISSUES = "issues";
    public static final String FIELDS = "fields";
    public static final String ASSIGNEE = "assignee";
    public static final String STATUS = "status";
    public static final String DATE_CREATED = "created";
    public static final String JIRA_KEY = "key";
    public static final String EMAIL = "emailAddress";
    public static final String NAME = "name";
    public static final String NOT_SPECIFIED = "Not Specified";

    private Constants() {
        // restrict instantiation
    }

    /**
     * States associated with tables in email.
     */
    public enum State {
        IN_DEV, IN_PATCH_QUEUE, IN_SIGNING, RELEASED, INACTIVE
    }
}
