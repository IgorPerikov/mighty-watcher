package com.github.igorperikov.heimdallr.domain;

import com.github.igorperikov.heimdallr.generated.Type;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(of = {"label", "type", "timestamp"})
public class NodeDefinition {
    private final String label;
    private final String address;

    @Setter
    private Instant timestamp;

    private final Type type;

    public static NodeDefinition buildLiveDefinition(String label, String address) {
        return new NodeDefinition(label, address, Instant.now(), Type.LIVE);
    }
}
