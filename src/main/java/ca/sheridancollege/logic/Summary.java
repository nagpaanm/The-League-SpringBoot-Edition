package ca.sheridancollege.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Summary {
	
	public static ArrayList<ArrayList<String>> summarize(ArrayList<ArrayList<String>> table, 
			ArrayList<ArrayList<String>> newTable, String token){
			int index = -1;
			ArrayList<String> rows = new ArrayList<String>();
			if(!table.isEmpty()) {
				if(newTable.isEmpty()) {
					/*
					int careerIndex = -1;
					for(int i = 0 ; i < table.size(); i++) {
						if(table.get(i).get(0).equalsIgnoreCase("career")) {
							careerIndex = i;
						}
					}
					*/
					for(int i = 0 ; i < table.size(); i++) {
						//if(i <= careerIndex) {
							//if(!table.get(i).get(2).contains("Did Not Play")) {
								rows.add(table.get(i).get(0));
								rows.add(table.get(i).get(2));
								newTable.add(rows);
								rows = new ArrayList<String>();
							//}
						//}
					}
					
					for(int i = 0; i < newTable.size(); i++) {
						if(newTable.get(i).get(1).contains("Did Not Play")){
							newTable.remove(i);
							i--;
						}
					}
					
				}
				ArrayList<String> row = table.get(0);
				for(int i = 0 ; i < row.size(); i++) {
					if(row.get(i).equalsIgnoreCase(token)) {
						index = i;
					}
				}				
				for(int i = 0; i < table.size(); i++) {
					if(table.get(i).get(2).contains("Did Not Play")){
						table.remove(i);
						i--;
					}
				}
				if(index >= 0) {
					for(int i = 0 ; i < table.size(); i++) {
						if(index < table.get(i).size() && i < newTable.size()) {
							newTable.get(i).add(table.get(i).get(index));
						}
					}
				}
			}
			return newTable;
	}
}
