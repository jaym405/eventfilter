
package com.jaym405.mjeventfilter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface ProcessFile {
    public List<ReportBean> getinputfilerecs(String filename);    
    
    default void writeoutputfile(List<String> headerlist, List<ReportBean> recordslist, String filename){
         try {
            FileWriter csvWriter = new FileWriter(filename);           
            // Headers
            for (int i = 0; i < headerlist.size(); i++) {
                csvWriter.append(headerlist.get(i).toString());
                if (i != headerlist.size() - 1) {
                        csvWriter.append(",");
                    } else {
                        csvWriter.append("\n");
                    }      
            }            
            // Records
            for (int i = 0; i < recordslist.size(); i++) {
                ReportBean lines = recordslist.get(i);
                csvWriter.append(lines.toString());
                if (i!=recordslist.size()-1)
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    default List<String> getinputfilehdrs(String filename){
        // Default implementation empty since CSV is the only one used
        List<String> headerlist = new ArrayList<>();
        return headerlist;
    }
}
