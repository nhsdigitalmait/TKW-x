/*
 Copyright 2017  Richard Robinson rrobinson@nhs.net

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

import java.nio.file.Path;
import uk.nhs.digital.mait.tkwx.mesh.Handler;

/**
 * Abstract superclass for the TKW Mesh handlers. Implementation of
 * uk.nhs.digital.mait.tkwx.mesh.Handler
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public abstract class ToolkitMeshHandler
        implements Handler {

    protected MeshTransport toolkit = null;
    protected String savedMessagesDirectory = null;
    protected String meshMailboxId = null;

    public void setToolkit(MeshTransport t) throws Exception {
        toolkit = t;
    }

    public void setSavedMessagesDirectory(String s) {
        savedMessagesDirectory = s;
    }

    public void setMESHMailboxId(String s) {
        meshMailboxId = s;
    }

    @Override
    public abstract void handle(String meshDirectoryType, Path ctlFile)
            throws Exception;
}
