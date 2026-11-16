package com.orangehrm.models;

/**
 * JSON wrapper for the employee test data.
 */
public record EmployeeDataFile(
        EmployeeData existingEmployee,
        EmployeeData nonExistingEmployee,
        EmployeeData newEmployee
) {
}
