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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sifa2
 */
public class RulesetMetadataTest {

    private RulesetMetadata instance;
    
    public RulesetMetadataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new RulesetMetadata();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class RulesetMetadata.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "name";
        instance.setName(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVersion method, of class RulesetMetadata.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        String expResult = "version";
        instance.setVersion(expResult);
        String result = instance.getVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimestamp method, of class RulesetMetadata.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        String expResult = "ts";
        instance.setTimestamp(expResult);
        String result = instance.getTimestamp();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAuthor method, of class RulesetMetadata.
     */
    @Test
    public void testGetAuthor() {
        System.out.println("getAuthor");
        String expResult = "author";
        instance.setAuthor(expResult);
        String result = instance.getAuthor();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class RulesetMetadata.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String s = "";
        instance.setName(s);
    }

    /**
     * Test of setVersion method, of class RulesetMetadata.
     */
    @Test
    public void testSetVersion() {
        System.out.println("setVersion");
        String s = "";
        instance.setVersion(s);
    }

    /**
     * Test of setTimestamp method, of class RulesetMetadata.
     */
    @Test
    public void testSetTimestamp() {
        System.out.println("setTimestamp");
        String s = "";
        instance.setTimestamp(s);
    }

    /**
     * Test of setAuthor method, of class RulesetMetadata.
     */
    @Test
    public void testSetAuthor() {
        System.out.println("setAuthor");
        String s = "";
        instance.setAuthor(s);
    }
    
}
