/*
  Copyright 2018  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.mesh;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Singleton class to register parts of a MESH transaction and allow access to
 * the originating request for (mainly) logging purposes but also in the future
 * for resending
 *
 * @author riro
 */
public class OpenMeshMessageRegister extends Thread {

    private static final String REPORTTIMEOUTPERIODPROPERTY = "tks.MeshTransport.reporttimeoutperiod";
    private boolean stopTimer;
    private int expirePeriod = 3600; // default period = 1 hour
    private HashMap<String, MeshMessage> openMeshMessages = new HashMap<>();
    private HashMap<String, Long> timers = new HashMap<>();

    private OpenMeshMessageRegister() {
        try {
            Configurator config = Configurator.getConfigurator();
            String s = config.getConfiguration(REPORTTIMEOUTPERIODPROPERTY);
            expirePeriod = Integer.parseInt(s);
            if (expirePeriod < 0) {
                throw new Exception("report timeout Period is not a positive integer: " + expirePeriod);
            } else {
                //convert to millis
                expirePeriod = expirePeriod * 1000;
            }
        } catch (Exception e) {
            System.out.println("Report Timout period (" + REPORTTIMEOUTPERIODPROPERTY + ") is not an integer" + e);
        }
        stopTimer = false;
        this.start();
    }

    /**
     * Counts time until the timeout period for each entry has expired when it
     * removes them from the relevant hashmaps the connection
     */
    @Override
    public void run() {
        this.setName("OpenMeshMessageRegisterThread");
        while (!stopTimer) {
            try {
                Thread.sleep(1000);
                Iterator it = timers.entrySet().iterator();
                while (it.hasNext()) {
                    Entry entry = (Entry) it.next();
                    Long value = (Long) entry.getValue();
                    String key = (String) entry.getKey();
                    if (System.currentTimeMillis() > value) {
                        it.remove();
                        MeshMessage meshMessage = openMeshMessages.get(key);
                        System.out.println("key = "+key);
                        meshMessage.log("MeshMessage timeout period has expired @ " + new Date() + " with id = " + key, 2, true);
                        deRegisterMeshRequest(key);
                        break;
                    }
//                    System.out.println("time: "+ new Date()+ " key = "+ key +  " timers: "+value+ " messages: "+openMeshMessages.get(key));
                }
            } catch (InterruptedException ex) {
                System.out.println("OpenMeshMessage sleep interrupted " + ex);
            }
        }
        System.out.println("OpenMeshMessage stopped");

    }

    /**
     * A method to register a Mesh Message with an id from the Mesh Control
     * File.
     *
     * @param mm MeshMessage to be used to log responses
     * @param id Id from a Mesh control file
     */
    public synchronized void registerMeshRequest(MeshMessage mm, String id) {
        System.out.println("Register id = " + id);
        Long expire = System.currentTimeMillis() + expirePeriod;
        if (openMeshMessages.get(id) != null) {
            timers.replace(id, expire);
        } else {
            openMeshMessages.put(id, mm);
            timers.put(id, expire);
        }
    }

    /**
     * A method to deregister a Mesh Message with a localId from the Mesh
     * Control File.
     *
     * @param id Local Id from a Mesh control file
     */
    public synchronized void deRegisterMeshRequest(String id) {
        System.out.println("Deregister id = " + id);
        MeshMessage mm = openMeshMessages.get(id);
        if (mm != null) {
            openMeshMessages.remove(id);
            timers.remove(id);
            // should the log be closed? Only if there are no more instances of the same LoggingFileOutputStream
            boolean closelog = true;
            Iterator it = openMeshMessages.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();
                MeshMessage value = (MeshMessage) entry.getValue();
                String key = (String) entry.getKey();
                if(value.getLoggingStream()==mm.getLoggingStream()){
                    System.out.println("dont close Log");
                    closelog = false;
                    break;
                }
            }
            if(closelog){
                mm.closeLog();
            }

        }

    }

    /**
     * A method to allow access to the MeshMethod class related to a registered
     * id
     *
     * @param id
     * @return MeshMessage
     */
    public synchronized MeshMessage getOpenMeshMessage(String id) {
        MeshMessage mm = openMeshMessages.get(id);
        if (mm == null) {
            System.out.println("Failed to access id = " + id);
        } else {
            System.out.println("Successfuly accessed id = " + id);
        }
        return mm;
    }

    /**
     * A method to stop the timer
     */
    public void stopTimer() {
        stopTimer = true;
    }

    public static OpenMeshMessageRegister getInstance() {
        return OpenMeshMessageRegisterHolder.INSTANCE;

    }

    private static class OpenMeshMessageRegisterHolder {

        private static final OpenMeshMessageRegister INSTANCE = new OpenMeshMessageRegister();
    }
}
