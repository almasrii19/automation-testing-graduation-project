package com.orangehrm.data;

import com.google.gson.Gson;
import com.orangehrm.models.EmployeeData;
import com.orangehrm.models.EmployeeDataFile;
import com.orangehrm.models.LoginDataFile;
import com.orangehrm.models.LoginScenario;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Loads JSON test data once and exposes it to TestNG through DataProviders.
 * No credential or employee value is hardcoded in the test methods.
 */
public final class TestDataProvider {

    private static final Gson GSON = new Gson();
    private static final String LOGIN_DATA_FILE = "testdata/login-data.json";
    private static final String EMPLOYEE_DATA_FILE = "testdata/employee-data.json";

    private static final LoginDataFile LOGIN_DATA = readJson(LOGIN_DATA_FILE, LoginDataFile.class);
    private static final EmployeeDataFile EMPLOYEE_DATA = readJson(EMPLOYEE_DATA_FILE, EmployeeDataFile.class);

    private TestDataProvider() {
    }

    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return new Object[][]{{loginScenario("valid")}};
    }

    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        return new Object[][]{{loginScenario("invalid")}};
    }

    @DataProvider(name = "emptyLoginData")
    public static Object[][] emptyLoginData() {
        return new Object[][]{{loginScenario("empty")}};
    }

    @DataProvider(name = "existingEmployeeData")
    public static Object[][] existingEmployeeData() {
        return new Object[][]{{existingEmployee()}};
    }

    @DataProvider(name = "nonExistingEmployeeData")
    public static Object[][] nonExistingEmployeeData() {
        return new Object[][]{{nonExistingEmployee()}};
    }

    @DataProvider(name = "newEmployeeData")
    public static Object[][] newEmployeeData() {
        return new Object[][]{{newEmployee()}};
    }

    public static LoginScenario loginScenario(String scenario) {
        return LOGIN_DATA.scenarios().stream()
                .filter(item -> item.scenario().equalsIgnoreCase(scenario))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Login scenario not found in JSON: " + scenario));
    }

    public static EmployeeData existingEmployee() {
        return EMPLOYEE_DATA.existingEmployee();
    }

    public static EmployeeData nonExistingEmployee() {
        return EMPLOYEE_DATA.nonExistingEmployee();
    }

    public static EmployeeData newEmployee() {
        return EMPLOYEE_DATA.newEmployee();
    }

    /**
     * Makes the created employee unique so repeated/parallel runs never collide
     * with records created by earlier executions on the shared public demo.
     */
    public static String uniqueLastName() {
        return newEmployee().lastName() + (System.nanoTime() % 1_000_000);
    }

    public static String uniqueNonExistingFullName() {
        EmployeeData data = nonExistingEmployee();
        return data.firstName() + data.lastName() + (System.nanoTime() % 1_000_000);
    }

    private static <T> T readJson(String resource, Class<T> type) {
        try (InputStream input = TestDataProvider.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalStateException("Test data file not found on classpath: " + resource);
            }
            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                T data = GSON.fromJson(reader, type);
                if (data == null) {
                    throw new IllegalStateException("Test data file is empty or invalid: " + resource);
                }
                return data;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read test data file: " + resource, exception);
        }
    }
}
