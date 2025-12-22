package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import com.ifa.tests.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(priority = 1)
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo("http://localhost:4200/auth/login");

        loginPage.login("wrong@example.com", "wrongpassword");

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
    }

    @Test(priority = 2)
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        // Navigate only if not already there (though invalid login likely leaves us
        // there)
        loginPage.navigateTo("http://localhost:4200/auth/login");

        loginPage.login("red.aitsaid02@gmail.com", "123456");

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard did not load after login");
    }
}
