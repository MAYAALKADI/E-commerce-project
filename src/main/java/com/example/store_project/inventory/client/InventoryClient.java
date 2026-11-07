package com.example.store_project.inventory.client;

import com.example.inventory.grpc.InventoryServiceGrpc;
import com.example.inventory.grpc.Item;
import com.example.inventory.grpc.RegisterProductRequest;
import com.example.inventory.grpc.ReleaseRequest;
import com.example.inventory.grpc.ReserveRequest;
import com.example.inventory.grpc.ReserveResponse;
import com.example.inventory.grpc.StockRequest;
import com.example.store_project.exception.InsufficientStockException;
import com.example.store_project.exception.InventoryUnavailableException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class InventoryClient {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;
    private final long deadlineSeconds;

    public InventoryClient(InventoryServiceGrpc.InventoryServiceBlockingStub stub,
                           @Value("${inventory.grpc.deadline-seconds:3}") long deadlineSeconds) {
        this.stub = stub;
        this.deadlineSeconds = deadlineSeconds;
    }

    public void registerProduct(Long productId, int quantity) {
        call(s -> s.registerProduct(RegisterProductRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build()));
    }

    public int getAvailable(Long productId) {
        return call(s -> s.checkStock(StockRequest.newBuilder()
                .setProductId(productId)
                .build())).getAvailable();
    }

    // حجز كل منتجات الطلب بنداء واحد (كله أو ولا شي)
    public void reserve(String reservationId, Map<Long, Integer> items) {
        ReserveRequest.Builder request = ReserveRequest.newBuilder().setOrderId(reservationId);
        items.forEach((productId, qty) -> request.addItems(
                Item.newBuilder().setProductId(productId).setQuantity(qty)));

        ReserveResponse response = call(s -> s.reserveStock(request.build()));
        if (!response.getSuccess()) {
            throw new InsufficientStockException(response.getMessage());
        }
    }

    public void release(String reservationId) {
        call(s -> s.releaseStock(ReleaseRequest.newBuilder()
                .setOrderId(reservationId)
                .build()));
    }

    // كل نداء بياخد Deadline، وأخطاء الشبكة بتتحوّل لرسالة واضحة
    private <T> T call(Function<InventoryServiceGrpc.InventoryServiceBlockingStub, T> action) {
        try {
            return action.apply(stub.withDeadlineAfter(deadlineSeconds, TimeUnit.SECONDS));
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            if (code == Status.Code.DEADLINE_EXCEEDED || code == Status.Code.UNAVAILABLE) {
                throw new InventoryUnavailableException(
                        "Inventory service is currently unavailable, please try again later");
            }
            throw e;
        }
    }
}