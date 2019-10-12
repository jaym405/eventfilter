package com.jaym405.mjeventfilter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReportUtils {

    public List<ReportBean> filterAndSort(List<ReportBean> beanlist) {

        // Filter out records that have packetsserviced = 0
        List<ReportBean> filteredlist
                = beanlist
                        .stream()
                        .filter(p -> !p.getPacketsserviced().equals("0"))
                        .collect(Collectors.toList());

        // Sort by request-time ascending
        List<ReportBean> filteredsortedlist = filteredlist.stream()
                .sorted((o1, o2) -> o1.getRequesttime().compareTo(o2.getRequesttime()))
                .collect(Collectors.toList());

        return filteredsortedlist;
    }

    public void outputserviceguidcounts(List<ReportBean> beanlist) {
        final Map<String, List<ReportBean>> svcguidgroupedmap
                = beanlist.stream()
                        .collect(
                                Collectors.groupingBy(s -> s.getServiceguid())
                        );
        for (final Map.Entry<String, List<ReportBean>> entry : svcguidgroupedmap.entrySet()) {
            System.out.println("Number of records with service-guid " + entry.getKey() + " is " + entry.getValue().size());
        }

    }

}
