/*
 * WrapperHelper.java
 *
 * Created on 17 March 2006, 11:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 *BIYA 31/05/2007
 * add static final String ACKRQ = "ackRq";
 */

package uk.nhs.digital.mait.tkwx.tk.internalservices.send;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 *
 * @author murff
 */
public class WrapperHelper {
    
    private static WrapperHelper me = new WrapperHelper();
    public static final String SDSREFERENCE = "uk.nhs.digital.mait.tkwx.spine.sds.reference";
//    public static final String INTERACTIONMAP = "uk.nhs.digital.mait.tkwx.spine.interaction.map";
    public static final String REFSDSENTRYTAGNAME = "entry";   
    public static final String REFASIDNAME = "asid";
    public static final String REFCPAIDNAME = "cpaid";
    public static final String REFINTERACTIONNAME = "interaction";
    public static final String REFSERVICE = "service";
    public static final String REFSOAPACTION = "soapaction";
    public static final String REFPARTYKEY = "partykey";
    public static final String SYNCREPLY = "syncReply";
    public static final String ENDPOINT = "endpoint";
    public static final String SYNCHRESPONSEONLYSERVICE = "SYNCH-RESPONSE-ONLY";
    public static final String MESSAGEHEADERTEMPLATE = "uk.nhs.digital.mait.tkwx.spine.message.headertemplate";
    public static final String WSHEADERTEMPLATE = "uk.nhs.digital.mait.tkwx.spine.webservice.headertemplate";
    public static final String ACKRQ = "ackRq";
    public static final String MESSAGEINTERACTIONCLASS = "MESSAGE";
    public static final String WEBSERVICEINTERACTIONCLASS = "WEBSERVICE";
    
    private NodeList nl = null;
    private HashMap<String,String> interactionMap = null;
    
    /** Creates a new instance of WrapperHelper */
    private WrapperHelper() {
        try {
//            loadInteractionMap();
            String rfile = System.getProperty(SDSREFERENCE);
            FileReader fr = new FileReader(rfile);
            InputSource is = new InputSource(fr);
            DOMParser d = new DOMParser();
            d.parse(is);
            Document refDoc = d.getDocument();
            nl = refDoc.getElementsByTagName(REFSDSENTRYTAGNAME);
        }
        catch (Exception e) {
            Logger.getInstance().log(SEVERE,WrapperHelper.class.getName(),e.getMessage());
            System.exit(1);
        }
    }
    
//    private void loadInteractionMap() 
//        throws Exception
//    {
//        String fName = System.getProperty(INTERACTIONMAP);
//        if (fName == null) 
//            throw new Exception("System property " + INTERACTIONMAP + " not set");
//        try {
//            HashMap<String,String> h = new HashMap<String,String>();
//            String line = null;
//            FileReader fr = new FileReader(fName);
//            BufferedReader br = new BufferedReader(fr);
//            while ((line = br.readLine()) != null) {
//                int delimiter = line.indexOf(':');
//                if (delimiter != -1) {
//                    h.put(line.substring(0, delimiter), line.substring(delimiter + 1));
//                }
//            }
//            fr.close();
//            interactionMap = h;
//        }
//        catch (Exception e) {
//            throw new Exception("Failed to read interaction map: " + fName + ": " + e.getMessage());
//        }
//    }
//    
//    public String getInteractionTypeBySoapAction(String soapaction) {
//        if ((soapaction == null) || (soapaction.trim().length() == 0)) {
//            return null;
//        }
//        int a = soapaction.indexOf((int)'/');
//        if (a == -1) {
//            return null;
//        }
//        ++a;
//        String hl7id = soapaction.substring(a);
//        int b = hl7id.indexOf((int)'\"');
//        if (b != -1) {
//            hl7id = hl7id.substring(0, b);
//        }
//        try {
//            return resolveInteractionType(hl7id);
//        }
//        catch (Exception e) {
//            return null;
//        }
//    }
    
//    String resolveInteractionType(String interaction)
//        throws Exception
//    {
//        if ((interaction == null) || (interaction.length() == 0)) 
//            throw new Exception("Invalid interaction: " + interaction);
//        if (!interactionMap.containsKey(interaction))
//            throw new Exception("Interaction not found or unsupported: " + interaction + " check the interaction map file");
//        return interactionMap.get(interaction);
//    }
    
    public static synchronized WrapperHelper getInstance() { return me; }

    public HashMap<String,String> getVirtualSDSEntry(String asid, String interactionid, boolean service) {
        Element sdsElement = getVirtualSDSNode(asid, interactionid, service);
        return getSDSEntry(sdsElement);
    }
    
    private HashMap<String,String> getSDSEntry(Element sdsElement) {
        if (sdsElement == null)
            return null;
        HashMap<String,String> ent = new HashMap<String,String>();
        NamedNodeMap nnm = sdsElement.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Attr a = (Attr)nnm.item(i);
            ent.put(a.getName(),a.getValue());
        }
        return ent;        
    }
    public HashMap<String,String> getSDSInteractionEntry(String asid, String interactionid) {
        Element sdsElement = getSDSNode(asid, interactionid);
        return getSDSEntry(sdsElement);
    }
    
    public StringBuilder getMessageHeaderTemplate()
        throws Exception
    {
        String fname = System.getProperty(MESSAGEHEADERTEMPLATE);
        return new StringBuilder(getTemplate(fname));
    }
    
    public StringBuilder getWebServiceHeaderTemplate()
        throws Exception
    {
        String fname = System.getProperty(WSHEADERTEMPLATE);
        return new StringBuilder(getTemplate(fname));
    }
    
    private String getTemplate(String fileName)
        throws Exception
    {
        try {
            File f = new File(fileName);
            char[] cBuf = new char[(int)f.length()];
            FileReader fr = new FileReader(f);
            fr.read(cBuf);
            fr.close();
            fr = null;
            f = null;
            return new String(cBuf);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private Element getVirtualSDSNode(String asid, String interactionid, boolean service) {
        /*
         * This operates in two ways: an interaction id will be passed for a "to" asid,
         * and not for the "from". We won't find the interaction id for the from asid
         * anyway, so we just return the first node for that asid that we find because
         * all we actually want is its MHS endpoint.
         */
        Node n;
        for (int i = 0; i < nl.getLength(); i++) {
            n = nl.item(i);
            Attr a = (Attr)(n.getAttributes().getNamedItem(REFASIDNAME));
            Attr c = (Attr)(n.getAttributes().getNamedItem(REFCPAIDNAME));
            Attr s = (Attr)(n.getAttributes().getNamedItem(REFSERVICE));
            if (c.getValue().equals("")) {
                Attr h = (Attr)(n.getAttributes().getNamedItem(REFINTERACTIONNAME));
                if (!service && s.getValue().equals(SYNCHRESPONSEONLYSERVICE)) {
                    // if (a.getValue().equals(asid) && h.getValue().equals(interactionid)) {
                    if (a.getValue().equals(asid)) {
                        return (Element)n;
                    }
                } else {
                    if (a.getValue().equals(asid) && h.getValue().equals(interactionid) && !s.getValue().equals(SYNCHRESPONSEONLYSERVICE)) {
                        return (Element)n;
                    }                    
                }
            }
        }
        return null;
    }
    
    public String resolveServiceEndpoint(String soapaction, String toParty) {
        Node n;
        StringBuffer sb = new StringBuffer(soapaction);
        sb.setCharAt(sb.indexOf("/"), ':');
        String sa = sb.toString();
        for (int i = 0; i < nl.getLength(); i++) {
            n = nl.item(i);
            Attr p = (Attr)(n.getAttributes().getNamedItem(REFPARTYKEY));
            Attr s = (Attr)(n.getAttributes().getNamedItem(REFSOAPACTION));
            if (p.getValue().equals(toParty) && s.getValue().equals(sa)) {
                Attr e = (Attr)(n.getAttributes().getNamedItem(ENDPOINT));
                return e.getValue();
            }
        }
        return null;
    }
    
    private Element getSDSNode(String asid, String interactionid) {
        /*
         * This operates in two ways: an interaction id will be passed for a "to" asid,
         * and not for the "from". We won't find the interaction id for the from asid
         * anyway, so we just return the first node for that asid that we find because
         * all we actually want is its MHS endpoint.
         */
        Node n;
        for (int i = 0; i < nl.getLength(); i++) {
            n = nl.item(i);
            Attr a = (Attr)(n.getAttributes().getNamedItem(REFASIDNAME));
            Attr c = (Attr)(n.getAttributes().getNamedItem(REFCPAIDNAME));
            if (a.getValue().equals(asid)) {
                if (interactionid != null) {
                    Attr h = (Attr)(n.getAttributes().getNamedItem(REFINTERACTIONNAME));
                    if (!c.getValue().equals("") && h.getValue().equals(interactionid)) {
                        return (Element)n;
                    }
                } else {
                    if (!c.getValue().equals("") && a.getValue().equals(asid))
                        return (Element)n;
                }
            }
        }
        return null;
    }
}
