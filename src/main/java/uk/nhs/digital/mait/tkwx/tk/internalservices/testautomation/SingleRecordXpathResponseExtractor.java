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
import java.util.HashMap;
import javax.xml.xpath.XPathExpression;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ExtractorContext;
import uk.nhs.digital.mait.commonutils.util.ConfigurationTokenSplitter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.URL_DECODE_HTTP_HEADERS_PROPERTY;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.RegexpSubstitution;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Implementation of the ResponseExtractor that uses a separate XPath expression
 * to identify the record id of a data source to update. Also extracts data from
 * response headers
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SingleRecordXpathResponseExtractor
        implements ResponseExtractor {

    private String name = null;
    private String configFile = null;
    private final ArrayList<DataSource> registeredDataSources = new ArrayList<>();
    //                      expression    tag
    private final HashMap<XPathExpression, String> xmlExtracts = new HashMap<>();
    //                    headername tag
    private final HashMap<HeaderRegexpSubstitution, String> httpHeaderExtracts = new HashMap<>();
    private XPathExpression recordid = null;

    /**
     * wraps the header name and the associated substitution
     */
    private static class HeaderRegexpSubstitution extends RegexpSubstitution {

        private String headerName;
        private static Configurator configurator = null;
        private static boolean urlDecodeHttpHeaders = false;

        /**
         * No regexp
         *
         * @param headerName
         */
        public HeaderRegexpSubstitution(String headerName) {
            super(null, null);
            this.headerName = headerName;
        }

        /**
         * assumes $1 is the subst parameter
         *
         * @param headerName
         * @param matchRegExp
         */
        public HeaderRegexpSubstitution(String headerName, String matchRegExp) {
            super(matchRegExp, null);
            this.headerName = headerName;
        }

        /**
         *
         * @param headerName
         * @param matchRegExp
         * @param substituteRegExp
         */
        public HeaderRegexpSubstitution(String headerName, String matchRegExp, String substituteRegExp) {
            super(matchRegExp, substituteRegExp);
            this.headerName = headerName;
        }

        /**
         *
         * @param hm HttpHeaderManager
         * @return substituted header value
         */
        public String substitute(HttpHeaderManager hm) {
            String hv = hm.getHttpHeaderValue(headerName);
            if (configurator == null) {
                try {
                    configurator = Configurator.getConfigurator();
                } catch (Exception ex) {
                    Logger.getInstance().log(SEVERE, SingleRecordXpathResponseExtractor.class.getName(), ex.getMessage());
                }
                urlDecodeHttpHeaders = isY(configurator.getConfiguration(URL_DECODE_HTTP_HEADERS_PROPERTY));
            }
            if (urlDecodeHttpHeaders && hv != null) {
                try {
                    hv = java.net.URLDecoder.decode(hv, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException ex) {
                }
            }
            if (hv != null) {
                return substitute(hv);
            } else {
                // header not present return ""
                return "";
            }
        }
    }

    public SingleRecordXpathResponseExtractor() {
    }

    @Override
    public void init(ExtractorContext extractorCtx) {
        try {
            name = extractorCtx.extractorName().getText();
            configFile = extractorCtx.PATH().getText();
            load();
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, SingleRecordXpathResponseExtractor.class.getName(), ex.getMessage());
        }
    }

    /**
     * This takes a String because whilst it is expected to be called by the
     * pass/fail check (which gets an InputStream), the check will have already
     * consumed the InputStream - so if it wants to run an extractor it has to
     * save the stream to a string, and pass that string here.When extracts
     * extract something, they call setValue() on any registered data sources.
     *
     * The XPath expression "recordid" is used to determine the record id. If
     * there isn't one, use "". Otherwise, cycle through them.
     *
     * @param body String containing response payload
     * @param hm HttpHeaderManager
     * @throws Exception
     */
    @Override
    public void extract(String body, HttpHeaderManager hm)
            throws Exception {
        String recordIdentity = null;

        // usually we getthe id from the response payload but sometimes
        // there is no response payload
        if (recordid == null || body.isEmpty()) {
            recordIdentity = "";
        } else {
            InputSource is = new InputSource(new StringReader(body));
            recordIdentity = recordid.evaluate(is);

            for (XPathExpression xpe : xmlExtracts.keySet()) {
                String tag = xmlExtracts.get(xpe);
                is = new InputSource(new StringReader(body));
                String value = xpe.evaluate(is);
                for (DataSource d : registeredDataSources) {
                    if (!d.isReadOnly()) {
                        d.setValue(recordIdentity, tag, value);
                    }
                }
            }
        }

        // http header values
        for (HeaderRegexpSubstitution headerSubstitution : httpHeaderExtracts.keySet()) {
            String tag = httpHeaderExtracts.get(headerSubstitution);
            String value = headerSubstitution.substitute(hm);
            if (value == null) {
                value = "";
            }
            for (DataSource d : registeredDataSources) {
                if (!d.isReadOnly()) {
                    if (recordIdentity.isEmpty()) {
                        // we may not have a payload so we may not be able to derive and id
                        // pick the first record  for that table
                        String rid = d.getNextId();
                        d.setValue(rid, tag, value);
                    } else {
                        d.setValue(recordIdentity, tag, value);
                    }
                }
            }
        }
    }

    /**
     * format id ( <xmlpath>| httpheader <headerName> [ matchregexp [substregexp]]) <tagname> 'ID' ?
     *
     * @throws Exception
     */
    private void load()
            throws Exception {
        try ( BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.length() == 0) {
                    continue;
                }
                String[] parts = (new ConfigurationTokenSplitter(line)).split();
                if (!parts[1].equals(HTTPHEADER)) {
                    XPathExpression xpe = getXpathExtractor(parts[1]);
                    if (parts.length == 3) {
                        if (parts[2].contentEquals("ID")) {
                            recordid = xpe;
                        }
                    }
                    if (xpe != recordid) {  // Yes, a check for physically the same instance
                        xmlExtracts.put(xpe, parts[0]);
                    }
                } else {
                    String headerName = parts[2];
                    switch (parts.length) {
                        case 3:
                            // no regexp
                            httpHeaderExtracts.put(new HeaderRegexpSubstitution(headerName), parts[0]);
                            break;
                        case 4:
                            // match regexp but no subst (assumes $1 for subst )
                            httpHeaderExtracts.put(new HeaderRegexpSubstitution(headerName, parts[3]), parts[0]);
                            break;
                        case 5:
                            // match and subst
                            httpHeaderExtracts.put(new HeaderRegexpSubstitution(headerName, parts[3], parts[4]), parts[0]);
                            break;
                        default:
                    }
                }
            }
        }
    }
    private static final String HTTPHEADER = "httpheader";

    @Override
    public void registerDatasourceListener(DataSource c) {
        registeredDataSources.add(c);
    }

    @Override
    public String getName() {
        return name;
    }

}
