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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine;

import java.io.*;
import javax.xml.transform.URIResolver;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.xsltransform.ResourceURIResolver;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;

/**
 * Subclass of the SpineSchemaValidator to apply the "template CDA" transform to
 * a captured, on-the-wire CDA message before schema validating with the
 * template schema. Since the output of schema validation is written with
 * reference to this transform output, and not the actual validated message,
 * this uses the "supporting data" function of the TKW validator mode to save
 * the templated CDA form.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class CDAConformanceSpineXsltValidator
        extends XsltValidator {

    private String conformanceTransformOutput = null;
    private VariableProvider vProvider = null;

    public CDAConformanceSpineXsltValidator()
            throws Exception {
        super();
    }

    @Override
    public String getSupportingData() {
        return conformanceTransformOutput;
    }

    @Override
    public synchronized void initialise()
            throws Exception {
        // Separate copies of the transforms because we don't believe that
        // the MIM and ITK ones are the same (the story keeps changing)
        //
        InputStream is = null;
        TransformManager t = TransformManager.getInstance();
        URIResolver u = t.getFactoryURIResolver();

        ResourceURIResolver r = new ResourceURIResolver();
        is = getClass().getResourceAsStream("TemplateLookup.xml");
        r.addResource("TemplateLookup.xml", is);
        is = getClass().getResourceAsStream("postTxReorder_v2.xslt");
        r.addResource("postTxReorder_v2.xslt", is);
        is = getClass().getResourceAsStream("SerialisedCDAModel.xml");
        r.addResource("SerialisedCDAModel.xml", is);
        is = getClass().getResourceAsStream("sortedMifClassAtts.xml");
        r.addResource("sortedMifClassAtts.xml", is);

        t.setFactoryURIResolver(r);

        is = getClass().getResourceAsStream("TrueCDAToCDALike_spine_v2.xsl");
        t.addTransform("TrueCDAToCDALike_v2.xsl", is);
        is = getClass().getResourceAsStream("postTxReorder_v2.xslt");
        t.addTransform("postTxReorder_v2.xslt", is);

        t.setURIResolver("postTxReorder_v2.xslt", r);
        t.setURIResolver("TrueCDAToCDALike_v2.xsl", r);

        t.setFactoryURIResolver(u);
        setTransform();
    }

    // TODO: This is just a copy of the one from SpineSchemaValidator - put in
    // the transform code...
    @Override
    public ValidationReport[] validate(SpineMessage sm)
            throws Exception {
        String o = sm.getHL7Part();
        if (o == null) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error: null content");
            return e;
        }
        ValidationReport report[] = null;
        try {

            // TEMPORARY FIX
            PrintStream stdout = System.out;
            ByteArrayOutputStream logstream = new ByteArrayOutputStream();
            PrintStream logcapture = new PrintStream(logstream);
            System.setOut(logcapture);
            // END TEMPORARY FIX            
            conformanceTransformOutput = TransformManager.getInstance().doTransform("TrueCDAToCDALike_v2.xsl", new StringReader(o));

            // TEMPORARY FIX
            System.setOut(stdout);
            Logger.getInstance().log(CDAConformanceSpineXsltValidator.class.getName() + ".validate() TEMPORARY REDIRECTION " + logstream.toString());
            // END TEMPORARY FIX

            try {
                StringBuilder sb = new StringBuilder(transformFile);
                TransformManager t = TransformManager.getInstance();
                String x = t.doTransform(transformFile, conformanceTransformOutput);
                if (x.toUpperCase().contains(checkterm.toUpperCase())) {
                    ValidationReport v = new ValidationReport("Failed");
                    sb.append(" returned:\n");
                    sb.append(x);
                    v.setTest(sb.toString());
                    report = new ValidationReport[1];
                    report[0] = v;
                } else {
                    ValidationReport v = new ValidationReport("Pass");
                    v.setPassed();
                    sb.append(" returned:\n");
                    sb.append(x);
                    v.setTest(sb.toString());
                    report = new ValidationReport[1];
                    report[0] = v;
                }
            } catch (Exception e) {
                ValidationReport ex = new ValidationReport("Exception executing validation " + transformFile + " : " + e.getMessage());
                report = new ValidationReport[1];
                report[0] = ex;
            }

//            StreamSource subjectSource = new StreamSource(new StringReader(conformanceTransformOutput));
//            SaxValidator sv = new SaxValidator(subjectSource, schemaSource);
//            sv.validate(v);
        } catch (Exception e1) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error during validation: " + e1.getMessage());
            return e;
        }
//        if (validatorExceptions.size() == 0) {
//            ValidationReport[] e = new ValidationReport[1];
//            e[0] = new ValidationReport("Pass");
//            e[0].setTest(transformFile);
//            e[0].setPassed();
//            return e;
//        }
//        return (ValidationReport[])validatorExceptions.toArray(new ValidationReport[validatorExceptions.size()]);
        return report;
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        vProvider = vp;
    }
}
