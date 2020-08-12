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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Stoppable;
 /**
 * Class representing a complete test automation script. This acts as a container
 * for the automation classes and is responsible for marshalling log files,
 * constructing reports, and running ancillary services such as the Validator.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class Script {
    
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(FILEDATEMASK);
    
    private final ArrayList<Schedule> schedules = new ArrayList<>();
    private String name = null;
    private Properties bootProperties = null;
    private File runDirectory = null;
    private String simulatorRules = null;
    private boolean stopWhenComplete = false;

    protected ToolkitSimulator toolkitSimulator = null;
    protected String validatorConfig = null;
    protected ToolkitService validator = null;
    protected ReportWriter reportWriter = null;
    
    /**
     * public constructor
     */
    public Script() {}
    
    public void addSchedule(Schedule s) { schedules.add(s); }
    public void setStopWhenComplete() { stopWhenComplete = true; }
    
    public void setName(String n) { name = n; }
    public void setSimulatorRules(String r) { simulatorRules = r; }
    
    public void setProperties(Properties p) { bootProperties = p; }
    
    Properties getBootProperties() { return bootProperties; }
    String getProperty(String s) { return bootProperties.getProperty(s); }
    File getRunDirectory() { return runDirectory; }
    ToolkitSimulator getSimulator() { return toolkitSimulator; }
    String getName() { return name; }
    
    public void setValidatorConfig(String v) {
        validatorConfig = v; 
    }
    
    private void startValidator() 
        throws Exception 
    { 
        // Boot a default Validator ready for reconfiguring in the schedule
        validator = (ToolkitService)ServiceManager.getInstance().getService("Validator");
            validator.boot(toolkitSimulator, bootProperties, "TKW AutoTest Response Validator");
    }
    
        
    void log(ReportItem r) {
        reportWriter.log(r);
    }
    
    public void clear() {
        runDirectory = null;
        reportWriter = null;
    }
    
    /**
     * 
     * @param t ToolkitSimulator object
     * @throws Exception 
     */
    public void execute(ToolkitSimulator t)
            throws Exception
    {
        System.setProperty("tkw.internal.runningautotest", "true");
        toolkitSimulator = t;
        if (reportWriter != null) {
            throw new Exception("Script " + name + " already running");
        }
        String runid = name + "_" + FORMAT.format(new Date());
        makeLogs(runid);

        if (validatorConfig != null) {
            startValidator();
        }
        
        for (Schedule schedule : schedules) {
            schedule.setSimulator(simulatorRules);
            schedule.execute(this, runid);
        }
        if (stopWhenComplete) {
            stopSimulator();
        }
        System.getProperties().remove("tkw.internal.runningautotest");
        reportWriter.dump();
    }
    
    /**
     * after an appropriate delay stop any stoppable running transports
     * @throws Exception 
     */
    public void stopSimulator()
            throws Exception
    {
        int simWait = Integer.parseInt(getProperty("tks.autotest.asynchronousacknowledgementsimulator.wait"));
        try {
            System.out.println("Waiting " + simWait + " ms for any Async Acknowledgements");
            synchronized (this) {
                wait(simWait);
            }
        } catch (InterruptedException e) {
        }
        
        // stop any stoppable running transport servcies
        for ( String serviceName : ServiceManager.getInstance().getServiceNames()) {
            if ( serviceName.endsWith("Transport")){
                ToolkitService tks = ServiceManager.getInstance().getService(serviceName);
                if ( tks instanceof Stoppable) {
                    ((Stoppable)tks).stop();
                }
            }
        }
    }
    
    private void makeLogs(String runid) 
            throws Exception
    {
        StringBuilder rootDir = new StringBuilder(bootProperties.getProperty(RUNROOT_PROPERTY));
        rootDir.append(System.getProperty("file.separator"));
        rootDir.append(runid);
        runDirectory = new File(rootDir.toString());
        if (!runDirectory.mkdirs()) {
            throw new Exception("Failed to make root directory: " + runDirectory.getAbsolutePath());
        }
        reportWriter = new ReportWriter(this, runDirectory);
    }
}
