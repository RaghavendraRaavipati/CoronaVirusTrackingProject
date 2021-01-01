package com.raghu.coronavirustracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.raghu.coronavirustracker.modals.CoronaVirusDataEntity;
import com.raghu.coronavirustracker.service.CoronaVirusTrackerService;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusTrackerService coronaVirusTrackerService;

    @GetMapping("/")
    public String home(Model model) {
        
    	List<CoronaVirusDataEntity> allStats = coronaVirusTrackerService.getAllStats();
        
    	int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        
    	int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        
    	model.addAttribute("coronaVirusDataEntity", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
    }
}
