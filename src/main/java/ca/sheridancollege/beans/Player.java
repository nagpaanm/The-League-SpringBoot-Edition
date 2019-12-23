package ca.sheridancollege.beans;

import java.util.ArrayList;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Player implements java.io.Serializable {
	
	private static final long serialVersionUID = -4428315655906677914L;
	
	private String name;
	private String first;
	private String last;
	private String url;
	private String source;
	private String youtubeUrl;
	private String height;
	private String weight;
	private String wingspan;
	private ArrayList<String> team;
	private String teamShort;
	private int draftYear;
	//private Map<String, ArrayList<String>> stats;
	private ArrayList<ArrayList<String>> stats;
	private ArrayList<ArrayList<String>> advancedStats;
	private ArrayList<ArrayList<String>> perPossStats;
	private ArrayList<ArrayList<String>> playoffStats;
	private ArrayList<ArrayList<String>> playoffAdvancedStats;
	private ArrayList<ArrayList<String>> playoffPerPossStats;
	private ArrayList<String[]> statsLine;
	private ArrayList<ArrayList<String>> summary;
	
}
