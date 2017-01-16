/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class LazyInt implements DeferrableInt {

    private static final Logger LOGGER = LoggerFactory.getLogger(LazyInt.class);

    private int instance;
    private final IntSupplier constructor;    
    private boolean isInitialized = false;
    private IntConsumer onInitialize;

    public void setOnInitialize(final IntConsumer callback) {
        this.onInitialize = callback;
    }

    public LazyInt(final IntSupplier constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }        
    
    public boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public int restore() {
        if (this.isInitialized) {
            LOGGER.warn("Restore called when object has already been initialized!");
        }
        
        this.instance = this.constructor.getAsInt();
        this.isInitialized = true;
        
        if (this.onInitialize != null) {
            this.onInitialize.accept(this.instance);
        }
        
        return this.instance;
    }

    @Override
    public int getAsInt() {
        return this.isInitialized
                ? this.instance
                : this.restore();
    }
    
    public void ifInitialized(final IntConsumer isInit) {
        if (this.isInitialized()) {
            isInit.accept(this.instance);
        }
    }
}
