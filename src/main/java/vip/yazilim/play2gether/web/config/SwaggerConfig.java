package vip.yazilim.play2gether.web.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.maemresen.senior.webapp.rest"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {

        Contact contact = new Contact("Emre Sen", "https://www.linkedin.com/in/maemresen/", "maemresen07@gmail.com");
        return new ApiInfoBuilder()
                .title("Spring Boot Swagger 2")
                .description("Spring Boot Swagger API Example")
                .version("1.0.0")
                .contact(contact)
                .build();
    }
}
