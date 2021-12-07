package com.servicePoller.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  final JsonObject loadedConfig = new JsonObject();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    doConfig()
      .compose(this::storeConfig)
      .compose(this::deployVerticles)
      .onSuccess(startPromise::complete)
      .onFailure(startPromise::fail);
  }

  Future<JsonObject> doConfig() {
    ConfigStoreOptions defaultOptions = new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path", "src/main/resources/config.json"));

    ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
      .addStore(defaultOptions);

    ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);

    return Future.future(configRetriever::getConfig);
  }

  Future<Void> storeConfig(JsonObject config) {
    loadedConfig.mergeIn(config);
    return Future.succeededFuture();
  }

  Future<Void> deployVerticles(Void unused) {
    DeploymentOptions options = new DeploymentOptions().setConfig(loadedConfig);

    Future<String>  apiVerticle = Future.future(promise -> vertx.deployVerticle(new ApiVerticle(), options, promise));
    Future<String>  dataBaseVerticle = Future.future(promise -> vertx.deployVerticle(new DataBaseVerticle(), options, promise));
    Future<String>  pollingVerticle = Future.future(promise -> vertx.deployVerticle(new PollingVerticle(), options, promise));

    return CompositeFuture.all(apiVerticle, dataBaseVerticle, pollingVerticle).mapEmpty();
  }
}
