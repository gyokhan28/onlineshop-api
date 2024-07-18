package com.example.online_shop_api;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {

    private final DataSource dataSource;

    @Autowired
    public FlywayConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Flyway.configure()
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}