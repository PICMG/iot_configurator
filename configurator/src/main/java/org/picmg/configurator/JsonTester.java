package org.picmg.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JsonTester {
	
	public static boolean isFormatted;
	
	public static void testLine(String str, Scanner scn) {
		String line = scn.nextLine();
		if(!line.startsWith(str)) {
			isFormatted = false;
			System.out.println("Error at: "+line);
		}else {
			isFormatted = true;
		}
	}
	
	public static void main(String[] args) {
		File json = new File("C:\\Users\\David\\git\\picmg_iot_config\\configurator\\src\\main\\resources\\state_sets\\PICMG_5.json");
		try {
			Scanner scanner = new Scanner(json);
			testLine("{",scanner);
			testLine("	\"name\":",scanner);
			testLine("	\"stateSetId\":",scanner);
			testLine("	\"vendorIANA\":",scanner);
			testLine("	\"vendorName\":",scanner);
			testLine("	\"oemStateValueRecords\":[",scanner);
			
			String line = "		},";
			while(line.startsWith("		},")) {
				testLine("		{",scanner);
				testLine("			\"minStateValue\":",scanner);
				testLine("			\"maxStateValue\":",scanner);
				testLine("			\"languageTags\":[\"en\"],",scanner);
				testLine("			\"stateName\":[\"",scanner);
				line=scanner.nextLine();
			}
			testLine("	]",scanner);
			testLine("}",scanner);
			
			if(isFormatted) {
				System.out.println("Successful test");
			}else {
				System.out.println("Check for errors");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
