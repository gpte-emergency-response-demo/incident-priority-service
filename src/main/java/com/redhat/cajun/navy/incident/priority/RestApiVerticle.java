package com.redhat.cajun.navy.incident.priority;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;

public class RestApiVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return initializeHttpServer(config());
    }

    private Completable initializeHttpServer(JsonObject config) {

        Router router = Router.router(vertx);

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
                .register("health", f -> f.complete(Status.OK()));
        router.get("/health").handler(healthCheckHandler);

        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(config.getInteger("port", 8080))
                .ignoreElement();
    }
}
