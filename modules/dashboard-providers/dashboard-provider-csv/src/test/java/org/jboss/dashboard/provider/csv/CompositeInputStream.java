/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.provider.csv;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class CompositeInputStream extends InputStream {

    List<InputStream> inputStreams;
    protected transient int _index;

    public CompositeInputStream() {
        inputStreams = new ArrayList<InputStream>();
        _index = 0;
    }

    public void addPart(InputStream is) {
        is.mark(1024*1024*1024);
        inputStreams.add(is);
    }

    public int read() throws IOException {
        InputStream is = currentStream();
        if (is == null) return -1;

        int b = is.read();
        if (b != -1) return b;

        nextStream();
        return read();
    }

    public void close() throws IOException {
        for (InputStream inputStream : inputStreams) {
            inputStream.close();
        }
    }

    protected InputStream currentStream() throws IOException {
        if (_index < inputStreams.size()) {
            return inputStreams.get(_index);
        }
        return null;
    }

    protected void nextStream() throws IOException {
        _index++;
        if (_index < inputStreams.size()) {
            inputStreams.get(_index).reset();
        }
    }
}
