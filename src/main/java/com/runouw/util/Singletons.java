/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * A collection of utility functions for Singleton object wrappers.
 * @author zmichaels
 * @since 15.12.15
 */
public final class Singletons {
    private Singletons() {}
    
    public static <T> Supplier<T> of(final T value) {
        return () -> value;
    }
    
    public static IntSupplier of(final int value) {
        return () -> value;
    }
    
    public static LongSupplier of(final long value) {
        return () -> value;
    }
    
    public static DoubleSupplier of(final double value) {
        return () -> value;
    }
}
