package com.jira.updater.lib;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalTime;
import java.util.Set;

/**
 * Created by sbt-velichko-aa on 29.02.2016.
 */
public class DriverExt {

    public void waitForPageToLoad(boolean... stopRecursion) {
        LocalTime timeOutWait = LocalTime.now().plusNanos(Init.getTimeOut());
        while (timeOutWait.getNano() > LocalTime.now().getNano()) {
            try {
                if (((JavascriptExecutor) Init.getDriver()).executeScript("return document.readyState")
                        .equals("complete"))
                    return;
            } catch (Exception | AssertionError e) {
                if ((stopRecursion.length == 0) || (stopRecursion.length > 0 && !stopRecursion[0]))
                    waitForPageToLoad(true);
            }
        }
    }

    public WebElement waitUntilElementAppearsInDom(By by) {
        Wait wait = new WebDriverWait(Init.getDriver(), Init.getTimeOutInSeconds());
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        return Init.getDriver().findElement(by);
    }

    public void waitForElementGetEnabled(WebElement element, long timeout) throws Exception {
        long deadLine = System.currentTimeMillis() + timeout;
        while (deadLine > System.currentTimeMillis()) {
            try {
                Thread.sleep(1000);
                if (element.isEnabled())
                    return;
            } catch (Exception e) {
                System.err.println("Target element still not enable. Erroe message = " + e.getMessage());
            }
        }
        throw new Exception("Timeout of waiting the element has expired. \nExpected element: " + element + ".");
    }

    public String findNewWindowHandle(Set<String> existingHandles, int milliSec) throws Exception {
        for (long timeOutTime = System.currentTimeMillis() + (long) milliSec; timeOutTime > System.currentTimeMillis();
             Thread.sleep(100L)) {
            Set<String> currentHandles = Init.getDriver().getWindowHandles();
            if (currentHandles.size() != existingHandles.size() || currentHandles.size() == existingHandles.size()
                    && !currentHandles.equals(existingHandles)) {
                for (String currentHandle : currentHandles) {
                    if (!existingHandles.contains(currentHandle))
                        return currentHandle;
                }
            }
        }
        throw new Exception("No modal window found");
    }


}
