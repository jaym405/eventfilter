/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaym405.mjeventfilter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProcessJSON implements ProcessFile{
     
    @Override
    public List<ReportBean> getinputfilerecs(String filename){
        List<ReportBean> jsonrecordslist = new ArrayList<ReportBean>();
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(filename)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            Iterator iterJsonArray = jsonArray.iterator();
            System.out.println("JSON File Size: " + jsonArray.size());
            while (iterJsonArray.hasNext()) {
                // Cast row to object
                JSONObject jo = (JSONObject) iterJsonArray.next();
                // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
                String clientaddress = (String) jo.get("client-address");
                String clientguid = (String) jo.get("client-guid");
                long requesttimems = (Long) jo.get("request-time");
                LocalDateTime requesttimedt = LocalDateTime.ofInstant(Instant.ofEpochMilli(requesttimems), ZoneOffset.UTC);
                ZonedDateTime zonedDateTime = ZonedDateTime.of(requesttimedt, ZoneId.of("Atlantic/Bermuda"));
                DateTimeFormatter formt13 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
                String requesttimestr = zonedDateTime.format(formt13);
                String serviceguid = (String) jo.get("service-guid");
                long retriesrequest = (Long) jo.get("retries-request");
                long packetsrequested = (Long) jo.get("packets-requested");
                long packetsserviced = (Long) jo.get("packets-serviced");
                long maxholesize = (Long) jo.get("max-hole-size");

                ReportBean rep = new ReportBean();
                rep.setClientaddress(clientaddress);
                rep.setClientguid(clientguid);
                rep.setRequesttime(requesttimestr);
                rep.setServiceguid(serviceguid);
                rep.setRetriesrequest(Long.toString(retriesrequest));
                rep.setPacketsrequested(Long.toString(packetsrequested));
                rep.setPacketsserviced(Long.toString(packetsserviced));
                rep.setMaxholesize(Long.toString(maxholesize));
                jsonrecordslist.add(rep);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonrecordslist;
     }
}
