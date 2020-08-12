/*
  Copyright 2012  Richard Robinson rick@robinsonhq.com

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

import java.io.IOException;
import java.net.Socket;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
* A class to time connections on the socket to ensure that if a connection is 
* pipelining requests i.e. connection:close has not been called that the connection
* will stay open for a definable finite period
* 
* @author Richard Robinson rrobinson@hscic.gov.uk
*/
public class HttpTimer extends Thread{
    private static final String HTTPEXPIREPROPERTY = "tks.HttpTransport.pipeline.persistperiod";
    private final Socket socket;
    private long expireTime;
    private boolean stopTimer;
    private int expirePeriod = 15000;

    public HttpTimer(Socket sock) {
        socket = sock;
        try {
            Configurator config = Configurator.getConfigurator();
            String s = config.getConfiguration(HTTPEXPIREPROPERTY);
            expirePeriod = Integer.parseInt(s);
            if (expirePeriod < 0) {
                throw new Exception("HTTP Expire Period is not a positive integer: " + expirePeriod);
            }
        }
        catch (Exception e) {
            System.out.println("HTTP Persist period (tks.HttpTransport.pipeline.persistperiod) is not an integer" + e);
        }
        stopTimer = false;
        reset();
        this.start();
    }

    /**
     * Counts time until the HTTP timeout period has expired when it closes down
     * the connection
     */
    @Override
    public void run(){
        this.setName("HttpTimerThread");
        while(expireTime > System.currentTimeMillis()|| expirePeriod == 0){
            if(stopTimer){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("HTTPTimer Sleep interrupted " + ex);
            }
        }
        if(!stopTimer){
            try{
                socket.close();
//                System.out.println("HTTP Connection closed at: "+ System.currentTimeMillis());
            } catch(IOException i){};
        }
        
            
            
    }

    /**
     * A method to reset the timer to the original timeout period as defined
     * by the timeout configuration
     */
    public void reset(){
        expireTime = System.currentTimeMillis() + expirePeriod;
    }

    /**
     * A method to stop the timer without closing the connection as this is 
     * expected to be handled by the Pipelining queue which has the responsibility
     * in normal operation
     */
    public void stopTimer(){
        stopTimer = true;
    }
}
