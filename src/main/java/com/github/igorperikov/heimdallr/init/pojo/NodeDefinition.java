package com.github.igorperikov.heimdallr.init.pojo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode(of = {"label"})
public class NodeDefinition implements Serializable {
    private final UUID label;
    private final InetSocketAddress address;

    @Override
    public String toString() {
        return "{" + address + "," + label + "}";
    }
}
