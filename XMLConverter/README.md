# techassessment


To run the application:

Clone/extract the project from Github as a Maven project into any IDE.

Main class - XmlConverter and XmlConverterTest
Files - input-1.xml -> Input file 
	 - output.xml -> Output file generated by the application
	 - output-expected.xml -> Output to test against

I have used Maven for dependency management purpose.

XmlConverterTest has an input filename in the code and makes call to XmlConverter to convert that particular file and stores the new file with OUTPUT_FILENAME. And then the test compares the XMLs - the one generated by XmlConverter and the output file provided.

To run XmlConverter itself it takes the input file name from the command line arguments itself.

I have added few comments in the code for understanding of the functionality. 

I have implemented a rudimentary test framework that makes use of XMLUnit(Test framework for XML) to check if the two XMLs are identical or not. Though we can have it more sophisticated as per the needs.

XMLUnit makes use of SAX parser other than that the application code makes use of DOM parser as it is asked for.
