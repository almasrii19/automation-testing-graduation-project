package com.orangehrm.models;

/**
 * Employee values used by the PIM test cases.
 */
public record EmployeeData(String firstName, String lastName) {

    public String fullName() {
        return (firstName + " " + lastName).trim().replaceAll("\\s+", " ");
    }
}
