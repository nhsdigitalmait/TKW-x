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

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 *
 * @author riro
 */
public class MAEstablished implements HandshakeCompletedListener {

    @Override
    public void handshakeCompleted(HandshakeCompletedEvent e) {
        System.out.println("Client - Mutual Authentication Established");
        Logger.getInstance().log("Client - Mutual Authentication Established");
    }
}
