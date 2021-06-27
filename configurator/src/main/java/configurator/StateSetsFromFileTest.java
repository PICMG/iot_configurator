package configurator;

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
