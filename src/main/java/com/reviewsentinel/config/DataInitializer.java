package com.reviewsentinel.config;

import com.reviewsentinel.service.ReviewAnalysisService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ReviewAnalysisService analysisService;

    public DataInitializer(ReviewAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Sample 1: Likely Fake
        analysisService.analyzeAndSave(
                "Wireless Noise-Canceling Headphones",
                5,
                "Amazing amazing product!!!",
                "Amazing amazing amazing product!!! Best thing ever!!! 100% perfect!!! Must buy right now, dont hesitate 5 stars!!!",
                false
        );

        // Seed Sample 2: Likely Genuine
        analysisService.analyzeAndSave(
                "Mechanical Gaming Keyboard RGB",
                4,
                "Solid keyboard for daily typing and gaming",
                "I bought this mechanical keyboard 3 weeks ago for work and gaming. The tactile switches feel great and keycaps have good build quality. Battery life in wireless mode lasts around 40 hours. Sound is slightly clicky, but overall very satisfied with the value.",
                true
        );

        // Seed Sample 3: Suspicious
        analysisService.analyzeAndSave(
                "UltraHD 4K Smart Monitor 32\"",
                5,
                "Best monitor of my life",
                "This is literally the best purchase ever made! Quality is incredible, colors are amazing. 100% recommended to everyone. Buy it now!",
                false
        );

        // Seed Sample 4: Likely Genuine
        analysisService.analyzeAndSave(
                "Ergonomic Office Chair Mesh",
                5,
                "Excellent lumbar support after 2 months of use",
                "As someone who sits for 9 hours a day coding, lumbar support was my main priority. The mesh material is breathable, plastic armrests feel sturdy, and assembly took about 20 minutes. Minor issue with the height adjustment lever being stiff at first.",
                true
        );

        // Seed Sample 5: Suspicious
        analysisService.analyzeAndSave(
                "Portable Power Bank 20000mAh",
                5,
                "Mindblowing battery life",
                "Awesome awesome power bank! Life changing speed and 100% perfection. Guaranteed satisfaction, buy it today!",
                false
        );
    }
}
