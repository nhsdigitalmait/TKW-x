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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.*;


/**
 * Validation Class to parse through document messages using bundle. It can
 * detect literal, logical, relative and absolute references and parse through
 * them where applicable. It also reports on groupings of the resources which
 * means that if there is more than one coherent structure (joined together
 * using any of the referencing methods), it will report on each grouping. It
 * returns an error when it detects circular references or
 * literal/relative/logical references without a resource present.
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class FHIRResourceValidator
        implements ValidationCheck, VariableConsumer {

    private DocumentBuilder builder = null;
    private List<Resource> resourcesParsedList = new ArrayList<>();
    private ArrayList<Issues> failures = new ArrayList<>();
    private ArrayList<ArrayList<String>> chains = new ArrayList<>();
    private HashMap<Integer, ArrayList<String>> connectedChains = new HashMap<>();

    @Override
    public void initialise() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        builder = factory.newDocumentBuilder();

        builder.setErrorHandler(new SimpleErrorHandler());
        resourcesParsedList = new ArrayList<>();
        failures = new ArrayList<>();
    }

    @Override
    public void setType(String t) {
    }

    @Override
    public void setResource(String r) {
    }

    @Override
    public void setData(String d) throws Exception {
    }

    @Override
    public ValidatorOutput validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader) throws Exception {
        try {

            // the "parse" method also validates XML, will throw an exception if misformatted
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(o.getBytes())));
            String rootNode = document.getDocumentElement().getNodeName();
            failures.clear();
            resourcesParsedList.clear();
            chains.clear();
            connectedChains.clear();
            ValidationReport report[] = new ValidationReport[1];

            // Only validate FHIR messages i.e. things that have root node of bundle
            //????????  Do we need to check for <type value="document"/> ?
            //Parse through the document, resource by resource
            if (rootNode.toLowerCase().equals("bundle")) {
                NodeList nl = document.getElementsByTagName("*");
                int j = 0;
                Node node = nl.item(j);
                while (node != null) {
                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().toLowerCase().equals("resource")) {
                        Element e = (Element) node;
//                        System.out.println("resource found");
                        int nodeCounter = parseResource(e.getElementsByTagName("*"));
                        j = j + nodeCounter;
                    } else {
                        j++;
                    }
                    node = nl.item(j);
                }
            }
            // Now look for links, circular references
            if (!resourcesParsedList.isEmpty() && failures.isEmpty()) {
                //loop round the Parsed resources references and follow them
                for (Resource r : resourcesParsedList) {
                    lookUpReferences(r, new ArrayList<>(Arrays.asList(r.getId().toString())));
//                    lookUpResources(r.getId(), new StringBuilder());
                }
            }

            // Now look for homogeneity of links - i.e. how many grouping of linked Resources are there?
            ArrayList<ArrayList<String>> returnPool = chainChecker(chains);

            if (returnPool.size() > 1) {
                failures.add(new Issues(Issues.WARNING, "The Resources are not linked in one coherent grouping. There are " + returnPool.size() + " groups: see below. This may be an issue.", null));
            }
            for (ArrayList<String> resourceGroup : returnPool) {
                failures.add(new Issues(Issues.INFO, "The following Resources are linked together: <ul><li>" + String.join("</li><li>", resourceGroup) + "</li></ul>", null));
            }
            ValidationReport validationReport = null;

            boolean passed = true;
            StringBuilder sb = new StringBuilder();
            for (Issues i : failures) {
                sb.append("<br>");
                switch (i.getType()) {
                    case 0:
                        sb.append("Info: ");
                        break;
                    case 1:
                        sb.append("WARNING: ");
                        break;
                    case 2:
                        sb.append("ERROR: ");
                        passed = false;
                        break;
                    default:
                        break;
                }
                sb.append(i.getText());
                if (i.getIds() != null) {
                    sb.append(" " + String.join(", ", i.getIds()));
                }
            }

            if (passed) {
                validationReport = new ValidationReport("Pass");
                validationReport.setPassed();
            } else {
                validationReport = new ValidationReport("Failed");
            }
            validationReport.setTest(sb.toString());
            report[0] = validationReport;
            return new ValidatorOutput(o, report);

        } catch (Exception ex) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Fail");
            e[0].setTest("Basic FHIR Relationships Validator failed" + ex.getMessage());
            return new ValidatorOutput(null, e);
        }

    }

    private ArrayList<ArrayList<String>> chainChecker(ArrayList<ArrayList<String>> chainPool) {
        boolean anythingDone = false;
        ArrayList<ArrayList<String>> returnPool = new ArrayList<>();
        // loop round the chains collected during lookupreferences
        for (ArrayList<String> chain1 : chainPool) {
            ArrayList<String> loopChain = (ArrayList<String>) chain1.clone();
            boolean matchedThisChain = false;
            //check each against each of the chainpool
            for (ArrayList<String> chain2 : chainPool) {
                if (chain2 == chain1 || chain2.isEmpty()) {
                    continue;
                }
                //create a merged list without duplicates
                ArrayList<String> mergedList = (ArrayList< String>) loopChain.clone();
                mergedList.removeAll(chain2);
                mergedList.addAll(chain2);
                //if when merged there is no reduction in size there is no match
                if (loopChain.size() + chain2.size() == mergedList.size()) {
                } else {
                    returnPool.add(mergedList);
                    chain2.clear();
                    anythingDone = true;
                    matchedThisChain = true;
                }
            }
            if (!matchedThisChain) {
                if (!chain1.isEmpty()) {
                    returnPool.add((ArrayList< String>) chain1.clone());
                }
            }
            chain1.clear();
        }
        //continue to match against each other until run out of successful matching runs
        if (anythingDone) {
            return chainChecker(returnPool);
        }
        return returnPool;
    }

    private void lookUpReferences(Resource resource, ArrayList<String> history) {
//        System.out.println("recurse " + String.join(", ", history));
        // for each resource get references and look them up
        ArrayList<Ident> references = resource.getReferences();
        Ident id = resource.getId();
        if (references.isEmpty()) {
            Issues i = new Issues(Issues.INFO, "Resource " + id + " has no references", null);
            if (!isIssueAlreadyReported(i)) {
                failures.add(i);
            }
            // this marks the end of a chain of resources - add to chains for assessing connectedness
            ArrayList<String> chain = (ArrayList<String>) history.clone();
            chains.add(chain);
            return;
        }
        for (Ident refIdent : references) {
            //look up references in other parsed resources

            if (refIdent.toString().equals(id.toString())) {
                Issues i = new Issues(Issues.ERROR, refIdent.getReferenceType() + " Resource " + id.toString() + " is self referenced", null);
                if (!isIssueAlreadyReported(i)) {
                    failures.add(i);
                }
                continue;
            }
            if (refIdent.getValue().toLowerCase().startsWith("http")) {
                Issues i = new Issues(Issues.INFO, "Resource " + id + " has an external " + refIdent.getReferenceType() + " reference (" + refIdent.getValue() + ") therefore ignore", null);
                if (!isIssueAlreadyReported(i)) {
                    failures.add(i);
                }
                continue;
            }
            boolean foundReference = false;
            for (Resource r2 : resourcesParsedList) {
                if (r2.hasAReference(refIdent, id)) {
                    if (r2.getReferences().isEmpty()) {
                        Issues i = new Issues(Issues.INFO, refIdent.getReferenceType() + " Resource " + r2.getId() + " has no references", null);
                        if (!isIssueAlreadyReported(i)) {
                            failures.add(i);
                        }
                        // this marks the end of a chain of resources - add to chains for assessing connectedness
                        ArrayList<String> chain = (ArrayList<String>) history.clone();
                        chain.add(refIdent.toString());
                        chains.add(chain);
                    } else {
                        if (history.contains(refIdent.toString())) {
                            List<String> circular = new ArrayList<>();
                            //Clone the Arraylist otherwise a concurrent modification exeception occurs 
                            ArrayList<String> historyClone = (ArrayList<String>) history.clone();
                            int start = historyClone.indexOf(refIdent.toString());
                            circular = historyClone.subList(start, historyClone.size());
                            boolean match = false;
                            Collections.sort(circular);
                            Issues i = new Issues(Issues.WARNING, "Circular Reference between ids:", circular);
                            if (!isIssueAlreadyReported(i)) {
                                failures.add(i);
                            }
                            return;
                        }
                        history.add(refIdent.toString());

                        lookUpReferences(r2, history);
                        history.remove(history.size() - 1);
//                        System.out.println("recurse end " + String.join(", ", history));
                    }
                    foundReference = true;
                    break;
                }
            }
            if (!foundReference) {
                Issues i = new Issues(Issues.ERROR, refIdent.getReferenceType() + " Resource " + id + " CANNOT find its declared reference " + refIdent, null);
                if (!isIssueAlreadyReported(i)) {
                    failures.add(i);
                }
            }
        }
    }

    private int parseResource(NodeList nodeList) {
        int j = 0;
        Node node = nodeList.item(j);
        Resource resource = new Resource();
        while (node != null) {

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                // do something with the current element
//                            System.out.println(node.getNodeName());
                if (elem.getNodeName().toLowerCase().equals("id")) {
//                    System.out.println("id = " + elem.getAttribute("value"));
                    resource.setId(new Ident(elem.getAttribute("value"), null, Ident.ResourceType.ID, elem.getParentNode().getNodeName()));
                } else if (elem.getNodeName().toLowerCase().equals("identifier")) {
                    if (elem.hasChildNodes()) {
                        NodeList refnl = elem.getChildNodes();
                        String system = null;
                        String value = null;
                        for (int k = 0; k < refnl.getLength(); k++) {
                            if (refnl.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                Element refe = (Element) refnl.item(k);

                                if (refe.getNodeName().toLowerCase().equals("system")) {
//                                    System.out.println("identifier system = " + refe.getAttribute("value"));
                                    system = refe.getAttribute("value");
                                }
                                if (refe.getNodeName().toLowerCase().equals("value")) {
//                                    System.out.println("identifier value = " + refe.getAttribute("value"));
                                    value = refe.getAttribute("value");
                                }

                                if (system != null && value != null) {
                                    if (refe.getParentNode().getParentNode().getParentNode().getNodeName().equals("resource")) {
                                        //If its garndparent is a resource it's an identifier
                                        resource.addIdentifier(new Ident(value, system, Ident.ResourceType.IDENTIFIER, null));
                                    } else {
                                        //else it's a logical reference 

                                        resource.addReference(new Ident(value, system, Ident.ReferenceType.LOGICAL));
                                    }
                                }
                            }
                        }
                    }

                } else if (elem.getNodeName().toLowerCase().equals("resource")) {
//                    System.out.println("sub resource found");
                    int nodeCounter = parseResource(elem.getElementsByTagName("*"));
                    j = j + nodeCounter;
                } else if (elem.getNodeName().toLowerCase().equals("reference")) {
//                    System.out.println("reference = " + elem.getAttribute("value"));
                    if (elem.getAttribute("value").toLowerCase().contains("http")) {
                        resource.addReference(new Ident(elem.getAttribute("value"), null, Ident.ReferenceType.ABSOLUTE));
                    } else if (elem.getAttribute("value").contains("/")) {
                        resource.addReference(new Ident(elem.getAttribute("value"), null, Ident.ReferenceType.RELATIVE));
                    } else {
                        resource.addReference(new Ident(elem.getAttribute("value"), null, Ident.ReferenceType.LITERAL));
                    }
                }
            }

            j++;
            node = nodeList.item(j);
        }
        //check contents of created resource
        if (resource.getId() == null || resource.getId().getValue() == null || resource.getId().getValue().isEmpty()) {
            Issues i = new Issues(Issues.ERROR, "Resource has no id field - no subsequent checks have been performed by this validation", null);
            if (!isIssueAlreadyReported(i)) {
                failures.add(i);
            }
        }
        resourcesParsedList.add(resource);
        return j;
    }

    private boolean isIssueAlreadyReported(Issues issues) {
        for (Issues i : failures) {
            if (issues.getText().equals(i.getText())
                    && issues.getType() == i.getType()) {
                if (i.getIds() == null) {
                    return true;
                } else {
                    Collections.sort(i.getIds());
                    if (String.join(",", issues.getIds()).equals(String.join(",", i.getIds()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ValidationReport[] validate(SpineMessage o) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSupportingData() {
        return null;
    }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVariableProvider(VariableProvider vp
    ) {
    }

    /**
     * required for the xml parsing part
     */
    private static class SimpleErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
        }
    }

    // Issues are the reportable things found when parsing, linking and detecting groupings.
    private class Issues {

        private String text = null;
        private int type = -1;
        private List<String> ids = new ArrayList<>();
        public static final int INFO = 0;
        public static final int WARNING = 1;
        public static final int ERROR = 2;

        public Issues(int type, String text, List<String> ids) {
            this.type = type;
            this.text = text;
            this.ids = ids;
        }

        public int getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public List<String> getIds() {
            return ids;
        }

    }

    // Ident is a data object used for resource/id, resource/Identifier and resource/*/reference
    private static class Ident {

        private String value = null;
        private String system = null;
        private String parentResourceName = null;
        private ResourceType resourceType = null;
        private ReferenceType referenceType = null;

        enum ReferenceType {
            NONE,
            LITERAL,
            LOGICAL,
            RELATIVE,
            ABSOLUTE
        }

        enum ResourceType {
            NONE,
            ID,
            IDENTIFIER
        }

        public Ident(String value, String system, ReferenceType rt) {
            this.value = value;
            this.system = system;
            this.referenceType = rt;
        }

        public Ident(String value, String system, ResourceType rt, String parentResourceName) {
            this.value = value;
            this.system = system;
            this.resourceType = rt;
            this.parentResourceName = parentResourceName;
        }

        public String getValue() {
            if (value.startsWith("urn:uuid:")) {
                return value.replace("urn:uuid:", "");
            } else {
                return value;
            }
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public ResourceType getResourceType() {
            return resourceType;
        }

        public ReferenceType getReferenceType() {
            return referenceType;
        }

        public boolean isReference() {
            return resourceType == null;
        }

        private void setResourceName(String parentResourceName) {
            this.parentResourceName = parentResourceName;
        }

        public String getParentResourceName() {
            return parentResourceName;
        }

        @Override
        public String toString() {
            if (getResourceType() == ResourceType.IDENTIFIER || getReferenceType() == ReferenceType.LOGICAL) {
                return "value=" + this.getValue() + "&system=" + this.getSystem();
            } else {
                return this.getValue();
            }
        }
    }

    // Resource is a data object used to put identifying and referencing information from all instances of resource
    private class Resource {

        private Ident id = null;
        private ArrayList<Ident> identifier = new ArrayList<>();
        private ArrayList<Ident> references = new ArrayList<>();

        public Resource() {
        }

        public void setId(Ident idValue) {
            this.id = idValue;
        }

        public void addIdentifier(Ident identifier) {
            this.identifier.add(identifier);
        }

        public Ident getId() {
            return id;
        }

        public ArrayList<Ident> getIdentifier() {
            return identifier;
        }

        private void addReference(Ident reference) {
            references.add(reference);
        }

        private ArrayList<Ident> getReferences() {
            return references;
        }

        private boolean hasAReference(Ident ref, Ident res) {
            Ident.ReferenceType refType = ref.getReferenceType();
            if (null != refType) switch (refType) {
                case RELATIVE:
                    //is it a relative reference i.e. one instance of "/"
                    String[] s = ref.getValue().split("/");
                    if (s.length != 2) {
                        Issues i = new Issues(Issues.WARNING, refType + " Resource " + res.toString() + " has more than one \"/\" " + ref.toString(), null);
                        if (!isIssueAlreadyReported(i)) {
                            failures.add(i);
                        }
                        return false;
                    }   if (id.getValue().equals(s[1]) && id.getParentResourceName().equals(s[0])) {
                        Issues i = new Issues(Issues.INFO, "Resource " + res.toString() + " has a " + refType + " reference to " + ref.toString(), null);
                        if (!isIssueAlreadyReported(i)) {
                            failures.add(i);
                        }
                        return true;
                    }   break;
                case LITERAL:
                    if (id.getValue().equals(ref.getValue())) {
                        Issues i = new Issues(Issues.INFO, "Resource " + res.toString() + " has a " + refType + " reference to " + ref.toString(), null);
                        if (!isIssueAlreadyReported(i)) {
                            failures.add(i);
                        }
                        return true;
                    }   break;
                case LOGICAL:
                    for (Ident ident : identifier) {
                        if (ref.getSystem().equals(ident.getSystem()) && ref.getValue().equals(ident.getValue())) {
                            Issues i = new Issues(Issues.INFO, "Resource " + res.toString() + " has a " + refType + " reference to " + ref.toString(), null);
                            if (!isIssueAlreadyReported(i)) {
                                failures.add(i);
                            }
                            return true;
                        }
                    }   break;
                default:
                    break;
            }
            return false;
        }

    }
}
