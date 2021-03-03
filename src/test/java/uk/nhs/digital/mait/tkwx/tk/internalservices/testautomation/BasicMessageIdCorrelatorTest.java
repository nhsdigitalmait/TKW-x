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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class BasicMessageIdCorrelatorTest {

    public static final String LOGFILE = "src/test/resources/temp.log";
    public static final String NAME = "DE_EMPPID";
    public static final String TIMESTAMP = "20160125000000";
    public static final String TKWROOT = System.getenv("TKWROOT").replace('\\', '/');
    private static AutotestParser.MessageContext messageCtx;
    private static TestVisitor visitor = null;

    public BasicMessageIdCorrelatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        visitor = new TestVisitor();
        messageCtx = visitor.getMessageContext();
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        for (String filename : new String[]{LOGFILE, "src/test/resources/test.tdv", "src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of correlate method, of class BasicMessageIdCorrelator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCorrelate() throws Exception {
        System.out.println("correlate");

        new File(TIMESTAMP).mkdir();
        Message request = getMessage(NAME, TIMESTAMP);
        BasicMessageIdCorrelator instance = new BasicMessageIdCorrelator();

        writeLogFile("stuff\r\nRelatesTo " + request.getMessageId() + "\r\nstuff");
        File log = new File(LOGFILE);

        boolean expResult = true;
        boolean result = instance.correlate(log, request);
        assertEquals(expResult, result);

        writeLogFile("stuff\r\nRelatesTo " + "xxx" + "\r\nstuff");
        expResult = false;
        result = instance.correlate(log, request);
        assertEquals(expResult, result);

        writeLogFile("stuff\r\nRelatesXX " + "xxx" + "\r\nstuff");
        result = instance.correlate(log, request);
        assertEquals(expResult, result);

        if (!log.delete()) {
            fail("delete log file failed (not closed?)");
        }
        new File(TIMESTAMP + "/" + NAME + "_1.msg").delete();
        new File(TIMESTAMP).delete();
    }

    public static Message getMessage(final String name, final String timestamp) throws Exception, IOException {
        // all this just to get a message id set in request
        // namely soapwrap must be called so we must strip the soap before rewrapping it.
        if (messageCtx == null) {
            if (visitor == null) {
                visitor = new TestVisitor();
            }
            messageCtx = visitor.getMessageContext();
        }
        Message request = new Message(messageCtx);
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        ScriptParser p = new ScriptParser(props);
        p.parse("src/test/resources/test.tst");
        request.link(p);

        final String EXTRACT_DE_XFORM = TKWROOT + "/contrib/TKWAutotestManager/transforms/extract_de.xslt";
        // name , location
        TransformManager.getInstance().addTransform(EXTRACT_DE_XFORM, EXTRACT_DE_XFORM);

        HashMap<String, ArrayList<String>> pst = new HashMap<>();
        // transform list
        pst.put("data", new ArrayList(Arrays.asList(new String[]{EXTRACT_DE_XFORM})));

        request.instantiate(null, timestamp, "to","from","replyto", pst, null, "profileid", 0);

        return request;
    }

    public static void writeLogFile(String s) throws IOException {
        Utils.writeString2File(LOGFILE, s);
    }

}
