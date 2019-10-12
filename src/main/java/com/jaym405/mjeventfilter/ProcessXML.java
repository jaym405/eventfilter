package com.jaym405.mjeventfilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProcessXML implements ProcessFile {

    public List<ReportBean> getinputfilerecs(String filename) {
        List<ReportBean> xmlrecordslist = new ArrayList<ReportBean>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File xmlFile = new File(filename);
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                NodeList recordNodes = doc.getElementsByTagName("report");
                System.out.println("XML File size: " + recordNodes.getLength());

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

                        ReportBean rep = new ReportBean();
                        rep.setClientaddress(clientaddress);
                        rep.setClientguid(clientguid);
                        rep.setRequesttime(requesttime);
                        rep.setServiceguid(serviceguid);
                        rep.setRetriesrequest(retriesrequest);
                        rep.setPacketsrequested(packetsrequested);
                        rep.setPacketsserviced(packetsserviced);
                        rep.setMaxholesize(maxholesize);
                        xmlrecordslist.add(rep);
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
}
