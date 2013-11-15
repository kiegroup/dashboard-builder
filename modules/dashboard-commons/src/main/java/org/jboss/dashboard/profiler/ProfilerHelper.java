/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.profiler;

public class ProfilerHelper {

    public static String printCurrentContext() {
        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        CodeBlockTrace blockInProgress = threadProfile.getCodeBlockInProgress();
        return blockInProgress.printContext(true);
    }

    public static void checkRuntimeConstraints() {
        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        if (threadProfile == null) return;

        CodeBlockTrace blockInProgress = threadProfile.getCodeBlockInProgress();
        if (blockInProgress != null) {
            try {
                blockInProgress.checkRuntimeConstraints();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addRuntimeConstraint(RuntimeConstraint constraint) {
        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        if (threadProfile == null) return;

        CodeBlockTrace blockInProgress = threadProfile.getCodeBlockInProgress();
        if (blockInProgress != null) blockInProgress.addRuntimeConstraint(constraint);
    }
}
