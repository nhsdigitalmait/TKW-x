/*
 Copyright 2015  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;

/**
 * base class provides framework for testing subclassed handlers
 *
 * @author SIFA2
 */
public class AbstractHandler {

    protected static String content;

    protected InputStream istream;
    protected final static String TEMPFOLDER = "src/test/resources/tempfolder";
    protected OutputStream ostream;

    /**
     *
     * @param messageFile
     */
    public static void setUpClass(String messageFile) {
        try {
            new File(TEMPFOLDER).mkdirs();
            if (!messageFile.trim().isEmpty()) {
                byte[] encoded = Files.readAllBytes(Paths.get(messageFile));
                content = new String(encoded);
                content = content.replaceFirst("__SENDTO__", "http://localhost:5000");
                content = content.replaceFirst("__REPLYTO__", "http://localhost:4001");
                content = content.replaceFirst("__FAULTTO__", "http://localhost:4001");
                content = content.replaceFirst("__TIMESTAMP__", "2015-09-07T12:11:59Z");
                content = content.replaceFirst("__EXPIRES__", "2016-09-07T12:11:59Z");
            } else {
                // empty filepath string => no message
                content = "";
            }
        } catch (IOException ex) {
        }
    }

    /**
     *
     * @throws java.io.IOException
     */
    public static void tearDownClass() throws IOException {
        deleteFolderAndContents(TEMPFOLDER);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        istream = new ByteArrayInputStream(content.getBytes());
        ostream = new ByteArrayOutputStream();
    }

    /**
     *
     * @throws java.io.IOException
     */
    protected void tearDown() throws Exception {
        istream.close();
        ostream.close();
    }

}
