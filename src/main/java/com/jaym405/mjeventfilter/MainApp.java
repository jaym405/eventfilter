/*
Read the 3 input files reports.json, reports.csv, reports.xml and output a combined CSV file:
1. The same column order and formatting as reports.csv 
2. All report records with packets-serviced equal to zero should be excluded 
3. records should be sorted by request-time in ascending order 
4. the application should print a summary showing the number of 
records in the output file associated with each service-guid.
 */
package com.jaym405.mjeventfilter;

import com.opencsv.CSVReader;
import java.util.Arrays;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainApp {

    public static void main(String[] args) {
        ReportUtils rutils = new ReportUtils();
        ProcessFile pcsv = new ProcessCSV();
        ProcessFile pjson = new ProcessJSON();
        ProcessFile pxml = new ProcessXML();

        List<String> inputcsvheaderlist = new ArrayList<String>();
        List<ReportBean> inputcsvrecordslist = new ArrayList<ReportBean>();

        List<ReportBean> outputrecordslist = new ArrayList<ReportBean>();

        inputcsvheaderlist = pcsv.getinputfilehdrs("reports.csv");
        inputcsvrecordslist = pcsv.getinputfilerecs("reports.csv");

        // Copy to recordslist
        outputrecordslist = inputcsvrecordslist.stream().collect(Collectors.toList());
        System.out.println("recordslist size after adding CSV: " + outputrecordslist.size());

        //  Read from JSON and add to recordslist
        List<ReportBean> jsonrecordslist = pjson.getinputfilerecs("reports.json");
        outputrecordslist.addAll(jsonrecordslist);
        System.out.println("recordslist size after adding JSON: " + outputrecordslist.size());

        // Read from XML and add to recordslist
        List<ReportBean> xmlrecordslist = pxml.getinputfilerecs("reports.xml");
        outputrecordslist.addAll(xmlrecordslist);

        System.out.println("recordslist size after adding XML: " + outputrecordslist.size());

        // Filter and sort combined list 
        outputrecordslist = rutils.filterAndSort(outputrecordslist);

        System.out.println("Total records size after filter and sort: " + outputrecordslist.size());

        // Generate output CSV
        pcsv.writeoutputfile(inputcsvheaderlist, outputrecordslist, "combinedreport.csv");

        // Generate output
        rutils.outputserviceguidcounts(outputrecordslist);

    }
}
