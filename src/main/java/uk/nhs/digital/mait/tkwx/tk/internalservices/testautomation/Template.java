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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TemplateContext;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.replaceTkwroot;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;

/**
 * Class representing a message template loaded from a file, which is used to
 * instantiate test messages.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Template {

    private String name = null;
    private String filename = null;
    private String template = null;

    private static final SimpleDateFormat HL7FORMATDATE = new SimpleDateFormat(HL7FORMATDATEMASK);
    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    /**
     * antlr parser constructor
     *
     * @param templateCtx
     * @throws java.lang.Exception
     */
    public Template(TemplateContext templateCtx)
            throws Exception {
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
        name = templateCtx.templateName().getText();
        filename = templateCtx.getChild(1).getText();
        template = Utils.readFile2String(replaceTkwroot(filename));
    }

    public String getName() {
        return name;
    }

    String makeMessage(String recordid, DataSource d)
            throws Exception {
        if (d == null) {
            return template;
        }
        String id = null;
        StringBuilder sb = new StringBuilder(template);
        if ((recordid == null) || recordid.contentEquals("NEXT")) {
            id = d.getNextId();
            if (id == null) {
                throw new Exception("Data source " + d.getName() + " has no next record");
            }
        } else {
            id = recordid;
        }
        for (String s : d.getTags()) {
            String v = resolveDataValue(d.getValue(id, s));
            substitute(sb, s, v);
        }
        return sb.toString();
    }

    static String resolveDataValue(String v)
            throws Exception {
        if (v == null) {
            return v;
        }
        if (!v.startsWith("__")) {
            return v;
        }
        if (v.contentEquals("__HL7_DATE__")) {
            return HL7FORMATDATE.format(new Date());
        }
        if (v.contentEquals("__ISO_8601_DATE__")) {
            return ISO8601FORMATDATE.format(new Date());
        }
        if (v.contentEquals("__UCASE_UUID__")) {
            return randomUUID().toString().toUpperCase();
        }
        if (v.contentEquals("__LCASE_UUID__")) {
            return randomUUID().toString().toLowerCase();
        }
        if (v.startsWith("__SYSTEM_PROPERTY__:")) {
            String p = v.substring(v.indexOf(":") + 1);
            String q = System.getProperty(p);
            return (q == null) ? "" : q;
        }
        return "";
    }
}
