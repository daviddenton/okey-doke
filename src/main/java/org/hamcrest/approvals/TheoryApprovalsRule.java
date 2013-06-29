package org.hamcrest.approvals;

import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;


public class TheoryApprovalsRule extends ApprovalsRule {

    private Map<Description, StringBuilder> results = new HashMap<Description, StringBuilder>();

    public TheoryApprovalsRule(String sourceRoot) {
        super(sourceRoot);
    }

    public TheoryApprover approver() {
        return new TheoryApprover(sourceRoot);
    }

    @Override
    protected void succeeded(Description description) {
        List<Throwable> errors = new ArrayList<Throwable>();
        for (Map.Entry<Description, StringBuilder> entry : results.entrySet()) {
            String actual = entry.getValue().toString();
            String testName = entry.getKey().getDisplayName();
            try {
                assertThat(actual, isAsApproved(testName));
            } catch (Throwable t) {
                errors.add(t);
            }
        }
        if (errors.isEmpty())
            return;
        else
            rethrow(errors.get(0));
    }

    private void rethrow(Throwable t) {
        if (t instanceof Error)
            throw (Error) t;
        else if (t instanceof RuntimeException)
            throw (RuntimeException) t;
        else throw new RuntimeException(t);
    }

    public class TheoryApprover extends TestRememberer {

        private Description theory;

        public TheoryApprover(String sourceRoot) {
            super(sourceRoot);
        }

        @Override
        public void starting(Description description) {
            theory = description;
            if (!results.containsKey(description))
                results.put(theory, new StringBuilder());
            super.starting(description);
        }

        public void lockDown(Object result, Object... arguments) {
            results.get(theory).append(formatted(result, arguments));
        }

        private String formatted(Object result, Object[] parameters) {
            StringBuilder myResult = new StringBuilder();
            myResult.append("[").append(formatted(parameters)).append("] -> ");
            myResult.append(String.valueOf(result)).append("\n");
            return myResult.toString();
        }

        private String formatted(Object[] parameters) {
            StringBuilder result = new StringBuilder();
            for (Object parameter : parameters) {
                result.append(String.valueOf(parameter)).append(", ");
            }
            return result.substring(0, result.length() - 2).toString();
        }
    }
}