package com.jira.updater.worker;

import com.jira.updater.lib.Init;
import com.jira.updater.lib.Page;
import com.jira.updater.lib.Props;
import com.jira.updater.model.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by nestor on 17.02.2018.
 */
public class TaskUpdater extends Page {

    //    private String ticket;
//    private String status;
//    private String comment;
    private String idDivStatus = "customfield_10301-val";
    private String cssInputStatus = "input#customfield_10301";
    private String cssSubmitStatus = "form#customfield_10301-form button.submit";
    private String idAddComment = "footer-comment-button";
    private String cssComment = "textarea#comment";
    private String cssAddSubmitComment = "input#issue-comment-add-submit";

//    public TaskUpdater(String ticket, String status, String comment) {
//        this.ticket = ticket;
//        this.status = status;
//        this.comment = comment;
//    }
//
//    public TaskUpdater(Task task) {
//        this.ticket = task.getTicket();
//        this.status = task.getStatus();
//        this.comment = task.getComment();
//    }

    public void updateTicket(Task task) {
        synchronized (this) {
//        Init.getDriver().get(Props.get("applications.url") + "browse/" + ticket);
//        try {
//            Thread.sleep(50000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
            Init.getDriver().get(Props.get("applications.url") + "browse/" + task.getTicket());
            waitClickable(By.id(idDivStatus));
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
        }
    }

    private void waitClickable(By by) {
        new WebDriverWait(Init.getDriver(), Init.getTimeOutInSeconds()).
                until(ExpectedConditions.elementToBeClickable(by));
    }
}
