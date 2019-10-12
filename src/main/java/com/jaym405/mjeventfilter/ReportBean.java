package com.jaym405.mjeventfilter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class ReportBean {

    // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
    String clientaddress;
    String clientguid;
    LocalDateTime requesttime;
    String serviceguid;
    String retriesrequest;
    String packetsrequested;
    String packetsserviced;
    String maxholesize;

    ReportBean() {
        this.clientguid = null;
    }

    ReportBean(String clientaddress, String clientguid, String requesttime, String serviceguid, String retriesrequest, String packetsrequested, String packetsserviced, String maxholesize) {
        this.clientaddress = clientaddress;
        this.clientguid = clientguid;
    }

    public String getClientaddress() {
        return this.clientaddress;
    }

    public void setClientaddress(String clientaddress) {
        this.clientaddress = clientaddress;
    }

    public String getClientguid() {
        return this.clientguid;
    }

    public void setClientguid(String clientguid) {
        this.clientguid = clientguid;
    }

    public LocalDateTime getRequesttime() {
        return this.requesttime;
    }

    public void setRequesttime(String requesttime) {
        ZonedDateTime.now(ZoneId.of("Atlantic/Bermuda")).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(Locale.CANADA));
        DateTimeFormatter formt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        LocalDateTime requesttimeldt = LocalDateTime.parse(requesttime, formt1);
        this.requesttime = requesttimeldt;
    }

    public String getServiceguid() {
        return this.serviceguid;
    }

    public void setServiceguid(String serviceguid) {
        this.serviceguid = serviceguid;
    }

    public String getRetriesrequest() {
        return this.retriesrequest;
    }

    public void setRetriesrequest(String retriesrequest) {
        this.retriesrequest = retriesrequest;
    }

    public String getPacketsrequested() {
        return this.packetsrequested;
    }

    public void setPacketsrequested(String packetsrequested) {
        this.packetsrequested = packetsrequested;
    }

    public String getPacketsserviced() {
        return this.packetsserviced;
    }

    public void setPacketsserviced(String packetsserviced) {
        this.packetsserviced = packetsserviced;
    }

    public String getMaxholesize() {
        return this.maxholesize;
    }

    public void setMaxholesize(String maxholesize) {
        this.maxholesize = maxholesize;
    }

    @Override
    public String toString() {
        // client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size
        
        ZonedDateTime zonedDateTime = ZonedDateTime.of(this.requesttime, ZoneId.of("Atlantic/Bermuda"));
        DateTimeFormatter formt13 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"); 
        String requesttimestr = zonedDateTime.format(formt13);        
                
        StringBuffer sb = new StringBuffer();
        sb.append(this.clientaddress);
        sb.append(",");
        sb.append(this.clientguid);
        sb.append(",");
        sb.append(requesttimestr);
        sb.append(",");
        sb.append(this.serviceguid);
        sb.append(",");
        sb.append(this.retriesrequest);
        sb.append(",");
        sb.append(this.packetsrequested);
        sb.append(",");
        sb.append(this.packetsserviced);
        sb.append(",");
        sb.append(this.maxholesize);

        return sb.toString();
    }
}
