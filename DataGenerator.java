import java.io.File;
import java.io.IOException;
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

public class DataGenerator 
{
	private final int numberToGenerate;
	private final String outputDir;
	private int currentID;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	private DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	public DataGenerator (int numberToGenerate, String outputDir)
	{
		this.numberToGenerate = numberToGenerate;
		this.outputDir = outputDir;
	}
	
	public void generate ()
	{
		for (int i = 0; i < numberToGenerate; i++) 
		{
			generateID();
			generatePublicationMetaData();
			generatePublicationFullText();
			generatePublicationImage();
		}
	}
	
	private void generateID ()
	{
		currentID++;
	}
	
	private void generatePublicationMetaData ()
	{
		try 
		{
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			addPublication(document);
			File publicationDir = new File(outputDir + File.separator + "publication-" + currentID);
			publicationDir.mkdirs();
			File outputFile = new File(publicationDir.getAbsolutePath() + File.separator + "publication.xml");
			writeDocumentToFile(document, outputFile);
		} 
		catch (ParserConfigurationException e) 
		{
			System.err.println("ERROR: could not generate publication metadata: " + e.getMessage());
		}
	}
	
	private void addPublication (Document document)
	{
		Element publication = document.createElement("publication");
		document.appendChild(publication);
		addIdentifier(document, publication);
		addAuthors(document, publication);
		addTitle(document, publication);
		addSubjects(document, publication);
	}
	
	private void addIdentifier (Document document, Element publication)
	{
		Element identifier = document.createElement("identifier");
		identifier.appendChild(document.createTextNode(Integer.toString(currentID)));
		publication.appendChild(identifier);
	}
	
	private void addAuthors (Document document, Element publication)
	{
		Element authors = document.createElement("authors");
		int numberOfAuthors = getRandomNumber(1, 5);
		for (int i = 0; i < numberOfAuthors; i++) 
		{
			Element author = document.createElement("author");
			addAuthorName(document, author);
			addAuthorDOB(document, author);
			authors.appendChild(author);
		}
		publication.appendChild(authors);
	}
	
	private void addAuthorName (Document document, Element author)
	{
		Element authorName = document.createElement("authorname");
		String name = getRandomText(getRandomNumber(1, 30));
		authorName.appendChild(document.createTextNode(name));
		author.appendChild(authorName);
	}
	
	private void addAuthorDOB (Document document, Element author)
	{
		Element dob = document.createElement("dob");
		String date = dateFormatter.format(getRandomDate());
		dob.appendChild(document.createTextNode(date));
		author.appendChild(dob);
	}
	
	private void addTitle (Document document, Element publication)
	{
		Element title = document.createElement("title");
		addLongTitle(document, title);
		addShortTitle(document, title);
		publication.appendChild(title);
	}
	
	private void addLongTitle (Document document, Element title)
	{
		Element longTitle = document.createElement("longtitle");
		String longTitleStr = getRandomText(getRandomNumber(10, 100));
		longTitle.appendChild(document.createTextNode(longTitleStr));
		title.appendChild(longTitle);
	}
	
	private void addShortTitle (Document document, Element title)
	{
		Element shortTitle = document.createElement("shorttitle");
		String shortTitleStr = getRandomText(getRandomNumber(10, 10));
		shortTitle.appendChild(document.createTextNode(shortTitleStr));
		title.appendChild(shortTitle);
	}
	
	private void addSubjects (Document document, Element publication)
	{
		Element subjects = document.createElement("subjects");
		int numberOfSubjects = getRandomNumber(1, 10);
		for (int i = 0; i < numberOfSubjects; i++) 
			addSubject(document, subjects);
		publication.appendChild(subjects);
	}
	
	private void addSubject (Document document, Element subjects)
	{
		Element subject = document.createElement("subject");
		subject.appendChild(document.createTextNode(getRandomText(getRandomNumber(1, 15))));	
		subjects.appendChild(subject);
	}
	
	private void writeDocumentToFile (Document document, File outputFile)
	{
		try 
		{
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result =  new StreamResult(outputFile);
			transformer.transform(source, result);
		} 
		catch (TransformerException e) 
		{	
			System.err.println("ERROR: could not write document to file: " + e.getMessage());
		}
	}
	
	private void generatePublicationFullText ()
	{
		try 
		{
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			addPages(document);
			File publicationDir = new File(outputDir + File.separator + "publication-" + currentID);
			publicationDir.mkdirs();
			File outputFile = new File(publicationDir.getAbsolutePath() + File.separator + "fulltext.xml");
			writeDocumentToFile(document, outputFile);
		} 
		catch (ParserConfigurationException e) 
		{
			System.err.println("ERROR: could not generate publication full text: " + e.getMessage());
		}
	}
	
	private void addPages (Document document)
	{
		Element pages = document.createElement("pages");
		document.appendChild(pages);
		int numberOfPages = getRandomNumber(1, 1000);
		int pageNumber = getRandomNumber(1, numberOfPages);
		for (int i = 0; i < numberOfPages; i++) 
		{
			addPage(document, pages, pageNumber, i + 1);
			pageNumber++;
		}
	}
	
	private void addPage (Document document, Element pages, int pageNumber, int sequenceNumber)
	{
		Element page = document.createElement("page");
		page.setAttribute("number", Integer.toString(pageNumber));
		page.setAttribute("sequence", Integer.toString(sequenceNumber));
		addText(document, page);
		pages.appendChild(page);
	}
	
	private void addText (Document document, Element page)
	{
		Element text = document.createElement("text");
		int numberOfWords = getRandomNumber(100, 200);
		for (int j = 0; j < numberOfWords; j++) 
			addWord(document, text);
		page.appendChild(text);
	}
	
	private void addWord (Document document, Element text)
	{
		Element word = document.createElement("word");
		word.setAttribute("coords", getRandomCoordinates());
		String wordStr = getRandomText(getRandomNumber(1,15));
		word.appendChild(document.createTextNode(wordStr));
		text.appendChild(word);
	}
	
	private String getRandomCoordinates ()
	{
		int coord1 = getRandomNumber(1,300);
		int coord2 = getRandomNumber(1,300);
		int coord3 = getRandomNumber(1,300);
		int coord4 = getRandomNumber(1,300);
		return coord1 + "," + coord2 + "," + coord3 + "," + coord4;
	}
	
	private void generatePublicationImage ()
	{
		try 
		{
			File imageDir = new File(outputDir + File.separator + "publication-" + currentID + File.separator + "images");
			imageDir.mkdirs();
			File image = new File(imageDir.getAbsolutePath() + File.separator + currentID + ".tif");
			image.createNewFile();
		} 
		catch (IOException e) 
		{
			System.err.println("ERROR: could not generate publication image: " + e.getMessage());
		}
	}
	
	private String getRandomText (int length) 
	{
		StringBuilder randomText = new StringBuilder();
		for(int i = 0; i < length; i++)
			randomText.append(getRandomChar());
		return randomText.toString();
	}
	
	private char getRandomChar () 
	{
		int number = getRandomNumber(33, 126);
		char randomChar = (char) number;
		return randomChar;
	}
	
	private int getRandomNumber (int min, int max) 
	{  
		int randomNumber = (int) (Math.random() * (max + 1 - min)) + min;
		return randomNumber;
	}
	
	private Date getRandomDate () 
	{
	    try 
	    {
			int day = getRandomNumber(1,31);
			int month = getRandomNumber(1,12);
			int year = getRandomNumber(1000,2000);
			Date date = dateFormatter.parse(day + "-" + month + "-" + year);
		    return date;
		} 
	    catch (ParseException e) 
	    {
			System.err.println("ERROR: could not create random date: " + e.getMessage());
			return null;
		} 
	}
	
	private static int getNumberToGenerate (Scanner scanner)
	{
	    int numberToGenerate;
	    do 
	    {
		    System.out.println("Enter a positive number of publications to generate: ");
		    while (!scanner.hasNextInt()) 
		    {
		    	System.out.println("Not a number");
		        scanner.next();
		    }
		    numberToGenerate = scanner.nextInt();
	    } while (numberToGenerate < 0);
		return numberToGenerate;
	}
	
	private static String getOutputDir (Scanner scanner)
	{
		String outputDir = null;
		do
		{
		    System.out.println("Enter path to output directory: ");
		    outputDir = scanner.next();
		} while (!isValidDir(outputDir));
		return outputDir;
	}
	
	private static boolean isValidDir (String dir)
	{
		File file = new File(dir);
		return file.isDirectory();
	}
	
	public static void main (String[] args) 
	{
		Scanner scanner = new Scanner(System.in);
		int numberToGenerate = getNumberToGenerate(scanner);
		String outputDir = getOutputDir(scanner);
		scanner.close();
		DataGenerator dataGenerator = new DataGenerator(numberToGenerate, outputDir);
		dataGenerator.generate();
		System.out.println("Done!");
	}
}
