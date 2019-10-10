
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
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// Not using currently - please ignore HeaderRec
class HeaderRec {

    // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
    String clientaddress;
    String clientguid;
    String requesttime;
    String serviceguid;
    String retriesrequest;
    String packetsrequested;
    String packetsserviced;
    String maxholesize;

    HeaderRec(String clientaddress, String clientguid) {
        this.clientaddress = clientaddress;
        this.clientguid = clientguid;
    }

    @Override
    public String toString() {
        return serviceguid;
    }
}

public class MainApp {

    public static void main(String[] args) {

        // Read from CSV   
        List<List<String>> headerlist = new ArrayList<>();
        List<List<String>> csvrecordslist = new ArrayList<List<String>>();
        List<List<String>> filteredrecordslist = new ArrayList<List<String>>();

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Copy to another list
        filteredrecordslist = csvrecordslist.stream()
                .collect(Collectors.toList());

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

        filteredrecordslist = filterzeropackets(filteredrecordslist,psposition);
        filteredrecordslist = sortbyrequesttime(filteredrecordslist);

        
        // Read from JSON 
          List<List<String>> jsonrecordslistfinal = processgetJSON();

        // Read from XML
          List<List<String>> xmlrecordslistfinal = processgetXML();
          
         //Combine and sort,filter?
         //List<List<String>> combinedrecordslistfinal = combineCSVJSONXML();
                
        // WRITE TO CSV
        writeoutputtoCSV(headerlist, filteredrecordslist);


    } //main
    
    public static List<List<String>> processgetJSON()
    {
        List<List<String>> jsonrecordslist = new ArrayList<List<String>>();
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("reports.json")) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            Iterator iterJsonArray = jsonArray.iterator();
            List<String> currentrecord = new ArrayList<>();
            while (iterJsonArray.hasNext()) {
                //    System.out.println(iterJsonArray.next());
                // Cast row to object
                JSONObject jo = (JSONObject) iterJsonArray.next();

                String clientaddress = (String) jo.get("client-address");
                String clientguid = (String) jo.get("client-guid");
                long requesttime = (Long) jo.get("request-time");
                String serviceguid = (String) jo.get("service-guid");
                long retriesrequest = (Long) jo.get("retries-request");
                long packetsrequested = (Long) jo.get("packets-requested");
                long packetsserviced = (Long) jo.get("packets-serviced");
                long maxholesize = (Long) jo.get("max-hole-size");

                currentrecord.add(clientaddress);
                currentrecord.add(clientguid);
                currentrecord.add(Long.toString(requesttime));
                currentrecord.add(serviceguid);
                currentrecord.add(Long.toString(retriesrequest));
                currentrecord.add(Long.toString(packetsrequested));
                currentrecord.add(Long.toString(packetsserviced));
                currentrecord.add(Long.toString(maxholesize));

                // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
                //     System.out.println("clientguid"+clientguid); 
            }
            jsonrecordslist.add(currentrecord);
            //    System.out.println("jsonrecordslist"+jsonrecordslist); 
            //  System.out.println(packetsserviced);                
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonrecordslist;
    }

    public static List<List<String>> processgetXML()
    {
        List<List<String>> xmlrecordslist = new ArrayList<List<String>>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File xmlFile = new File("reports.xml");
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                Document doc = builder.parse(xmlFile);

                NodeList recordNodes = doc.getElementsByTagName("records");
                List<String> currentrecordxml = new ArrayList<>();
                for (int i = 0; i < recordNodes.getLength(); i++) {
                    Node recordNode = recordNodes.item(i);
                    if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element recordElement = (Element) recordNode;

                        // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
                        String clientaddress = recordElement.getElementsByTagName("client-address").item(i).getTextContent();
                        String clientguid = recordElement.getElementsByTagName("client-guid").item(i).getTextContent();
                        String requesttime = recordElement.getElementsByTagName("request-time").item(i).getTextContent();
                        String serviceguid = recordElement.getElementsByTagName("service-guid").item(i).getTextContent();
                        String retriesrequest = recordElement.getElementsByTagName("retries-request").item(i).getTextContent();
                        String packetsrequested = recordElement.getElementsByTagName("packets-requested").item(i).getTextContent();
                        String packetsserviced = recordElement.getElementsByTagName("packets-serviced").item(i).getTextContent();
                        String maxholesize = recordElement.getElementsByTagName("max-hole-size").item(i).getTextContent();
                        currentrecordxml.add(clientaddress);
                        currentrecordxml.add(clientguid);
                        currentrecordxml.add(requesttime);
                        currentrecordxml.add(serviceguid);
                        currentrecordxml.add(retriesrequest);
                        currentrecordxml.add(packetsrequested);
                        currentrecordxml.add(packetsserviced);
                        currentrecordxml.add(maxholesize);
                        //System.out.println("Student Id = " + studentId);
                        // System.out.println("Student Name = " + studentName);
                    }
                }
                xmlrecordslist.add(currentrecordxml);
                System.out.println("xmlrecordslist" + xmlrecordslist);

            } catch (SAXException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return xmlrecordslist;
    }
    
    public static void writeoutputtoCSV(List<List<String>> headerlist, List<List<String>> recordslist){
            try {
            FileWriter csvWriter = new FileWriter("combinedreport.csv");

            for (int i = 0; i < headerlist.size(); i++) {
                List<String> headers = headerlist.get(i);
                for (int k = 0; k < headers.size(); k++) {
                    csvWriter.append(headers.get(k));
                    if (k != headers.size() - 1) {
                        csvWriter.append(",");
                    } else {
                        csvWriter.append("\n");
                    }
                }
            }

            //filteredrecordslist
            //  jsonrecordslist
            // xmlrecordslist
            for (List<String> rowData : recordslist) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<List<String>> filterzeropackets(List<List<String>> listtofilter,int psposition){
        // Filter out packets-serviced = 0
        for (int i = 0; i < listtofilter.size(); i++) {
            List<String> record = listtofilter.get(i);
            boolean pszero = false;
            for (int j = 0; j < record.size(); j++) {
                String recordval = record.get(j);
                if (j == psposition && recordval.equals("0")) {
                    pszero = true;
                }
            }
            if (pszero) {
                listtofilter.remove(i);
            }
            
        }
        return listtofilter;
    }
    
       public static List<List<String>> sortbyrequesttime(List<List<String>> listtosort){
           
                   // Bubble sort by request time - for now soritng by packets-serviced, not request time due to datetime error
        List<String> temp1 = new ArrayList<String>();
        for (int i = 0; i < listtosort.size(); i++) {
            for (int j = 1; j < (listtosort.size() - i); j++) {
                List<String> prevval = listtosort.get(j - 1);
                List<String> currval = listtosort.get(j);
                // Thisdoent work so commenting out for now
                /*   DateTimeFormatter formt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ZZZ"); //2016-06-29 07:22:30 ADT
                LocalDateTime dowD1 = LocalDateTime.parse(prevval.get(2).toString(),formt1);
                 System.out.println("Formatted todays date: " + dowD1.format(formt1));*/
                int prevvalps = Integer.parseInt(prevval.get(6));
                int currvalps = Integer.parseInt(currval.get(6));
                if (prevvalps > currvalps) {
                    //swap
                    temp1 = prevval;
                    prevval = currval;
                    currval = temp1;
                    listtosort.set(j - 1, prevval);
                    listtosort.set(j, currval);
                }
            }
        }
           
           
           return listtosort;
       }
 
    
    
    
} //class

