/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.byu.hr.model.timereporting;

import edu.byu.hr.HrPortletRuntimeException;
import org.apache.commons.lang.StringUtils;

/**
 * Description
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class Duration {
    int minutes;

    public Duration() {
    }

    public Duration (int minutes) {
        this.minutes = minutes;
    }

    //todo Change from hard-coded strategy to local-based formatting, maybe using some JodaTime converters
    public Duration (String hhmm) {
        if (StringUtils.isBlank(hhmm)) {
            // Be tolerant, consider empty or null string as 0.
            this.minutes = 0;
        } else {
            String[] values = hhmm.split(":");
            if (values.length == 2) {
                try {
                    int hours = Integer.parseInt(values[0]);
                    int minutes = Integer.parseInt(values[1]);
                    this.minutes = hours * 60 + minutes;
                } catch (NumberFormatException e) {
                    throw new HrPortletRuntimeException("Invalid Time entry not HH:MM, was:" + hhmm);
                }
            } else {
                throw new HrPortletRuntimeException("Invalid Time entry not HH:MM, was:" + hhmm);
            }
        }
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    //todo Change from hard-coded strategy to local-based formatting, maybe using some JodaTime converters
    public String getHoursAndMinutes() {
        return asHoursAndMinutes(minutes);
    }

    private static String getDoubleDigit(int number) {
        return (number < 10) ? "0"+number : Integer.toString(number);
    }

    public static String asHoursAndMinutes(int minutes) {
        int hours = minutes / 60;
        return getDoubleDigit(hours) + ":" + getDoubleDigit(minutes % 60);
    }

}