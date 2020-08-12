/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.DSTU2;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.DSTU3;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Unix tool like utility Takes valid fhir in xml|json and converts it to
 * json|xml optional fhir version 2 or 3, can take input from stdin using -
 * parameter in place of filename Writes to stdout
 *
 * @author simonfarrow
 */
public class FhirFormatSwap {

    /**
     *
     * @param args [-2|3] &lt;filename&gt; | -
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        if (args.length == 0 || args.length > 2) {
            usage();
        }

        int offset = 0;
        if (args[0].matches("^-[23]$")) {
            offset++;
            FHIRJsonXmlAdapter.setFhirVersion(args[0].equals("-2") ? DSTU2 : DSTU3);
        } else {
            FHIRJsonXmlAdapter.setFhirVersion(DSTU3);
        }

        if (args.length - offset != 1) {
            usage();
        }

        String content = null;
        String filename = args[offset];
        if (filename.equals("-")) {
            content = readFromStdin();
        } else if (new File(filename).exists()) {
            content = Utils.readFile2String(args[offset]);
        } else {
            error("Source file " + filename + " does not exist");
        }

        content = content.trim();
        if (content.startsWith("{")) {
            System.out.println(FHIRJsonXmlAdapter.fhirConvertJson2Xml(content));
        } else if (content.startsWith("<")) {
            System.out.println(FHIRJsonXmlAdapter.fhirConvertXml2Json(content));
        } else {
            error("Cannot detect content type for file " + filename);
        }
    }

    private static void usage() {
        error("usage: [-(2|3)] <filename> | -");
    }

    private static void error(String s) {
        System.err.println(s);
        System.exit(-1);
    }

    /**
     *
     * @return String read from stdin
     * @throws IOException
     */
    private static String readFromStdin() throws IOException {
        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String inputStr = null;
        while ((inputStr = bufReader.readLine()) != null) {
            sb.append(inputStr);
        }
        return sb.toString();
    }
}
