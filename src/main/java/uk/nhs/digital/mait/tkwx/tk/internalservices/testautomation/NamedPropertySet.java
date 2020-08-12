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

import java.util.ArrayList;
import java.util.Properties;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.NamedPropertySetDirective.PropertySetOperation;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.NamedPropertySetContext;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Class representing a named set of properties (specifically, individual
 * property <i>changes</i>) in a test automation script. The named property set
 * includes the "directives" setting or unsetting properties and optionally a
 * "baseline" (the original set of values) such that property sets can be
 * applied either cumulatively, from a single initial condition, or to re-set to
 * the initial condition.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class NamedPropertySet
        implements Linkable {

    public static final String BASELINE = "base";
    private String name = null;
    private final ArrayList<NamedPropertySetDirective> directives = new ArrayList<>();
    private boolean isBase = false;
    private Properties base = null;

    /**
     * antlr parser constructor First token is the property set name. Subsequent
     * tokens are either SET or REMOVE followed by a property name and, in the
     * case of SET, a value (or function descriptor). These are built into a
     * list of NamedPropertySetDirective instances.
     *
     * @param namedPropertySetCtx
     * @throws java.lang.Exception
     */
    public NamedPropertySet(NamedPropertySetContext namedPropertySetCtx)
            throws Exception {
        name = namedPropertySetCtx.propertySetName().getText();
        for (AutotestParser.PropertySetDirectiveContext propertySetDirective : namedPropertySetCtx.propertySetDirective()) {
            PropertySetOperation op = NamedPropertySetDirective.resolveOperation(propertySetDirective.getChild(1).getText());
            if (op == PropertySetOperation.UNDEFINED) {
                throw new Exception("Invalid property set syntax in " + name + " : Unrecognised directive: " + propertySetDirective.getChild(1).getText());
            }
            String p = propertySetDirective.propertyName().getText();
            String v = null;
            if (op == PropertySetOperation.SET) {
                // just hand off the arg line including spaces for parsing no point in reconstrcting from parsed text
                // since we need to support the single line constructor for BASE
                v = Utils.getTextFromAntlrRule(propertySetDirective.psArg());
            }
            directives.add(new NamedPropertySetDirective(op, p, v));
        }
    }

    /**
     * public Constructor Instantiated via the given Properties object.
     *
     * @param p Properties object
     * @throws Exception
     */
    public NamedPropertySet(Properties p)
            throws Exception {
        isBase = true;
        name = BASELINE;
        base = new Properties();
        for (String s : p.stringPropertyNames()) {
            base.setProperty(s, p.getProperty(s));
        }
        for (String s : base.stringPropertyNames()) {
            directives.add(new NamedPropertySetDirective(PropertySetOperation.SET, s, base.getProperty(s)));
        }
    }

    /**
     * Updates the underlying "original" properties, for when this named
     * property set is re-set.
     *
     * @param p property name String
     * @param v property value String
     */
    public void updateBase(String p, String v) {
        if (!isBase) {
            return;
        }
        base.setProperty(p, v);
        try {
            Configurator c = Configurator.getConfigurator();
            c.setConfiguration(p, v);
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, NamedPropertySet.class.getName(), "Error updating base properties for schedule: " + e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Updates the underlying properties with the given object.
     *
     * @param p
     * @throws Exception
     */
    public void resetBase(Properties p)
            throws Exception {
        if (!isBase) {
            throw new Exception("Not base property set: " + name);
        }
        p.clear();
        for (String s : base.stringPropertyNames()) {
            p.setProperty(s, base.getProperty(s));
        }
    }

    public boolean isBase() {
        return isBase;
    }

    /**
     * Resets the passed Configurator object with the base set.
     *
     * @param c Configurator object
     * @throws Exception
     */
    public void resetBase(Configurator c)
            throws Exception {
        if (!isBase) {
            throw new Exception("Not base property set: " + name);
        }
        c.clear();
        for (String s : base.stringPropertyNames()) {
            c.setConfiguration(s, base.getProperty(s));
        }
    }

    /**
     * Updates the passed Properties object with the set of directives Also
     * updates the Configurator
     *
     * @param p Properties object
     * @throws Exception
     */
    public void update(Properties p)
            throws Exception {
        for (NamedPropertySetDirective d : directives) {
            switch (d.getOp()) {
                case SET:
                    p.setProperty(d.getPropertyName(), d.getValue());
                    break;
                case REMOVE:
                    p.remove(d.getPropertyName());
                    break;
                default:
                    throw new Exception("Unrecognised property directive in " + name);
            }
        }
    }

    /**
     * Updates the passed Configurator object with the set of directives Not
     * currently called
     *
     * @param c Configurator object
     * @throws Exception
     */
    public void update(Configurator c)
            throws Exception {
        for (NamedPropertySetDirective d : directives) {
            switch (d.getOp()) {
                case SET:
                    c.setConfiguration(d.getPropertyName(), d.getValue());
                    break;
                case REMOVE:
                    c.removeConfiguration(d.getPropertyName());
                    break;
                default:
                    throw new Exception("Unrecognised property directive in " + name);
            }
        }
    }

    @Override
    public void link(ScriptParser p)
            throws Exception {

    }
}
