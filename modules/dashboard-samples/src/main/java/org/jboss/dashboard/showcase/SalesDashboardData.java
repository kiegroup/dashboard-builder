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
package org.jboss.dashboard.showcase;

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Simple tool for generating random sales dashboard data
 */
public class SalesDashboardData {
    private static String[] DIC_PIPELINE = {"EARLY", "STANDBY", "ADVANCED"};
    private static String[] DIC_STATUS = {"CONTACTED", "STANDBY", "DEMO", "SHORT LISTED", "LOST", "WIN", "VERBAL COMMITMENT", "QUALIFIED"};
    private static String[] DIC_COUNTRIES = {
            "United States",
            "China",
            "Japan",
            "Germany",
            "France",
            "United Kingdom",
            "Brazil",
            "Italy",
            "India",
            "Canada",
            "Russia",
            "Spain",
            "Australia",
            "Mexico",
            "South Korea",
            "Netherlands",
            "Turkey",
            "Indonesia",
            "Switzerland",
            "Poland",
            "Belgium",
            "Sweden",
            "Saudi Arabia",
            "Norway"};
    private static String[] DIC_PRODUCT = {"PRODUCT 1", "PRODUCT 2", "PRODUCT 3", "PRODUCT 4", "PRODUCT 5", "PRODUCT 6", "PRODUCT 7", "PRODUCT 8", "PRODUCT 8", "PRODUCT 10", "PRODUCT 11"};
    private static String[] DIC_SALES_PERSON = {"Roxie Foraker", "Jamie Gilbeau", "Nita Marling", "Darryl Innes", "Julio Burdge", "Neva Hunger", "Kathrine Janas", "Jerri Preble"};
    private static String[] DIC_CUSTOMER = {"Company 1", "Company 2", "Company 3", "Company 3", "Company 4", "Company 5", "Company 6", "Company 7", "Company 8", "Company 9"};
    private static String[] DIC_SOURCE = {"Customer", "Reference", "Personal contact", "Partner", "Website", "Lead generation", "Event"};
    private static double MAX_AMOUNT = 15000;
    private static double MIN_AMOUNT = 8000;
    private static double AVG_CLOSING_DAYS = 90;
    private static String CSV_SEPARATOR = ";";
    private static int START_ID_VALUES = 10000;
    private NumberFormat numberFormat = DecimalFormat.getInstance(Locale.US);
    private DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public static class Opportunity {
        public String pipeline;
        public String status;
        public String country;
        public String product;
        public String customer;
        public String salesPerson;
        double amount;
        double probability;
        double expectedAmount;
        public Date creationDate;
        public Date closingDate;
        public String source;
        public String color;
    }

    private Random random = new Random(System.currentTimeMillis());

    public SalesDashboardData() {
    }

    private Date buildDate(int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, random.nextInt(28)); // No sales on 29, 30 and 31 ;-)
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1); // Some genius thought that the first month is 0
        c.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
        c.set(Calendar.MINUTE, random.nextInt(60));
        return c.getTime();
    }

    private Date addDates(Date d, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    private String randomValue(String[] dic) {
        return dic[random.nextInt(dic.length)];
    }

    private String format(double d) {
        return numberFormat.format(d);
    }

    private String format(Date d) {
        return dateFormat.format(d);
    }

    private int getParam(String param, CommandRequest req, int defaultValue) {
        String paramStr = req.getRequestObject().getParameter(param);
        if (paramStr == null || paramStr.trim().equals("")) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(paramStr);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * Generates a list of random opportunities in a range of years
     *
     * @param nOpportunitiesPerMonth Number of opportunities to create per month
     * @param yearStart              Start year
     * @param yearEnd                End year
     * @return
     */
    public List<Opportunity> generateRandomOpportunities(int nOpportunitiesPerMonth, int yearStart, int yearEnd) {

        List<Opportunity> opportunities = new ArrayList<Opportunity>();

        for (int year = yearStart; year <= yearEnd; year++) {
            for (int month = 1; month <= 12; month++) {
                for (int i = 0; i < nOpportunitiesPerMonth; i++) {
                    Opportunity opportunity = new Opportunity();
                    opportunity.amount = MIN_AMOUNT + random.nextDouble() * (MAX_AMOUNT - MIN_AMOUNT);
                    opportunity.creationDate = buildDate(month, year);
                    opportunity.closingDate = addDates(opportunity.creationDate, (int) (AVG_CLOSING_DAYS + random.nextDouble() * AVG_CLOSING_DAYS * 0.5));
                    opportunity.pipeline = randomValue(DIC_PIPELINE);
                    opportunity.status = randomValue(DIC_STATUS);
                    opportunity.country = randomValue(DIC_COUNTRIES);
                    opportunity.customer = randomValue(DIC_CUSTOMER);
                    opportunity.product = randomValue(DIC_PRODUCT);
                    opportunity.salesPerson = randomValue(DIC_SALES_PERSON);
                    opportunity.probability = random.nextDouble() * 100.0;
                    opportunity.expectedAmount = opportunity.amount * opportunity.probability;
                    opportunity.source = randomValue(DIC_SOURCE);
                    if (opportunity.probability < 25) {
                        opportunity.color = "RED";
                    } else if (opportunity.probability < 50) {
                        opportunity.color = "GREY";
                    } else if (opportunity.probability < 75) {
                        opportunity.color = "YELLOW";
                    } else {
                        opportunity.color = "GREEN";
                    }

                    opportunities.add(opportunity);
                }
            }
        }
        return opportunities;
    }

    /**
     * Generate a CSV representing a list of sales opportunities
     *
     * @param opportunities
     * @return
     */
    public String buildCSV(List<Opportunity> opportunities) {
        StringBuffer sb = new StringBuffer();

        sb.append(/*"ID" + CSV_SEPARATOR
                + */"Amount" + CSV_SEPARATOR
                + "Creation date" + CSV_SEPARATOR
                + "Closing date" + CSV_SEPARATOR
                + "Pipeline" + CSV_SEPARATOR
                + "Status" + CSV_SEPARATOR
                + "Customer" + CSV_SEPARATOR
                + "Country" + CSV_SEPARATOR
                + "Product" + CSV_SEPARATOR
                + "Sales person" + CSV_SEPARATOR
                + "Probability" + CSV_SEPARATOR
                + "Source" + CSV_SEPARATOR
                + "Expected amount" + CSV_SEPARATOR
                + "Color");

        sb.append("\n");

        int idOpportunity = START_ID_VALUES;
        for (Opportunity opportunity : opportunities) {
            //sb.append(format(idOpportunity) + CSV_SEPARATOR);
            sb.append(format(opportunity.amount) + CSV_SEPARATOR);
            sb.append(format(opportunity.creationDate) + CSV_SEPARATOR);
            sb.append(format(opportunity.closingDate) + CSV_SEPARATOR);
            sb.append(opportunity.pipeline + CSV_SEPARATOR);
            sb.append(opportunity.status + CSV_SEPARATOR);
            sb.append(opportunity.customer + CSV_SEPARATOR);
            sb.append(opportunity.country + CSV_SEPARATOR);
            sb.append(opportunity.product + CSV_SEPARATOR);
            sb.append(opportunity.salesPerson + CSV_SEPARATOR);
            sb.append(format(opportunity.probability) + CSV_SEPARATOR);
            sb.append(opportunity.source + CSV_SEPARATOR);
            sb.append(format(opportunity.expectedAmount) + CSV_SEPARATOR);
            sb.append(opportunity.color);
            sb.append("\n");
            idOpportunity++;
        }

        return sb.toString();
    }

    /**
     * Generates CSV corresponding to a random sales dashboard demo. It outputs the generated CSV through the response
     * stream. It can receive some optional parameters:
     * opMonth - Number of opportunities per month to generale - default: 50
     * startYear - Initial year to generate random data - default: current year - 2
     * endYear - End year to generate random data - default: current year + 2
     * @param request
     * @return
     * @throws Exception
     */
    public CommandResponse actionGenerateCSV(CommandRequest request) throws Exception {

        SalesDashboardData salesDashboardData = new SalesDashboardData();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int opportunitiesPerMonth = getParam("opMonth", request, 30);
        int startYear = getParam("startYear", request, calendar.get(Calendar.YEAR) -2);
        int endYear = getParam("endYear", request, calendar.get(Calendar.YEAR) +2);

        List<Opportunity> opportunities = salesDashboardData.generateRandomOpportunities(opportunitiesPerMonth, startYear, endYear);

        String csv = salesDashboardData.buildCSV(opportunities);

        String contentDisposition = "text/csv";

        return new SendStreamResponse(new ByteArrayInputStream(csv.getBytes()), contentDisposition, true);

    }
}
