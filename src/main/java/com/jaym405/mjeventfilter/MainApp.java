
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

        List<List<String>> headerlist = new ArrayList<>();
        List<List<String>> csvrecordslist = new ArrayList<List<String>>();
        List<List<String>> recordslist = new ArrayList<List<String>>();

        // Read from CSV 
        try (CSVReader csvReader = new CSVReader(new FileReader("reports.csv"));) {
            String[] values = null;
            int lineno = 0;
            while ((values = csvReader.readNext()) != null) {
                if (lineno == 0) {
                    headerlist.add(Arrays.asList(values));
                } else {
                    csvrecordslist.add(Arrays.asList(values));
                }
                lineno++;
            }
            System.out.println("CSV File size: "+lineno);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Copy to recordslist
        recordslist = csvrecordslist.stream().collect(Collectors.toList());

        ReportUtils rutils = new ReportUtils();

        // Read from JSON 
        List<List<String>> jsonrecordslist = rutils.processgetJSON();
        // Read from XML
        List<List<String>> xmlrecordslist = rutils.processgetXML();
        // Add JSON to recordslist
        recordslist.addAll(jsonrecordslist);
        // Add XML to recordslist
        recordslist.addAll(xmlrecordslist);
        
         System.out.println("Total records size: "+recordslist.size());

        // Get position for filtering by packets-serviced
        int psposition = 0;
        for (int i = 0; i < headerlist.size(); i++) {
            List<String> headers = headerlist.get(i);
            System.out.println("headers:" + headers);
            for (int k = 0; k < headers.size(); k++) {
                if ("packets-serviced".equals(headers.get(k))) {
                    psposition = k;
                }
            }
        }
        
        // Filter and sort combined list
        recordslist = rutils.filterzeropackets(recordslist, psposition);
        System.out.println("Total records size after filter: "+recordslist.size());
        recordslist = rutils.sortbyrequesttime(recordslist);        
       System.out.println("Total records size after sort: "+recordslist.size());

        // Generate output CSV
        rutils.writeoutputtoCSV(headerlist, recordslist);
    }
}
