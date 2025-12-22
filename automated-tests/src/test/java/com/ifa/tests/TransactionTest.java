package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import com.ifa.tests.pages.LoginPage;
import com.ifa.tests.pages.TransactionPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TransactionTest extends BaseTest {

    @Test
    public void testCreateTransfer() {
        // Pre-requisite: Already Logged in (Single Login Flow)
        DashboardPage dashboardPage = new DashboardPage(driver);
        // Assert.assertTrue(dashboardPage.isPageLoaded(), "Login failed"); // Optional
        // check if we trust the flow

        // Navigate to Transaction List
        driver.get("http://localhost:4200/admin/transactions");

        // Click "Nouveau Virement" button
        // Logic: Wait for button to be clickable then click
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                        org.openqa.selenium.By.xpath("//button[contains(., 'Nouveau Virement')]")))
                .click();

        TransactionPage transactionPage = new TransactionPage(driver);
        transactionPage.fillTransferForm("100", "4882064412973377", "9235557853399000", "Test Automation Transfer");

        // Uncomment to actually submit (might fail if data is invalid)
        transactionPage.submit();

        Assert.assertTrue(transactionPage.isTransactionListDisplayed(), "Notredirected to list after transfer");
    }
}
