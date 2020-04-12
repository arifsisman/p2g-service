package vip.yazilim.p2g.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;

@EnableScheduling
@EnableAsync
@Configuration
public class OnlineStatusScheduler {

    private ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    private void initScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
    }

    public TaskScheduler getScheduler() {
        return scheduler;
    }

}
