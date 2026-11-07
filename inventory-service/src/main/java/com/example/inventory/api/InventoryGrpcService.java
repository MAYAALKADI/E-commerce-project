package com.example.inventory.api;

import com.example.inventory.grpc.InventoryServiceGrpc;
import com.example.inventory.grpc.RegisterProductRequest;
import com.example.inventory.grpc.RegisterProductResponse;
import com.example.inventory.grpc.ReleaseRequest;
import com.example.inventory.grpc.ReleaseResponse;
import com.example.inventory.grpc.ReserveRequest;
import com.example.inventory.grpc.ReserveResponse;
import com.example.inventory.grpc.StockRequest;
import com.example.inventory.grpc.StockResponse;
import com.example.inventory.model.StockItem;
import com.example.inventory.service.StockManager;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final StockManager manager;

    @Override
    public void registerProduct(RegisterProductRequest req,
                                StreamObserver<RegisterProductResponse> obs) {
        manager.register(req.getProductId(), req.getQuantity());
        obs.onNext(RegisterProductResponse.newBuilder().setSuccess(true).build());
        obs.onCompleted();
    }

    @Override
    public void checkStock(StockRequest req, StreamObserver<StockResponse> obs) {
        var item = manager.check(req.getProductId());
        obs.onNext(StockResponse.newBuilder()
                .setProductId(req.getProductId())
                .setExists(item.isPresent())
                .setAvailable(item.map(StockItem::getQuantity).orElse(0))
                .build());
        obs.onCompleted();
    }

    @Override
    public void reserveStock(ReserveRequest req, StreamObserver<ReserveResponse> obs) {
        ReserveResponse.Builder res = ReserveResponse.newBuilder();
        try {
            manager.reserve(req.getOrderId(), req.getItemsList());
            res.setSuccess(true).setMessage("Reserved");
        } catch (IllegalStateException e) {
            res.setSuccess(false).setMessage(e.getMessage());
        }
        obs.onNext(res.build());
        obs.onCompleted();
    }

    @Override
    public void releaseStock(ReleaseRequest req, StreamObserver<ReleaseResponse> obs) {
        manager.release(req.getOrderId());
        obs.onNext(ReleaseResponse.newBuilder().setSuccess(true).build());
        obs.onCompleted();
    }
}