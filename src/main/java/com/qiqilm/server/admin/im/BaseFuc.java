package com.qiqilm.server.admin.im;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Supplier;

public interface BaseFuc extends Supplier<ObjectNode> {
    String getApi();
}
