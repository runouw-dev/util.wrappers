/* 
 * Copyright (c) 2015, Zachary Michaels
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.runouw.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lazy is an object wrapper utility for implementing lazy initialization.
 *
 * @author zmichaels
 * @param <T> the type of the object.
 * @since 15.07.30
 */
public class Lazy<T> implements Deferrable<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lazy.class);
    
    private T instance;
    private final Supplier<T> constructor;
    private boolean isInitialized = false;
    private Consumer<T> onInitialize = null;

    /**
     * Sets a callback method for when the object is initialized.
     *
     * @param callback the method to call when the object is initialized.
     * @since 15.11.20
     */
    public void setOnInitialize(final Consumer<T> callback) {
        this.onInitialize = callback;
    }

    /**
     * Constructs a new Lazy initialization object.
     *
     * @param constructor the constructor for the object.
     * @since 15.07.29
     */
    public Lazy(final Supplier<T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }

    @Override
    public T get() {
        return this.isInitialized
                ? this.instance
                : this.restore();
    }

    @Override
    public T restore() {
        if(this.isInitialized) {
            LOGGER.warn("Restore called when object has already been initialized!");
        }
        
        this.instance = this.constructor.get();
        this.isInitialized = true;
        
        if(this.onInitialize != null) {
            this.onInitialize.accept(this.instance);
        }
        
        return this.instance;
    }

    /**
     * Checks of the object has been initialized.
     *
     * @return true if it has been initialized.
     * @since 15.09.01
     */
    public boolean isInitialized() {
        return this.isInitialized;
    }

    /**
     * Executes a method if the internal object has been initialized.
     *
     * @param isInitialized the method to execute if the object has been
     * initialized.
     * @since 15.11.20
     */
    public void ifInitialized(final Consumer<T> isInitialized) {
        if (this.isInitialized) {
            isInitialized.accept(this.get());
        }
    }
}
