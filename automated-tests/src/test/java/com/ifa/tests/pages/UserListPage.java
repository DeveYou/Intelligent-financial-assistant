package com.ifa.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class UserListPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Selectors
    // We target the first available "more_vert" button in the table body
    private By firstRowActionMenuButton = By.xpath(
            "//table[contains(@class, 'users-table')]//tbody//tr[1]//button[.//mat-icon[contains(text(), 'more_vert')]]");

    // The "Voir détails" button appears in the overlay menu
    private By viewDetailsButton = By.xpath("//button[@role='menuitem'][.//span[contains(text(), 'Voir détails')]]");

    public UserListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickActionsMenu() {
        // Wait for table to load rows
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("users-table")));

        WebElement menuBtn = wait.until(ExpectedConditions.elementToBeClickable(firstRowActionMenuButton));
        menuBtn.click();
    }

    public void clickViewDetails() {
        WebElement detailsBtn = wait.until(ExpectedConditions.elementToBeClickable(viewDetailsButton));
        detailsBtn.click();
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.urlContains("/admin/users"));
    }
}
