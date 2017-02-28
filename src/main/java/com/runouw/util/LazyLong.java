/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.Objects;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class LazyLong implements DeferrableLong, Supplier<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyLong.class);
    
    private long instance;
    private final LongSupplier constructor;
    private boolean isInitialized = false;
    private LongConsumer onInitialize = null;
    
    public void setOnInitialize(final LongConsumer callback) {
        this.onInitialize = callback;
    }
    
    public LazyLong(final LongSupplier constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }
    
    @Override
    public long restore() {
        if (this.isInitialized) {
            LOGGER.warn("Restore called when object has already been initialized!");
        }
        
        this.instance = this.constructor.getAsLong();
        this.isInitialized = true;
        
        if (this.onInitialize != null) {
            this.onInitialize.accept(this.instance);
        }
        
        return this.instance;
    }

    @Override
    public long getAsLong() {
        return this.isInitialized
                ? this.instance
                : this.restore();
    }
    
    public boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public Long get() {
        return this.getAsLong();
    }
}
