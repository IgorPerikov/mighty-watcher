package com.github.igorperikov.heimdallr.domain;

import com.github.igorperikov.heimdallr.generated.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(of = {"label", "type"})
public class NodeDefinition {
    private final String label;
    private final String address;
    private final Instant timestamp;
    private final Type type;

    public static NodeDefinition buildLiveDefinition(String label, String address) {
        return new NodeDefinition(label, address, Instant.now(), Type.LIVE);
    }
}
