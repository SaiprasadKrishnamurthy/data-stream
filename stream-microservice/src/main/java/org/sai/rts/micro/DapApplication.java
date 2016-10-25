package org.sai.rts.micro;

import akka.actor.ActorSystem;
import com.google.common.base.Predicates;
import org.sai.rts.micro.config.ActorFactory;
import org.sai.rts.micro.config.AppProperties;
import org.sai.rts.micro.es.ESInitializer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.inject.Inject;

/**
 * Created by saipkri on 07/09/16.
 */
@ComponentScan("org.sai.rts.micro")
@EnableAutoConfiguration
@EnableAsync
@PropertySource("classpath:application.properties")
@EnableSwagger2
@Configuration
public class DapApplication {

    @Inject
    private AppProperties appProperties;

    private ActorSystem actorSystem() {
        return ActorSystem.create("RtsActorSystem");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ActorFactory actorFactory() throws Exception {
        return new ActorFactory(actorSystem(), appProperties);
    }

    @Bean
    public ESInitializer esinit() throws Exception {
        return new ESInitializer(appProperties);
    }

    /**
     * Swagger 2 docket bean configuration.
     *
     * @return swagger 2 Docket.
     */
    @Bean
    public Docket configApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("config")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error"))) // Exclude Spring error controllers
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Realtime Data Streams REST API")
                .contact("sai@concordesearch.co.uk")
                .version("1.0")
                .build();
    }

    public static void main(String[] args) {
        SpringApplicationBuilder application = new SpringApplicationBuilder();
        application //
                .headless(true) //
                .addCommandLineProperties(true) //
                .sources(DapApplication.class) //
                .main(DapApplication.class) //
                .registerShutdownHook(true)
                .run(args);
    }


}
