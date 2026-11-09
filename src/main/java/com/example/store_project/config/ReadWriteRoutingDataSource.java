package com.example.store_project.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {

    public enum Route { PRIMARY, REPLICA }

    @Override
    protected Object determineCurrentLookupKey() {
        if (RoutingContext.isForcePrimary()) {
            return Route.PRIMARY;
        }
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? Route.REPLICA
                : Route.PRIMARY;
    }
}