package com.ifa.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TransactionPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Selectors
    private By amountInput = By.cssSelector("input[formControlName='amount']");
    private By sourceIbanInput = By.cssSelector("input[formControlName='sourceIban']");
    private By recipientIbanInput = By.cssSelector("input[formControlName='recipientIban']");
    private By reasonInput = By.cssSelector("textarea[formControlName='reason']");
    private By submitButton = By.cssSelector("button[type='submit']");

    // Selectors for Transaction List (to verify redirection)
    private By transactionListHeader = By.xpath("//h1[contains(text(), 'Transactions')]");

    public TransactionPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void fillTransferForm(String amount, String sourceIban, String recipientIban, String reason) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput)).clear();
        driver.findElement(amountInput).sendKeys(amount);
        driver.findElement(sourceIbanInput).clear();
        driver.findElement(sourceIbanInput).sendKeys(sourceIban);
        driver.findElement(recipientIbanInput).clear();
        driver.findElement(recipientIbanInput).sendKeys(recipientIban);
        driver.findElement(reasonInput).clear();
        driver.findElement(reasonInput).sendKeys(reason);
    }

    public void fillDepositForm(String amount, String sourceIban, String reason) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput)).clear();
        driver.findElement(amountInput).sendKeys(amount);
        driver.findElement(sourceIbanInput).clear();
        driver.findElement(sourceIbanInput).sendKeys(sourceIban);
        driver.findElement(reasonInput).clear();
        driver.findElement(reasonInput).sendKeys(reason);
    }

    public void fillWithdrawalForm(String amount, String sourceIban, String reason) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput)).clear();
        driver.findElement(amountInput).sendKeys(amount);
        driver.findElement(sourceIbanInput).clear();
        driver.findElement(sourceIbanInput).sendKeys(sourceIban);
        driver.findElement(reasonInput).clear();
        driver.findElement(reasonInput).sendKeys(reason);
    }

    public void submit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    public boolean isTransactionListDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(transactionListHeader)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
