package com.orangehrm.listeners;

import com.orangehrm.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Central TestNG listener: writes an environment file for Allure and logs
 * suite/test lifecycle events. Failure details are always logged at ERROR level.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("Starting TestNG suite: {}", context.getSuite().getName());
        writeAllureEnvironment();
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("TEST STARTED: {} [thread={}]", result.getMethod().getMethodName(), Thread.currentThread().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("TEST PASSED: {} ({} ms)", result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("TEST FAILED: {} ({} ms)", result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
        if (result.getThrowable() != null) {
            LOG.error("Failure reason: {}", result.getThrowable().getMessage(), result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("TEST SKIPPED: {}", result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            LOG.warn("Skip reason: {}", result.getThrowable().getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        List<ITestResult> passed = List.of(context.getPassedTests().getAllResults().toArray(new ITestResult[0]));
        List<ITestResult> failed = List.of(context.getFailedTests().getAllResults().toArray(new ITestResult[0]));
        List<ITestResult> skipped = List.of(context.getSkippedTests().getAllResults().toArray(new ITestResult[0]));
        LOG.info("Suite '{}' finished - passed={}, failed={}, skipped={}",
                context.getSuite().getName(), passed.size(), failed.size(), skipped.size());
    }

    private String parallelThreadCount() {
        String configured = System.getProperty("testng.thread.count");
        return (configured == null || configured.isBlank()) ? "2" : configured;
    }

    private void writeAllureEnvironment() {
        String resultsDirectory = System.getProperty("allure.results.directory", "allure-results");
        Path resultsPath = Paths.get(resultsDirectory);
        String environment = String.join(System.lineSeparator(),
                "Browser=" + ConfigReader.get("browser"),
                "Base.URL=" + ConfigReader.getBaseUrl(),
                "Headless=" + ConfigReader.getBoolean("headless"),
                "Explicit.Wait=" + ConfigReader.getInt("explicit.wait") + "s",
                "Operating.System=" + System.getProperty("os.name") + " " + System.getProperty("os.version"),
                "Java.Version=" + System.getProperty("java.version"),
                "Parallel.Mode=tests (thread-count=" + parallelThreadCount() + ")"
        );

        try {
            Files.createDirectories(resultsPath);
            Files.writeString(resultsPath.resolve("environment.properties"), environment);
        } catch (IOException exception) {
            LOG.warn("Unable to write Allure environment file: {}", exception.getMessage());
        }
    }
}
