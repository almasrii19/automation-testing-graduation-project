package com.orangehrm.models;

import java.util.List;

/**
 * JSON wrapper for the login scenario collection.
 */
public record LoginDataFile(List<LoginScenario> scenarios) {
}
