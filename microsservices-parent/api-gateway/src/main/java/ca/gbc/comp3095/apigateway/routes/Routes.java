package ca.gbc.comp3095.apigateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@Slf4j
public class Routes {

    @Value("${service.event-url}")
    private String eventServiceUrl;

    @Value("${service.wellness-url}")
    private String wellnessResourceServiceUrl;

    @Value("${service.goal-url}")
    private String goalTrackingServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> eventServiceRoute() {
        return proxyRoute("event_service", "/api/events", eventServiceUrl);
    }

    @Bean
    public RouterFunction<ServerResponse> wellnessResourceRoute() {
        return proxyRoute("wellness_resource_service", "/api/resources", wellnessResourceServiceUrl);
    }

    @Bean
    public RouterFunction<ServerResponse> goalTrackingRoute() {
        return proxyRoute("goal_tracking_service", "/api/goals", goalTrackingServiceUrl);
    }

    private RouterFunction<ServerResponse> proxyRoute(String routeId, String basePath, String serviceUrl) {
        RequestPredicate predicate = RequestPredicates.path(basePath)
                .or(RequestPredicates.path(basePath + "/**"));

        return GatewayRouterFunctions.route(routeId)
                .route(predicate, request -> forwardRequest(routeId, serviceUrl, request))
                .build();
    }

    private ServerResponse forwardRequest(String routeId, String targetUrl, ServerRequest request) {
        log.info("Routing {} to {} for URI {}", routeId, targetUrl, request.uri());
        try {
            ServerResponse response = HandlerFunctions.http(targetUrl).handle(request);
            log.info("{} response status {}", routeId, response.statusCode());
            return response;
        } catch (Exception ex) {
            log.error("{} routing failure: {}", routeId, ex.getMessage());
            return ServerResponse.status(502).body("Gateway routing failure");
        }
    }
}
