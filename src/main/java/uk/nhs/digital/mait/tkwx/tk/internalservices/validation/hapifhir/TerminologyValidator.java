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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.xsltransform.ResourceURIResolver;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.*;


/**
 * Validation Class to validate all snomed terms against an instance of
 * ONTOSERVER
 *
 * Each FHIR message is parsed and all snomed values are extracted using XSL.
 * Each term is then looked up on the ONTOSERVER individually and as part of a
 * batch in order to establish efficiency and baseline expectations of a lookup
 * service. This class is designed to be a half way house for a production style
 * ontoserver lookup. There are a number of variables which need to be
 * parameterised- see code. Also included here is a hack for subverting valid
 * hostnames on the certs. This class currently outputs ONTOSERVER responses to
 * file for further inspection
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class TerminologyValidator
        implements ValidationCheck, VariableConsumer {

    private final Transformer transformer = null;
    private PrintWriter allData;
    private PrintWriter groupedData;
    private PrintWriter errorData;
    private String fileId = UUID.randomUUID().toString();

    @Override
    public void initialise() throws Exception {
        InputStream is = null;
        TransformManager t = TransformManager.getInstance();
        URIResolver u = t.getFactoryURIResolver();

        ResourceURIResolver r = new ResourceURIResolver();
        t.setFactoryURIResolver(r);
        is = getClass().getResourceAsStream("SnomedExtractor.xsl");
        t.addTransform("SnomedExtractor.xsl", is);
        t.setURIResolver("SnomedExtractor.xsl", r);

        t.setFactoryURIResolver(u);

//        String runDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String runDate = "";
//TODO Hard coded paths need parameterising
        try {
            FileWriter fw = new FileWriter("/home/riro/TKW-5.0.5/TKW/config/FHIR_MESH/allTerminologyData" + runDate + ".csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            allData = new PrintWriter(bw);
            allData.append("FileNumber,TerminologyCode,CheckTime" + System.lineSeparator());
            allData.flush();
        } catch (IOException e) {
            System.out.println("Failed to open/access/create All Terminolgy Validator CSV output File");
        }
        try {
            FileWriter fw = new FileWriter("/home/riro/TKW-5.0.5/TKW/config/FHIR_MESH/groupedTerminologyData" + runDate + ".csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            groupedData = new PrintWriter(bw);
            groupedData.append("FileNumber,LookupMethod,NoOfTerminologyLookups,AverageTime,TotalTime" + System.lineSeparator());
            groupedData.flush();
        } catch (IOException e) {
            System.out.println("Failed to open/access/create Grouped Terminolgy Validator CSV output File");
        }
        try {
            FileWriter fw = new FileWriter("/home/riro/TKW-5.0.5/TKW/config/FHIR_MESH/errorTerminologyData" + runDate + ".csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            errorData = new PrintWriter(bw);
            errorData.append("FileNumber,LookupMethod,Failure" + System.lineSeparator());
            errorData.flush();
        } catch (IOException e) {
            System.out.println("Failed to open/access/create Error Terminolgy Validator CSV output File");
        }

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

            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            Transformer tx = TransformManager.getInstance().getTransformerFactory().newTransformer();
            tx.transform(new StreamSource(new StringReader(o)), sr);
            String root = sw.toString();
            String s = TransformManager.getInstance().doTransform("SnomedExtractor.xsl", new StringReader(root));
            fileId = UUID.randomUUID().toString();
            String[] urlParameters = s.split(",");
            System.out.println("Snomed Lookups: " + urlParameters.length);

// *****************************************MASSIVE HACK REMOVE!!!!************************************************
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                    }

                }
            };

            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("SSL");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier validHosts = new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };
            // All hosts will be valid
            HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
// *****************************************END OF MASSIVE HACK REMOVE!!!!************************************************

            batchLookup(urlParameters, false);
            batchLookup(urlParameters, true);

        } catch (Exception e) {
            errorData.append(fileId + ",unknown," + e.getMessage() + System.lineSeparator());
            flushAndClose();
            ValidationReport[] ve = new ValidationReport[1];
            ve[0] = new ValidationReport("Error converting to Conformance Form: " + e.toString());
            ValidatorOutput vr = new ValidatorOutput(null, ve);
            return vr;
        }
        flushAndClose();
        ValidatorOutput vr = new ValidatorOutput(null, null);
        return vr;
    }

    private void batchLookup(String[] urlParameters, boolean batch) throws IOException {
        long maxLookup = 0;
        long minLookup = 0;
        long averageLookupTime = 0;
        long totalTime = 0;
        long noOfLookups = urlParameters.length;

        if (!batch) {
            //Individual Checks
            for (String urlparameter : urlParameters) {
                if (!Pattern.matches("^[0-9]*$", urlparameter)) {
                    errorData.append(fileId + ",individual," + "code parameter is not valid:" + urlparameter + System.lineSeparator());
                    return;
                }
                long startLookup = System.currentTimeMillis();
                URL url = new URL("https://Ontoserver-dev.dataproducts.nhs.uk/fhir/CodeSystem/$lookup?" + "system=http://snomed.info/sct&code=" + urlparameter + "&_format=xml");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                print_content(con);
                long endLookup = System.currentTimeMillis();
                long lookupDuration = endLookup - startLookup;
                if (lookupDuration > maxLookup) {
                    maxLookup = lookupDuration;
                }
                if (minLookup == 0 || lookupDuration < minLookup) {
                    minLookup = lookupDuration;
                }
                totalTime = totalTime + lookupDuration;

                allData.append(fileId + "," + urlparameter + "," + lookupDuration + System.lineSeparator());
//                    System.out.println(urlparameter + " took " + lookupDuration + " milliseconds");
            }

        } else {
            // BATCH Checks
            String batchBundle = "<Bundle xmlns=\"http://hl7.org/fhir\">\n"
                    + "    <type value=\"batch\"/>\n"
                    + "  __ENTRIES__\n"
                    + "</Bundle>";
            StringBuilder entries = new StringBuilder();
            for (String code : urlParameters) {
                if (!Pattern.matches("^[0-9]*$", code)) {
                    errorData.append(fileId + ",individual," + "code parameter is not valid:" + code + System.lineSeparator());
                    continue;
                }
                String batchEntry = "  <entry>\n"
                        + "    <request>\n"
                        + "      <method value=\"GET\"/>\n"
                        + "      <url value=\"/CodeSystem/$lookup?system=http://snomed.info/sct&amp;code=__CODE__&amp;_format=xml\"/>\n"
                        + "    </request>\n"
                        + "  </entry>\n";
                batchEntry = batchEntry.replace("__CODE__", code);
                entries.append(batchEntry);

            }
            if (entries.length() == 0) {
                return;
            }
            batchBundle = batchBundle.replace("__ENTRIES__", entries);

            long startLookup = System.currentTimeMillis();

            URL url = new URL("https://Ontoserver-dev.dataproducts.nhs.uk/fhir");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/fhir+xml");
            con.setRequestProperty("Accept", "application/fhir+xml");
            con.setRequestProperty("content-length", String.valueOf(batchBundle.length()));
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(batchBundle.toString().getBytes("UTF-8"));
            os.close();
            //dumpl all cert info
            //                print_https_cert(con);
            //dump all the content
            print_content(con);
            long endLookup = System.currentTimeMillis();
            long lookupDuration = endLookup - startLookup;
            if (lookupDuration > maxLookup) {
                maxLookup = lookupDuration;
            }
            if (minLookup == 0 || lookupDuration < minLookup) {
                minLookup = lookupDuration;
            }
            totalTime = totalTime + lookupDuration;

            allData.append(fileId + "," + "batch" + "," + lookupDuration + System.lineSeparator());
            System.out.println("batch" + " took " + lookupDuration + " milliseconds");

        }
        averageLookupTime = totalTime / noOfLookups;
        String type = "individual";
        if (batch) {
            type = "batch";
        }
        groupedData.append(fileId + "," + type + "," + noOfLookups + "," + averageLookupTime + "," + totalTime + System.lineSeparator());
        System.out.println("FileNumber " + fileId + ", Number of terminology checks = " + noOfLookups + ",Average time per terminology check = " + averageLookupTime + "ms ,total time = " + totalTime + "ms");
    }

    private void flushAndClose() {
        allData.flush();
//        allData.close();
        groupedData.flush();
//        groupedData.close();
        errorData.flush();
    }

    private void print_https_cert(HttpsURLConnection con) {

        if (con != null) {

            try {

                System.out.println("Response Code : " + con.getResponseCode());
                System.out.println("Cipher Suite : " + con.getCipherSuite());
                System.out.println("\n");

                Certificate[] certs = con.getServerCertificates();
                for (Certificate cert : certs) {
                    System.out.println("Cert Type : " + cert.getType());
                    System.out.println("Cert Hash Code : " + cert.hashCode());
                    System.out.println("Cert Public Key Algorithm : "
                            + cert.getPublicKey().getAlgorithm());
                    System.out.println("Cert Public Key Format : "
                            + cert.getPublicKey().getFormat());
                    System.out.println("\n");
                }

            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String print_content(HttpsURLConnection con) {
        StringBuilder output = new StringBuilder();
        if (con != null) {

            try {

//                System.out.println("****** " + con.getURL() + " ********");
//                System.out.println("****** " + con.getHeaderField("keep-alive")+ " ********");
//                System.out.println("****** " + con.getRequestMethod()+ " ********");
//                System.out.println("****** " + con.getRequestProperty("system")+ " ********");
                BufferedReader br
                        = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null) {
                    output.append(input);
                }
                br.close();

            } catch (IOException e) {
                errorData.append(fileId + "," + con.getURL() + "," + e.getMessage() + System.lineSeparator());
            }

        }
        return output.toString();

    }

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
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

}
