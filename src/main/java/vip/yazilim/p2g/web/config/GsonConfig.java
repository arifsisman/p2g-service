package vip.yazilim.p2g.web.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mustafaarifsisman - 29.05.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class GsonConfig {

    private Gson gson = new GsonBuilder().create();

    @Bean
    public Gson getGson() {
        return gson;
    }
}
