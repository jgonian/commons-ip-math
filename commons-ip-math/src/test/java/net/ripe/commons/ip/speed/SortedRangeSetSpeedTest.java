/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014, Yannis Gonianakis
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
package net.ripe.commons.ip.speed;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;

import net.ripe.commons.ip.Ipv4;
import net.ripe.commons.ip.Ipv4Range;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SortedRangeSetSpeedTest {

    List<Ipv4Range> testRanges = new LinkedList<Ipv4Range>();
    NavigableSet<Ipv4Range> correctSet;

    @SuppressWarnings("unchecked")
    @Before
    public void fillWithStuff() {
        testRanges = (List<Ipv4Range>) readResource("values.ser");
        correctSet = (NavigableSet<Ipv4Range>)  readResource("correct.ser");
    }

    private Object readResource(String resourceName) {
        try {
            InputStream serializeResource = SortedRangeSetSpeedTest.class.getResourceAsStream(resourceName);
            InputStream buffer = new BufferedInputStream(serializeResource);
            ObjectInput input = new ObjectInputStream(buffer);
            try {
                return input.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Ignore
    @Test
    public void shouldBeQuick() throws InterruptedException {
        long aSum = 0l;
        long bSum = 0l;
        for (int i = 0; i < 12; i++) {
            Thread.sleep(1000L); // warmup;
            SpeedTestInterface<Ipv4, Ipv4Range> subject;
            
            
            /*
             * In order to run the speed tests you need to:
             * 
             * 1. Make SortedRangeSet implement SpeedTestInterface
             * 2. Copy SortedRangeSet into for instance QuickerSortedRangeSet
             *    (to use as reference for)
             * 3. Initialize subject in the if statement below (replace the
             *    nulls)
             * 4. Profit
             */
            
            if (i % 2 == 0) {
                subject = null; //new SortedRangeSet<Ipv4, Ipv4Range>();
            } else {
                subject = null; // new QuickerSortedRangeSet<Ipv4, Ipv4Range>();
            }
            
            
            long start = System.currentTimeMillis();
            for (Ipv4Range range : testRanges) {
                subject.add(range);
            }
            long end = System.currentTimeMillis();
            if (i < 2) {
                NavigableSet<Ipv4Range> set = subject.getSet();
                if (set.size() != correctSet.size()) {
                    System.out.println("fail");
                    break;
                }
                for (Ipv4Range range : set) {
                    if (!correctSet.contains(range)) {
                        System.out.println("missmatch in sets");
                        break;
                    }
                }
                continue;
            }
            if (i%2 == 0) {
                aSum += end-start;
                System.out.println("A Adding " + testRanges.size() + " ranges took " + (end-start) + " ms.");
            } else {
                bSum += end-start;
                System.out.println("B Adding " + testRanges.size() + " ranges took " + (end-start) + " ms.");
            }
        }
        
        System.out.println("Average for A: " + (aSum/5) + " ms.");
        System.out.println("Average for B: " + (bSum/5) + " ms.");
    }
    
    
}
