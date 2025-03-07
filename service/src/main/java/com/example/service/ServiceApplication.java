package com.example.service;

import com.example.service.grpc.impl.AdoptionsGrpc;
import com.example.service.grpc.impl.DogsResponse;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

record Dog(int id, String name, String owner) {
}

@Service
class AdoptionsService
        extends AdoptionsGrpc.AdoptionsImplBase {

    private final Set<Dog> dogs = new HashSet<>(Set.of(
            new Dog(1, "Felix", null),
            new Dog(2, "Fido", null),
            new Dog(3, "Rover", null)
    ));


    @Override
    public void all(Empty request, StreamObserver<DogsResponse> responseObserver) {

        var all = this.dogs
                .stream()
                .map(od -> {
                    var b = com.example.service.grpc.impl.Dog.newBuilder()
                            .setName(od.name())
                            .setId(od.id());
                    if (od.owner() == null) b.clearOwner();
                    else b.setOwner(od.owner());
                    return b.build();
                })
                .toList();

        responseObserver.onNext(DogsResponse.newBuilder().addAllDogs(all).build());
        responseObserver.onCompleted();
    }
}
