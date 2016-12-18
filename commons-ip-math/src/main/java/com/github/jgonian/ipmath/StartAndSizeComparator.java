/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017, Yannis Gonianakis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.jgonian.ipmath;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

public final class StartAndSizeComparator<C extends Rangeable<C, R>, R extends Range<C, R>>
        implements Comparator<R>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private static Comparator<?> instance;
    private static Comparator<?> reverse;

    @SuppressWarnings("unchecked")
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> Comparator<R> get() {
        return (Comparator<R>) (instance == null ? instance = new StartAndSizeComparator<C, R>() : instance);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Rangeable<C, R>, R extends Range<C, R>> Comparator<R> reverse() {
        return (Comparator<R>) (reverse == null ? reverse = Collections.reverseOrder(StartAndSizeComparator.<C, R>get()) : reverse);
    }

    private StartAndSizeComparator() {
    }

    @Override
    public int compare(R left, R right) {
        int result = left.start().compareTo(right.start());
        if (result == 0) {
            result = left.end().compareTo(right.end());
        }
        return result;
    }
}
