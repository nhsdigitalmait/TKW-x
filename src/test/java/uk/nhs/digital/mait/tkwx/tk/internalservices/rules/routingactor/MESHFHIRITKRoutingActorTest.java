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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest.makeMeshRequest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_OUT_FOLDER;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class MESHFHIRITKRoutingActorTest {

    public MESHFHIRITKRoutingActorTest() {
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
     * Test of makeResponse method, of class MESHFHIRITKRoutingActor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String,Substitution> substitutions = new HashMap<>();
        substitutions.put("__IAID_BUNDLE_UUID__", new Substitution("__IAID_BUNDLE_UUID__",Substitution.SubstitutionType.LITERAL,null,"uidvalue",null));
        MeshRequest mr = makeMeshRequest();
        MESHFHIRITKRoutingActor instance = new MESHFHIRITKRoutingActor();
        String expResult = "";
        String result = instance.makeResponse(substitutions, mr);
        assertEquals(expResult, result);

        // 2 pairs of dat and ctl for inf and bus
        File of = new File(TEST_MESH_OUT_FOLDER);
        assertEquals(4, of.listFiles().length);
    }

}
