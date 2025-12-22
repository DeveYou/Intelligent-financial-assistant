package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import com.ifa.tests.pages.LoginPage;
import com.ifa.tests.pages.TransactionListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationFilterTest extends BaseTest {

    @Test
    public void testNavigationAndFilter() {
        // Pre-requisite: Already Logged in
        DashboardPage dashboardPage = new DashboardPage(driver);
        slowDown();
        slowDown();

        // Navigation Test
        dashboardPage.goToAccounts();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("accounts"));
        Assert.assertTrue(driver.getCurrentUrl().contains("accounts"), "Failed to navigate to Accounts");
        slowDown();

        dashboardPage.goToTransactions();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("transactions"));
        Assert.assertTrue(driver.getCurrentUrl().contains("transactions"), "Failed to navigate to Transactions");
        slowDown();

        // Filter Test
        TransactionListPage listPage = new TransactionListPage(driver);
        // We filter by a random string to verify the input interaction
        listPage.filterByReference("TXN-09D04CD2");
        listPage.clickSearch();
        slowDown();

        // Assert that the table is still visible (even if empty) or that search didn't
        // crash app
        // In a real scenario we would assert specific row visibility
        Assert.assertTrue(driver.findElement(org.openqa.selenium.By.className("users-table")).isDisplayed());
    }
}
