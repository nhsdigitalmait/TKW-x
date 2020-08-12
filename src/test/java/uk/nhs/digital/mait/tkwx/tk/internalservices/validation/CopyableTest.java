/*
 Copyright 2012-13  Simon Farrow <simon.farrow1@nhs.net>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class CopyableTest {

    public CopyableTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of copy method, of class Copyable.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        Copyable instance = new CopyableImpl(1);
        Object expResult = new CopyableImpl(1);
        Object result = instance.copy();
        assertEquals(expResult, result);
        assertTrue(expResult != result);
    }

    public class CopyableImpl implements Copyable {

        private int value = 0;

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + this.value;
            return hash;
        }
        
        @Override
        public boolean equals(Object o) {
            return ( o.getClass() == this.getClass() && ((CopyableImpl)o).value == this.value);
        }

        CopyableImpl(int value) {
            this.value= value;
        }

        @Override
        public Object copy() {
            return (CopyableImpl) new CopyableImpl(this.value);
        }
    }

}
