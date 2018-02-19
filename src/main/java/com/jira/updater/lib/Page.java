package com.jira.updater.lib;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Created by VelichkoAA on 12.01.2016.
 */
public class Page {

    public static String returnItemFromStashIfExist(final String item) {
        if (item.contains("stash")) {
            String[] items = item.split("_");
            if (null != Init.getStash().get(items[1]))
                return Init.getStash().get(items[1]).toString();
        }
        return item;
    }

    protected void waitForVisibilityOf(By locator) {
        List<WebElement> elements = Init.getDriver().findElements(locator);
        int i = 0;
        boolean isVisible = false;
        while (i < 10
                && elements.size() < 0
                && !isVisible) {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            elements = Init.getDriver().findElements(locator);
            if (elements.size() > 0 && elements.get(0).isDisplayed()) {
                isVisible = true;
            }
            i++;
        }
    }

    public void press_button(WebElement webElement) {
        System.out.println("Pressed button title = " + webElement + ".");
        webElement.click();
    }

    public void fill_field(WebElement element, String text) {
        if (null != text) {
            System.out.println("Fill field " + element +
                    ", with text = " + text);
            try {
                element.click();
            } catch (WebDriverException e) {
                System.err.println("Error when try to click on element " + element + ". Error - " + e);
            }
            try {
                element.clear();
            } catch (InvalidElementStateException e) {
                System.err.println("Failed to clear web element. Error message = " + e.getMessage());
            }
            element.sendKeys(text);
        } else
            System.err.println("For field " + element + " text == null.");
    }

    public void sleep(int i) throws InterruptedException {
        Thread.sleep(i * 1000);
    }

    public void scrollTo(WebElement element) throws InterruptedException {
        ((JavascriptExecutor) Init.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(1000);
    }

    public void scrollToActions(WebElement element) throws InterruptedException {
        new Actions(Init.getDriver()).moveToElement(element).build().perform();
        Thread.sleep(1000);
    }

    public void scrollDown() throws InterruptedException {
        ((JavascriptExecutor) Init.getDriver()).executeScript("window.scrollBy(0,250)", "");
    }

    public void scrollDown(String pix) throws InterruptedException {
        ((JavascriptExecutor) Init.getDriver()).executeScript("window.scrollBy(0," + pix + ")", "");
        Thread.sleep(500);
    }

    public void scrollUp() throws InterruptedException {
        ((JavascriptExecutor) Init.getDriver()).executeScript("window.scrollBy(0,-250)", "");
    }

    public void press_key(String keyName) {
        Keys key;
        try {
            key = Keys.valueOf(keyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Can't click button: " + keyName, e);
        }
        Actions actions = new Actions(Init.getDriver());
        actions.sendKeys(key).perform();
    }

    public void press_button_by_js(WebElement element) {
        ((JavascriptExecutor) Init.getDriver()).executeScript("arguments[0].click();", element);
    }

}
