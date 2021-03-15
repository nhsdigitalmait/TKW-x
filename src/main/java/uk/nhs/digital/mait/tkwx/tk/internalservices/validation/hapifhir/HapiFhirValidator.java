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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import java.util.ArrayList;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Validation Class to take the FHIR message and separate it into two separate
 * messages. This is done because the HAPI FHIR validator cannot validate a
 * bundle within a bundle and this is the structure which has been chosen. The 2
 * separate messages are then validated.
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class HapiFhirValidator
        implements ValidationCheck, VariableConsumer {

    private final String HAPIFHIR = "tks.validator.hapifhirvalidator";
    private static final String FILTER = ".filter.";
    private final ArrayList<FilterTotals> informationFilter = new ArrayList<>();
    private final ArrayList<FilterTotals> warningFilter = new ArrayList<>();
    private final ArrayList<FilterTotals> errorFilter = new ArrayList<>();
    private final ArrayList<FilterTotals> fatalFilter = new ArrayList<>();
    private static Configurator config;
    private HapiFhirValidatorEngine hfvEngine;
    private String hapiFhirValidatorInstanceName = null;
    private final HapiFhirValidatorEngineOrchestrator orchestrator = HapiFhirValidatorEngineOrchestrator.getInstance();
    // adding this variable for to deal with multiple report issue.
    private int ReportNumber;

    @Override
    public void initialise() throws Exception {
        hfvEngine = orchestrator.getEngine(hapiFhirValidatorInstanceName);

        String HapiFhirInstancePath = null;
        if (hapiFhirValidatorInstanceName != null && hapiFhirValidatorInstanceName.length() > 0) {
            HapiFhirInstancePath = HAPIFHIR + "." + hapiFhirValidatorInstanceName;
        } else {
            HapiFhirInstancePath = HAPIFHIR;
        }
        config = Configurator.getConfigurator();
        ReportNumber = 0;
        int i = 0;
        while (true) {
            String sp = config.getConfiguration(HapiFhirInstancePath + FILTER + i);
            if ((sp == null) || (sp.trim().length() == 0)) {
                break;
            } else {
                String[] filter = sp.split(" ", 2);
                switch (filter[0]) {
                    case "INFORMATION":
                        informationFilter.add(new FilterTotals(filter[1], 0, i));
                        break;
                    case "WARNING":
                        warningFilter.add(new FilterTotals(filter[1], 0, i));
                        break;
                    case "ERROR":
                        errorFilter.add(new FilterTotals(filter[1], 0, i));
                        break;
                    case "FATAL":
                        fatalFilter.add(new FilterTotals(filter[1], 0, i));
                        break;
                    default:
                        break;
                }
            }
            i++;
        }

    }

    @Override
    public void setType(String t) {
        if (t.equals("hapifhirvalidator")) {
            hapiFhirValidatorInstanceName = null;
        } else {
            hapiFhirValidatorInstanceName = t.substring(t.lastIndexOf(".") + 1);
        }
    }

    @Override
    public void setResource(String r
    ) {
    }

    @Override
    public void setData(String d) throws Exception {
    }

    @Override
    public ValidatorOutput validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader) throws Exception {

        ValidationReport report[] = new ValidationReport[1];
        String x = null;
        StringBuilder vsb = new StringBuilder();
        ++ReportNumber;
        try {

            ValidationResult result = hfvEngine.validate(o);
            ValidationReport validationReport;
// You can also convert the results into an OperationOutcome resource
//           OperationOutcome oo = (OperationOutcome) result.toOperationOutcome();
//            String ooResults = context.newXmlParser().setPrettyPrint(true).encodeResourceToString(oo);
//            System.out.println(results);
            if (result.isSuccessful()) {
                System.out.println("Hapi FHIR Validation passed");
                validationReport = new ValidationReport("Pass");
                validationReport.setPassed();
            } else {
                System.out.println("Hapi FHIR Validation failed");
                validationReport = new ValidationReport("Failed");
            }

// Show the issues
            List<SingleValidationMessage> messages = result.getMessages();
            int warn = 0;
            int fatal = 0;
            int info = 0;
            int error = 0;
            int total = 0;
            String uuid = UUID.randomUUID().toString();
            vsb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>");
            vsb.append("<div class=\"data" + uuid + "\">"
                    + "<form name=\"FilterForm\" id=\"severity" + uuid + "\">"
                    + "<ul style=\"list-style: none;\"><li>"
                    + "<input type=\"checkbox\" name=\"severity" + uuid + "\" value=\"INFORMATION\" id=\"info\" checked/>"
                    + "<label for=\"filter_1\">INFORMATION (__infototal__)</label>");
            if (!informationFilter.isEmpty()) {
                vsb.append("<ul style=\"list-style: none;\"><li>Remove validations of type:</li>");
            }
            for (FilterTotals ft : informationFilter) {
                vsb.append("<li>"
                        + "<input type=\"checkbox\" class=\"subOption\" name=\"INFORMATIONmessage" + Integer.toString(ReportNumber) + "\" value=\"" + ft.getCondition() + "\"/><label for=\"filter_5\">\"" + ft.getCondition() + "\" (__total" + ft.getLabel() + "__ instances)</label>"
                        + "</li>");
            }
            if (!informationFilter.isEmpty()) {
                vsb.append("</ul>");
            }
            vsb.append("</li><li>"
                    + "<input type=\"checkbox\" name=\"severity" + uuid + "\" value=\"WARNING\" id=\"warn\" checked/>"
                    + "<label for=\"filter_2\">WARNING (__warntotal__)</label>");
            if (!warningFilter.isEmpty()) {
                vsb.append("<ul style=\"list-style: none;\"><li>Remove validations of type:</li>");
            }
            for (FilterTotals ft : warningFilter) {
                vsb.append("<li>"
                        + "<input type=\"checkbox\" class=\"subOption\" name=\"WARNINGmessage" + Integer.toString(ReportNumber) + "\" value=\"" + ft.getCondition() + "\"/><label for=\"filter_5\">\"" + ft.getCondition() + "\" (__total" + ft.getLabel() + "__ instances)</label>"
                        + "</li>");
            }
            if (!warningFilter.isEmpty()) {
                vsb.append("</ul>");
            }
            vsb.append("</li><li>"
                    + "<input type=\"checkbox\" name=\"severity" + uuid + "\" value=\"ERROR\" id=\"err\" checked/>"
                    + "<label for=\"filter_3\">ERROR (__errtotal__)</label>");
            if (!errorFilter.isEmpty()) {
                vsb.append("<ul style=\"list-style: none;\"><li>Remove validations of type:</li>");
            }
            for (FilterTotals ft : errorFilter) {
                vsb.append("<li>"
                        + "<input type=\"checkbox\" class=\"subOption\" name=\"ERRORmessage" + Integer.toString(ReportNumber) + "\" value=\"" + ft.getCondition() + "\"/><label for=\"filter_5\">\"" + ft.getCondition() + "\" (__total" + ft.getLabel() + "__ instances)</label>"
                        + "</li>");
            }
            if (!errorFilter.isEmpty()) {
                vsb.append("</ul>");
            }
            vsb.append("</li><li>"
                    + "<input type=\"checkbox\" name=\"severity" + uuid + "\" value=\"FATAL\" id=\"fat\" checked/>"
                    + "<label for=\"filter_4\">FATAL (__fattotal__)</label>");
            if (!fatalFilter.isEmpty()) {
                vsb.append("<ul style=\"list-style: none;\"><li>Remove validations of type:</li>");
            }
            for (FilterTotals ft : fatalFilter) {
                vsb.append("<li>"
                        + "<input type=\"checkbox\" class=\"subOption\" name=\"FATALmessage" + Integer.toString(ReportNumber) + "\" value=\"" + ft.getCondition() + "\"/><label for=\"filter_5\">\"" + ft.getCondition() + "\" (__total" + ft.getLabel() + "__ instances)</label>"
                        + "</li>");
            }
            if (!fatalFilter.isEmpty()) {
                vsb.append("</ul>");
            }

            vsb.append("<br><br>").append("Total: __grandtotal__");

            vsb.append("</li></ul>"
                    + "</form>"
                    + "</div>");
            vsb.append("<table id=\"StatusTable" + uuid + "\">");
            vsb.append("<thead><tr bgcolor=\"#000000\">"
                    + "<td style=\"color:#FFFFFF\"><b>Severity</b></td>"
                    + "<td style=\"color:#FFFFFF\"><b>Message</b></td>"
                    + "<td style=\"color:#FFFFFF\"><b>Location</b></td>"
                    + "</tr></thead><tbody>");
            for (SingleValidationMessage next : messages) {
                boolean reportAnything = false;
                boolean reportThis = true;
                switch (next.getSeverity()) {
                    case INFORMATION:
                        for (FilterTotals ft : informationFilter) {
                            if (next.getMessage().matches(ft.getCondition())) {
                                ft.increaseByOne();
                            }
                        }
                        if (!reportThis) {
                            break;
                        }
                        reportAnything = true;
                        info++;
                        break;
                    case WARNING:
                        for (FilterTotals ft : warningFilter) {
                            if (next.getMessage().matches(ft.getCondition())) {
                                ft.increaseByOne();
                            }
                        }
                        if (!reportThis) {
                            break;
                        }
                        reportAnything = true;
                        warn++;
                        break;
                    case ERROR:
                        for (FilterTotals ft : errorFilter) {
                            if (next.getMessage().matches(ft.getCondition())) {
                                ft.increaseByOne();
                            }
                        }
                        if (!reportThis) {
                            break;
                        }
                        reportAnything = true;
                        error++;
                        break;
                    case FATAL:
                        for (FilterTotals ft : fatalFilter) {
                            if (next.getMessage().matches(ft.getCondition())) {
                                ft.increaseByOne();
                            }
                        }
                        if (!reportThis) {
                            break;
                        }
                        reportAnything = true;
                        fatal++;
                        break;
                    default:
                        break;
                }
                total++;
                if (reportAnything) {
                    vsb.append("<tr bgcolor=\"#EAEAEA\">"
                            + "<td class=\"Severity\">" + next.getSeverity() + "</td>"
                            + "<td class=\"Message\">" + next.getMessage() + "</td>"
                            + "<td class=\"Location\">" + next.getLocationString() + "</td>"
                            + "</tr>");
                }
            }

            vsb.append("</tbody></table>");
            vsb.append("  <script>\n"
                    + "function refresh" + Integer.toString(ReportNumber) + "(){"
                    + "  var selected = [];\n"
                    + "  $('.data" + uuid + " input:checked').each(function() {\n"
                    + "    selected.push($(this).val());\n"
                    + "    $(\"#StatusTable" + uuid + ">tbody>tr\").each(function() {\n"
                    + "      var row = $(this);\n"
                    + "      var show = false;\n"
                    + "      var innerShow = true;\n"
                    + "      $(\"input[name='severity" + uuid + "']:checked\").each(function() {\n"
                    + "        var sevVal = $(this).val();\n"
                    + "        if (row.find('.Severity').html().indexOf(sevVal) >= 0) {\n"
                    + "          show = true;\n"
                    + "          $(\"input[name='\" + sevVal + \"message" + Integer.toString(ReportNumber) + "']:checked\").each(function() {\n"
                    + "            var messVal = $(this).val();\n"
                    + "            if (row.find('.Message').html().indexOf(messVal) >= 0) {\n"
                    + "              innerShow = false;\n"
                    + "                           return false;\n"
                    + "            } else {\n"
                    + "            }\n"
                    + "          });\n"
                    + "        } else {\n"
                    + "        }\n"
                    + "      });\n"
                    + "      if (show && innerShow) {\n"
                    + "        row.show();\n"
                    + "      } else {\n"
                    + "        row.hide();\n"
                    + "      }\n"
                    + "    });\n"
                    + "  });\n"
                    + "  if($('.data" + uuid + " input:checked').length == 0){\n"
                    + "      $(\"#StatusTable" + uuid + ">tbody>tr\").each(function() {\n"
                    + "      var row = $(this);\n"
                    + "      row.hide();\n"
                    + "      });\n"
                    + "  }\n"
                    + "}\n"
                    + "$(\"input[name='severity" + uuid + "']\").on('click', function() {\n"
                    + " refresh" + Integer.toString(ReportNumber) + "();\n"
                    + "});\n"
            );
            if (hfvEngine.getMinimumReportLevel() > 0) {
                vsb.append("$(\"input[id='info'], select.filter\").click();\n");
            }
            if (hfvEngine.getMinimumReportLevel() > 1) {
                vsb.append("$(\"input[id='warn'], select.filter\").click();\n");
            }
            if (hfvEngine.getMinimumReportLevel() > 2) {
                vsb.append("$(\"input[id='err'], select.filter\").click();\n");
            }
            if (hfvEngine.getMinimumReportLevel() > 3) {
                vsb.append("$(\"input[id='fat'], select.filter\").click();\n");
            }
            if (!informationFilter.isEmpty()) {
                vsb.append("$(\"input[name='INFORMATIONmessage" + Integer.toString(ReportNumber) + "']\").on('click', function() {refresh" + Integer.toString(ReportNumber) + "()});\n");
            }
            if (!warningFilter.isEmpty()) {
                vsb.append("$(\"input[name='WARNINGmessage" + Integer.toString(ReportNumber) + "']\").on('click', function() {refresh" + Integer.toString(ReportNumber) + "()});\n");
            }
            if (!errorFilter.isEmpty()) {
                vsb.append("$(\"input[name='ERRORmessage" + Integer.toString(ReportNumber) + "']\").on('click', function() {refresh" + Integer.toString(ReportNumber) + "()});\n");
            }
            if (!fatalFilter.isEmpty()) {
                vsb.append("$(\"input[name='FATALmessage" + Integer.toString(ReportNumber) + "']\").on('click', function() {refresh" + Integer.toString(ReportNumber) + "()});\n");
            }
            vsb.append("</script>");

            vsb.append("<br>").append("Artefact Versions:");
            vsb.append("<br><ul><li>").append(" HAPI FHIR Software Version: ").append(hfvEngine.getSoftwareVersion()).append("</li>");
            vsb.append("<li>").append("Minimum Report Level: ");

            switch (hfvEngine.getMinimumReportLevel()) {
                case 0:
                    vsb.append("INFORMATION");
                    break;
                case 1:
                    vsb.append("WARNING");
                    break;
                case 2:
                    vsb.append("ERROR");
                    break;
                case 3:
                    vsb.append("FATAL");
                    break;
                default:
                    break;
            }
            ;
            vsb.append("<li>").append("HAPI FHIR Validator FHIR Version: ").append(hfvEngine.getFhirVersion()).append("</li>");
            vsb.append("<li>").append("INSTALLED: Caching Validation Support module installed").append("</li>");
            vsb.append("<li>").append("INSTALLED: Default Validation Support module installed").append("</li>");
            vsb.append("<li>").append(hfvEngine.isPrepopulatedValidationSupport()? "INSTALLED: " : "NOT INSTALLED: ").append("Prepopulated Validation Support Module").append("</li>");
            if (hfvEngine.isPrepopulatedValidationSupport()) {
                vsb.append("<ul><li>").append("Profile Version: ").append(hfvEngine.getProfileVersion()).append("</li>");
                if (hapiFhirValidatorInstanceName != null) {
                    vsb.append("<li>").append("HAPI FHIR Validation Instance Name: ").append(hapiFhirValidatorInstanceName).append("</li>");
                }
                vsb.append("</ul>");
            }
            vsb.append("<li>").append(hfvEngine.isInMemoryTerminologyServerValidationSupport() ? "INSTALLED: " : "NOT INSTALLED: ").append("In Memory Terminology Server Validation Support Module").append("</li>");
            vsb.append("<li>").append(hfvEngine.isCommonCodeSystemTerminologyServiceValidationSupport()? "INSTALLED: " : "NOT INSTALLED: ").append("Common Code System Terminology Service Validation Support Module").append("</li>");
            vsb.append("<li>").append((hfvEngine.getRemoteTerminologyServiceUrl()!=null)? "INSTALLED: " : "NOT INSTALLED: ").append("Remote Terminology Server Validation Support Module");
            if (hfvEngine.getRemoteTerminologyServiceUrl() != null) {
                vsb.append(". Base URL: ").append(hfvEngine.getRemoteTerminologyServiceUrl()).append("</li>");
            }
            vsb.append("</ul>");

            String vs = vsb.toString();
            vs = vs.replace("__infototal__", String.valueOf(info));
            vs = vs.replace("__warntotal__", String.valueOf(warn));
            vs = vs.replace("__errtotal__", String.valueOf(error));
            vs = vs.replace("__fattotal__", String.valueOf(fatal));
            vs = vs.replace("__grandtotal__", String.valueOf(total));

            for (FilterTotals ft : informationFilter) {
                vs = vs.replace("__total" + ft.getLabel() + "__", String.valueOf(ft.getTotal()));
            }
            for (FilterTotals ft : warningFilter) {
                vs = vs.replace("__total" + ft.getLabel() + "__", String.valueOf(ft.getTotal()));
            }
            for (FilterTotals ft : errorFilter) {
                vs = vs.replace("__total" + ft.getLabel() + "__", String.valueOf(ft.getTotal()));
            }
            for (FilterTotals ft : fatalFilter) {
                vs = vs.replace("__total" + ft.getLabel() + "__", String.valueOf(ft.getTotal()));
            }
            validationReport.setTest(vs);

            report[0] = validationReport;
        } catch (Exception e1) {
            String commentText = "";
            if (e1 instanceof DataFormatException) {
                commentText = "Data Format issue";
            } else {
                commentText = "EXCEPTION";
            }
            ValidationReport vr = new ValidationReport(commentText);
            StringBuilder sb = new StringBuilder(e1.getMessage());
            if (e1.getCause() != null) {
                sb.append(" : ").append(e1.getCause().getMessage());
            }
            vr.setTest(sb.toString());
            report[0] = vr;
        }
        if (report.length == 0) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Pass");
            e[0].setTest("HAPI FHIR Validator successfully completed");
            e[0].setPassed();
            return new ValidatorOutput(null, e);
        }
        return new ValidatorOutput(x, report);
//        return new ValidatorOutput(null, (ValidationReport[]) validatorExceptions.toArray(new ValidationReport[validatorExceptions.size()]));

    }

    @Override
    public ValidationReport[] validate(SpineMessage o) throws Exception {
        throw new Exception("Incorrect validation class used");
    }

    @Override
    public String getSupportingData() {
        return null;
    }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
    }

    private class FilterTotals {

        private final String condition;
        private int total;
        private int label;

        public FilterTotals(String condition, int total, int label) {
            this.condition = condition;
            this.total = total;
            this.label = label;
        }

        public String getCondition() {
            return condition;
        }

        public int getTotal() {
            return total;
        }

        public int getLabel() {
            return label;
        }

        private void increaseByOne() {
            this.total++;
        }

    }

}
