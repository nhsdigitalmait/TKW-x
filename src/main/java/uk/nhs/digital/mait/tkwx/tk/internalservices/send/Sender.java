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

package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author riro
 */
public interface Sender {

    public void init(ToolkitSimulator simulator, SenderRequest sr, boolean useTls, String destinationDirectory);
}
