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
package uk.nhs.digital.mait.tkwx.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 *
 * @author sifa2
 */
public class RequestReaderTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private RequestReader instance;
    private ByteArrayOutputStream bos;
    private HttpServer httpServer;
    private boolean requestAdded;

    public RequestReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("tks.HttpTransport.pipeline.persistperiod", "100");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws InterruptedException, IOException {
        requestAdded = false;
        Socket socket = mock(Socket.class);
        bos = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(bos);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("POST / HTTP/1.1\r\nConnection: close\r\nContent-length: 6\r\n\r\nmessage".getBytes()));
        when(socket.getInetAddress()).thenReturn(InetAddress.getByName("127.0.0.1"));

        httpServer = mock(HttpServer.class);
        // Mockito doesn't seem handle methods returning void in a very clean manner hence all this stuff below.
        doAnswer((Answer) (InvocationOnMock invocation) -> {
            if (invocation.getArguments().length > 0) {
                HttpRequest req = (HttpRequest) invocation.getArguments()[0];
                System.out.println("HttpRequest using method "+req.getRequestType()+" added to HttpServer");
                requestAdded = true;
            }
            return null;
        }).when(httpServer).addRequest(any(HttpRequest.class));

        instance = new RequestReader(socket, httpServer, "id");
       // instance.join();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getException method, of class RequestReader.
     */
    @Test
    public void testGetException() {
        System.out.println("getException");
        String expResult = "Socket is not connected";
        Exception result = instance.getException();
        //assertEquals(expResult, result.getMessage());
        assertNull(result);
    }

    /**
     * Test of run method, of class RequestReader.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRun() throws InterruptedException {
        System.out.println("run");
        // give things time to settle down before exiting the test
        Thread.sleep(1000);
        assertTrue(requestAdded);
        // tested by the constructor which invokes start
        System.out.println("[" + bos.toString() + "]");
    }

}
