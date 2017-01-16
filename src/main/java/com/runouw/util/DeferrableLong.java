/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runouw.util;

import java.util.function.LongSupplier;

/**
 *
 * @author zmichaels
 */
public interface DeferrableLong extends LongSupplier {
    long restore();
}
