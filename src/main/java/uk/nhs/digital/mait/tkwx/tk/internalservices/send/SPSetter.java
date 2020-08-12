/*
 Copyright 2015  Simon Farrow simon.farrow1@hscic.gov.uk

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
import java.util.function.Consumer;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Handles setting of system properties from tks properties via Properties objects or Configurators. 
 * Encapsulates some common patterns for setting system properties in tkw
 * can use lambda functions for item specific behaviour if required.
 * Provides a family of static iterators executeSettings methods enabling multiple property setting activities
 * to be expressed concisely.
 * @author sifa2
 */
public class SPSetter {

    private String tksProperty; // source property name may refer to a propeties object or a Configurator
    private String systemProperty; // system property to be set
    private Object warning;  // may be a String or an Exception or a lambda function (Consumer<String>) used when source property is not valid to be set
    private Consumer<String> setter; // lambda function specifying behaviour when source property is valid to be set

    //array indexes into string array used by execute and the relevant SPSetter constructor
    private static final int TKS_PROPERTY = 0;
    private static final int SYSTEM_PROPERTY_TO_SET = 1;
    private static final int WARNING_MESSAGE = 2;

    /**
     * public constructor taking a 2 or 3 element String array
     *
     * @param sa array of strings of length 2 or 3 
     * 0 - tksProperty 
     * 1 - systemProperty
     * 2 - [warning]
     */
    public SPSetter(String[] sa) {
        warning = null;
        setter = null;
        switch (sa.length) {
            case 3:
                this.warning = sa[WARNING_MESSAGE];
            // drop through
            case 2:
                this.tksProperty = sa[TKS_PROPERTY];
                this.systemProperty = sa[SYSTEM_PROPERTY_TO_SET];
                break;
            default:
                throw new IllegalArgumentException("Invalid String array length ("+sa.length+")");
        }
    }

    /**
     * public constructor overload
     *
     * @param tksProperty source property name
     * @param systemProperty system property name
     * @param setter lambda function invoked when source property has non null value
     * @throws java.lang.Exception
     */
    public SPSetter(String tksProperty, String systemProperty, Consumer<String> setter) throws Exception {
        this(tksProperty, systemProperty, null, setter);
    }

    /**
     * public constructor overload only tks property and action
     *
     * @param tksProperty tkw source property name
     * @param setter lambda function invoked when source property has non null value
     * @throws java.lang.Exception
     */
    public SPSetter(String tksProperty, Consumer<String> setter) throws Exception {
        this(tksProperty, null, null, setter);
    }

    /**
     * public constructor overload
     * 
     * @param tksProperty tkw source property name
     * @param systemProperty system property name
     * @param warning Exception  to be thrown when source property is null or empty
     * @throws java.lang.Exception 
     */
    public SPSetter(String tksProperty, String systemProperty, Exception warning) throws Exception {
        this(tksProperty, systemProperty, warning, null);
    }

    
    /**
     * public constructor (main constructor)
     *
     * @param tksProperty tkw source property name
     * @param systemProperty  system property name
     * @param warning either a String (output to stderr) or Exception (to be thrown)
     * @param setter lambda function invoked when source property has non null value
     * @throws java.lang.Exception
     */
    public SPSetter(String tksProperty, String systemProperty, Object warning, Consumer<String> setter) throws Exception {
        this.warning = warning;
        this.tksProperty = tksProperty;
        this.systemProperty = systemProperty;
        this.setter = setter;
    }

       /**
     * public constructor (main constructor)
     *
     * @param tksProperty tkw source property name
     * @param systemProperty  system property name
     * @param warning lambda function invoked when source property has null value
     * @param setter lambda function invoked when source property has non null value
     * @throws java.lang.Exception
     */
    public SPSetter(String tksProperty, String systemProperty, Consumer<String> warning, Consumer<String> setter) throws Exception {
        this.warning = warning;
        this.tksProperty = tksProperty;
        this.systemProperty = systemProperty;
        this.setter = setter;
    }
    /**
     * overload do the business for the property from a configurator
     * @param configurator the configurator object
     * @throws java.lang.Exception
     */
    public void execute(Configurator configurator) throws Exception {
        execute(configurator.getConfiguration(tksProperty));
    }

    /**
     * overload do the business for the property from a properties object
     * @param properties object
     * @throws Exception 
     */
    public void execute(Properties properties) throws Exception {
        execute(properties.getProperty(tksProperty));
    }

    /**
     * core execute method do the business for the property string value
     * if there's an associated lambda function (setter) specified execute it (there may not be)
     * warning objects can either be Strings that are printed to system.err or Exceptions which are thrown.
     * 
     * @param s string value to be set into the system property
     * @throws Exception 
     */
    private void execute(String s) throws Exception {
        if ((s != null) && (s.trim().length() != 0)) {
            if (setter != null) {
                setter.accept(s);
            }
            if (systemProperty != null) {
                System.setProperty(systemProperty, s);
            }
        } else if (warning != null) {
            if (warning instanceof String) {
                System.err.println(warning);
            } else if (warning instanceof Exception) {
                throw (Exception) warning;
            } else if (warning instanceof Consumer) {
                Consumer<String> consumer = (Consumer)warning;
                consumer.accept(s);
            } else {
                throw new IllegalArgumentException("The warning argument is of the wrong type:" + warning.getClass().getName());
            }
        }
    }


    /**
     * loop through setting required system properties for the contents of the
     * array of String arrays
     *
     * @param properties the source properties object to use
     * @param settings array of string arrays
     * @throws java.lang.Exception
     */
    public static void executeSettings(Properties properties, String[][] settings) throws Exception {
        for (String[] as : settings) {
            new SPSetter(as).execute(properties);
        }
    }

    /**
     * loop through setting required system properties for the contents of the
     * array of String arrays from a source Configurator
     *
     * @param configurator the source configurator to use
     * @param settings array of string arrays
     * @throws java.lang.Exception
     */
    public static void executeSettings(Configurator configurator, String[][] settings) throws Exception {
        for (String[] as : settings) {
            new SPSetter(as).execute(configurator);
        }
    }

    
    /**
     * loop through setting required system properties for the contents of the
     * SPSetter array from a source properties object
     *
     * @param properties the source properties object to use
     * @param settings array of SPSetter objects
     * @throws java.lang.Exception
     */
    public static void executeSettings(Properties properties, SPSetter[] settings) throws Exception {
        for (SPSetter setter : settings) {
            setter.execute(properties);
        }
    }
    
    /**
     * loop through setting required system properties for the contents of the
     * SPSetter array from a source Configurator
     *
     * @param configurator the source configurator to use
     * @param settings array of SPSetter objects
     * @throws java.lang.Exception
     */
    public static void executeSettings(Configurator configurator, SPSetter[] settings) throws Exception {
        for (SPSetter setter : settings) {
            setter.execute(configurator);
        }
    }
}
