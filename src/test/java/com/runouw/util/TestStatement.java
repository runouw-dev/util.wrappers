/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import org.junit.Test;

/**
 *
 * @author zmichaels
 */
public class TestStatement {
    @Test
    public void testStatement() {
        System.out.println(ConditionalSupplier.If(11)
                .GreaterThan(10)
                .Then("Value was greater than 10!")
                .Else("Value was not greater than 10!")
                .get());
    }
}
