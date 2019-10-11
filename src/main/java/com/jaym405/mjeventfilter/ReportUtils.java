package com.jaym405.mjeventfilter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReportUtils {
    
    // Could improve by using interface for processing XML and JSON, possibly CSV as well.
    public List<List<String>> processgetXML()
    {
        List<List<String>> xmlrecordslist = new ArrayList<List<String>>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File xmlFile = new File("reports.xml");
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();
                
                NodeList recordNodes = doc.getElementsByTagName("report");               
                System.out.println("XML File size: "+recordNodes.getLength());
                for (int i = 0; i < recordNodes.getLength(); i++) {
                    Node recordNode = recordNodes.item(i);
                    if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element recordElement = (Element) recordNode;           
                        // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
                        String clientaddress = recordElement.getElementsByTagName("client-address").item(0).getTextContent();
                        String clientguid = recordElement.getElementsByTagName("client-guid").item(0).getTextContent();
                        String requesttime = recordElement.getElementsByTagName("request-time").item(0).getTextContent();
                        String serviceguid = recordElement.getElementsByTagName("service-guid").item(0).getTextContent();
                        String retriesrequest = recordElement.getElementsByTagName("retries-request").item(0).getTextContent();
                        String packetsrequested = recordElement.getElementsByTagName("packets-requested").item(0).getTextContent();
                        String packetsserviced = recordElement.getElementsByTagName("packets-serviced").item(0).getTextContent();
                        String maxholesize = recordElement.getElementsByTagName("max-hole-size").item(0).getTextContent();
                        List<String> currentrecordxml = new ArrayList<>();
                        currentrecordxml.add(clientaddress);
                        currentrecordxml.add(clientguid);
                        currentrecordxml.add(requesttime);
                        currentrecordxml.add(serviceguid);
                        currentrecordxml.add(retriesrequest);
                        currentrecordxml.add(packetsrequested);
                        currentrecordxml.add(packetsserviced);
                        currentrecordxml.add(maxholesize);
                        xmlrecordslist.add(currentrecordxml);
                    }                    
                }              
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return xmlrecordslist;
    }
    
    
    public List<List<String>> processgetJSON()
    {
        List<List<String>> jsonrecordslist = new ArrayList<List<String>>();
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("reports.json")) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            Iterator iterJsonArray = jsonArray.iterator();
            System.out.println("JSON File Size: "+jsonArray.size());
            while (iterJsonArray.hasNext()) {      
                // Cast row to object
                JSONObject jo = (JSONObject) iterJsonArray.next();

                // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
                String clientaddress = (String) jo.get("client-address");
                String clientguid = (String) jo.get("client-guid");
                long requesttimems = (Long) jo.get("request-time");                  
                
                LocalDateTime requesttimedt = LocalDateTime.ofInstant(Instant.ofEpochMilli(requesttimems),ZoneOffset.UTC);
                ZonedDateTime zonedDateTime = ZonedDateTime.of(requesttimedt, ZoneId.of("Atlantic/Bermuda"));
                DateTimeFormatter formt13 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"); 
                String requesttimestr = zonedDateTime.format(formt13);                
                
                String serviceguid = (String) jo.get("service-guid");
                long retriesrequest = (Long) jo.get("retries-request");
                long packetsrequested = (Long) jo.get("packets-requested");
                long packetsserviced = (Long) jo.get("packets-serviced");
                long maxholesize = (Long) jo.get("max-hole-size");
                List<String> currentrecord = new ArrayList<>();
                currentrecord.add(clientaddress);
                currentrecord.add(clientguid);
                currentrecord.add(requesttimestr);
                currentrecord.add(serviceguid);
                currentrecord.add(Long.toString(retriesrequest));
                currentrecord.add(Long.toString(packetsrequested));
                currentrecord.add(Long.toString(packetsserviced));
                currentrecord.add(Long.toString(maxholesize));
                jsonrecordslist.add(currentrecord);                  
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonrecordslist;
    }
    
      public void writeoutputtoCSV(List<List<String>> headerlist, List<List<String>> recordslist){
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
            for (List<String> rowData : recordslist) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(ReportUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<List<String>> filterzeropackets(List<List<String>> listtofilter,int psposition){
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
    
       public List<List<String>> sortbyrequesttime(List<List<String>> listtosort){
           
        // Bubble sort by request time - this is the bottleneck, can be improved
        List<String> temp1 = new ArrayList<String>();
        for (int i = 0; i < listtosort.size(); i++) {
            for (int j = 1; j < (listtosort.size() - i); j++) {
                List<String> prevval = listtosort.get(j - 1);
                List<String> currval = listtosort.get(j);
                // Set zone 
                ZonedDateTime.now(ZoneId.of( "America/Anchorage")).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(Locale.CANADA));                
                DateTimeFormatter formt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"); //2016-06-29 07:22:30 ADT
                LocalDateTime prevdate = LocalDateTime.parse(prevval.get(2).toString(),formt1);
                LocalDateTime currdate = LocalDateTime.parse(currval.get(2).toString(),formt1);
                if (prevdate.compareTo(currdate)>0) {
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
    
    
}
