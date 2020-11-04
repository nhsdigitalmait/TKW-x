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
import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.DatasourceContext;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Abstract class to provide the storage and common methods for test automation
 * data sources that are file-based, can be updated with information extracted
 * from responses, and present their records in a predictable order.
 *
 * @author Damian Murphy murff@warlock.org
 */
public abstract class AbstractUpdatableOrderedPersistentFileDataSource
        implements DataSource {

    private boolean open = false;
    protected String name = null;
    protected String file = null;
    protected String extractorName = null;
    protected ResponseExtractor extractor = null;

    protected ArrayList<String> fields = new ArrayList<>();
    protected ArrayList<String> recordids = new ArrayList<>();
    protected HashMap<String, HashMap<String, String>> data = new HashMap<>();

    private void load() throws Exception, FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        boolean firstLine = true;
        while ((line = br.readLine()) != null) {
            if (firstLine) {
                readFields(line);
                firstLine = false;
            } else {
                readRecord(line);
            }
        }
        br.close();
        open = true;
    }

    /**
     * antlr parser constructor Loads the file. The context is expected to
     * have at least two elements, the first containing the data source name,
     * the second the file name. Data source files are expected to be text
     * files, one-record-per-line, with tab-delimited fields. The first line is
     * expected to contain field names - the field names are also used as the
     * substitution tag names for loading data into a message to be sent. The
     * first column in each row is assumed to be a record identifier.
     *
     * @param datasourceCtx
     */
    @Override
    public void init(DatasourceContext datasourceCtx) {
        try {
            name = datasourceCtx.datasourceName().getText();
            file = datasourceCtx.PATH().getText();
            load();
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, AbstractUpdatableOrderedPersistentFileDataSource.class.getName(), "Failed to load datasource " + ex.getMessage());
        }
    }

    private void readRecord(String l)
            throws Exception {
        // added a negative second parameter to force empty strings to appear for null fields
        String[] f = l.split("\\t", -1);
        if (f.length != fields.size()) {
            throw new Exception("Data error in source " + name + " : Field id " + f[0] + " field-count mismatch\r\nrecord field count: "+f.length+" table field count: "+fields.size());
        }
        recordids.add(f[0]);
        HashMap<String, String> record = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            record.put(fields.get(i), f[i]);
        }
        data.put(f[0], record);
    }

    private void readFields(String l) {
        String[] f = l.split("\\t");
        fields.addAll(Arrays.asList(f));
    }

    /**
     * Close this data source. The old copy of the file is written to a file of
     * the same name with ".backup" appended. The new state of the file is
     * written back to the original file name. Note that no check is actually
     * made that there are changes to save.
     *
     * @throws Exception
     */
    @Override
    public void close()
            throws Exception {
        synchronized (this) {
            File old = new File(file);
            File bck = new File(file + ".backup");
            old.renameTo(bck);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < fields.size() - 1; i++) {
                    bw.append(fields.get(i));
                    bw.append("\t");
                }
                bw.append(fields.get(fields.size() - 1));
                bw.append("\n");
                bw.flush();
                for (int i = 0; i < recordids.size(); i++) {
                    HashMap<String, String> rec = data.get(recordids.get(i));
                    for (int j = 0; j < fields.size() - 1; j++) {
                        bw.append(rec.get(fields.get(j)));
                        bw.append("\t");
                    }
                    bw.append(rec.get(fields.get(fields.size() - 1)));
                    bw.append("\n");
                    bw.flush();
                }
            }
            open = false;
        }
    }

    /**
     *
     * @return The data source name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Called by the loader to set up this data source in the compiled
     * automation script.
     *
     * @param p
     * @throws Exception
     */
    @Override
    public void link(ScriptParser p)
            throws Exception {
        if (extractorName != null) {
            extractor = p.getExtractor(extractorName);
            if (extractor == null) {
                throw new Exception("DataSource " + name + " extractor " + extractorName + " not found");
            }
            extractor.registerDatasourceListener(this);
        }
    }

    /**
     * Gets the list of field/tag names from the first row of the file.
     *
     * @return Iterable list of fields
     */
    @Override
    public Iterable<String> getTags() {
        return fields;
    }

    /**
     * Gets the list of ids
     *
     * @return Iterable list of keys
     */
    @Override
    public Iterable<String> getRecordids() {
        return recordids;
    }

    /**
     * Does the data source contain the given id.
     *
     * @param id
     * @return
     */
    @Override
    public boolean hasId(String id) {
        return data.containsKey(id);
    }

    /**
     * Does the data source contain the given tag.
     *
     * @param id
     * @param tag
     * @return
     */
    @Override
    public boolean hasValue(String id, String tag) {
        if (!data.containsKey(id)) {
            return false;
        }
        HashMap<String, String> r = data.get(id);
        return r.containsKey(tag);
    }

    /**
     * Get the value for the given record identifier and tag.
     *
     * @param id
     * @param tag
     * @return
     * @throws Exception if an unknown record id or tag is requested.
     */
    @Override
    public String getValue(String id, String tag)
            throws Exception {
        HashMap<String, String> r = data.get(id);
        if (r == null) {
            throw new Exception("Unknown record id " + id + " in data source " + name);
        }
        if (!fields.contains(tag)) {
            throw new Exception("Unknown field " + tag + " requested from data source " + name);
        }
        String v = r.get(tag);
        return v;
    }

    /**
     * Updates the given record id and tag.
     *
     * @param id record id in table
     * @param tag column name in table
     * @param value actual data string
     * @throws Exception if an attempt is made to update an unknown record or
     * tag.
     */
    @Override
    public void setValue(String id, String tag, String value)
            throws Exception {
        HashMap<String, String> r = data.get(id);
        if (r == null) {
            Logger.getInstance().log(WARNING, AbstractUpdatableOrderedPersistentFileDataSource.class.getName(),
                    "Unknown record id " + id + " in write request to data source " + name);
            return;
        }
        if (!fields.contains(tag)) {
            Logger.getInstance().log(WARNING, AbstractUpdatableOrderedPersistentFileDataSource.class.getName(),
                    "Unknown field " + tag + " tried to be written to data source " + name);
            return;
        }
        synchronized (this) {
            r.remove(tag);
            r.put(tag, value);
        }
    }

    /**
     * @return Always returns false since this is an updatable data source.
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Finalizer calls the close() method.
     *
     * @throws Throwable because "finalize()" does.
     */
    @Override
    public void finalize()
            throws Throwable {
        super.finalize();
        if (open) {
            this.close();
        }
    }

    /**
     * @param extractorName the extractorName to set
     */
    @Override
    public void setExtractorName(String extractorName) {
        this.extractorName = extractorName;
    }
}
