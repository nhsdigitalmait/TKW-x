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
 * Implemented by test automation components that reference others from a
 * script, or that need an opportunity to initialise after the script has been
 * parsed. The script itself is read in order, so any given element in it may
 * reference another that has not yet been processed - hence script parsing
 * works via a compile+link pattern.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public interface Linkable {
    public void link(ScriptParser p) throws Exception;
}
