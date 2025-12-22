package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import com.ifa.tests.pages.LoginPage;
import com.ifa.tests.pages.TransactionListPage;
import com.ifa.tests.pages.TransactionPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DepositTest extends BaseTest {

    @Test
    public void testCreateDeposit() {
        // Pre-requisite: Already Logged in
        DashboardPage dashboardPage = new DashboardPage(driver);
        slowDown(); // Visibility for user
        slowDown(); // Visibility for user

        // Navigation
        dashboardPage.goToTransactions();
        slowDown();

        TransactionListPage listPage = new TransactionListPage(driver);
        listPage.clickNewDeposit();
        slowDown();

        // Form
        TransactionPage formPage = new TransactionPage(driver);
        // Using same IBAN as source for deposit (simulating funding own account)
        formPage.fillDepositForm("500", "4882064412973377", "Dépôt Automatisé");
        slowDown();

        formPage.submit();
        slowDown();

        Assert.assertTrue(formPage.isTransactionListDisplayed(), "Not redirected after deposit");
    }
}
