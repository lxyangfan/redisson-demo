package com.frank.redssiondemo;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureEmbeddedDatabase(
    refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
@FlywayTest
@Testcontainers
public abstract class DatabaseTest {

  @Container
  @ServiceConnection
  static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2.3-alpine3.18").withExposedPorts(6379);



}
