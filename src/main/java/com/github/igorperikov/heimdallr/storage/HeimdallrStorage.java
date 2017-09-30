package com.github.igorperikov.heimdallr.storage;

import reactor.core.publisher.Mono;

public interface HeimdallrStorage {
    Mono<String> get(String key);

    Mono<Void> put(String key, String value);

    Mono<Void> remove(String key);
}
