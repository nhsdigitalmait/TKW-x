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

import java.lang.reflect.Method;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.NamedPropertySetDirective.PropertySetOperation.SET;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.NamedPropertySetDirective.PropertySetOperation.UNDEFINED;
import static uk.nhs.digital.mait.tkwx.util.Utils.FUNCTION_PREFIX;

/**
 * Class representing a single instruction to set a property to a value, or to
 * unset a property. Only found collected in a NamedPropertySet.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class NamedPropertySetDirective {

    public enum PropertySetOperation {
        UNDEFINED,
        SET,
        REMOVE
    }

    private PropertySetOperation operation = UNDEFINED;
    private String property = null;
    private String value = null;
    private Method function = null;
    private Object[] params = null;   // for reflection invocation
    private Class[] parameterTypes = null; // for reflection invocation
    private boolean isFunction = false;

    /**
     *
     * @param op PropertySetOperation enum (SET or REMOVE)
     * @param prop property name
     * @param val String value (for set operations) can be direct from a
     * properties value or via a synthesised entry from syntax via the parser
     * @throws Exception
     */
    public NamedPropertySetDirective(PropertySetOperation op, String prop, String val)
            throws Exception {
        operation = op;
        property = prop;
        if (operation != SET) {
            return;
        }
        value = val;
        if (value.startsWith(FUNCTION_PREFIX)) {
            isFunction = true;
            String[] localparams = value.split("\\s+");
            int lastdot = localparams[0].lastIndexOf('.');
            int classstart = FUNCTION_PREFIX.length();
            String methodname = localparams[0].substring(lastdot + 1);
            String classname = localparams[0].substring(classstart, lastdot);
            if (localparams.length > 1) {
                params = new Object[localparams.length - 1];
                parameterTypes = new Class[params.length];
                for (int i = 0; i < params.length; i++) {
                    params[i] = localparams[i+1];
                    parameterTypes[i] = String.class;
                }
            }
            function = getFunction(classname, methodname);
        }
    }

    @SuppressWarnings("unchecked")
    private Method getFunction(String classname, String methodname)
            throws Exception {
        Class c = Class.forName(classname);
        return c.getMethod(methodname, parameterTypes);
    }

    public static PropertySetOperation resolveOperation(String op) {
        if (op != null && !op.trim().isEmpty()) {
            return PropertySetOperation.valueOf(op.toUpperCase());
        }
        return UNDEFINED;
    }

    public String getPropertyName() {
        return property;
    }

    public PropertySetOperation getOp() {
        return operation;
    }

    public String getValue()
            throws Exception {
        if (!isFunction) {
            return value;
        } else {
            return (String) function.invoke(null, params);
        }
    }
}
