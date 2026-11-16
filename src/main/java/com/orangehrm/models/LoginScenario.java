package com.orangehrm.models;

/**
 * A single login scenario loaded from {@code testdata/login-data.json}.
 */
public record LoginScenario(
        String scenario,
        String username,
        String password,
        String expectedUrlFragment,
        String expectedMessage,
        String description
) {

    /**
     * Keeps the password out of logs, Allure parameters and failure messages.
     */
    @Override
    public String toString() {
        return "LoginScenario[scenario=" + scenario
                + ", username=" + username
                + ", password=[REDACTED]"
                + ", expectedUrlFragment=" + expectedUrlFragment
                + ", expectedMessage=" + expectedMessage
                + ", description=" + description + "]";
    }
}
