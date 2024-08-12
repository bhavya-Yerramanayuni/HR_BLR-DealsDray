package com.ui.tests;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class UiTesting {

    public static void main(String[] args) throws IOException {
        // Fetch URLs from the sitemap
        List<String> urls = fetchUrlsFromSitemap("https://www.getcalley.com/page-sitemap.xml", 5);

        // Resolutions to test
        int[][] resolutions = {
                {1920, 1080}, {1366, 768}, {1536, 864}, // Desktop resolutions
                {360, 640}, {414, 896}, {375, 667}     // Mobile resolutions
        };

        // Test in Chrome
        WebDriver chromeDriver = setupWebDriver("chrome");
        runTests(chromeDriver, urls, resolutions, "Chrome");

        // Test in Firefox
        WebDriver firefoxDriver = setupWebDriver("firefox");
        runTests(firefoxDriver, urls, resolutions, "Firefox");

        // Test in Safari
        WebDriver safariDriver = setupWebDriver("safari");
        runTests(safariDriver, urls, resolutions, "Safari");
    }

    // Fetch URLs from the sitemap
    public static List<String> fetchUrlsFromSitemap(String sitemapUrl, int limit) throws IOException {
        List<String> urls = new ArrayList<>();
        URL url = new URL(sitemapUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.contains("<loc>")) {
                    String extractedUrl = line.split("<loc>")[1].split("</loc>")[0];
                    urls.add(extractedUrl);
                    if (urls.size() == limit) break;
                }
            }
        }

        return urls;
    }

    // Setup WebDriver based on the browser type
    public static WebDriver setupWebDriver(String browser) {
        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "safari":
                driver = new SafariDriver();
                break;
            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    // Run tests for each URL and resolution
    public static void runTests(WebDriver driver, List<String> urls, int[][] resolutions, String browserName) throws IOException {
        for (String url : urls) {
            for (int[] resolution : resolutions) {
                String deviceType = resolution[0] > 768 ? "Desktop" : "Mobile";
                driver.manage().window().setSize(new Dimension(resolution[0], resolution[1]));
                driver.get(url);
                takeScreenshot(driver, browserName, deviceType, resolution);
            }
        }
        driver.quit();
    }

    // Capture and save a screenshot
    public static void takeScreenshot(WebDriver driver, String browser, String device, int[] resolution) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String folderPath = device + "/" + resolution[0] + "x" + resolution[1];
        new File(folderPath).mkdirs(); // Ensure the directory exists
        FileUtils.copyFile(srcFile, new File(folderPath + "/" + browser + "_Screenshot_" + timestamp + ".png"));
    }
}
