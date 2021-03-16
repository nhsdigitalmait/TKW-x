/*
  Copyright 2012  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

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
public class XmlFragmentSubstitutionTest {
    private XmlFragmentSubstitution instance;

    public XmlFragmentSubstitutionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new XmlFragmentSubstitution();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class XmlFragmentSubstitution.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String xpath = "/a/*[1]";
        instance.setData(xpath);

        String o = "<a><b>xxx<c>yyyy</c></b><b>xxx</b></a>";
        String expResult = "<b>xxx<c>yyyy</c>\n</b>\n";
        String result = instance.getValue(o);
        assertEquals(expResult, result);
    }

    /**
     * Test of setData method, of class XmlFragmentSubstitution.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String xpath = "/*[1]";
        instance.setData(xpath);
    }
    
}
