package com.github.igorperikov.heimdallr.init.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ClusterStateRequest implements Serializable {
    private final NodeDefinition nodeDefinition;
}
