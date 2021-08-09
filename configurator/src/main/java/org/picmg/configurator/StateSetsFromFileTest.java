//*******************************************************************
//    StateSetsFromFileTest.java
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
import java.util.ArrayList;
import java.util.Scanner;

public class StateSetsFromFileTest {
	
	private static String returnLine(String str, Scanner scn) {
		String line = scn.nextLine();
		return (String) line.subSequence(str.length(), line.length()-2);
	}
	
	public static void main(String[] args) {
		
		File stateSetFolder = new File("C:\\\\Users\\\\David\\\\git\\\\picmg_iot_config\\\\configurator\\\\src\\\\main\\\\resources\\\\state_sets");
        File[] stateSets = stateSetFolder.listFiles();
        
        ArrayList<String> vendor = new ArrayList<String>();
        ArrayList<String> name   = new ArrayList<String>();
        
        // for each file, read in values for name and vendor and store them in their respective arrayLists
        for(File f: stateSets){
        	try {
				Scanner lineReader = new Scanner(f);
				lineReader.nextLine();
				name.add(returnLine("	\"name\":\"",lineReader));
				lineReader.nextLine();
				lineReader.nextLine();
				vendor.add(returnLine("	\"vendorName\":\"",lineReader));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        //print out resultant arraylists
        for(int i =0; i<vendor.size(); i++) {
        	System.out.println(vendor.get(i)+" "+name.get(i));
        }
        
	}

}
