package com.reviewsentinel.controller;

import com.reviewsentinel.model.AnalysisResult;
import com.reviewsentinel.service.ReviewAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ReviewController {

    private final ReviewAnalysisService analysisService;

    public ReviewController(ReviewAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("page", "home");
        return "index";
    }

    @GetMapping("/analyzer")
    public String analyzer(Model model) {
        model.addAttribute("page", "analyzer");
        return "analyzer";
    }

    @GetMapping("/result/{id}")
    public String result(@PathVariable Long id, Model model) {
        AnalysisResult result = analysisService.getAnalysisResultById(id);
        if (result == null) {
            return "redirect:/history";
        }
        model.addAttribute("result", result);
        model.addAttribute("page", "result");
        return "result";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", analysisService.getDashboardStats());
        model.addAttribute("page", "dashboard");
        return "dashboard";
    }

    @GetMapping("/history")
    public String history(@RequestParam(required = false) String search,
                          @RequestParam(required = false) String filter,
                          Model model) {
        List<AnalysisResult> reviews = analysisService.getAllResults(search, filter);
        model.addAttribute("reviews", reviews);
        model.addAttribute("currentSearch", search != null ? search : "");
        model.addAttribute("currentFilter", filter != null ? filter : "ALL");
        model.addAttribute("page", "history");
        return "history";
    }
}
