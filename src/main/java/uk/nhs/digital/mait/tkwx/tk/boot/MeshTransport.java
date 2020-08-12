/*
 Copyright 2017  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import uk.nhs.digital.mait.tkwx.mesh.MeshMailboxWatcher;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Mesh Transport service. This class starts all the handlers specified in the
 * configuration file
 *
 *
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class MeshTransport
        implements ToolkitService,
        uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable {

    private static final String TKWROOT = "tks.";
    private static final String NAMELIST = ".namelist";
    public static final String NAMEIN = ".in";
    public static final String NAMEFORWARDER = ".forwarder";
    public static final String NAMESENT = ".sent";
    private static final String NAMECLASS = ".class";

    private ToolkitSimulator simulator = null;
    private String serviceName = null;
    private Properties bootProperties = null;
    private HashMap<String, MeshMailboxWatcher> meshDirInstances = new HashMap<>();
    private ArrayList<ToolkitMeshHandler> handlers = null;

    public MeshTransport() {
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void reconfigure(Properties p)
            throws Exception {
        for (MeshMailboxWatcher mmw : meshDirInstances.values()) {
            mmw.close();
        }
        boot(simulator, p, serviceName);
    }

    @Override
    public String reconfigure(String what, String value)
            throws Exception {
        throw new Exception("Cannot reconfigure " + what);
    }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        bootProperties = p;
        simulator = t;
        serviceName = s;

        // Call all the Mesh mailboxes to be monitored
        String dirNames = p.getProperty(TKWROOT + serviceName + NAMELIST);
        if (dirNames == null) {
            throw new Exception("ToolkitHandler booted with no MESH Directories configured: " + TKWROOT + serviceName + NAMELIST + " property not defined");
        }
        startHandlers(p, dirNames);

        System.out.println("MeshTransport service ready");
        Logger.getInstance().log("MeshTransport service ready");
    }

    private void startHandlers(Properties p, String n)
            throws Exception {
        // Calls each directory with its own handler
        HashMap<String, ToolkitMeshHandler> cache = new HashMap<>();
        handlers = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(n);
        while (st.hasMoreTokens()) {
            String meshDir = st.nextToken();
            StringBuilder sbhandler = new StringBuilder(TKWROOT);
            sbhandler.append(serviceName);
            sbhandler.append(".");
            sbhandler.append(meshDir);
            StringBuilder sbclass = new StringBuilder(sbhandler.toString());
            sbclass.append(NAMECLASS);

            ArrayList<String> mailboxeTypes = new ArrayList<>();
            mailboxeTypes.add(sbhandler.toString().concat(NAMEIN));
            mailboxeTypes.add(sbhandler.toString().concat(NAMEFORWARDER));
            mailboxeTypes.add(sbhandler.toString().concat(NAMESENT));

            String classname = p.getProperty(sbclass.toString());
            if (classname == null || classname.equals("")) {
                throw new Exception("MeshTransport does not have a classname configured for the directory: " + meshDir);
            }
            try {
                ToolkitMeshHandler h = null;
                if (cache.containsKey(classname)) {
                    h = cache.get(classname);
                } else {
                    h = (ToolkitMeshHandler) Class.forName(classname).newInstance();
                    h.setToolkit(this);
                    h.setMESHMailboxId(meshDir);
                }
                handlers.add(h);
                for (String mailboxType : mailboxeTypes) {
                    String path = p.getProperty(mailboxType);
                    if (path == null || path.equals("")) {
                        throw new Exception("MeshTransport does not have a directory configured for the directory: " + mailboxType);
                    }

                    Path dir = Paths.get(path);
                    MeshMailboxWatcher mmw = new MeshMailboxWatcher(dir, mailboxType, h);
                    meshDirInstances.put(mailboxType, mmw);
                }
            } catch (Exception e) {
                throw new Exception("ToolkitHandler boot failed: Loading handler " + classname + " : " + e.getMessage());
            }

        }

    }

    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        return new ServiceResponse(0, TOOLKIT_SIMULATOR);
    }
}
