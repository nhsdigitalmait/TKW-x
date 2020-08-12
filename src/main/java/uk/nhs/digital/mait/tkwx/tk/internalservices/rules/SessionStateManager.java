/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * allows management of session state for rules This is a singleton.
 *
 * @author simonfarrow
 */
public class SessionStateManager {

    private static final boolean DEBUG = false;

    /**
     *
     * @return single instance
     */
    public static SessionStateManager getInstance() {
        if (sessionStateManager == null) {
            sessionStateManager = new SessionStateManager();
        }
        return sessionStateManager;
    }

    /**
     * overload with default current session id
     *
     * @param variableName
     * @return
     */
    public String getValue(String variableName) {
        return getValue(currentSessionID, variableName);
    }

    /**
     *
     * @param sessionID
     * @param variableName
     * @return
     */
    public String getValue(String sessionID, String variableName) {
        String result = null;
        if (sessionID != null) {
            if (sessions.get(sessionID) != null) {
                result = sessions.get(sessionID).get(variableName);
            }
        }
        return result;
    }

    /**
     * overload with default current session id
     *
     * @param variableName
     * @param value
     */
    public void setValue(String variableName, String value) {
        setValue(currentSessionID, variableName, value);
    }

    /**
     * Setting a null value causes an unset. When the last variable has been
     * unset the session is removed.
     *
     * @param sessionID
     * @param variableName
     * @param value
     */
    public void setValue(String sessionID, String variableName, String value) {
        if (DEBUG) {
            System.out.println("Setting " + sessionID + " variable " + variableName + " to " + value);
        }
        if (sessionID != null) {
            HashMap<String, String> session = null;
            if (sessions.get(sessionID) == null) {
                session = new HashMap<>();
                sessions.put(sessionID, session);
            } else {
                session = sessions.get(sessionID);
            }

            if (value != null) {
                session.put(variableName, value);
            } else {
                if (session.get(variableName) != null) {
                    session.remove(variableName);
                }
                if (session.isEmpty()) {
                    sessions.remove(sessionID);
                }
            }
        }
    }

    /**
     * reset all the state for this session
     *
     * @param sessionID
     */
    public void resetSession(String sessionID) {
        if (DEBUG) {
            System.out.println("Resetting state variables for session " + sessionID);
        }
        if (sessions.get(sessionID) != null) {
            sessions.remove(sessionID);
        }
        if (sessionID.equals(currentSessionID)) {
            currentSessionID = null;
        }
    }

    /**
     * singleton
     */
    private SessionStateManager() {
        sessions = new ConcurrentHashMap<>();
    }

    /**
     * @return the currentSessionID
     */
    public String getCurrentSessionID() {
        return currentSessionID;
    }

    /**
     * @param currentSessionID the currentSessionID to set
     */
    public void setCurrentSessionID(String currentSessionID) {
        if (DEBUG) {
            System.out.println("Setting current session id to " + currentSessionID);
        }
        this.currentSessionID = currentSessionID;
    }

    private String currentSessionID = null;
    // the main collection needs to be threadsafe but the contained collections should not
    private final ConcurrentHashMap<String, HashMap<String, String>> sessions;

    private static SessionStateManager sessionStateManager;
}
