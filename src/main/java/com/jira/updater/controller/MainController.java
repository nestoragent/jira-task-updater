package com.jira.updater.controller;

import com.jira.updater.lib.Init;
import com.jira.updater.model.Task;
import com.jira.updater.model.User;
import com.jira.updater.consumer.JiraTaskConsumer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nestor on 08.11.2016.
 */
@Controller
public class MainController {

    private BlockingQueue<Task> sharedQueue = new LinkedBlockingQueue<Task>();
    {
        Thread consThread = new Thread(new JiraTaskConsumer(sharedQueue));
        consThread.start();
    }

    /*First method on start application*/
    /*Попадаем сюда на старте приложения (см. параметры аннотации и настройки пути после деплоя) */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userJSP", new User());
        modelAndView.setViewName("index");
        return modelAndView;
    }

    /*как только на index.jsp подтвердится форма
    <spring:form method="post"  modelAttribute="userJSP" action="check-user">,
    то попадем вот сюда
     */
    @RequestMapping(value = "/check-user")
    public ModelAndView checkUser(@ModelAttribute("userJSP") User user) {
        ModelAndView modelAndView = new ModelAndView();

        //имя представления, куда нужно будет перейти
        modelAndView.setViewName("secondPage");

        //записываем в атрибут userJSP (используется на странице *.jsp объект user
        modelAndView.addObject("userJSP", user);


        return modelAndView; //после уйдем на представление, указанное чуть выше, если оно будет найдено.
    }

    /*как только на index.jsp подтвердится форма
    <spring:form method="post"  modelAttribute="userJSP" action="check-user">,
    то попадем вот сюда
    */
    @RequestMapping(value = "/updateTask", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView updateTask(@RequestParam String ticketNumber, @RequestParam String status,
                           @RequestParam String comment) throws InterruptedException {
        ModelAndView modelAndView = new ModelAndView();
        Task task = new Task();
        task.setTicket(ticketNumber);
        task.setStatus(status);
        task.setComment(comment);
        sharedQueue.put(task);
        System.out.println("put to queue: " + task);
        modelAndView.setViewName("initPass");
        modelAndView.addObject("initStatus", "sucess");
        return modelAndView;
    }

    @RequestMapping(value = "/init")
    public ModelAndView iniBrowser() {
        ModelAndView modelAndView = new ModelAndView();
        Init.getDriver();
//        jiraTaskProducer = new JiraTaskConsumer(sharedQueue);
        modelAndView.setViewName("initPass");
        modelAndView.addObject("initStatus", "sucess");
        return modelAndView;
    }
}
