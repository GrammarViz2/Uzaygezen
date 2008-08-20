/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.uzaygezen.core;

import junit.framework.TestCase;

import java.util.BitSet;

/**
 * @author Daniel Aioanei
 */
public class LongBitVectorTest extends TestCase {

  public void testCompareTo() {
    int n = 10;
    LongBitVector x = new LongBitVector(64);
    LongBitVector negativeX = new LongBitVector(64);
    LongBitVector y = new LongBitVector(64);
    LongBitVector negativeY = new LongBitVector(64);
    for (long i = 1 << n; --i >= 0; ) {
      x.copyFrom(i);
      negativeX.copyFrom(-i);
      for (long j = 1 << n; --j >= 0; ) {
        y.copyFrom(j);
        negativeY.copyFrom(-j);
        assertEquals(Long.valueOf(i).compareTo(Long.valueOf(j)), Long.signum(x.compareTo(y)));
        assertEquals(j == 0 ? Long.signum(i)
            : (i == 0 ? -1 : Long.valueOf(-i).compareTo(Long.valueOf(-j))),
            Long.signum(negativeX.compareTo(negativeY)));
        assertEquals(j == 0 ? Long.signum(i) : -1, Long.signum(x.compareTo(negativeY)));
        assertEquals(j == 0 ? -Long.signum(i) : +1, Long.signum(negativeY.compareTo(x)));
      }
    }
  }
  
  public void testCompareToCornerCases() {
    LongBitVector x = new LongBitVector(64);
    LongBitVector y = new LongBitVector(64);
    y.copyFrom(Long.MAX_VALUE);
    assertTrue(x.compareTo(y) < 0);
    x.copyFrom(Long.MIN_VALUE);
    assertTrue(y.compareTo(x) < 0);
    y.copyFrom(-1);
    assertTrue(x.compareTo(y) < 0);
  }
  
  public void testCopyFromBitSet() {
    int n = 10;
    LongBitVector x = new LongBitVector(n);
    BitSet counter = new BitSet(n);
    for (long i = 0; i < 1 << n; ++i) {
      x.copyFrom(counter);
      assertEquals(i, x.toLong());
      assertEquals(64 - Long.numberOfLeadingZeros(i), x.length());
      BitSetMath.increment(counter);
    }
  }
  
  public void testOptimisationForGrayCodeDoesNotAffectResults() {
    for (int bitCount = 5; --bitCount >= 0; ) {
      for (int i = 1 << bitCount; --i >= 0; ) {
        LongBitVector mu = new LongBitVector(bitCount);
        mu.copyFrom(i);
        LongBitVector r = new LongBitVector(mu.cardinality());
        for (int j = 1 << bitCount; --j >= 0; ) {
          LongBitVector w = new LongBitVector(bitCount);
          w.copyFrom(j);
          r.grayCodeRank(mu, w, true);
          LongBitVector rCopy = r.clone();
          r.grayCodeRank(mu, w, false);
          assertEquals(rCopy, r);
        }
      }
    }
  }
}
