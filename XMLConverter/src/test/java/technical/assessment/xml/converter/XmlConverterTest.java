package technical.assessment.xml.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import technical.assessment.xml.converter.XmlConverter;
public class XmlConverterTest extends TestCase
{
	public static final Logger LOGGER = LoggerFactory.getLogger(XmlConverterTest.class);

    private static final String OUTPUT_FILENAME = "output.xml";
	private static final String OUTPUT_EXPECTED_XML = "output-expected.xml";
	private static final String INPUT_FILENAME = "input-1.xml";

	public void testApp()
    {
		try {
			//Reads from input-1.xml and then applies the transformation.
			//Though these values are here part of the code but we can 
			//externalize them and pass it from configuration properties.
			XmlConverter temp = new XmlConverter();
	    	temp.writeXMLFile(temp.readXMLFromFile(new File(INPUT_FILENAME)));
	    	
			//Read both the xml files
	    	Scanner scannerExpected = new Scanner( new File(OUTPUT_EXPECTED_XML), "UTF-8" );
	    	String expectedOutput = scannerExpected.useDelimiter("\\A").next();
	    	scannerExpected.close(); 

			Scanner scannerActual = new Scanner( new File(OUTPUT_FILENAME), "UTF-8" );
			String actualOutput = scannerActual.useDelimiter("\\A").next();
	    	scannerActual.close(); 

	        XMLUnit.setIgnoreWhitespace(true); // ignore whitespace differences
	        //XMLUnit.setIgnoreAttributeOrder(true);
	        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(actualOutput, expectedOutput));
	        List<?> allDifferences = diff.getAllDifferences();
	        Assert.assertEquals("Differences found: "+ diff.toString(), 0, allDifferences.size());

		} catch (FileNotFoundException e) {
			LOGGER.debug("File not found: " + e.getMessage());
		} catch (SAXException e) {
			LOGGER.debug("File not found: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.debug("File not found: " + e.getMessage());
		}
    }
}
