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
package org.jboss.dashboard.commons.misc;

import java.util.Map;
import java.util.HashMap;

/**
 * The class <code>Chronometer</code> permits to measure the time, with millisecond precision.
 */
public final class Chronometer {

    protected Map marks;
    protected Long stopTime;

    public Chronometer() {
        marks = new HashMap();
    }

	/**
	 * Start the timer.
	 */
	public void start() {
        stopTime = null;
        marks.clear();
        marks.put("START", new Long(System.currentTimeMillis()));
    }

	/**
	 * Stop the timer.
	 */
	public void stop() {
        stopTime = new Long(System.currentTimeMillis());
	}

    /**
     * Resume the timer.
     */
    public void resume() {
        stopTime = null;
    }

    /**
     * Set a mark.
     */
    public void start(String mark) {
        marks.put(mark, new Long(System.currentTimeMillis()));
    }


    /**
	 * Return the elapsed time measured in milliseconds since the very start.
	 * @return  long, the time.
	 */
	public long elapsedTime() {
		return elapsedTime("START");
	}

    /**
	 * Return the elapsed time measured in milliseconds since the specified mark was set.
	 * @return  long, the time.
	 */
    public long elapsedTime(String mark) {
        if (!marks.containsKey(mark)) return 0;

        Long mtime = (Long) marks.get(mark);
        long stop = stopTime != null ? stopTime.longValue() : System.currentTimeMillis();
        return stop - mtime.longValue();
    }

    public static String formatElapsedTime(long millis) {
        long milliseconds = millis;
        long seconds = milliseconds / 1000; milliseconds %= 1000;
        long minutes = seconds / 60; seconds %= 60;
        long hours = minutes / 60; minutes %= 60;
        long days = hours / 24; hours %= 24;
        long weeks = days / 7; days %= 7;
        double secondsd = (double) (seconds * 1000 + milliseconds) / 1000;

        StringBuffer buf = new StringBuffer();
        if (weeks > 0) buf.append(weeks).append(" weeks ");
        if (days > 0) buf.append(days).append("d ");
        if (hours > 0) buf.append(hours).append("h ");
        if (minutes > 0) buf.append(minutes).append("m ");
        if (secondsd > 0) buf.append(secondsd).append("s");
        if (buf.length() == 0) return "0s";
        return buf.toString();
    }

    /**
	 * Main function
	 * @param arg command-line arguments.
	 */
	public static void main(String[] arg) throws Exception {
        Chronometer chrono = new Chronometer();
        chrono.start();
		Thread.sleep(3000);
		chrono.start("INTERMEDIATE");
        Thread.sleep(2000);
        System.out.println("INTERMEDIATE "+chrono.elapsedTime("INTERMEDIATE"));
        System.out.println("TOTAL "+chrono.elapsedTime());
        Thread.sleep(1000);
        System.out.println("INTERMEDIATE "+chrono.elapsedTime("INTERMEDIATE"));
        System.out.println("TOTAL "+chrono.elapsedTime());
	}
}
