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
package uk.nhs.digital.mait.tkwx.tk.boot;
import java.util.HashMap;
import java.util.Set;
/**
 *
 * @author Damian Murphy murff@warlock.org
 */
public class ServiceManager {
    
    private HashMap<String, ToolkitService>services = null;
    private static final ServiceManager me = new ServiceManager();
    // hold a back pointer to the toolkit simulator so we can start new services in Test
    private static ToolkitSimulator tks = null;
    
    private ServiceManager() {
        services = new HashMap<>();
    }
    
    public static ServiceManager getInstance() { return me; }
        
    public static ServiceManager getInstance(ToolkitSimulator t) { if ( tks == null) {tks = t;} return getInstance(); }
    
    public static ToolkitSimulator getToolkitSimulator() {return tks;} 

    void addService(String n, ToolkitService s) { services.put(n, s); }
    
    public Set<String> getServiceNames() { return services.keySet(); }
    
    public ToolkitService getService(String n) { return services.get(n); }
    
}
