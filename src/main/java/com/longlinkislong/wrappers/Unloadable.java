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
package com.longlinkislong.wrappers;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object container that allows unloading of the object.
 *
 * @author zmichaels
 * @param <T> the internal object to hold.
 * @since 15.09.01
 */
public class Unloadable<T> implements Supplier<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Unloadable.class);

    private T instance;
    private Reference<T> retainedReference;
    private final Supplier<T> restoreFunction;
    private final RetainPolicy retainPolicy;
    private Consumer<T> onReload = null;
    private Consumer<T> onUnload = null;

    /**
     * Sets a callback for when the internal resource is reloaded.
     *
     * @param callback the method to call when the resource is reloaded.
     * @since 15.11.20
     */
    public void setOnReload(final Consumer<T> callback) {
        this.onReload = callback;
    }
    
    /**
     * Sets a callback for when the internal resource is unloaded.
     * 
     * @param callback the method to call when the resource is unloaded.
     * @since 15.11.20
     */
    public void setOnUnload(final Consumer<T> callback) {
        this.onUnload = callback;
    }

    private enum RetainPolicy {

        WEAK_RETAIN,
        SOFT_RETAIN,
        NO_RETAIN
    }

    private Unloadable(final T initialValue, final Supplier<T> restoreFunction, final RetainPolicy policy) {
        if (initialValue == null) {
            LOGGER.debug("Initial value is set to null; Unloadable will use lazy initialization.");
        }

        this.instance = initialValue;
        this.restoreFunction = restoreFunction;
        this.retainPolicy = policy;
    }

    /**
     * Creates an Unloadable object that will discard its reference immediately.
     *
     * @param <T> the type of object.
     * @param initialValue the initial value. Null results in lazy
     * initialization.
     * @param restoreFunction the restore function. Cannot be null.
     * @return the object wrapped in an Unloadable container.
     * @since 15.09.01
     */
    public static <T> Unloadable<T> eagerUnload(final T initialValue, final Supplier<T> restoreFunction) {
        return new Unloadable<>(initialValue, Objects.requireNonNull(restoreFunction), RetainPolicy.NO_RETAIN);
    }

    /**
     * Creates an Unloadable object that will attempt to resurrect the object if
     * it is referenced elsewhere.
     *
     * @param <T> the type of object.
     * @param initialValue the initial value. Null results in lazy
     * initialization.
     * @param restoreFunction the restore function. Cannot be null.
     * @return the object wrapped in an Unloadable container.
     * @since 15.09.01
     */
    public static <T> Unloadable<T> salvagedReload(final T initialValue, final Supplier<T> restoreFunction) {
        return new Unloadable<>(initialValue, Objects.requireNonNull(restoreFunction), RetainPolicy.WEAK_RETAIN);
    }

    /**
     * Creates an Unloadable object that will only garbage collect the internal
     * object if the system is under memory pressure.
     *
     * @param <T> the type of object.
     * @param initialValue the initial value. Null results in lazy
     * initialization.
     * @param restoreFunction the restore function. Cannot be null.
     * @return the object wrapped in an Unloadable container.
     * @since 15.09.01
     */
    public static <T> Unloadable<T> deferredUnload(final T initialValue, final Supplier<T> restoreFunction) {
        return new Unloadable<>(initialValue, Objects.requireNonNull(restoreFunction), RetainPolicy.SOFT_RETAIN);
    }

    /**
     * Checks if the object is present in memory.
     *
     * @return true if the object is directly accessible.
     * @since 15.09.01
     */
    public boolean isPresent() {
        return this.instance != null;
    }

    /**
     * Executes a callback if the contained object is currently loaded.
     *
     * @param onIsPresent the callback to execute if present.
     * @since 15.11.20
     */
    public void ifPresent(Consumer<T> onIsPresent) {
        if (this.isPresent()) {
            onIsPresent.accept(this.get());
        }
    }

    /**
     * Requests the internal object. The object will be restored if it is not
     * present.
     *
     * @return the instance of the object.
     * @since 15.09.01
     */
    @Override
    public T get() {
        if (this.isPresent()) {
            return this.instance;
        } else if (this.retainPolicy != RetainPolicy.NO_RETAIN) {
            final T salvaged = this.retainedReference.get();

            if (salvaged == null) {
                LOGGER.trace("Restoring object...");                
                this.instance = this.restoreFunction.get();                
            } else {
                LOGGER.trace("Salvaging object...");
                this.instance = salvaged;                
            }
        } else {
            LOGGER.trace("Restoring object...");
            this.instance = this.restoreFunction.get();            
        }
        
        if(this.onReload != null) {
            this.onReload.accept(this.instance);
        }
        
        return this.instance;
    }

    /**
     * Unloads the object. The next time the object is requested, it will be
     * restored.
     *
     * @since 15.09.01
     */
    public void unload() {
        if (this.instance == null) {
            LOGGER.trace("Unload called when no value is held!");
            return;
        }

        switch (this.retainPolicy) {
            case WEAK_RETAIN:
                this.retainedReference = new WeakReference<>(this.instance);
                break;
            case SOFT_RETAIN:
                this.retainedReference = new SoftReference<>(this.instance);
                break;
        }
        
        if(this.onUnload != null) {
            this.onUnload.accept(this.instance);
        }
        
        this.instance = null;
    }
}
