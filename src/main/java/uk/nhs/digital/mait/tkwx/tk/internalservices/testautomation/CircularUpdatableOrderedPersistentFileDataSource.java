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

/**
 * This is an implementation of the test automation data source which is
 * updatable, ordered and persistent. When getNextId() reaches the end of the
 * data, it starts again from the beginning.
 *
 * The data source is based on text files with one record per line, and fields
 * delimited by tabs.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class CircularUpdatableOrderedPersistentFileDataSource
        extends AbstractUpdatableOrderedPersistentFileDataSource {

    private int currentId = 0;

    public CircularUpdatableOrderedPersistentFileDataSource() {
    }

    @Override
    public String getNextId() {
        if (recordids.isEmpty()) {
            return null;
        }
        String id = null;
        synchronized (this) {
            id = recordids.get(currentId);
            ++currentId;
            if (currentId == recordids.size()) {
                currentId = 0;
            }
        }
        return id;
    }

}
