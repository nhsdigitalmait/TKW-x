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

import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileWriter;

/**
 * Internal class for preparing test reports.
 *
 * @author Damian Murphy murff@warlock.org
 */
class ReportWriter {

    static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final String runDate = ReportWriter.FORMAT.format(new Date());
    private Script script = null;
    private File runDirectory = null;
    private final ArrayList<ReportItem> items = new ArrayList<>();
    private String validatorReport;
    private boolean dumped = false;

    ReportWriter(Script s, File l) {
        this.validatorReport = null;
        script = s;
        runDirectory = l;
    }

    void setValidatorReport(String v) {
        validatorReport = v;
    }

    @Override
    protected void finalize()
            throws Throwable {
        super.finalize();
        if (!dumped) {
            dump();
        }
    }

    void log(ReportItem r) {
        items.add(r);
    }

    void dump()
            throws Exception {
        if (dumped) {
            return;
        }
        dumped = true;
        File f = new File(runDirectory, "test_log.html");
        FileWriter fw = new FileWriter(f);

        fw.write("<html><head><title>TKW Automated Test Report: ");
        fw.write(script.getName());
        fw.write(" run at ");
        fw.write(runDate);
        fw.write("</title></head><body><h1>Automated Test Report</h1>");
        fw.write("<h3>Run at: ");
        fw.write(runDate);
        fw.write(" using test script ");
        fw.write(script.getName());
        fw.write("</h3>");
        fw.flush();
        // Summary
        int testcount = 0;
        int passcount = 0;
        int failcount = 0;
        int checkcount = 0;
        for (ReportItem i : items) {
            if (i.getPassed() != null) {
                switch (i.getPassed()) {
                    case PASS:
                        testcount++;
                        passcount++;
                        break;
                    case FAIL:
                        testcount++;
                        failcount++;
                        break;
                    case CHECK:
                        checkcount++;
                        break;
                    default:
                        break;
                }
            }
        }
        fw.write("<h2>Summary</h2><table><tr><td>Tests run</td><td>");
        fw.write(Integer.toString(testcount));
        fw.write("</td></tr><tr><td>Passed</td><td>");
        fw.write(Integer.toString(passcount));
        fw.write("</td></tr><tr><td>Failed</td><td>");
        fw.write(Integer.toString(failcount));
        fw.write("</td></tr><tr><td>Checks run</td><td>");
        fw.write(Integer.toString(checkcount));
        fw.write("</td></tr></table><br/>");

        fw.flush();
        fw.write("<h2>Results</h2>");
        int row = 0;
        boolean subStatus = true;
        boolean reportedAnything = false;
        StringBuilder tables = new StringBuilder();
        StringBuilder sched = new StringBuilder();
        String prevSchedule = "initial";
        String prevTime = null;
        for (ReportItem r : items) {
            // create a stringbuilder to create each schedule

            if (!r.getSchedule().equals(prevSchedule)) {
                if (reportedAnything) {
                    //wrap up looseends and start new table object
                    sched.append("</table></h3>");
                    StringBuilder subhead = new StringBuilder();
                    String colour = (subStatus) ? "#008000" : "#900000";
                    subhead.append("<h3><table><tr><td style=\"color:" + colour + "\">Test Case: ");
                    subhead.append(prevSchedule);
                    subhead.append("</td></tr><tr><td>Run at: ");
                    subhead.append(prevTime);
                    subhead.append("</td></tr><tr>");
                    if (subStatus) {
                        subhead.append("<td style=\"color:" + colour + "\">Status: PASSED</td></tr></table>");
                    } else {
                        subhead.append("<td style=\"color:" + colour + "\">Status: FAILED</td></tr></table>");
                    }
                    sched.insert(0, subhead);
                    tables.append(sched.toString());
                    sched = new StringBuilder();
                }

                reportedAnything = true;
                subStatus = true;
                sched.append("<table width=\"100%\"><tr bgcolor=\"#000000\">");
                sched.append("<td width=\"15%\" style=\"color:#FFFFFF\"><b>Test</b></td>");
                sched.append("<td width=\"40%\" style=\"color:#FFFFFF\"><b>Description</b></td>");
                sched.append("<td width=\"5%\" style=\"color:#FFFFFF\"><b>Result</b></td>");
                sched.append("<td width=\"40%\" style=\"color:#FFFFFF\"><b>Comment</b></td></tr>");
            }
            String bgc = null;
            if ((row % 2) == 0) {
                bgc = "#FFFFFF";
            } else {
                bgc = "#EAEAEA";
            }
            sched.append("<tr bgcolor=\"");
            sched.append(bgc);
            sched.append("\">");
            String colour = null;
            if (r.getPassed() == TestResult.PASS) {
                colour = "#008000";
            } else if (r.getPassed() == TestResult.FAIL) {
                colour = "#900000";
                subStatus = false;
            } else if (r.getPassed() == TestResult.CHECK) {
                colour = "#3A5FCD";
            } else {
                colour = "#000000";
            }
            if (r.getLogFile() != null) {
                StringBuilder sb = new StringBuilder("<a href=\"");
                sb.append(Schedule.getRelativeLinkPath(r.getLogFile(), runDirectory, false));
                sb.append("\">");
                sb.append(r.getTest());
                sb.append("</a>");
                sched.append(writeCell(sb.toString(), colour));
            } else {
                sched.append(writeCell(r.getTest(), colour));
            }
            sched.append(writeCell(r.getDetail(), colour));
            sched.append(writeCell(r.getPassed() != null ? r.getPassed().toString() : "", colour));
            sched.append(writeCell(r.getComment(), colour));
            sched.append("</tr>");

            prevSchedule = r.getSchedule();
            prevTime = r.getTime();

            ++row;
        }
        if (reportedAnything) {
            //wrap up looseends

            sched.append("</table></h3>");
            StringBuilder subhead = new StringBuilder();
            String colour = (subStatus) ? "#008000" : "#900000";
            subhead.append("<h3><table><tr><td style=\"color:" + colour + "\">Test Case: ");
            subhead.append(prevSchedule);
            subhead.append("</td></tr><tr><td>Run at: ");
            subhead.append(prevTime);
            subhead.append("</td></tr><tr><td>Status: ");
            if (subStatus) {
                subhead.append("<font color=\"" + colour + "\">PASSED</font></td></tr></table>");
            } else {
                subhead.append("<font color=\"" + colour + "\">FAILED</font></td></tr></table>");
            }

            sched.insert(0, subhead);
            tables.append(sched.toString());
        }

        fw.write(tables.toString());
        fw.write("<hr/>Prepared by: ");
        fw.write(uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator.versionString);
        fw.write("</body></html>");
        fw.flush();
        fw.close();
    }

    private String writeCell(String content, String clr) {
        if (content == null) {
            return "<td/>";
        }
        StringBuilder sb = new StringBuilder("<td style=\"color:");
        sb.append(clr);
        sb.append("\">");
        sb.append(content);
        sb.append("</td>");
        return sb.toString();
    }
}
