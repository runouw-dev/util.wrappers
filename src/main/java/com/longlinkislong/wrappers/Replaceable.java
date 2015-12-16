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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A container designed to wrap around a value that can be replaced.
 *
 * @author zmichaels
 * @param <T> the internal type wrapped by the Replaceable container.
 * @since 15.09.01
 */
public class Replaceable<T> implements Supplier<T> {

    private Consumer<T> onReplaceCallback = null;

    /**
     * Sets a callback for when the contained value changes.
     *
     * @param callback the callback to run if the contained value changes.
     * @since 15.11.20
     */
    public void setOnReplace(final Consumer<T> callback) {
        this.onReplaceCallback = callback;
    }

    /**
     * Constructs a Replaceable object that has the default value of null.
     *
     * @param <T> the type of the internal object.
     * @return the Replaceable object.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Replaceable<T> nullDefault() {
        return new Replaceable((T) null);
    }

    private T replacedValue;
    private final Supplier<T> defaultSupplier;

    /**
     * Constructs a Replaceable container wrapped around a supplier to the
     * default value.
     *
     * @param defaultSupplier supplier to the default value.
     * @since 15.09.01
     */
    public Replaceable(final Supplier<T> defaultSupplier) {
        this.defaultSupplier = Objects.requireNonNull(defaultSupplier);
    }

    /**
     * Constructs a Replaceable container wrapped around the specified default
     * value.
     *
     * @param defaultValue the default value.
     * @since 15.09.01
     */
    public Replaceable(final T defaultValue) {
        this.defaultSupplier = () -> {
            return defaultValue;
        };
    }

    /**
     * Checks if the value was set. This will return true even if the value was
     * replaced with a value equivalent to the default value.
     *
     * @return true if the value was set.
     * @since 15.09.01
     */
    public boolean isReplaced() {
        return this.replacedValue != null;
    }

    /**
     * Executes a callback if the internal value is not the default value.
     *
     * @param isReplaced the function to execute if the value was changed.
     * @since 15.11.20
     */
    public void ifReplaced(final Consumer<T> isReplaced) {
        if (this.isReplaced()) {
            isReplaced.accept(this.get());
        }
    }

    /**
     * Checks if the value is equivalent to the default value. Replacing the
     * default value with a value equivalent to the default value will result in
     * this function returning true.
     *
     * @return true if the value is equivalent to the default value.
     * @since 15.09.01
     */
    public boolean isDefault() {
        return this.replacedValue == null || this.replacedValue.equals(this.defaultSupplier.get());
    }

    /**
     * Retrieves the replaced value or the default value.
     *
     * @return the value.
     * @since 15.09.01
     */
    @Override
    public T get() {
        return this.isReplaced() ? this.replacedValue : this.defaultSupplier.get();
    }

    /**
     * Replaces the default value with a new value. Assigning null has the same
     * effect as calling revertToDefault.
     *
     * @param replacedValue the value to set.
     * @since 15.09.01
     */
    public void set(final T replacedValue) {
        if (this.onReplaceCallback != null && this.replacedValue != replacedValue) {
            if (replacedValue == null) {
                this.onReplaceCallback.accept(this.defaultSupplier.get());
            } else {
                this.onReplaceCallback.accept(replacedValue);
            }
        }

        this.replacedValue = replacedValue;
    }

    /**
     * Sets the value contained by Replaceable with the default.
     *
     * @since 15.09.01
     */
    public void revertToDefault() {
        this.set(null);
    }
}
