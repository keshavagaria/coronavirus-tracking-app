package com.company.coronavirustrackerapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.company.coronavirustrackerapp.models.LocationStats;
import com.company.coronavirustrackerapp.service.CoronavirusDataService;

@Controller
public class HomeController {

	@Autowired
	CoronavirusDataService coronavirusDataService;
	@GetMapping("/")
	private String home(Model model) {
		 List<LocationStats> allStats= coronavirusDataService.getAllStats();
		int totalCasesReported =  allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases =  allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDate()).sum();
		
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalCasesReported", totalCasesReported);
		model.addAttribute("totalNewCases",totalNewCases);
		return "home";
	}
}
