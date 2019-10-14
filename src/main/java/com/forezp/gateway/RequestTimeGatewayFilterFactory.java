package com.forezp.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author ZhangZhuang
 * @date 2019/10/14
 * @description 自定义过滤器工厂类，这样就可以在配置文件中配置过滤器了
 */
public class RequestTimeGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestTimeGatewayFilterFactory.Config> {
    private static Logger logger = LoggerFactory.getLogger(GatewayFilter.class);
    private final static String REQUEST_TIME_BEGIN = "requestTimeBegin";
    private static final String KEY = "withParams";

    public RequestTimeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(KEY);
    }

    /**
     * apply(Config config)方法内创建了一个GatewayFilter的匿名类，具体的实现逻辑跟之前一样，只不过加了是否打印请求参数的逻辑，
     * 而这个逻辑的开关是config.isWithParams()。静态内部类类Config就是为了接收那个boolean类型的参数服务的，
     * 里边的变量名可以随意写，但是要重写List shortcutFieldOrder()这个方法。
     *
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getAttributes().put(REQUEST_TIME_BEGIN, System.currentTimeMillis());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
                if (startTime != null) {
                    StringBuilder sb = new StringBuilder(exchange.getRequest().getURI().getRawPath()).
                            append(":")
                            .append(System.currentTimeMillis() - startTime)
                            .append("ms");
                    if (config.isWithParams()) {
                        sb.append(" params").append(exchange.getRequest().getQueryParams());
                    }
                    logger.info(sb.toString());
                }
            }));
        };
    }

    public static class Config {
        private boolean withParams;

        public boolean isWithParams() {
            return withParams;
        }

        public void setWithParams(boolean withParams) {
            this.withParams = withParams;
        }
    }
}
