/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @author zmichaels
 * @param <InType> 
 * @param <OutType>
 */
public final class ConditionalSupplier<InType, OutType> implements Supplier<OutType> {

    private final Supplier<InType> input;
    private final Predicate<InType> test;
    private final Supplier<OutType> output;
    private final Supplier<OutType> elseOut;    

    private ConditionalSupplier(final Supplier<InType> input, final Predicate<InType> test, final Supplier<OutType> output, final Supplier<OutType> elseOut) {
        this.elseOut = elseOut;
        this.input = input;
        this.test = test;
        this.output = output;
    }

    public static <In, Out> ConditionalSupplier<In, Out> If(final Supplier<In> input) {
        return new ConditionalSupplier<>(input, val -> true, () -> null, () -> null);
    }
    
    public static <In, Out> ConditionalSupplier<In, Out> If(final In value) {
        return new ConditionalSupplier<>(() -> value, val -> true, () -> null, () -> null);
    }
    
    public static <Out> ConditionalSupplier<?, Out> Always() {
        return new ConditionalSupplier<>(() -> null, val -> true, () -> null, () -> null);
    }
    
    public ConditionalSupplier<InType, OutType> Equals(final InType other) {
        return new ConditionalSupplier<>(input, testValue -> Objects.equals(testValue, other), output, elseOut);
    }

    public ConditionalSupplier<InType, OutType> Equals(final Supplier<InType> other) {
        return new ConditionalSupplier<>(input, testValue -> Objects.equals(testValue, other.get()), output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> GreaterThan(InType other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other) > 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> GreaterThan(final Supplier<InType> other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other.get()) > 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> GreaterThanOrEquals(final InType other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other) >= 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> GreaterThanOrEquals(final Supplier<InType> other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other.get()) >= 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> LessThan(final InType other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other) < 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> LessThan(final Supplier<InType> other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other.get()) < 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> LessThanOrEquals(final Supplier<InType> other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other.get()) <= 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> LessThanOrEquals(final InType other) {
        return new ConditionalSupplier<>(input, testValue -> ((Comparable<InType>) testValue).compareTo(other) <= 0, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> NotEquals(final Supplier<InType> other) {
        return new ConditionalSupplier<>(input, testValue -> !Objects.equals(testValue, other.get()), output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> NotEquals(final InType other) {
        return new ConditionalSupplier<>(input, testValue -> !Objects.equals(testValue, other), output, elseOut);
    }

    public ConditionalSupplier<InType, OutType> ThenGet(final Supplier<OutType> output) {
        return new ConditionalSupplier<>(input, test, output, elseOut);
    }
    
    public ConditionalSupplier<InType, OutType> Then(final OutType output) {
        return new ConditionalSupplier<>(input, test, () -> output, elseOut);
    }
    
    public ConditionalSupplier Else(final OutType elseOut) {
        return new ConditionalSupplier(input, test, output, () -> elseOut);
    }

    @Override
    public OutType get() {
        if (this.test.test(this.input.get())) {
            return this.output.get();
        } else {
            return this.elseOut.get();
        }
    }

    public ConditionalSupplier<InType, OutType> ElseGet(final Supplier<OutType> elseStatement) {
        return new ConditionalSupplier(input, test, output, elseStatement);
    }
}
