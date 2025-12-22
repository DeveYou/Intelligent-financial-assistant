package com.ifa.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Selectors based on admin-layout and account-list
    private By userMenuButton = By.xpath("//button[.//mat-icon[contains(text(), 'account_circle')]]");
    // Note: The menu items effectively appear in a CDK overlay container
    private By logoutButton = By.xpath("//button[contains(., 'Déconnexion')]");
    private By pageTitle = By.className("page-title");

    // Navigation
    private By transactionsLink = By.xpath("//a[contains(@href, '/admin/transactions')]");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.urlContains("/admin"));
    }

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
    }

    public void logout() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement menuBtn = wait.until(ExpectedConditions.elementToBeClickable(userMenuButton));
        menuBtn.click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // Ensure the menu open animation completes or the element is fully visible
        WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(logoutButton));
        executor.executeScript("arguments[0].click();", logoutBtn);
    }

    public void goToTransactions() {
        wait.until(ExpectedConditions.elementToBeClickable(transactionsLink)).click();
    }

    public void goToAccounts() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/accounts')]")))
                .click();
    }

    public void goToUsers() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/admin/users')]")))
                .click();
    }

    public void goToDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[span[contains(text(), 'Dashboard')]]")))
                .click();
    }
}
