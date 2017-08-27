package com.github.igorperikov.heimdallr.init.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@Getter
public class ClusterState implements Serializable {
    private final Set<NodeDefinition> nodes;
}
