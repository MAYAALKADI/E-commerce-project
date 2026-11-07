package com.example.store_project.inventory.config;

import com.example.inventory.grpc.InventoryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    // القناة = الاتصال الدائم مع المستودع (بتنفتح بأول نداء، فالمتجر بيشتغل حتى لو المستودع واقف)
    @Bean(destroyMethod = "shutdown")
    public ManagedChannel inventoryChannel(
            @Value("${inventory.grpc.host}") String host,
            @Value("${inventory.grpc.port}") int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    // الـ stub = الكائن اللي منناديه كأنو method عادية
    @Bean
    public InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub(ManagedChannel channel) {
        return InventoryServiceGrpc.newBlockingStub(channel);
    }
}