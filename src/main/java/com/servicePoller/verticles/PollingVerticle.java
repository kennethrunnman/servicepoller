package com.servicePoller.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLException;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class PollingVerticle extends AbstractVerticle {

  public static final String UPDATE_SERVICE_STATUS = "UPDATE services SET status = ? WHERE id = ?";

  WebClient webClient;
  MySQLPool sqlPool;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.setPeriodic(1000 * 15, x -> configWebClient()
      .compose(this::configSQLPool)
      .compose(this::getServices)
      .onSuccess(rs -> rs.forEach(row -> pollService(row)
        .onSuccess(
          response -> {
            if (response.statusCode() == 200) {
              updateServiceStatus(row.getInteger("id"), "OK");
            } else {
              updateServiceStatus(row.getInteger("id"), "FAIL");
            }
          }
        )
        .onFailure(
          error -> updateServiceStatus(row.getInteger("id"), "FAIL")
        )
      )
      )
      );
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    sqlPool.close();
    stopPromise.complete();
  }

  Future<HttpResponse<Buffer>> pollService(Row row) {
    String url = row.getString("url");
    return webClient.getAbs(url)
      .timeout(5000)
      .send();
  }

  Future<Void> configWebClient() {
    WebClientOptions options = new WebClientOptions()
      .setUserAgent("ServicePoller/1.0");
    webClient = WebClient.create(vertx, options);
    return Future.succeededFuture();
  }

  Future<Void> configSQLPool(Void unused) {
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
      sqlPool = MySQLPool.pool(vertx, connectOptions, poolOptions);
    } catch (MySQLException e){
      return Future.failedFuture(e.getCause().getLocalizedMessage());
    }
    return Future.succeededFuture();
  }

  Future<RowSet<Row>> getServices (Void unused) {
    return sqlPool
      .query(DataBaseVerticle.LIST_ALL_SERVICES)
      .execute();
  }

  void updateServiceStatus(Integer id, String status) {
    sqlPool.preparedQuery(UPDATE_SERVICE_STATUS).execute(Tuple.of(id, status));
  }

}
