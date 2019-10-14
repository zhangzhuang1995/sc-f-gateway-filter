package com.forezp;

import com.forezp.gateway.RequestTimeFilter;
import com.forezp.gateway.RequestTimeGatewayFilterFactory;
import com.forezp.gateway.TokenFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ScFGatewayFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScFGatewayFilterApplication.class, args);
    }

    /**
     * 由filter工作流程点，可以知道filter有着非常重要的作用，在“pre”类型的过滤器可以做参数校验、权限校验、流量监控、日志输出、
     * 协议转换等，在“post”类型的过滤器中可以做响应内容、响应头的修改，日志的输出，流量监控等。
     * <p>
     * 与zuul不同的是，filter除了分为“pre”和“post”两种方式的filter外，在Spring Cloud Gateway中，filter从作用范围可分为另外两种，
     * 一种是针对于单个路由的gateway filter，它在配置文件中的写法同predict类似；另外一种是针对于所有路由的global gateway filer。
     * <p>
     * 过滤器允许以某种方式修改传入的HTTP请求或传出的HTTP响应。过滤器可以限定作用在某些特定请求路径上。
     * Spring Cloud Gateway包含许多内置的GatewayFilter工厂。
     * GatewayFilter工厂同上一篇介绍的Predicate工厂类似，都是在配置文件application.yml中配置，
     * 遵循了约定大于配置的思想，只需要在配置文件配置GatewayFilter Factory的名称，
     * 而不需要写全部的类名，比如AddRequestHeaderGatewayFilterFactory只需要在配置文件中写AddRequestHeader，
     * 而不是全部类名。在配置文件中配置的GatewayFilter Factory最终都会相应的过滤器工厂类处理。
     * filter.png为Spring Cloud Gateway 内置的过滤器工厂
     */


    /**
     * 将该过滤器注册到router中
     * curl localhost:8081/customer/123 输出/customer/123:627ms
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/customer/**")
                        .filters(f -> f.filter(new RequestTimeFilter())
                                .addRequestHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri("http://httpbin.org:80/get")
                        .order(0)
                        .id("customer_filter_router")
                ).build();
    }

    /**
     * 向Srping Ioc容器注册RequestTimeGatewayFilterFactory类的Bean。
     *
     * @return
     */
    @Bean
    public RequestTimeGatewayFilterFactory elapsedGatewayFilterFactory() {
        return new RequestTimeGatewayFilterFactory();
    }

    /**
     * TokenFilter在工程的启动类中注入到Spring Ioc容器中
     *
     * @return
     */
    @Bean
    public TokenFilter tokenFilter() {
        return new TokenFilter();
    }
}
