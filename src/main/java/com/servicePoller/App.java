package com.servicePoller;

import io.reactivex.rxjava3.core.Flowable;

public class App {
  public static void main(String[] args) {
    Flowable.just("Hello world").subscribe(System.out::println);
  }
}
