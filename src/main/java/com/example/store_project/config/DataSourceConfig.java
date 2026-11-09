package com.example.store_project.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource primaryDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String user,
            @Value("${spring.datasource.password}") String pass) {
        return build(url, user, pass, "primary-pool");
    }

    @Bean
    public DataSource replicaDataSource(
            @Value("${replica.datasource.url}") String url,
            @Value("${replica.datasource.username}") String user,
            @Value("${replica.datasource.password}") String pass) {
        return build(url, user, pass, "replica-pool");
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSource primaryDataSource, DataSource replicaDataSource) {
        ReadWriteRoutingDataSource routing = new ReadWriteRoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put(ReadWriteRoutingDataSource.Route.PRIMARY, primaryDataSource);
        targets.put(ReadWriteRoutingDataSource.Route.REPLICA, replicaDataSource);
        routing.setTargetDataSources(targets);
        routing.setDefaultTargetDataSource(primaryDataSource);
        routing.afterPropertiesSet();
        // Lazy: ما يفتح اتصال إلا لما فعلاً يحتاجه، بعد ما تتحدد الـ readOnly
        return new LazyConnectionDataSourceProxy(routing);
    }

    private HikariDataSource build(String url, String user, String pass, String name) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setPoolName(name);
        return ds;
    }
}