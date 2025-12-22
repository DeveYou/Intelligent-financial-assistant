package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    @Test
    public void testLogout() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.logout();
        slowDown();
    }
}
