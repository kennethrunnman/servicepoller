package com.servicePoller.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;

public class ApiVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    configRouter()
      .compose(this::startHttpServer)
      .onSuccess(success -> startPromise.complete())
      .onFailure(startPromise::fail);
  }

  Future<Router> configRouter() {
    Router router = Router.router(vertx);
    router.route().handler(LoggerHandler.create());
    router.route().handler(CorsHandler.create("http://localhost:3000")
      .allowedMethod(HttpMethod.DELETE)
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST));
    router.route().handler(BodyHandler.create());
    router.get("/api/v1/services").blockingHandler(this::getServices);
    router.get("/api/v1/services/:id").blockingHandler(this::getService);
    router.delete("/api/v1/services/:id").blockingHandler(this::removeService);
    router.post("/api/v1/services").blockingHandler(this::addService);
    return Future.succeededFuture(router);
  }

  void getServices(RoutingContext ctx) {
    vertx.eventBus().request(DataBaseVerticle.LIST_ALL_SERVICES_ADDR, "", reply ->
      ctx.request().response().end((String)reply.result().body()));
  }

  void getService(RoutingContext ctx) {
    String id = ctx.pathParam("id").replace(":", "");
    vertx.eventBus().request(DataBaseVerticle.GET_SERVICE_BY_ID_ADDR, id, reply ->
      ctx.request().response().end((String)reply.result().body()));
  }

  void removeService(RoutingContext ctx) {
    String id= ctx.pathParam("id").replace(":", "");
    vertx.eventBus().request(DataBaseVerticle.REMOVE_SERVICE_BY_ID_ADDR, id, reply ->
      ctx.request().response().end((String)reply.result().body()));
  }

  void addService(RoutingContext ctx) {
    JsonObject body= ctx.getBodyAsJson();
    String msgBody = body.toString();
    vertx.eventBus().request(DataBaseVerticle.ADD_SERVICE_ADDR, msgBody, reply -> ctx.request().response().end((String)reply.result().body()));
  }

  Future<HttpServer> startHttpServer(Router router) {
    JsonObject httpConfig = config().getJsonObject("http");
    int httpPort = httpConfig.getInteger("port");
    HttpServer server = vertx.createHttpServer().requestHandler(router);
    return Future.future(promise -> server.listen(httpPort, promise));
  }
}
