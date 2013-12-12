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

package edu.byu.hr.timereporting.service;

import java.util.List;

import javax.portlet.PortletRequest;

import edu.byu.hr.dao.timereporting.StaffTimePunchDao;
import edu.byu.hr.model.timereporting.TimePunchEntry;
import edu.byu.hr.timereporting.util.TimePunchEmployeeKeyGenerator;
import edu.byu.hr.timereporting.util.TimePunchEmployeeKeyGeneratorImpl;
import org.springframework.stereotype.Service;

/**
 * Description
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */
@Service
public class StaffTimePunchServiceImpl implements StaffTimePunchService {

    StaffTimePunchDao dao;
    TimePunchEmployeeKeyGenerator timePunchEmployeeKeyGenerator = new TimePunchEmployeeKeyGeneratorImpl();

    public void setDao(StaffTimePunchDao dao) {
        this.dao = dao;
    }

    public void setTimePunchEmployeeKeyGenerator(TimePunchEmployeeKeyGenerator timePunchEmployeeKeyGenerator) {
        this.timePunchEmployeeKeyGenerator = timePunchEmployeeKeyGenerator;
    }

    /**
     * Returns a list of <code>TimePunchEntry</code> items for the employee.
     *
     * @param emplId Employee ID
     * @return List of <code>TimePUnchEntry</code> items
     */
    @Override
    public List<TimePunchEntry> getTimePunchEntries(PortletRequest request, String emplId, boolean refresh) {
        return dao.getTimePunchEntries(emplId);
    }

    /**
     * Starts logging time for the employee to the indicated job code.
     *
     * @param emplId
     * @param jobCode
     */
    @Override
    public void punchInTimeClock(PortletRequest request, String emplId, int jobCode, String clientIP) {
        dao.punchInTimeClock(emplId, jobCode, clientIP);
    }

    /**
     * Stops logging time for the employee to the indicated job code.
     *
     * @param emplId
     * @param jobCode
     * @param clientIP
     */
    @Override
    public void punchOutTimeClock(PortletRequest request, String emplId, int jobCode, String clientIP) {
        dao.punchOutTimeClock(emplId, jobCode, clientIP);
    }
}
