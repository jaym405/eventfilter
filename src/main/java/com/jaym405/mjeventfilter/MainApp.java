
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
            if (lineno==0) 
                headerlist.add(Arrays.asList(values));          
            else
                csvrecordslist.add(Arrays.asList(values));
            lineno++;      
        }
    } catch (IOException e) {
                e.printStackTrace();
            }    
    
    // Copy to another list
    filteredrecordslist = csvrecordslist.stream()
    .collect(Collectors.toList());    
    
        int psposition = 0;
        for (int i=0;i<headerlist.size();i++){
            List<String> headers = headerlist.get(i);
            System.out.println("headers:"+headers);
             for (int k=0;k<headers.size();k++){
                    if ("packets-serviced".equals(headers.get(k))){
                        psposition = k;
                    }
                 }
            }       

         // Filter out packets-serviced = 0
         for (int i=0;i<filteredrecordslist.size();i++){
             List<String> record = filteredrecordslist.get(i);                
             boolean pszero = false;
              for (int j=0;j<record.size();j++){
                  String recordval = record.get(j);
                  if (j==psposition && recordval.equals("0")){                     
                     pszero = true;
                  }               
              }
              if (pszero) {
                  filteredrecordslist.remove(i);
              }
         }
         
         // Bubble sort by request time - for now soritng by packets-serviced, not request time due to datetime error
          List<String> temp1 = new ArrayList<String>();
          for (int i=0;i<filteredrecordslist.size();i++){
             for(int j=1; j < (filteredrecordslist.size()-i); j++){
                 List<String> prevval = filteredrecordslist.get(j-1);
                 List<String> currval = filteredrecordslist.get(j);                 
                // Thisdoent work so commenting out for now
              /*   DateTimeFormatter formt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ZZZ"); //2016-06-29 07:22:30 ADT
                LocalDateTime dowD1 = LocalDateTime.parse(prevval.get(2).toString(),formt1);
                 System.out.println("Formatted todays date: " + dowD1.format(formt1));*/
                 
                 int prevvalps = Integer.parseInt(prevval.get(6));
                 int currvalps = Integer.parseInt(currval.get(6));                
                 if(prevvalps > currvalps){  
                      //swap
                      temp1 = prevval;
                      prevval = currval;
                      currval = temp1;
                      filteredrecordslist.set(j-1, prevval);
                      filteredrecordslist.set(j, currval);                      
                  }                 
             }            
         }
         
       
    // Read from JSON - start
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("reports.json")) {   
            JSONArray jsonArray = (JSONArray) parser.parse(reader);  
            Iterator iterJsonArray = jsonArray.iterator(); 
          
            while (iterJsonArray.hasNext()) {
            //    System.out.println(iterJsonArray.next());
                // Cast row to object
                JSONObject jo = (JSONObject)iterJsonArray.next(); 
                long maxholesize = (Long) jo.get("max-hole-size"); 
                long packetsserviced = (Long) jo.get("packets-serviced"); 
          
              //  System.out.println(maxholesize); 
              //  System.out.println(packetsserviced);                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }    
        // Read from JSON - end

// Read from XML

DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
try{
File xmlFile = new File("reports.xml");
DocumentBuilder builder = factory.newDocumentBuilder();
try{
Document doc = builder.parse(xmlFile);

 NodeList studentNodes = doc.getElementsByTagName("records");
    for(int i=0; i<studentNodes.getLength(); i++)
    {
        Node studentNode = studentNodes.item(i);
        if(studentNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element studentElement = (Element) studentNode;
            String studentId = studentElement.getElementsByTagName("packets-serviced").item(0).getTextContent();
            String studentName = studentElement.getElementsByTagName("client-guid").item(0).getTextContent();
            //System.out.println("Student Id = " + studentId);
           // System.out.println("Student Name = " + studentName);
        }
    }

} catch(SAXException e){e.printStackTrace();}
}catch(ParserConfigurationException|IOException e){e.printStackTrace();}
    
   
    

    // WRITE TO CSV

    try {
        FileWriter csvWriter = new FileWriter("combinedreport.csv");
        
        for (int i=0;i<headerlist.size();i++){
            List<String> headers = headerlist.get(i);
             for (int k=0;k<headers.size();k++){                    
                            csvWriter.append(headers.get(k));
                            if (k!=headers.size()-1) {csvWriter.append(",");}
                            else{
                            csvWriter.append("\n");
                            }                            
                 }
            }
        

        for (List<String> rowData : filteredrecordslist) {
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }

  } //main
} //class



