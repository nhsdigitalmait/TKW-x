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

package uk.nhs.digital.mait.tkwx.tk.internalservices;

/**
 *
 * @author riro
 */
public class RefData {

    private boolean b64 = false;
    private boolean compress = false;
    private boolean sign = false;
    private String alias = null;
    private int arbitaryId = -1;

    public int getArbitaryId() {
        return arbitaryId;
    }

    public void setArbitaryId(int arbitaryId) {
        this.arbitaryId = arbitaryId;
    }

    public boolean isB64() {
        return b64;
    }

    public void setB64(boolean b64) {
        this.b64 = b64;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
