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
package uk.nhs.digital.mait.tkwx.mesh;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitMeshHandler;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Class to watch Mesh message directories and pass references to the message
 * control file to a handler
 *
 * This watcher is designed to look for the control file arriving in a directory
 *
 * Requirements: The data file should be placed in the destination directory
 * before the control file.The receiving application should look for the control
 * file before starting to handle the data transfer.
 *
 */
public class MeshMailboxWatcher extends Thread {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private boolean trace = false;
    private final Path dir;
    private final ToolkitMeshHandler toolkitMeshHandler;
    private final String mailboxType;
    private boolean stopWatcher = false;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public MeshMailboxWatcher(Path d, String s, ToolkitMeshHandler tmh) throws IOException {
        dir = d;
        toolkitMeshHandler = tmh;
        mailboxType = s;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        register();

        // enable trace after initial registration
        this.trace = true;

        start();
    }

    public void close() {
        stopWatcher = true;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register() throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
        }
        keys.put(key, dir);
    }

    /**
     * Process all events for keys queued to the watcher
     */
    @Override
    public void run() {
        this.setName("MeshMailboxWatcherThread");

        for (;;) {
            if (stopWatcher) {
                return;
            }

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                error("MESH Mailbox Watcher - failed to instantiate key: " + x);
                return;
            }

            Path path = keys.get(key);
            if (path == null) {
                error("WatchKey not recognized!!");
                continue;
            }

            try {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    if (kind == OVERFLOW) {
                        continue;
                    }
                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = path.resolve(name);
                    // The watcher looks for the ctl file because the spec 
                    // states that the data file will be placed in the directory first
                    if (child.getFileName().toString().endsWith(".ctl")) {
                        if (kind == ENTRY_CREATE) {
                            toolkitMeshHandler.handle(mailboxType, child);
                            continue;
                        }
                        if (kind == ENTRY_DELETE) {
                            continue;
                        }
                        if (kind == ENTRY_MODIFY) {
                            continue;
                        }
                    }
                }

            } catch (Exception e) {
                error("Failure to open Mesh mailbox element:" + e.getMessage());

            } finally {
                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        error("MESH Mailbox Watcher - Directories are inaccessible");
                        break;
                    }
                }
            }
        }
    }

    private void error(String s) {
        Logger.getInstance().log(s);
        System.err.println(s);

    }
}
