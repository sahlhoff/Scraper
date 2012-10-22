import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example program to list links from a URL.
 */
public class htmls {
	
	static String dollarWithoutSymbol = "(\\$?[0-9]*.[0-9][0-9])";
	static Pattern patternDollarWithoutSymbol = Pattern.compile(dollarWithoutSymbol);
	
	static String dollarWithSymbol = "(\\$[0-9]*.[0-9][0-9])";
	static Pattern patternDollarWithSymbol = Pattern.compile(dollarWithSymbol);
	
	static String priceWithNoString = "(.[0-9]*.[0-9][0-9].)";
	static Pattern patternPriceWithNoString = Pattern.compile(priceWithNoString);
	 
	static String containsClosed = "(.*(closed|Closed).*)";
	static Pattern patternContainsClosed = Pattern.compile(containsClosed);
	
	static String searchURL;
	static String stringSearchNumber;
	static String stringPageStartNumber;
	static String businessFile;
	static String menuFile;
	static String checkKeyFile;
	static String businessNoMenuFile;
	static String stringPageStart;
	static String stringPageStop;
	
	
	
	
	
	
    public static void main(String[] args) {
        
    	
    	
    	
		CSVReader setUpReader = null;
		try {
			setUpReader = new CSVReader(new FileReader("/Users/chadsahlhoff/Desktop/Menu/setup.txt"));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    String [] nextLine;
	    try {
			while ((nextLine = setUpReader.readNext()) != null) {
			    // nextLine[] is an array of values from the line
			   searchURL = nextLine[0];
			   stringSearchNumber = nextLine[1];
			   stringPageStartNumber = nextLine[2];
			   businessFile = nextLine[3];
			   menuFile = nextLine[4];
			   checkKeyFile = nextLine[5];
			   businessNoMenuFile = nextLine[6];
			    
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	
    	
       
     
        int counter = Integer.parseInt(stringSearchNumber);
        int start = Integer.parseInt(stringPageStartNumber);

        
        for(int w = start; w <= counter; w ++){
        	
        	String searchUrl = searchURL + w;
        
        
        	
        	
        	Document searchPage = null;
	        
	        System.out.println("try");
			try {
				searchPage = Jsoup.connect(searchUrl).timeout(30*1000).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			Elements restaurantUrls = searchPage.select("ul#restaurants li.row");
			
			
			
			for(Element officialUrl : restaurantUrls){
				
				System.out.println(officialUrl.attr("data-restaurant-id"));
        	
        	
        	
        	
        	String theOfficialUrl = officialUrl.attr("data-restaurant-id");
        	
        	
        	
        	if(containsID(theOfficialUrl)){
				System.out.println("contians ID" + theOfficialUrl);
        		continue;
				
			}
        	else{
        		printKeyFile(theOfficialUrl + "," + w +"\n");
        	}
       

        	try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        	
        String url = "http://www.urbanspoon.com/r/40/" + theOfficialUrl + "/restaurant/";	
        
        String title = "";
        String phone = "";
        String street ="";
        String city = "";
        String state = "";
        String zip = "";
        String website = "";
        
        
        	
        Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(10*1000).get();;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(url);
		
        if (doc.select("h1.page_title").val() == null)
        	continue;
        
        title = doc.select("h1.page_title").text();
        phone = doc.select("h3.phone").text();
        street = doc.select("span.street-address").text();
        city = doc.select("span.locality").text();
        state = doc.select("span.region").text();
        zip = doc.select("a.postal-code").text();
        Elements elementWebsite = doc.select("p.website a");
        website = elementWebsite.attr("abs:href");
        
        
        //remove facebook, other junk, take first url
        String[] arr = website.split("\\s+");
        
        //grab menu
        Elements menu = doc.select("div.menu a[href*=/cities/]");
        
        
        //get size
        int menuSize = menu.size();
       
        //grab the specific url
        String menuUrl = menu.attr("abs:href");
        
        //check if closed
        Matcher h = patternContainsClosed.matcher(title);
        
        
        
        //if checks business is closed or we are at the wrong page
        if(title.length() == 0 || h.matches()){
        	
        	continue;
        }
        
        //else print the business information and update useless counter that i am not using??
        else{
        printBusiness(theOfficialUrl + "\t" + title + "\t" + phone + "\t" + street + "\t" + city + "\t" + state + "\t" + zip + "\t" + website + "\n");
        
        }
        
        
        
        if(menuSize == 0 || h.matches()){
        	System.out.println("broke");
    	   printBusinessWithNoMenu(theOfficialUrl + "\t" + menuUrl);
        	continue;
       }   
       
        else{
       
        Document menuPage = null;
		try {
			menuPage = Jsoup.connect(menuUrl).timeout(10*1000).get();;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
        
      //get all the selections
        Elements sectionsContents = menuPage.select("li.dish_section");
        
        //get number of selections
        int numOfSections = sectionsContents.size();	
        
        
        
        //Element section = menuPage.select("h3.section_title");
       
        
        //loop through all sections and start parsing individual sections
        for(int p = 0; p < numOfSections; p ++){
        	
        	String currentSectionTitle = " ";
        	String currentSectionDescription = " ";
        	
        	
        	
        	
        	Element currentSection = sectionsContents.get(p);
        	//get currentSection's HTML
        	String currentSectionHTML = currentSection.html();
        	
        	
        	//parse the current section
        	Document sectionFragment = Jsoup.parse(currentSectionHTML);
        	currentSectionTitle = sectionFragment.select("h3.section_title").text();
        	currentSectionDescription = sectionFragment.select("h5.description").text();
        	
        	//print title
        	System.out.println(currentSectionTitle);
        	//print description
        	System.out.println(currentSectionDescription);
        	
        	//get rowDish elements && then get number of rowdishes
        	Elements rowDishes = sectionFragment.select("li.row");
        	int numOfRowDishes = rowDishes.size();
        	
        	Elements dishTitles = sectionFragment.select("div.title");
        	
        	
        	
        	
        	for(int j = 0; j < numOfRowDishes; j ++){
        	
        		String currentDishTitle = " ";
            	String currentFoodDescription = " ";
        		String calories = " ";
        		
        		//getcurrentDishTitle
        		currentDishTitle = dishTitles.get(j).text();
        		System.out.println(currentDishTitle);
        		
        		//get descriptionElements
        		Elements foodDescriptions = sectionFragment.select("div.body");
        		currentFoodDescription = foodDescriptions.get(j).text();
        		System.out.println(currentFoodDescription);
        		
        		Elements elementCalories = foodDescriptions.get(j).select("span.calories");
        		
        		calories = elementCalories.text();
        		
        		if(calories.length() != 0){
        			
        			String [] foodDescriptionSplits = currentFoodDescription.split("\\s");
					int calorieLocation = foodDescriptionSplits.length - 1;
					
					String actualCalorie = foodDescriptionSplits[calorieLocation];
					
					List<String> list = new ArrayList<String>(Arrays.asList(foodDescriptionSplits));	
	        		list.remove(actualCalorie);
	        		foodDescriptionSplits = list.toArray(new String[0]);
					
	        		
	        		List<String> list1 = new ArrayList<String>(Arrays.asList(foodDescriptionSplits));
	        		currentFoodDescription = "";
	        		
	        		for (String s : list1){
	        			currentFoodDescription += s + " ";
	        		}
        		}
        		
        		
        		//get price descriptions
        		Elements foodPricesElements = sectionFragment.select("div.price");
        		String foodPrices = foodPricesElements.get(j).text();
        		
        		
        		//check if only one price with sign; if only one price without sign; if price has value; else no price present 
        		
        		Matcher a = patternDollarWithSymbol.matcher(foodPrices);
        		Matcher b = patternDollarWithoutSymbol.matcher(foodPrices);
        		
        		//create string to send with prices
        		String menuPrint = theOfficialUrl + "\t" + currentSectionTitle + "\t" + currentSectionDescription + "\t" + currentDishTitle + "\t" + currentFoodDescription + "\t" + calories; 
        		
        		//one price with sign
        		if(a.matches()){
        			
        			foodPrices = foodPrices.substring(1, foodPrices.length());
        			System.out.println(foodPrices);
        			printMenu(menuPrint + "\t \t" + foodPrices + addCommas(4) + "\n");
        			
        		}
        		
        		//one price without sign
        		else if(b.matches()){
        			
        			printMenu(menuPrint + "\t \t" + foodPrices + addCommas(4) + "\n");
        			
        		
        		}
        		
        		//no price
        		else if (foodPrices.length() == 0){
        			
        			printMenu(menuPrint + "\t \t" + foodPrices + addCommas(4) + "\n");
        			
        		}
        		
        		//price with descriptions
        		else {
        		
        			
        			//get HTML for prices
        		String formatFoodPricesWithDollars = foodPricesElements.get(j).html();
        		
        		//remover dollar signs
        		String formatFoodPrices = formatFoodPricesWithDollars.replaceAll( "\\$|","");
        		
        		//split prices between <HTML> tags
        		String [] formattedFoodPrices = formatFoodPrices.split("\\<.*?\\>");
        			
        		
        		
        		
        		//set dummy to remove first index
        		formattedFoodPrices[0] = "remove";
        		
        		
        		
        		
        		//removes first index
        		List<String> list = new ArrayList<String>(Arrays.asList(formattedFoodPrices));	
        		list.remove("remove");
        		formattedFoodPrices = list.toArray(new String[0]);
        				
        		//breaks apart price + description
        		breakarray(formattedFoodPrices, menuPrint);
        			
        			
        		}

        	}
        }
    }
        

        		
        	}
        }	

       }
        
        

    
    private static void printBusiness(String text){
    	
    	String businessPath = businessFile;
    	
    	try{
    		
    		  FileWriter fstream = new FileWriter(businessPath, true);
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(text);
    		  //Close the output stream
    		  out.close();
    		  }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }
    		  }
    
    
    
    private static void printBusinessWithNoMenu(String text){
    	
    	String businessPath = businessNoMenuFile;
    	
    	try{
    		
    		  FileWriter fstream = new FileWriter(businessPath, true);
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(text);
    		  //Close the output stream
    		  out.close();
    		  }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }
    		  }
    
    
    
    
    private static void printMenu(String text){
    	System.out.println("called");
    	String menuPath = menuFile;
    	
    	try{
    		
    		  FileWriter fstream = new FileWriter(menuPath, true);
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(text);
    		  //Close the output stream
    		  out.close();
    		  }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }
    		  }
    
    
    private static void printKeyFile(String text){
    	System.out.println("called");
    	String keyPath = checkKeyFile;
    	
    	try{
    		
    		  FileWriter fstream = new FileWriter(keyPath, true);
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(text);
    		  //Close the output stream
    		  out.close();
    		  }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }
    	
    	
    	
    }
    
    
    
    private static void breakarray(String [] price, String menu){
		
		int priceLength = price.length;
		int commasNeeded = 0;
		
		if(priceLength <= 5){
		commasNeeded = 5 - priceLength;
		}
		
		String priceItems = "";
		
		for (int n = 0; n < priceLength; n ++){
		
			Matcher o = patternPriceWithNoString.matcher(price[n]);
			
			
			if(o.matches()){
				priceItems +=  "\t \t" + price[n];
			}
			
			else{
				
				String currentPriceDescription = price[n];
				
				String [] priceSplits = currentPriceDescription.split("\\s");
					int actualPriceLocation = priceSplits.length - 1;
					
					String actualPrice = priceSplits[actualPriceLocation];
					
					List<String> list = new ArrayList<String>(Arrays.asList(priceSplits));	
	        		list.remove(actualPrice);
	        		priceSplits = list.toArray(new String[0]);
					
	        		
	        		List<String> list1 = new ArrayList<String>(Arrays.asList(priceSplits));
	        		String priceDescriptionFinal = "";
	        		
	        		for (String s : list1){
	        			priceDescriptionFinal += s + " ";
	        		}
	        		
	        		
					priceItems += "\t" + priceDescriptionFinal + "\t" + actualPrice;
				
			}
			
			
		}
		
		printMenu(menu + priceItems + addCommas(commasNeeded) + "\n");
		
		
	}
    
    
    private static String addCommas(int numNeeded){
		 String comma = "";
	    	for(int e = 0; e < numNeeded; e ++ ){
	    		
	    		comma += "\t \t";
	    	}
	    	
	    	return comma;
	    	
	    	
	    }
    
    
	private static boolean containsID(String id){
		
		
		
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(checkKeyFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String [] nextLine;
	    try {
			while ((nextLine = reader.readNext()) != null) {
			    // nextLine[] is an array of values from the line
			    if (nextLine[0].equals(id)){
			    	
			    	return true;
			    	
			    }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return false;
		
		
		
		
	}
    
    	
    }
    

