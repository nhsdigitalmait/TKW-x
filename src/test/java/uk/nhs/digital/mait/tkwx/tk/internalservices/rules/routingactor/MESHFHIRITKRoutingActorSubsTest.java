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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.io.IOException;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest.makeMeshRequest;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class MESHFHIRITKRoutingActorSubsTest {
    
    public MESHFHIRITKRoutingActorSubsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.setUpClass();
    }
    
    @AfterClass
    public static void tearDownClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.tearDownClass();
    }
    
    @Before
    public void setUp() throws IOException {
        commonSetup();
    }
    
    @After
    public void tearDown() throws IOException {
        commonTeardown();
    }

    /**
     * Test of setPriorityBlock method, of class MESHFHIRITKRoutingActorSubs.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetPriorityBlock() throws Exception {
        System.out.println("setPriorityBlock");
        MeshRequest meshReq = makeMeshRequest();
        meshReq.setPriorityCode("pc");
        meshReq.setPriorityDisplay("pcd");
        StringBuilder template = new StringBuilder("__PRIORITY_BLOCK__");
        MESHFHIRITKRoutingActorSubs instance = new MESHFHIRITKRoutingActorSubsImpl();
        instance.setPriorityBlock(meshReq, template);
        String result = XPathManager.xpathExtractor("/extension/valueCoding/code/@value",template.toString());
        assertEquals("pc",result);
        result = XPathManager.xpathExtractor("/extension/valueCoding/display/@value",template.toString());
        assertEquals("pcd",result);
    }

    public class MESHFHIRITKRoutingActorSubsImpl extends MESHFHIRITKRoutingActorSubs {

        public MESHFHIRITKRoutingActorSubsImpl() throws Exception {
            super();
        }

        @Override
        public String makeResponse(HashMap<String,Substitution> substitutions, Object o) throws Exception {
            return "";
        }
    }

}
