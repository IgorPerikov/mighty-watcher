package com.github.igorperikov.heimdallr.init.pojo;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;

public class NodeDefinition implements Serializable {
    private final UUID label;
    private final InetSocketAddress address;

    public NodeDefinition(UUID label, InetSocketAddress address) {
        this.label = label;
        this.address = address;
    }

    public UUID getLabel() {
        return label;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDefinition that = (NodeDefinition) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return "\r\n{label=" + label + ", address=" + address + "}\r\n";
    }
}
