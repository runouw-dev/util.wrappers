/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class LazyBoolean implements DeferrableBoolean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyBoolean.class);
    
    private boolean instance;
    private final BooleanSupplier constructor;
    private boolean isInitialized = false;
    private Consumer<Boolean> onInitialize;
    
    public void setOnInitialize(final Consumer<Boolean> callback) {
        this.onInitialize = callback;
    }
    
    public LazyBoolean(final BooleanSupplier constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }
    
    @Override
    public boolean restore() {
        if (this.isInitialized) {
            LOGGER.warn("Restore called when object has already been initialized!");
        }
        
        this.instance = this.constructor.getAsBoolean();
        this.isInitialized = true;
        
        if (this.onInitialize != null) {
            this.onInitialize.accept(instance);;
        }
        
        return this.instance;
    }

    @Override
    public boolean getAsBoolean() {
        return this.isInitialized
                ? this.instance
                : this.restore();
    }
    
    public void ifInitialized(final Consumer<Boolean> isInit) {
        if (this.isInitialized) {
            isInit.accept(this.instance);
        }
    }
    
    public boolean isInitialized() {
        return this.isInitialized;
    }
}
