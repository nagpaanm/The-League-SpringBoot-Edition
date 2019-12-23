package ca.sheridancollege.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.beans.Player;
import ca.sheridancollege.database.DatabaseAccess;
import ca.sheridancollege.logic.Fetch;

@Controller
public class HomeController {
	
	@Autowired
	private DatabaseAccess da;
	
	@GetMapping("/")
	public String goHome() throws IOException{
		//da.addPlayers();
		return "index.html";
	}
	
	@GetMapping("find")
	public String findPlayer(Model model, @RequestParam String name) throws IOException{
		Fetch fetch = new Fetch();
		List<String> names = fetch.getNames(name);
		if(names.size() == 2) {
			if(da.getPlayer(names.get(0), names.get(1))) {
					Player player = fetch.findPlayer(names.get(0), names.get(1));
					//player.setSource(fetch.getSource(player.getName()));
					model.addAttribute("player", player);
					return "player.html";
			}
			List<Map<String, Object>> potentialMatchesList = da.potentialMatches(names.get(0), names.get(1));
			model.addAttribute("pmList", potentialMatchesList);
		}
		return "research.html";
	}
	
	@GetMapping("browse")
	public String browsePlayers(Model model, @RequestParam String param) throws IOException{
		List<Map<String, Object>> browseList = da.browsePlayer(param);
		model.addAttribute("list", browseList);
		model.addAttribute("initial", param);
		return "browse.html";
	}
	
}
