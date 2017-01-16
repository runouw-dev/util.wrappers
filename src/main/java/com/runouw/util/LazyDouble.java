/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class LazyDouble implements DeferrableDouble {
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyDouble.class);
    
    private double instance;
    private final DoubleSupplier constructor;
    private boolean isInitialized = false;
    private DoubleConsumer onInitialize = null;
    
    public void setOnInitialize(final DoubleConsumer callback) {
        this.onInitialize = callback;
    }
    
    public LazyDouble(final DoubleSupplier constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }
    
    @Override
    public double restore() {
        if (this.isInitialized) {
            LOGGER.warn("Restore called when object has already been initialized!");
        }
        
        this.instance = this.constructor.getAsDouble();
        this.isInitialized = true;
        
        if (this.onInitialize != null) {
            this.onInitialize.accept(this.instance);
        }
        
        return this.instance;
    }

    @Override
    public double getAsDouble() {
        return this.isInitialized
                ? this.instance
                : this.restore();
    }
    
}
