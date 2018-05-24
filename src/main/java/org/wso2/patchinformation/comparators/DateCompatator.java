package org.wso2.patchinformation.comparators;

import org.wso2.patchinformation.pmt.InactivePatch;

import java.io.Serializable;
import java.util.Comparator;
/**
 * Implements the Comparator class to order objects of the OpenPatch class by it's "JiraCreateDate" attribute.
 */
public class DateCompatator implements Comparator<InactivePatch>, Serializable {

    public int compare(InactivePatch p1, InactivePatch p2) {
        return p1.getJiraCreateDate().compareTo(p2.getJiraCreateDate());
    }


}
