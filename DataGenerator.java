import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataGenerator {

	private static final String WORKING_DIRECTORY 	= System.getProperty("user.dir");
	private static final String OUTPUT_DIRECTORY 	= WORKING_DIRECTORY + "/output";
	
	private int currentIdentifier 		= 0;
	private int numberOfPublications 	= 0;
	private SimpleDateFormat formatter 	= new SimpleDateFormat("dd-MM-yyyy");
		
	private void generatePublications() {
		makeDirectory(OUTPUT_DIRECTORY);
		
		for(int i = 0; i < numberOfPublications; i++) {
			generateIdentifier();
			
			//Make publication directory
			String publicationDirectory = OUTPUT_DIRECTORY + "/publication-" + currentIdentifier;
			makeDirectory(publicationDirectory);
			
			//Generate publication and full text xml files
			generatePublication(publicationDirectory);
			generateFullText(publicationDirectory);
			
			//Make image directory
			String imageDirectory = publicationDirectory + "/images";
			makeDirectory(imageDirectory);
			generateImage(imageDirectory);
		}
	}
	
	private void makeDirectory(String directory) {
		
		try {
			File dir = new File(directory);
			if(dir.exists() && !dir.isDirectory()) {
				Files.delete(Paths.get(directory));
			}
			dir.mkdir();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private int generateIdentifier() {
		currentIdentifier++;
		return currentIdentifier;
	}

	private void generatePublication(String publicationDirectory) {
	
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			
			Document document = docBuilder.newDocument();
			
			//Add publication element
			Element publication = document.createElement("publication");
			document.appendChild(publication);
			
			//Add identifier element
			Element identifier = document.createElement("identifier");
			//Get identifier
			identifier.appendChild(document.createTextNode(Integer.toString(currentIdentifier)));
			publication.appendChild(identifier);
			
			//Add authors element
			Element authors = document.createElement("authors");
			//Get authors
			int numberOfAuthors = getRandomNumber(1,5);
			for (int i = 0; i < numberOfAuthors; i++) {
				Element author = document.createElement("author");
				
				//Add author name
				Element authorName = document.createElement("authorname");
				String name = getRandomText(getRandomNumber(1,30));
				authorName.appendChild(document.createTextNode(name));
				author.appendChild(authorName);
				
				//Add dob
				Element dob = document.createElement("dob");
				String date = formatter.format(getRandomDate());
				dob.appendChild(document.createTextNode(date));
				author.appendChild(dob);
				
				authors.appendChild(author);
			}
			publication.appendChild(authors);
			
			//Add title element
			Element title = document.createElement("title");
			Element shortTitle = document.createElement("shorttitle");
			title.appendChild(shortTitle);
			Element longTitle = document.createElement("longtitle");
			title.appendChild(longTitle);
			
			//Get random long title
			String longTitleStr = getRandomText(getRandomNumber(10, 100));
			longTitle.appendChild(document.createTextNode(longTitleStr));
			
			//Get short title from substring of long title
			int shortTitleLength = getRandomNumber(1, 10);
			String shortTitleStr = longTitleStr.substring(0, shortTitleLength);
			shortTitle.appendChild(document.createTextNode(shortTitleStr));
			publication.appendChild(title);
			
			//Add subjects element
			Element subjects = document.createElement("subjects");
			//Get subjects
			int numberOfSubjects = getRandomNumber(1,10);
			for (int i = 0; i < numberOfSubjects; i++) {
				//Get random subject name
				Element subject = document.createElement("subject");
				subject.appendChild(document.createTextNode(getRandomText(getRandomNumber(1, 15))));
				
				subjects.appendChild(subject);
			}
			publication.appendChild(subjects);
			
			//Write content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);

			StreamResult result =  new StreamResult(new File(publicationDirectory + "/publication.xml"));
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void generateFullText(String publicationDirectory) {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			
			Document document = docBuilder.newDocument();
			
			//Add pages element
			Element pages = document.createElement("pages");
			document.appendChild(pages);
			
			//Add pages
			int numberOfPages = getRandomNumber(1,1000);
			//Get starting page number
			int pageNumber = getRandomNumber(1, numberOfPages);
			for (int i = 0; i < numberOfPages; i++) {
				Element page = document.createElement("page");
				
				//Add page number
				page.setAttribute("number", Integer.toString(pageNumber));
				pageNumber++;
				
				//Add sequence number
				page.setAttribute("sequence", Integer.toString(i + 1));
				
				//Add text element
				Element text = document.createElement("text");
				
				//Add word elements
				int numberOfWords = getRandomNumber(100, 200);
				for (int j = 0; j < numberOfWords; j++) {
					Element word = document.createElement("word");
					
					//Add random coordinates
					int coord1 = getRandomNumber(1,300);
					int coord2 = getRandomNumber(1,300);
					int coord3 = getRandomNumber(1,300);
					int coord4 = getRandomNumber(1,300);
					String coords = coord1 + "," + coord2 + "," + coord3 + "," + coord4;
					word.setAttribute("coords", coords);
					
					//Add random word
					String wordStr = getRandomText(getRandomNumber(1,15));
					word.appendChild(document.createTextNode(wordStr));
					text.appendChild(word);
				}
				
				page.appendChild(text);
				pages.appendChild(page);
			}
			
			//Write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);

			StreamResult result =  new StreamResult(new File(publicationDirectory + "/fulltext.xml"));
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void generateImage(String imageDirectory) {

		try {
			File image = new File(imageDirectory + "/image.tif");
			image.createNewFile();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private String getRandomText(int length) {
		StringBuilder randomText = new StringBuilder();
		for(int i = 0; i < length; i++) {
			randomText.append(getRandomChar());
		}
		return randomText.toString();
	}
	
	private char getRandomChar() {
		int number = getRandomNumber(32,126); //ASCII code range
		char randomChar = (char) number;
		return randomChar;
	}
	
	private int getRandomNumber(int min, int max) {  
		int randomNumber = (int) (Math.random() * (max + 1 - min)) + min;
		return randomNumber;
	}
	
	private Date getRandomDate() {
		
	    try {
			int day 	= getRandomNumber(1,31);
			int month 	= getRandomNumber(1,12);
			int year 	= getRandomNumber(1000,2000);
			Date date 	= formatter.parse(day + "-" + month + "-" + year);
			
		    return date;
		    
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return null;
		} 
	    
	}
	
	private void getUserInput() {
	    Scanner scanner = new Scanner(System.in);
	    int input;
	    
	    do {
		    System.out.println("Enter number of publications to generate: ");
		    while (!scanner.hasNextInt()) {
		    	System.out.println("Enter a positive number: ");
		        scanner.next();
		    }
		    input = scanner.nextInt();
	    } while(input < 0);
	    
	    numberOfPublications = input;
	    scanner.close();
	}
	
	public static void main(String[] args) {
		DataGenerator dg = new DataGenerator();
		dg.getUserInput();
		dg.generatePublications();
		System.out.println("Done");
	}
	
}