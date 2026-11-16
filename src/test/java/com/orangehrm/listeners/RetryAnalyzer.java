package com.orangehrm.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Retries a failed test up to two times before TestNG marks it as failed.
 * This reduces noise from transient network or demo-environment issues.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT = 2;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    @Override
    public boolean retry(ITestResult result) {
        int currentAttempt = retryCount.incrementAndGet();
        if (currentAttempt <= MAX_RETRY_COUNT) {
            LOG.warn("Retrying test '{}' - attempt {}/{}",
                    result.getName(), currentAttempt, MAX_RETRY_COUNT);
            result.setAttribute("retryAttempt", currentAttempt);
            return true;
        }
        return false;
    }
}
