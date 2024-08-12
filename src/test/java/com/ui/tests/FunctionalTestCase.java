package com.ui.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class FunctionalTestCase {

    public static void main(String[] args) throws IOException, InterruptedException {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            // Open the URL
            driver.get("https://demo.dealsdray.com/");

            // Login
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='username']")));
            usernameField.sendKeys("prexo.mis@dealsdray.com");

            WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
            passwordField.sendKeys("prexo.mis@dealsdray.com");

            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
            loginButton.click();

            // Navigate to Orders
            WebElement ordersMenu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'compactNavItem')]")));
            ordersMenu.click();

            WebElement ordersOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Orders']")));
            ordersOption.click();

            WebElement addBulkOrdersButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Add Bulk Orders']")));
            addBulkOrdersButton.click();

            // Upload the file
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='file']")));
            String filePath = "C:/Users/Dell/Downloads/demo-data.xlsx";
            fileInput.sendKeys(filePath);

            // Click Import
            WebElement importButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Import']")));
            importButton.click();

            // Click Validate
            WebElement validateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Validate Data']")));
            validateButton.click();

            // Handle the alert popup after clicking Validate
            try {
                WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(10));
                Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
                alert.accept(); // Click "OK" on the alert
            } catch (NoAlertPresentException e) {
                System.out.println("No alert found.");
            }

            // Scroll down to make sure the result is visible
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

            // Optionally, you can wait for some time to ensure the scrolling and rendering are complete
            Thread.sleep(2000); // Adjust the sleep time as needed

            // Take a screenshot of the final output
            takeScreenshot(driver, "FinalOutput");

        } finally {
            driver.quit();
        }
    }

    // Method to take a screenshot
    public static void takeScreenshot(WebDriver driver, String filename) throws IOException {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(srcFile, new File(System.getProperty("user.dir") + "/screenshots/" + filename + ".png"));
    }
}
