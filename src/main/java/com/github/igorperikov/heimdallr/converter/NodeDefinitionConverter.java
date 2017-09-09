package com.github.igorperikov.heimdallr.converter;

import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;

import java.time.Instant;

public class NodeDefinitionConverter {
    public static NodeDefinition convertToDomain(NodeDefinitionTO to) {
        return new NodeDefinition(to.getLabel(), to.getAddress(), Instant.parse(to.getTimestamp()), to.getType());
    }

    public static NodeDefinitionTO convertDomain(NodeDefinition domain) {
        return NodeDefinitionTO.newBuilder()
                .setLabel(domain.getLabel())
                .setAddress(domain.getAddress())
                .setTimestamp(domain.getTimestamp().toString())
                .setType(domain.getType())
                .build();
    }
}
