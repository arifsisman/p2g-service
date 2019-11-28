package vip.yazilim.p2g.web.config.spotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */

@EnableScheduling
@EnableAsync
@Configuration
public class TokenRefreshScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenRefreshScheduler.class);

//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10);
//        scheduler.initialize();
//
//        return scheduler;
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
//        LOGGER.error("An error occurred while scheduling.");
//        return null;
//    }

    private ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    private void initScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(100);
        scheduler.initialize();
    }

    public TaskScheduler getScheduler(){
        return scheduler;
    }


}
