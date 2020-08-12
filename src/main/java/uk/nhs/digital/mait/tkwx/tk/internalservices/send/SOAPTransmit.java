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
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * see ITK_Correspondence tks.wrapper.SOAP.class uk.nhs.digital.mait.tkwx.tk.internalservices.send.SOAPTransmit
 * @author DAMU2
 */
public class SOAPTransmit 
    extends AbstractWrapper
{
    private static final String TEMPLATE = "tks.wrapper.SOAP.template";
    private static final String TOURL = "tks.wrapper.SOAP.tourl";
    private static final String FROMURL = "tks.wrapper.SOAP.fromurl";
    private static final String REPLYTOURL = "tks.wrapper.SOAP.replytourl";
    
    
    @Override
    public void init(ToolkitSimulator t, String n, Properties p)
            throws Exception
    {
        super.init(t, n, p);
        String tpfile = p.getProperty(TEMPLATE);
        template = Utils.readFile2String(tpfile);
    }

    @Override
    public boolean sendMessage(String dir, String file, String address)
            throws Exception
    {
        String s = Utils.readFile2String(dir, file);
        if (signEnvelope) {
            s = signDistributionEnvelope(s);
        }
        s = wrap(s);
        // TODO: Send
        return false;
    }
    
    @Override
    protected String wrap(String msg)
            throws Exception
    {
        // TODO NEXT: Build the SOAP header parts, and substitute in the message
        // body
        return null;
    }
}
