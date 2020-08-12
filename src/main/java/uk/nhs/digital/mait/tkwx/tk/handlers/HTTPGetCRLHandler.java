/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
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
package uk.nhs.digital.mait.tkwx.tk.handlers;
import java.io.File;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

 /**
 * Handler implementation for HTTP GET requests for a CRL. TKW wants a single
 * class as the handler for a request, but this request will have been put in its
 * own thread by the HTTP server. So for thread safety this class' handle() method
 * creates a separate HTTPGetCRLWorker instance to handle this particular request,
 * and calls handle() on that.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class HTTPGetCRLHandler 
    extends uk.nhs.digital.mait.tkwx.tk.boot.ToolkitHttpHandler
{

    private static final String TLSCRLFILEPROPERTY = "tks.tls.crl";
    private static final String SIGNINGCRLFILEPROPERTY = "tks.signing.crl";
    private static final String COMMONCRLFILEPROPERTY = "tks.crl";
    
    private byte[] tlsCrl = null;
    private byte[] signingCrl = null;
    private byte[] commonCrl = null;
    
    byte[] getTlsCrl() {return tlsCrl;}
    byte[] getSigningCrl() {return signingCrl;}
    byte[] getCommonCrl() {return commonCrl;}

    @Override
    public void setToolkit(HttpTransport t)
        throws Exception
    {
        super.setToolkit(t);
        String p = null;
        Configurator config = Configurator.getConfigurator();
        p = config.getConfiguration(TLSCRLFILEPROPERTY);
        tlsCrl = load(p);
        p = config.getConfiguration(SIGNINGCRLFILEPROPERTY);
        signingCrl = load(p);
        p = config.getConfiguration(COMMONCRLFILEPROPERTY);
        commonCrl = load(p);
    }

    /**
     * 
     * @param path
     * @param paramsIgnored
     * @param req
     * @param resp
     * @throws HttpException 
     */
    @Override
    public void handle(String path, String paramsIgnored, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        HTTPGetCRLWorker s = new HTTPGetCRLWorker(this);
        s.handle(path, paramsIgnored, req, resp);
    }
    
    /**
     * 
     * @param file
     * @return byte array
     * @throws Exception 
     */
    private byte[] load(String file)
            throws Exception
    {
        if ((file == null) || (file.trim().length() == 0)) {
            return null;
        }
        File f = new File(file);
        if (!f.exists()) {
            Logger.getInstance().log(SEVERE,HTTPGetCRLHandler.class.getName(),"CRL file " + f + " not found");
            return null;
        }
        return Utils.readFile2String(file).getBytes();
    }
}
