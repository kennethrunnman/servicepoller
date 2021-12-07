package com.servicePoller.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLException;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import org.infinispan.commons.dataconversion.internal.Json;

public class DataBaseVerticle extends AbstractVerticle {

  public static final String LIST_ALL_SERVICES_ADDR = "com.servicePoller.verticles.list_all_services";
  public static final String GET_SERVICE_BY_ID_ADDR = "com.servicePoller.verticles.get_service_by_id";
  public static final String REMOVE_SERVICE_BY_ID_ADDR = "com.servicePoller.verticles.remove_service_by_id";
  public static final String ADD_SERVICE_ADDR = "com.servicePoller.verticles.add_service";

  public static final String LIST_ALL_SERVICES = "SELECT * FROM services";
  public static final String GET_SERVICE_BY_ID = "SELECT * FROM services WHERE id = ?";
  public static final String REMOVE_SERVICE_BY_ID = "DELETE FROM services WHERE id = ?";
  public static final String ADD_SERVICE = "INSERT INTO services (name, url, status) VALUES (?, ?, 'OK')";

  MySQLPool client;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
      configSqlClient()
      .compose(this::configEventBusConsumers)
      .onSuccess(startPromise::complete)
      .onFailure(startPromise::fail);
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    client.close();
    stopPromise.complete();
  }

  Future<Void> configEventBusConsumers(Void unused) {
    vertx.eventBus().consumer(LIST_ALL_SERVICES_ADDR).handler(this::listAllServices);
    vertx.eventBus().consumer(GET_SERVICE_BY_ID_ADDR).handler(this::getServiceById);
    vertx.eventBus().consumer(REMOVE_SERVICE_BY_ID_ADDR).handler(this::removeServiceById);
    vertx.eventBus().consumer(ADD_SERVICE_ADDR).handler(this::addService);
    return Future.succeededFuture();
  }

  void listAllServices(Message<Object> msg) {
    client.query(LIST_ALL_SERVICES).execute()
      .compose(this::mapToJsonArray)
      .onComplete(res -> {
        if(res.succeeded()){
          msg.reply(res.result());
        } else {
          msg.fail(500, res.cause().getLocalizedMessage());
        }
      });
  }

  void getServiceById(Message<Object> msg) {
    if(msg.body() instanceof String) {
      String id = (String)msg.body();
      Tuple params = Tuple.of(id);
      preparedQuery(GET_SERVICE_BY_ID, params)
        .compose(this::mapToFirstResult)
        .onComplete(res -> {
          if (res.succeeded()) {
            msg.reply(res.result());
          } else {
            msg.fail(500, res.cause().getLocalizedMessage());
          }
        });
    } else {
      msg.fail(400, "Bad Request, no Id");
    }
  }

  void removeServiceById(Message<Object> msg) {
    if(msg.body() instanceof String) {
      String id = (String)msg.body();
      Tuple params = Tuple.of(id);
      preparedQuery(REMOVE_SERVICE_BY_ID, params)
        .onComplete(res -> {
          if (res.succeeded()) {
            msg.reply(String.format("Service %s deleted from DB", id));
          } else {
            msg.fail(500, res.cause().getLocalizedMessage());
          }
        });
    } else {
      msg.fail(400, "Bad Request, no Id");
    }
  }

  void addService(Message<Object> msg) {
    if(msg.body() instanceof String && ((String) msg.body()).length() > 0) {
      Json body = Json.read((String) msg.body());
      Tuple params = Tuple.of(
        body.at("name").toString().replace("\"", ""),
        body.at("url").toString().replace("\"", ""));
      preparedQuery(ADD_SERVICE, params)
        .onComplete(res -> {
        if (res.succeeded()) {
          msg.reply("Successfully added the service to the DB");
        } else {
          msg.fail(500, res.cause().getLocalizedMessage());
        }
      });
    } else {
      msg.fail(400, "Bad Request, no Params");
    }
  }

  Future<Void> configSqlClient() {
    JsonObject dbConfig = config().getJsonObject("db");
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(dbConfig.getInteger("port"))
      .setHost(dbConfig.getString("host"))
      .setDatabase(dbConfig.getString("db-name"))
      .setUser(dbConfig.getString("user"))
      .setPassword(dbConfig.getString("password"));


    // Pool options
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    // Create the client pool
    try {
      client = MySQLPool.pool(vertx, connectOptions, poolOptions);
    } catch (MySQLException e){
      return Future.failedFuture(e.getCause().getLocalizedMessage());
    }
    return Future.succeededFuture();
  }

  Future<RowSet<Row>> preparedQuery(String query, Tuple params) {
    return client.preparedQuery(query).execute(params);
  }

  Future<String> mapToFirstResult(RowSet<Row> rs) {
    if(rs.size() >= 1) {
      return Future.succeededFuture(rs.iterator().next().toJson().toString());
    } else {
      return Future.failedFuture("No Result");
    }
  }

  Future<String> mapToJsonArray(RowSet<Row> rs) {
    RowIterator<Row> iterator = rs.iterator();
    JsonArray jsonArray = new JsonArray();
    while(iterator.hasNext()) {
      jsonArray.add(iterator.next().toJson());
    }
    return Future.succeededFuture(jsonArray.toString());
  }
}
