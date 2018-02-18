package com.jira.updater.lib;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.jira.updater.lib.util.AutotestError;
import com.jira.updater.lib.util.OsCheck;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by VelichkoAA on 12.01.2016.
 */
public class Init {

    private static final Map<String, Object> stash = new HashMap<>();
    private static WebDriver driver;
    private static String os = Props.get("automation.os");
    private static String testInstrument = Props.get("automation.instrument");
    private static String timeout = Props.get("webdriver.page.load.timeout");
    private static String webDriverPrefixPath = Props.get("webdriver.prefix.path");

    /**
     * Get web driver.
     *
     * @return
     */
    public static WebDriver getDriver() {
        if (null == driver) {
            if (os.equalsIgnoreCase("Android")
                    || os.equalsIgnoreCase("IOS")) {
                return getBrowserStackDriver();
            }

            try {
                createWebDriver();
            } catch (UnreachableBrowserException e) {
                System.err.println("Failed to create web driver" + e.getMessage());
                int i = 2;
                while (i > 0) {
                    dispose();
                    try {
                        createWebDriver();
                    } catch (UnreachableBrowserException e1) {
                        System.err.println("Failed to create web driver. Try count = " + i + ". Error message" + e1.getMessage());
                    }
                    i--;
                }
            }
        }
        return driver;
    }

    public static void setDriver(WebDriver driver) {
        Init.driver = driver;
    }

    public static void createWebDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
        File downloadDir = new File("download");
        OsCheck checkOS = new OsCheck();
        StringBuilder pathToDriver = new StringBuilder(webDriverPrefixPath);

        if (null == testInstrument) {
            testInstrument = "Firefox";
        }

        switch (BrowserEnum.valueOf(testInstrument)) {
            case Firefox:
                capabilities.setBrowserName("firefox");
                FirefoxProfile fProfile = new FirefoxProfile();
                fProfile.setAcceptUntrustedCertificates(true);
                fProfile.setPreference("browser.download.dir", downloadDir.getAbsolutePath());
                fProfile.setPreference("browser.download.folderList", 2);
                fProfile.setPreference("browser.download.manager.showWhenStarting", false);
                fProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
                fProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/plain");
                fProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                        "application/zip;application/octet-stream;application/x-zip;application/x-zip-compressed");
                fProfile.setPreference("plugin.disable_full_page_plugin_for_types", "application/zip");

                capabilities.setJavascriptEnabled(true);
                capabilities.setCapability(FirefoxDriver.PROFILE, fProfile);

                switch (checkOS.getOperationSystemType()) {
                    case MacOS:
                        pathToDriver.append("mac/geckodriver");
                        break;
                    case Windows:
                        pathToDriver.append("windows/geckodriver.exe");
                        break;
                    case Linux:
                        if (checkOS.getArchType().equals("64")) {
                            pathToDriver.append("linux64/geckodriver");
                        } else {
                            pathToDriver.append("linux32/geckodriver");
                        }
                        break;
                    default:
                        throw new AutotestError("Unsupported system. Os = " + System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH));
                }
                System.setProperty("webdriver.gecko.driver", new File(pathToDriver.toString()).getPath());
                setDriver(new FirefoxDriver(capabilities));
                break;
            case Safari:
                System.setProperty("webdriver.safari.noinstall", "true");
                capabilities.setBrowserName("safari");
                setDriver(new SafariDriver(capabilities));
                break;
            case Chrome:
                HashMap<String, Object> chromePrefs = new HashMap<>();
                chromePrefs.put("profile.default_content_settings.popups", 0);
                chromePrefs.put("download.default_directory", downloadDir.getAbsolutePath());
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("prefs", chromePrefs);
                options.addArguments("--dns-prefetch-disable");
                options.addArguments("--start-maximized");
                options.addArguments("test-type");
                capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                capabilities.setCapability("chrome.switches", Arrays.asList("--disk-cache-size=1",
                        "--media-cache-size=1"));

                switch (checkOS.getOperationSystemType()) {
                    case MacOS:
                        pathToDriver.append("mac/chromedriver");
                        break;
                    case Windows:
                        pathToDriver.append("windows/chromedriver.exe");
                        break;
                    case Linux:
                        if (checkOS.getArchType().equals("64")) {
                            pathToDriver.append("linux64/chromedriver");
                        } else {
                            pathToDriver.append("linux32/chromedriver");
                        }
                        break;
                    default:
                        throw new AutotestError("Unsupported system. Os = " + System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH));
                }
                System.setProperty("webdriver.chrome.driver", new File(pathToDriver.toString()).getAbsolutePath());
                capabilities.setBrowserName("chrome");
                setDriver(new ChromeDriver(capabilities));
                break;
            case PhantomJS:
                capabilities = DesiredCapabilities.phantomjs();
                ArrayList<String> cliArgsCap = new ArrayList<>();
                cliArgsCap.add("--web-security=false");
                cliArgsCap.add("--ssl-protocol=any");
                cliArgsCap.add("--ignore-ssl-errors=true");
                cliArgsCap.add("--webdriver-loglevel=INFO");
                cliArgsCap.add("--load-images=false");

                capabilities.setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS, true);
                capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);

                switch (checkOS.getOperationSystemType()) {
                    case Windows:
                        pathToDriver.append("windows/phantomjs.exe");
                        break;
                    case Linux:
                        if (checkOS.getArchType().equals("64")) {
                            pathToDriver.append("linux64/phantomjs");
                        } else {
                            pathToDriver.append("linux32/phantomjs");
                        }
                        break;
                    default:
                        throw new AutotestError("Unsupported system. Os = " + System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH));
                }
                File phantomDriver = new File(pathToDriver.toString());
                capabilities.setCapability(
                        PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                        phantomDriver.getAbsolutePath()
                );
                System.setProperty("phantomjs.binary.path", phantomDriver.getAbsolutePath());
                setDriver(new PhantomJSDriver(capabilities));
                break;
            default:
                capabilities.setBrowserName("firefox");
                setDriver(new FirefoxDriver(capabilities));
                break;
        }
        getDriver().manage().timeouts().implicitlyWait(Init.getTimeOutInSeconds(), TimeUnit.SECONDS);
        getDriver().manage().timeouts().pageLoadTimeout(Init.getTimeOutInSeconds(), TimeUnit.SECONDS);
        getDriver().manage().window().maximize();
    }

    public static void dispose() {
        if (os.equals("Android") || os.equals("IOS")) {
        } else {
            try {
                System.out.println("Checking any alert opened");
                WebDriverWait alertWait = new WebDriverWait(driver, 2);
                alertWait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                System.out.println("Got and alert: " + alert.getText() + "\n closing it.");
                alert.dismiss();
            } catch (Exception | Error e) {
                System.err.println("No alert opened. Closing webdriver.");
            }
            Set<String> windowsHandlerSet = Init.getDriver().getWindowHandles();
            try {
                if (windowsHandlerSet.size() > 2) {
                    windowsHandlerSet.forEach((winHandle) -> {
                        driver.switchTo().window(winHandle);
                        ((JavascriptExecutor) Init.getDriver()).executeScript(
                                "var objWin = window.self;"
                                + "objWin.open('', '_self', '');'"
                                + "objWin.close();");
                    });
                }

            } catch (Exception e) {
                System.err.println("Failed to kill all of the browser windows. Error message = " + e.getMessage());
            }
        }
        try {
            driver.quit();
        } catch (Exception | Error e) {
            System.err.println("Failed to quit web driver. Error message = " + e.getMessage());
        }
        setDriver(null);
    }

    private static WebDriver getBrowserStackDriver() {
        String browserStackUserName = "";
        String browserStackAccessKey = "";
        String url = "http://" + browserStackUserName + ":" + browserStackAccessKey + "@hub.browserstack.com/wd/hub";

        DesiredCapabilities caps = new DesiredCapabilities();
        if (os.equalsIgnoreCase("Android")) {
            caps.setCapability("browserName", "android");
            caps.setCapability("platform", "ANDROID");
        } else {
            caps.setCapability("browserName", "iPhone");
            caps.setPlatform(Platform.MAC);
        }

        caps.setCapability("device", testInstrument);
        try {
            driver = new RemoteWebDriver(new URL(url), caps);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return driver;
    }

    public static DriverExt getDriverExtensions() {
        return new DriverExt();
    }

    public static int getTimeOut() {
        return Integer.parseInt(timeout);
    }

    public static int getTimeOutInSeconds() {
        return Integer.parseInt(timeout) / 1000;
    }

    public static Map<String, Object> getStash() {
        return stash;
    }

    public static String getOs() {
        return os;
    }

    public static String getTestInstrument() {
        return testInstrument;
    }

}
