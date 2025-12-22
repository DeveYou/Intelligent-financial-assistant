package com.ifa.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import java.time.Duration;

public class TransactionListPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Buttons - Using partial text to avoid accents and encoding issues
    private By newDepositButton = By.xpath("//button[contains(., 'Nouveau D')]");
    private By newWithdrawalButton = By.xpath("//button[contains(., 'Nouveau R')]");
    private By newTransferButton = By.xpath("//button[contains(., 'Nouveau V')]");

    // Filters
    private By referenceFilterInput = By.cssSelector("input[placeholder*='Référence']");
    private By searchButton = By.xpath("//button[contains(., 'Recherch')]"); // Approx selector based on user request

    // Table
    private By transactionTable = By.className("users-table");
    private By loadingSpinner = By.className("loading-container");

    public TransactionListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void waitForSpinner() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinner));
        } catch (Exception e) {
            // Context: Spinner might not appear if load is instant, or timeout if stuck.
            // Proceeding.
        }
    }

    public void clickNewDeposit() {
        waitForSpinner();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(newDepositButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void clickNewWithdrawal() {
        waitForSpinner();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(newWithdrawalButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void clickNewTransfer() {
        waitForSpinner();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(newTransferButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void filterByReference(String reference) {
        waitForSpinner();
        wait.until(ExpectedConditions.visibilityOfElementLocated(referenceFilterInput)).sendKeys(reference);
    }

    public void clickSearch() {
        waitForSpinner();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean isTransactionDisplayed(String reference) {
        try {
            return wait
                    .until(ExpectedConditions
                            .visibilityOfElementLocated(By.xpath("//td[contains(text(), '" + reference + "')]")))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
