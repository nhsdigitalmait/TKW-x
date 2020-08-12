/*

 Copyright 2014 Health and Social Care Information Centre
 Solution Assurance damian.murphy@hscic.gov.uk

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

import java.io.File;
import java.io.FileNotFoundException;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.spinetools.spine.messaging.DefaultFileSaveDistributionEnvelopeHandler;
import uk.nhs.digital.mait.commonutils.util.FileLocker;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Autotest specific subclass Overrides
 * DefaultFileSaveDistributionEnvelopeHandler to ensure that save directory path
 * is always set from current system property
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class AutotestFileSaveDistributionEnvelopeHandler
        extends DefaultFileSaveDistributionEnvelopeHandler {

    /**
     * public constructor
     *
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    public AutotestFileSaveDistributionEnvelopeHandler()
            throws FileNotFoundException, IllegalArgumentException {
        super();
    }

    /**
     * Ensures file save directory is always set to relevant system property
     *
     * @param d the distribution envelope object
     * @throws Exception
     */
    @Override
    public void handle(DistributionEnvelope d)
            throws Exception {
        // in autotest mode if we get a null pointer then we may as well throw an exception
        fileSaveDirectory = new File(System.getProperty(SAVE_DIRECTORY));

        // the following is a clone of the super method but includes file locking
        d.parsePayloads();
        String s = d.getService();
        s = s.replaceAll(":", "_");
        StringBuilder sb = new StringBuilder(s);
        sb.append("_");
        sb.append(getFileSafeMessageID(d.getTrackingId()));
        sb.append(".message");

        File outfile = new File(fileSaveDirectory, sb.toString());

        // unlike the default class we lock and unlock the log so that test autotamation 
        // only handles completed log files
        try (FileLocker fl = new FileLocker(outfile.getPath())) {
            Utils.writeString2File(outfile, d.toString());
        }

        if (reportFilename) {
            System.out.println(outfile.getCanonicalPath());
        }
    }
}
