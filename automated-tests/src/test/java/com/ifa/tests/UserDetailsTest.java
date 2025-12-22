package com.ifa.tests;

import com.ifa.tests.pages.DashboardPage;
import com.ifa.tests.pages.UserListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserDetailsTest extends BaseTest {

    @Test
    public void testViewUserDetails() {
        // Pre-requisite: Already Logged in
        DashboardPage dashboardPage = new DashboardPage(driver);
        slowDown();

        // 1. Navigate to Users List
        dashboardPage.goToUsers();
        UserListPage userListPage = new UserListPage(driver);
        Assert.assertTrue(userListPage.isPageLoaded(), "Failed to load User List page");
        slowDown();

        // 2. Click Actions Menu on the first row
        userListPage.clickActionsMenu();
        slowDown();

        // 3. Click "Voir détails"
        userListPage.clickViewDetails();
        slowDown();

        // 4. Verify Redirection to Details Page
        // URL pattern should be like /admin/users/{id}
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/admin/users/"),
                "URL does not contain /admin/users/. Current URL: " + currentUrl);

        // Ensure it's not staying on the list page (list page is just /admin/users)
        // A detail page usually has an ID, so length > base length, or contains a
        // digit/UUID
        // However, checking for trailing slash or ID is safer.
        // Let's assume the ID is appended.
        Assert.assertNotEquals(currentUrl, "http://localhost:4200/admin/users",
                "Did not navigate to specific user details");
    }
}
