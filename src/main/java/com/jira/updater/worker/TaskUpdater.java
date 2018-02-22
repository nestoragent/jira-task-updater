package com.jira.updater.worker;

import com.jira.updater.lib.Init;
import com.jira.updater.lib.Page;
import com.jira.updater.lib.Props;
import com.jira.updater.model.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by nestor on 17.02.2018.
 */
public class TaskUpdater extends Page {

    private int repeatCount = 0;
    private String idDivStatus = "customfield_10301-val";
    private String cssInputStatus = "input#customfield_10301";
    private String cssSubmitStatus = "form#customfield_10301-form button.submit";
    private String idAddComment = "footer-comment-button";
    private String cssComment = "textarea#comment";
    private String cssAddSubmitComment = "input#issue-comment-add-submit";
    private String idUsername = "username";
    private String idLoginSubmit = "login-submit";
    private String cssInputLoginMicrosoft = "input[name='loginfmt']";
    private String cssSubmitLoginMicrosoft = "input[type='submit']";
    private String cssInputPasswordMicrosoft = "input[name='passwd']";
    private String cssButtonPushButton = "div.push-label button.auth-button";
    private String idFrame = "duo_iframe";

    public void updateTicket(Task task) {
        synchronized (this) {
            try {
                Init.getDriver().get(Props.get("applications.url") + "browse/" + task.getTicket());
                waitVisible(By.id(idDivStatus));
                press_button(Init.getDriver().findElement(By.id(idDivStatus)));
                waitForVisibilityOf(By.cssSelector(cssInputStatus));
                fill_field(Init.getDriver().findElement(By.cssSelector(cssInputStatus)), task.getStatus());
                press_button(Init.getDriver().findElement(By.cssSelector(cssSubmitStatus)));

                //add comment
                press_button(Init.getDriver().findElement(By.id(idAddComment)));
                waitForVisibilityOf(By.cssSelector(cssInputStatus));
                fill_field(Init.getDriver().findElement(By.cssSelector(cssComment)), task.getComment());
                press_button(Init.getDriver().findElement(By.cssSelector(cssAddSubmitComment)));
                waitForVisibilityOf(By.id(idAddComment));
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println("ex: " + e);
                e.printStackTrace();
                if (repeatCount < 10) {
                    repeatCount++;
                    reInitSso(Props.get("applications.url") + "browse/" + task.getTicket());
                    updateTicket(task);
                }
            }
        }
    }

    private void waitClickable(By by) {
        new WebDriverWait(Init.getDriver(), Init.getTimeOutInSeconds()).
                until(ExpectedConditions.elementToBeClickable(by));
    }

    private void waitVisible(By by) {
        new WebDriverWait(Init.getDriver(), Init.getTimeOutInSeconds()).
                until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private void reInitSso(String url) {
        Init.dispose();
        Init.createWebDriver();
        Init.getDriver().get(url);
        try {
            waitForVisibilityOf(By.id(idUsername));
            fill_field(Init.getDriver().findElement(By.id(idUsername)), System.getProperty("login"));
            sleep(2);
            press_button(Init.getDriver().findElement(By.id(idLoginSubmit)));
            waitForVisibilityOf(By.cssSelector(cssInputLoginMicrosoft));
            fill_field(Init.getDriver().findElement(By.cssSelector(cssInputLoginMicrosoft)), System.getProperty("login"));
            sleep(2);
            press_button(Init.getDriver().findElement(By.cssSelector(cssSubmitLoginMicrosoft)));
            waitVisible(By.cssSelector(cssInputPasswordMicrosoft));
            sleep(2);
            fill_field(Init.getDriver().findElement(By.cssSelector(cssInputPasswordMicrosoft)), System.getProperty("password"));
            sleep(2);
            Init.getDriver().findElement(By.cssSelector(cssInputPasswordMicrosoft)).submit();

            sleep(10);
            waitVisible(By.id(idFrame));
            Init.getDriver().switchTo().frame(Init.getDriver().findElement(By.id(idFrame)));
            Init.getDriver().switchTo().activeElement();
            sleep(5);
            waitVisible(By.cssSelector(cssButtonPushButton));
//            press_button(Init.getDriver().findElement(By.cssSelector(cssButtonPushButton)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> pushes = Init.getDriver().findElements(By.cssSelector(cssButtonPushButton));
        int i = 0;
        while (!pushes.isEmpty() && i < 50) {
            press_button(pushes.get(0));
            try {
//                Thread.sleep(5 * 1000 * 60);
                Thread.sleep(50 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            pushes = Init.getDriver().findElements(By.cssSelector(cssButtonPushButton));
        }
        waitVisible(By.cssSelector(cssSubmitLoginMicrosoft));
        press_button(Init.getDriver().findElement(By.cssSelector(cssSubmitLoginMicrosoft)));
        try {
            sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
