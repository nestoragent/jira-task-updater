package com.jira.updater.consumer;

import com.jira.updater.model.Task;
import com.jira.updater.worker.TaskUpdater;

import java.util.concurrent.BlockingQueue;

/**
 * Created by nestor on 18.02.2018.
 */
public class JiraTaskConsumer implements Runnable {

    private BlockingQueue<Task> sharedQueue;
    private TaskUpdater taskUpdater;

    public JiraTaskConsumer(BlockingQueue<Task> sharedQueue) {
        this.sharedQueue = sharedQueue;
        taskUpdater = new TaskUpdater();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (sharedQueue.size() == 0)
                    Thread.sleep(10 * 1000);
                else {
                    Task task = sharedQueue.take();
                    System.out.println("Work with task: " + task);
                    taskUpdater.updateTicket(task);
                    System.out.println("Done task: " + task);
                }
            } catch (InterruptedException ex) {
                System.err.println(JiraTaskConsumer.class.getName());
            }
        }
    }


}
