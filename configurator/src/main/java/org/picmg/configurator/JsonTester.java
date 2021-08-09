//*******************************************************************
//    JsonTester.java
//
//    More information on the PICMG IoT data model can be found within
//    the PICMG family of IoT specifications.  For more information,
//    please visit the PICMG web site (www.picmg.org)
//
//    Copyright (C) 2020,  PICMG
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
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
