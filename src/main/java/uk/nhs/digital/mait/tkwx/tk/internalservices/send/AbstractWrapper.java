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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import java.util.Properties;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Experimental code for enabline flexible transmission and DE Signing
 * See ITK_Correspondence tkw.signeddistributionenvelope
 * 
 * @author DAMU2
 */
public abstract class AbstractWrapper {

    protected static final String SIGN_DE = "tks.wrapper.signdistributionenvelope";

    protected ToolkitSimulator simulator = null;
    protected String typeName = null;
    protected Properties bootProperties = null;
    protected String template = null;

    protected boolean signEnvelope = false;

    public void init(ToolkitSimulator t, String n, Properties p)
            throws Exception {
        simulator = t;
        typeName = n;
        bootProperties = p;

        signEnvelope = isY(p.getProperty(SIGN_DE));
    }

    public abstract boolean sendMessage(String dir, String file, String address)
            throws Exception;

    protected abstract String wrap(String msg)
            throws Exception;

    protected boolean doSender(String msg, String addr, String ofile, int chunkSize)
            throws Exception {
//        ToolkitService tksnd = simulator.getService("Sender");
        ToolkitService tksnd = ServiceManager.getInstance().getService("Sender");
        if (tksnd != null) {
            SenderRequest sr = new SenderRequest(msg, null, addr);
            if (System.getProperty("tkw.internal.runningautotest") != null) {
                sr.setOriginalFileName(ofile);
            }
            if (chunkSize != 0) {
                sr.setChunkSize(chunkSize);
            }
            tksnd.execute(sr);
            return true;
        } else {
            return false;
        }
    }

    protected String signDistributionEnvelope(String de)
            throws Exception {
        throw new Exception("Not yet implemented");
    }
}
