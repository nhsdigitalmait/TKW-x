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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author simonfarrow
 */
public abstract class AbstractFHIRITKRoutingActor extends RoutingActor implements SettableErrorCode {

    /**
     * encapsulates data items to be substituted into FHIR messaging inf/bus ack
     * templates
     */
    protected class AckDetails {

        /**
         * get default values if setErrorCode not called
         *
         * @param type "ia" or "ba"
         */
        public AckDetails(String type) {
            errorCode = getPropertyValue(FHIRPREFIX + type + "." + ERROR_CODE);
            responseCode = getPropertyValue(FHIRPREFIX + type + "." + RESPONSE_CODE);
            errorText = getPropertyValue(FHIRPREFIX + type + "." + ERROR_TEXT);
            issueCode = getPropertyValue(FHIRPREFIX + type + "." + ISSUE_CODE);
            issueSeverity = getPropertyValue(FHIRPREFIX + type + "." + ISSUE_SEVERITY);
        }

        /**
         *
         * @param ackType "ia" or "ba"
         * @param errorLabel can be an error code but could be anything allowing
         * the inf and bus ack to return different error codes
         */
        public AckDetails(String ackType, String errorLabel) {
            errorCode = getPropertyValue(FHIRPREFIX + ackType + "." + ERROR_CODE, errorLabel);
            // default to the error code but the label can be anything
            if (errorCode == null) {
                errorCode = errorLabel;
            }
            responseCode = getPropertyValue(FHIRPREFIX + ackType + "." + RESPONSE_CODE, errorLabel);
            errorText = getPropertyValue(FHIRPREFIX + ackType + "." + ERROR_TEXT, errorLabel);
            issueCode = getPropertyValue(FHIRPREFIX + ackType + "." + ISSUE_CODE, errorLabel);
            issueSeverity = getPropertyValue(FHIRPREFIX + ackType + "." + ISSUE_SEVERITY, errorLabel);
        }

        /**
         * do the tag substitution for all the attributes
         *
         * @param sb
         * @throws Exception
         */
        public void substituteDetails(StringBuilder sb) throws Exception {
            substitute(sb, "__RESPONSE_CODE__", responseCode);
            substitute(sb, "__ISSUE_SEVERITY__", issueSeverity);
            substitute(sb, "__ISSUE_CODE__", issueCode);
            substitute(sb, "__ERRORCODE__", errorCode);
            substitute(sb, "__ERRORTEXT__", errorText);
        }

        /**
         * lookup in configurator
         *
         * @param propertyName
         * @return
         */
        private String getPropertyValue(String propertyName) {
            String s = configurator.getConfiguration(propertyName);
            if (!Utils.isNullOrEmpty(s)) {
                return s.contentEquals("NONE") ? "" : s;
            } else {
                return null;
            }
        }

        /**
         * overload with error code
         *
         * @param propertyName
         * @param errorCode
         * @return
         */
        private String getPropertyValue(String propertyName, String errorCode) {
            String s = configurator.getConfiguration(propertyName + "." + errorCode);
            if (!Utils.isNullOrEmpty(s)) {
                return s;
            }
            return null;
        }

        private String errorCode;
        protected final String responseCode;
        private final String errorText;
        private final String issueCode;
        private final String issueSeverity;

        private static final String FHIRPREFIX = "tks.routingactor.fhir.";

        // propertyset name attribute labels 
        private static final String ERROR_CODE = "errorcode",
                ERROR_TEXT = "errortext",
                RESPONSE_CODE = "responsecode",
                ISSUE_CODE = "issuetext",
                ISSUE_SEVERITY = "issueseverity";

    } // class AckDetails

    protected static final String FHIRAPPACKTEMPLATE = "tks.routingactor.fhir.appacktemplate";
    private static final String FHIRBUSACKTEMPLATE = "tks.routingactor.fhir.busacktemplate";

    // ack types
    private static final String BA = "ba";
    private static final String IA = "ia";

    protected String appAckTemplate = null;
    protected String busAckTemplate = null;
    protected String appResponseDeliveryUrl = null;

    // Inf Ack values
    protected AckDetails ia = null;

    // Bus Ack values
    protected AckDetails ba = null;

    protected Configurator configurator = null;

    /**
     * public constructor
     *
     * @throws Exception
     */
    public AbstractFHIRITKRoutingActor() throws Exception {
        super();
        configurator = Configurator.getConfigurator();
        ia = new AckDetails(IA);
        ba = new AckDetails(BA);
    }

    @Override
    public void init() throws Exception {
        super.init();
        appAckTemplate = loadTemplate(FHIRAPPACKTEMPLATE);
        busAckTemplate = loadTemplate(FHIRBUSACKTEMPLATE);
    }

    /**
     * using a label not a specific error code allows combinations of IA and BA
     * with different error codes
     *
     * @param errorLabel
     */
    @Override
    public void setErrorCode(String errorLabel) {
        if (!Utils.isNullOrEmpty(errorLabel)) {
            ia = new AckDetails(IA, errorLabel);
            ba = new AckDetails(BA, errorLabel);
        }
    }
}
