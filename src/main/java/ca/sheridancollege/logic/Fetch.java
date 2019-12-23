package ca.sheridancollege.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ca.sheridancollege.beans.Player;

public class Fetch {
	
	/*
	public String findPlayer(String name) throws IOException{
		String url = "https://www.basketball-reference.com/players/";
        String searchURL = url + name.charAt(0) + "/"
        Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
	}
	*/
	public List<String> getNames(String name){
		List<String> names = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(name);
		String part = st.nextToken();
		names.add(part);
		while(st.hasMoreTokens()) {
			part = st.nextToken();
			names.add(part);
		}
		return names;
	}
	
	public Player findPlayer(String firstName, String lastName) throws IOException{
		Player player = new Player();
		String first = removeQuotation(firstName.toLowerCase()); //url is case sensitive
		String last = removeQuotation(lastName.toLowerCase()); //url is case sensitive
		String searchUrl = "https://www.basketball-reference.com/players/";
		String lastQuery;
		int queryCounter = 1;
		if(last.length() < 5) {
			lastQuery = last.substring(0, last.length());
		}
		else {
			lastQuery = last.substring(0, 5);
		}
		String query = last.charAt(0) + "/" + lastQuery + first.substring(0, 2) +
				"0"+Integer.toString(queryCounter)+".html";
		Document doc;
		try {
			doc = Jsoup.connect(searchUrl + query).userAgent("Mozilla/5.0").get();
			String pName = getPhysicalAttributes(doc, "h1", "itemprop", "name").toLowerCase();
			while(!pName.split(" ")[0].equals(first) || !pName.split(" ")[1].equals(last)) {
				queryCounter++;
				query = last.charAt(0) + "/" + lastQuery + first.substring(0, 2) +
						"0"+Integer.toString(queryCounter)+".html";
				doc = Jsoup.connect(searchUrl + query).userAgent("Mozilla/5.0").get();
				pName = getPhysicalAttributes(doc, "h1", "itemprop", "name").toLowerCase();
				
			}
		}catch(Exception e) {
	        doc = searchGoogle(first, last);
		}
		player.setYoutubeUrl(getYoutubeUrl(first, last));
		//System.out.println(player.getYoutubeUrl());
		player.setStatsLine(retrieveStatLine("stats_pullout", doc));
		player.setStats(retrieve("per_game", doc));
		player.setAdvancedStats(retrieve("all_advanced", "advanced", doc));
		player.setPerPossStats(retrieve("all_per_poss", "per_poss", doc));
		player.setPlayoffStats(retrieve("all_playoffs_per_game", "playoffs_per_game", doc));
		player.setPlayoffAdvancedStats(retrieve("all_playoffs_advanced", "playoffs_advanced", doc));
		player.setPlayoffPerPossStats(retrieve("all_playoffs_per_poss", "playoffs_per_poss", doc));
		player.setFirst(firstName);
		player.setLast(lastName);
		player.setName(firstName + " " + lastName);
		player.setUrl(searchUrl + query);
	    player.setHeight(getPhysicalAttributes(doc, "span", "itemprop", "height"));
	    player.setWeight(getPhysicalAttributes(doc, "span", "itemprop", "weight"));
	    player.setDraftYear(getDraftYear(doc));
	    player.setTeam(getTeamAndRecord(doc));
	    player.setSource(getSource(player.getName()));
	    ArrayList<ArrayList<String>> 
	    	summary = Summary.summarize(player.getStats(), new ArrayList<ArrayList<String>>(), "pts");
	    summary = Summary.summarize(player.getStats(), summary, "trb");
	    summary = Summary.summarize(player.getStats(), summary, "ast");
	    summary = Summary.summarize(player.getStats(), summary, "stl");
	    summary = Summary.summarize(player.getStats(), summary, "blk");
	    summary = Summary.summarize(player.getStats(), summary, "fg%");
	    summary = Summary.summarize(player.getStats(), summary, "3p%");
	    summary = Summary.summarize(player.getStats(), summary, "ft%");
	    summary = Summary.summarize(player.getAdvancedStats(), summary, "ts%");
	    summary = Summary.summarize(player.getPerPossStats(), summary, "ortg");
	    summary = Summary.summarize(player.getPerPossStats(), summary, "drtg");
	    summary = Summary.summarize(player.getStats(), summary, "tov");
	    summary = Summary.summarize(player.getStats(), summary, "mp");
	    summary = Summary.summarize(player.getAdvancedStats(), summary, "per");
	    player.setSummary(summary);
	    if(player.getTeam().get(0).length() > 0) {
	    	player.setTeamShort(player.getTeam().get(0).split(" ")[player.getTeam().get(0).split(" ").length - 1]);
	    }
	    else {
	    	player.setTeamShort(player.getTeam().get(0));
	    }
		return player;
	}
	
	public String getSource(String name) throws IOException{
        String googleSearchURL = "https://www.google.com/search";
        String searchURL = googleSearchURL + "?q="+name+" stats espn.com"+"&num=1";
        Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
        Element result = doc.getElementById("main");
        Element missSpell = result.getElementById("scl");
        /*
        if(missSpell != null){
            name = missSpell.text().substring(0, missSpell.text().length() - 9);
            return findPlayer(name);  //recalling function with proper name
        }
        */
        String longURL = result.getElementsByTag("a").get(13).attr("href");

        //System.out.println(result.getAllElements());
        //System.out.println(playerName);
        
        String url = "";
        int i = 0;
        boolean begin = false;
        while(longURL.charAt(i) != "&".charAt(0)){
            if(begin){
                 url += longURL.charAt(i);
            }
            if(longURL.charAt(i) == "=".charAt(0)){
                begin = true;
            }
            i++;
        }
        return findSource(url);
    }
	
	private static Document searchGoogle(String first, String last) throws IOException {
		String googleSearchURL = "https://www.google.com/search";
        String searchURL = googleSearchURL + "?q="+first + " " + last +" basketballreference.com"+"&num=1";
        Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
        Element result = doc.getElementById("main");
        /*
        Elements eles = result.select("a");
        for(Element el: eles){
        	System.out.println(el.toString());
        }
        */
        String longURL = result.getElementsByTag("a").get(13).attr("href");
        longURL = longURL.substring(longURL.indexOf("=") + 1, longURL.indexOf("&"));
        doc = Jsoup.connect(longURL).userAgent("Mozilla/5.0").get();
        return doc;
	}
	
	private static int getDraftYear(Document doc) throws IOException{
		Element meta = doc.getElementById("meta");
		Elements ahref = meta.select("a");
		String parse = "";
		int draftYear = -1;
		for(Element e: ahref) {
			Pattern p = Pattern.compile(".*\\bdraft\\b.*"); //regex
			Matcher m = p.matcher(e.attr("href")); //regex
			if(m.matches()) {
				parse = e.attr("href");
				p = Pattern.compile("\\d+"); //regex
				m = p.matcher(parse); //regex
				if(m.find()) {
					draftYear = Integer.parseInt(m.group());
				}
			}
		}
		return draftYear;
	}
	
	
	private static ArrayList<String> getTeamAndRecord(Document doc) throws IOException{
		Element meta = doc.getElementById("meta");
		Elements para = meta.select("p");
		String team = "";
		String record = "";
		ArrayList<String> teamList = new ArrayList<String>();
		for(Element e: para) {
			Pattern p = Pattern.compile(".*\\bTeam\\b.*"); //regex
			Matcher m = p.matcher(e.text()); //regex
			if(m.matches()) {
				team = e.select("a").text();
				doc = Jsoup.connect(e.select("a").attr("abs:href")).userAgent("Mozilla/5.0").get();
				meta = doc.getElementById("meta");
				para = meta.select("p");
				for(Element f: para) {
					p = Pattern.compile(".*\\bRecord\\b.*"); //regex
					m = p.matcher(f.text()); //regex
					if(m.matches()) {
						record = f.text();
						record = record.substring(record.indexOf(":") + 2);
					}
				}
			}
		}
		teamList.add(team);
		teamList.add(record);
		return teamList;
	}
	
	private static String getPhysicalAttributes(Document doc,
			String tag, String itemprop, String identifier) {
		Elements span = doc.select(tag + "["+itemprop+"]");
		String attributeVal = new String();
		for(Element element: span) {
			if(element.attr(itemprop).equals(identifier)) {
				attributeVal = element.text();
			}
		}
		//System.out.println(attributeVal);
		return attributeVal;
	}
	
	private static String getYoutubeUrl(String first, String last) throws IOException{
		String youtubeSearchUrl = "https://www.youtube.com/results?search_query=" + first + "+" + last + 
				"+" + "NBA" + "+" + "highlights";
		Document doc = Jsoup.connect(youtubeSearchUrl).userAgent("Mozilla/5.0").get();
		Elements ahrefs = doc.select("a");
		/*
		for(int i = 0; i < ahrefs.size(); i++) {
			System.out.println(ahrefs.get(i).attr("href"));
		}
		*/
		boolean found = false;
		String url = "youtubeUrl";
		int counter = 0;
		while(!found) {
			if(ahrefs.get(counter).attr("href").length() > 1) {
				Pattern p = Pattern.compile(".*\\bwatch\\b.*"); //regex
				Matcher m = p.matcher(ahrefs.get(counter).attr("href")); //regex
				if(m.find()) {
					System.out.println(ahrefs.get(counter).attr("href"));
					url = ahrefs.get(counter).attr("href");
					found = true;
				}
			}
			counter++;
		}
			
		return url.substring(url.indexOf("=") + 1, url.length());
	}
	
	
	
	private static ArrayList<String[]> retrieveStatLine(String className, 
			Document doc) throws IOException{
		Elements line = doc.getElementsByClass(className);
		String[] statsList = line.text().split(" ");
		ArrayList<String[]> statsLine = new ArrayList<String[]>();
		if(statsList.length > 1) {
			String summary = new String();
			String career = new String();
			String szn = new String();
			if(statsList[1].toLowerCase().equals("career")) {
				for(int i = 0; i < statsList.length; i++) {
					if( i % 2 == 0) {
						summary += statsList[i] + " ";
					}else {
						career += statsList[i] + " ";
					}
				}
			}else {
				for(int i = 0; i < statsList.length; i++) {
					if( i % 3 == 0) {
						summary += statsList[i] + " ";
					}else if( (i - 1) % 3 == 0){
						szn += statsList[i] + " ";
					}else {
						career += statsList[i] + " ";
					}
				}
			}
			statsLine.add(summary.split(" "));
			if(!statsList[1].toLowerCase().equals("career")) {
				statsLine.add(szn.split(" "));
			}
			statsLine.add(career.split(" "));
		}
		return statsLine;
	}
	
	private static ArrayList<ArrayList<String>> retrieve(String tableId,
			Document doc) throws IOException{
		Element perGame = doc.getElementById(tableId);
		ArrayList<ArrayList<String>> al = new ArrayList<>();
		if(perGame != null) {
			Elements tr = perGame.getElementsByTag("tr");
			for(int i = 0; i < tr.size(); i++) {
				if(tr.get(i).hasText()) {
					ArrayList<String> temp = new ArrayList<String>();
					Elements ele = tr.get(i).children();
					for(Element e: ele) {
						if(e.hasText()) {
							temp.add(e.text());
						}else {
							temp.add("-");
						}
					}
					al.add(temp);
					/*
					if(i == 0) {
						Elements th = tr.get(i).getElementsByTag("th");
						for(Element element: th) {
							if(element.hasText()) {
								temp.add(element.text());
							}else {
								temp.add("-");
							}
						}
						al.add(temp);
					}else {
						Elements td = tr.get(i).getElementsByTag("td");
						for(Element element: td) {
							if(element.hasText()) {
								temp.add(element.text());
							}else {
								temp.add("-");
							}
						}
						temp.add(0, tr.get(i).getElementsByTag("th").text());
						al.add(temp);
					}
					*/
				}
			}
		}
		return al;
	}
	
	//This method is to retrieve the tables which have the html code embedded in comments
	private static ArrayList<ArrayList<String>> retrieve(String tableId,
			String table, Document d) throws IOException{
		ArrayList<ArrayList<String>> al = new ArrayList<>();
		Elements tables = d.select("#" + tableId);
		if(tables.size() > 0) {
			String data = tables.get(0).getAllElements().toString();
			data = data.substring(data.indexOf("!") + 3, data.length());
			Document doc = Jsoup.parse(data);
			Element perGame = doc.getElementById(table);
			Elements tr = perGame.getElementsByTag("tr");
			for(int i = 0; i < tr.size(); i++) {
				if(tr.get(i).hasText()) {
					ArrayList<String> temp = new ArrayList<String>();
					if(i == 0) {
						Elements th = tr.get(i).getElementsByTag("th");
						for(Element element: th) {
							if(element.hasText()) {
								temp.add(element.text());
							}else {
								temp.add("-");
							}
						}
						al.add(temp);
					}else {
						Elements td = tr.get(i).getElementsByTag("td");
						for(Element element: td) {
							if(element.hasText()) {
								temp.add(element.text());
							}else {
								temp.add("-");
							}
						}
						temp.add(0, tr.get(i).getElementsByTag("th").text());
						al.add(temp);
					}
				}
			}
		}
		return al;
	}
	
    private static String findSource(String url){
    	String source = "https://a.espncdn.com/combiner/i?img=/i/headshots/nba/players/full/";
        for(int i = 0; i < url.length(); i++){
            if(Character.isDigit(url.charAt(i))){
                	source += url.charAt(i);
            }
        }
        source += ".png";
        
        return source;
    }
    
    private static ArrayList<String> filter(ArrayList<String> al, int index){
    	ArrayList<String> filteredList = new ArrayList<String>();
    	for(int i = 0; i < al.size(); i++) {
    		if(i != index) {
    			filteredList.add(al.get(i));
    		}
    	}
    	return filteredList;
    }
    
    private static String removeQuotation(String name) {
    	if(name.indexOf("'") != -1) {
			int index = name.indexOf("'");
			name = name.substring(0, index) + name.substring(index + 1, name.length());
		}
    	return name;
    }

}
