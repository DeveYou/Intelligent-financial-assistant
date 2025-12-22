package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import com.ifa.tests.pages.LoginPage;
import com.ifa.tests.pages.TransactionListPage;
import com.ifa.tests.pages.TransactionPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WithdrawalTest extends BaseTest {

    @Test
    public void testCreateWithdrawal() {
        // Pre-requisite: Already Logged in
        DashboardPage dashboardPage = new DashboardPage(driver);
        slowDown();
        slowDown();

        // Navigation
        dashboardPage.goToTransactions();
        slowDown();

        TransactionListPage listPage = new TransactionListPage(driver);
        listPage.clickNewWithdrawal();
        slowDown();

        // Form
        TransactionPage formPage = new TransactionPage(driver);
        formPage.fillWithdrawalForm("100", "4882064412973377", "Retrait Automatisé");
        slowDown();

        formPage.submit();
        slowDown();

        Assert.assertTrue(formPage.isTransactionListDisplayed(), "Not redirected after withdrawal");
    }
}
