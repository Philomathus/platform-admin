package com.qiqilm.server.admin.cache;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.function.Supplier;

public abstract class AbstractSupplier<T> extends TypeReference<T> implements Supplier<T> {
}
