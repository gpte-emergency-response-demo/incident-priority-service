package com.redhat.cajun.navy.incident.priority;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.vertx.MetricsHandler;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class RestApiVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return initializeHttpServer(config());
    }

    private Completable initializeHttpServer(JsonObject config) {

        Router router = Router.router(vertx);

        // Metrics
        MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate("prometheus");
        CollectorRegistry registry = CollectorRegistry.defaultRegistry;
        registry.register(new DropwizardExports(metricRegistry));
        new StandardExports().register(registry);
        new MemoryPoolsExports().register(registry);
        router.get("/metrics").handler(event -> new MetricsHandler().handle(event.getDelegate()));

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
                .register("health", f -> f.complete(Status.OK()));
        router.get("/health").handler(healthCheckHandler);
        router.get("/priority/:incidentId").handler(this::priority);

        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(config.getInteger("port", 8080))
                .ignoreElement();
    }

    private void priority(RoutingContext rc) {
        String incidentId = rc.request().getParam("incidentId");
        vertx.eventBus().rxSend("incident-priority", new JsonObject().put("incidentId", incidentId))
                .subscribe((json) -> rc.response().setStatusCode(200)
                                .putHeader("content-type", "application/json")
                                .end(json.body().toString()),
                        rc::fail);
    }
}
