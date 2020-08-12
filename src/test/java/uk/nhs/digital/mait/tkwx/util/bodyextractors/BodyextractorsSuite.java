/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author simonfarrow
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SecondAsynchronousResponseBodyExtractorTest.class, HttpRequestBodyExtractorAdapterTest.class, SynchronousResponseBodyExtractorTest.class, AsynchronousResponseBodyExtractorTest.class, RequestBodyExtractorTest.class, AbstractBodyExtractorTest.class})
public class BodyextractorsSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
