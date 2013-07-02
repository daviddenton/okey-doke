package org.hamcrest.approvals;

import org.junit.runner.Description;

public class Naming {

    public static String testNameFor(Description description) {
        String justTheClassName = description.getTestClass().getSimpleName();
        return justTheClassName + "." + description.getDisplayName().replaceFirst("\\(.*\\)", "");
    }

}
