package com.github.igorperikov.heimdallr.storage;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class HeimdallrStorageMapImplementation implements HeimdallrStorage {
    private final Map<String, String> kv = new HashMap<>();

    @Override
    public Mono<String> get(String key) {
        return Mono.justOrEmpty(kv.get(key));
    }

    @Override
    public Mono<Void> put(String key, String value) {
        return Mono.fromRunnable(() -> kv.put(key, value));
    }

    @Override
    public Mono<Void> remove(String key) {
        return Mono.fromRunnable(() -> kv.remove(key));
    }
}
