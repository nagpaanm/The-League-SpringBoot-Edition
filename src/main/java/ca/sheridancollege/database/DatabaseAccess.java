package ca.sheridancollege.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseAccess {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbc;
	
	public void addPlayers() throws IOException{
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "\r\n" + 
				"INSERT INTO players VALUES "
				+ "(:first, :last)";
		Resource resource = new ClassPathResource("players.txt");
		InputStream input = resource.getInputStream();
		File file = resource.getFile();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while(line != null) {
			int comma = line.indexOf(",");
			String last = line.substring(0, comma);
			String first = line.substring(comma + 2, line.length());
			parameters.addValue("first", first);
			parameters.addValue("last", last);
			jdbc.update(query, parameters);
			line = br.readLine();
		}
		br.close();
		fr.close();
	}
	
	public boolean getPlayer(String first, String last){

		String query = "SELECT first, last FROM players WHERE first = " + "\"" + first + "\"" + 
				" AND last = " + "\"" + last + "\"";
		List<Map<String, Object>> rows = 
				jdbc.queryForList(query, new HashMap<String, Object>());
		if(rows.size() > 0) {
			return true;
		}
		return false;
	}
	
	public List<Map<String, Object>> browsePlayer(String initial){
		String query = "SELECT first, last FROM players WHERE last LIKE " + "'" + initial + "%'" +
					"ORDER BY last";
		List<Map<String, Object>> rows = 
				jdbc.queryForList(query, new HashMap<String, Object>());
		return rows;
	}
	
	public List<Map<String, Object>> potentialMatches(String first, String last){
		String queryFirst = "SELECT first, last FROM players WHERE first = " + "'" + first + "'"  +
				" ORDER BY last";
		List<Map<String, Object>> rowsFirst = jdbc.queryForList(queryFirst, new HashMap<String, Object>());
		String querySecond = "SELECT first, last FROM players WHERE last = " + "'" + last + "'" +
				" ORDER BY last";
		List<Map<String, Object>> rowsSecond = jdbc.queryForList(querySecond, new HashMap<String, Object>());
		for(Map<String, Object> row: rowsSecond){
			rowsFirst.add(row);
		}
		String queryOpposite = "SELECT first, last FROM players WHERE first = " + "'" + last + "'" + 
		" AND last = " + "'" + first + "'" + " ORDER BY last";
		List<Map<String, Object>> rowsOpposite = jdbc.queryForList(queryOpposite, new HashMap<String, Object>());
		for(Map<String, Object> row: rowsOpposite){
			rowsFirst.add(row);
		}
		if(rowsFirst.isEmpty()) {//more advanced search
			String queryAdvanced = "SELECT first, last FROM players WHERE first LIKE " + "'" + first.charAt(0) + "%'" +
					" AND last LIKE " + "'" + last.charAt(0) + "%'" + " ORDER BY last";
			List<Map<String, Object>> rowsAdvanced = jdbc.queryForList(queryAdvanced, new HashMap<String, Object>());
			for(Map<String, Object> row: rowsAdvanced){
				rowsFirst.add(row);
			}
		}
		return rowsFirst;
	}
	
	public boolean playerExists(String first){

		String query = "SELECT first FROM players WHERE first = " + "\"" + first + "\"";
		List<Map<String, Object>> rows = 
				jdbc.queryForList(query, new HashMap<String, Object>());
		if(rows.size() > 0) {
			return true;
		}
		return false;
	}
	
}
