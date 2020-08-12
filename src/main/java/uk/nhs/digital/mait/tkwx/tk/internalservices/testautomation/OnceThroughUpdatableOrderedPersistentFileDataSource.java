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
 * Implementation of the ordered persistent file data source whose getNextId()
 * method will iterate once through the data set, and then return null.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class OnceThroughUpdatableOrderedPersistentFileDataSource
        extends AbstractUpdatableOrderedPersistentFileDataSource {

    private int currentId = -1;

    public OnceThroughUpdatableOrderedPersistentFileDataSource() {
    }

    @Override
    public String getNextId() {
        if (recordids.isEmpty()) {
            return null;
        }
        String id = null;
        synchronized (this) {
            ++currentId;
            if (currentId < recordids.size()) {
                id = recordids.get(currentId);
            }
        }
        return id;
    }

}
