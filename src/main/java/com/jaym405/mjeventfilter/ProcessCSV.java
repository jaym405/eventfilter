package com.jaym405.mjeventfilter;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessCSV implements ProcessFile {

    public List<ReportBean> getinputfilerecs(String filename) {
        List<ReportBean> csvrecordslist = new ArrayList<ReportBean>();
        // Read from CSV 
        try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
            String[] values = null;
            int lineno = 0;

            while ((values = csvReader.readNext()) != null) {
                ReportBean rep = new ReportBean();
                List<String> csvrecord = new ArrayList<String>();
                csvrecord = Arrays.asList(values);
                for (int i = 1; i < csvrecord.size(); i++) {
                    // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
                    if (lineno != 0) {
                        rep.setClientaddress(csvrecord.get(0));
                        rep.setClientguid(csvrecord.get(1));
                        rep.setRequesttime(csvrecord.get(2));
                        rep.setServiceguid(csvrecord.get(3));
                        rep.setRetriesrequest(csvrecord.get(4));
                        rep.setPacketsrequested(csvrecord.get(5));
                        rep.setPacketsserviced(csvrecord.get(6));
                        rep.setMaxholesize(csvrecord.get(7));
                    }
                }
                if (lineno != 0) {
                    csvrecordslist.add(rep);
                }
                lineno++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvrecordslist;
    }

    public List<String> getinputfilehdrs(String filename) {
        List<String> headerlist = new ArrayList<>();

        // Read from CSV 
        try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
            String[] values = null;
            int lineno = 0;
            while ((values = csvReader.readNext()) != null && lineno == 0) {
                List<String> csvrecord = new ArrayList<String>();
                csvrecord = Arrays.asList(values);        
                headerlist.add(csvrecord.get(0));
                headerlist.add(csvrecord.get(1));
                headerlist.add(csvrecord.get(2));
                headerlist.add(csvrecord.get(3));
                headerlist.add(csvrecord.get(4));
                headerlist.add(csvrecord.get(5));
                headerlist.add(csvrecord.get(6));
                headerlist.add(csvrecord.get(7));
                lineno++;
            }            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headerlist;
    }

}
