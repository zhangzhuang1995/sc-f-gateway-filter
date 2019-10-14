package com.forezp.gateway;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ZhangZhuang
 * @date 2019/10/14
 * @description 全局过滤器
 */
public class TokenFilter implements GlobalFilter, Ordered {
    /**
     * Spring Cloud Gateway根据作用范围划分为GatewayFilter和GlobalFilter，二者区别如下：
     * <p>
     * GatewayFilter : 需要通过spring.cloud.routes.filters 配置在具体路由下，
     * 只作用在当前路由上或通过spring.cloud.default-filters配置在全局，作用在所有路由上
     * GlobalFilter : 全局过滤器，不需要在配置文件中配置，作用在所有的路由上，
     * 最终通过GatewayFilterAdapter包装成GatewayFilterChain可识别的过滤器，
     * 它为请求业务以及路由的URI转换为真实业务服务的请求地址的核心过滤器，不需要配置，系统初始化时加载，并作用在每个路由上。
     */
    private static Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    /**
     * 该GlobalFilter会校验请求中是否包含了请求参数“token”
     * curl localhost:8081/customer/123
     * curl localhost:8081/customer/123?token=123456
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if (StringUtils.isBlank(token)) {
            logger.info("token is empty...");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
