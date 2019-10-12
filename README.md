
how to run the program:

Please run the program from src/main/java/com/jaym405/mjeventfilter/MainApp.java. The output will be generated in the main project folder as combinedreport.csv.

why you chose the tools/libraries used:

OpenCSV used for reading CSV because it provides a good way of working with CSV.
OpenCSV can be used for generating output CSV as well, but since it is a simple use case I've used FileWriter.
json-simple used for JSON parsing, it's a simple library to use.

Other comments:
- Original working version committed Oct 11 and did not use POJO, used slow bubble sort instead of streams.
- Improved version is now:
- Using POJO (ProcessFile) instead of ArrayList<String> - this enables stream filter and sort in a better way.
- Faster (since not using bubble sort and ArrayList<String>)
- Abstraction using ProcessFile
- POJO allowed easy grouping using streams


# Data sorting and filtering

Read the 3 input files reports.json, reports.csv, reports.xml and output a combined CSV file with the following characteristics:

- The same column order and formatting as reports.csv
- All report records with packets-serviced equal to zero should be excluded
- records should be sorted by request-time in ascending order

Additionally, the application should print a summary showing the number of records in the output file associated with each service-guid.

Please provide source, documentation on how to run the program and an explanation on why you chose the tools/libraries used.

## Submission

You may fork this repo, commit your work and let us know of your project's location, or you may email us your project files in a zip file.
