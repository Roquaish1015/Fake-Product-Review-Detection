# 🛡️ Review Sentinel – Fake Product Review Detection System

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-green.svg)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-blue.svg)](https://www.thymeleaf.org/)
[![Database](https://img.shields.io/badge/Database-H2-lightblue.svg)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Review Sentinel** is an enterprise-grade AI & heuristic fake product review detection platform built with **Java Spring Boot**, **Thymeleaf**, **Spring Data JPA**, and **JSoup**. It features a dark, futuristic, cybersecurity-inspired UI with live telemetry visuals, multi-stage review analysis, phrase highlighting, e-commerce URL scraping, and interactive analytics dashboards.

---

## 📸 Overview & Features

### 🔍 Core Capabilities
* **Interactive AI Analyzer**: Scan raw review content or full product URLs (Amazon, Flipkart, eBay, Meesho, etc.) to evaluate fake probability and authenticity.
* **Deterministic Heuristic Detection Engine**: Multi-dimensional rule engine assessing punctuation density, shouting ratios, consecutive repetitions, promotional hyperbole, review length, specificity nouns, and buyer verification status.
* **Phrase-Level Risk Highlighting**: Interactive text highlighting with risk-score tooltips explaining exact suspicious phrases detected in the text.
* **E-Commerce URL Batch Extraction**: Scrapes product titles and reviews from live product pages using JSoup and evaluates batch sentiment and fake probability.
* **Cybersecurity Dashboard**: Real-time stats and dark-themed Chart.js visualizations for verdict distribution, risk categorization, sentiment breakdown, and 7-day volume trends.
* **History Log & Management**: Full historical record of evaluated reviews with search, category filtering (Likely Fake, Suspicious, Genuine), and instant deletion capabilities.
* **Zero-Config Embedded Setup**: Embedded H2 database pre-populated with initial seed data on startup.

---

## 🏗️ Architecture & Technology Stack

| Layer | Technology | Description |
|---|---|---|
| **Backend Framework** | Java 17, Spring Boot 3.2.3 | REST API, Business Services, Dependency Injection |
| **Persistence** | Spring Data JPA, Hibernate, H2 Database | Relational ORM with zero-config in-memory database |
| **Web / Scraping** | JSoup 1.17.2 | Live e-commerce HTML parsing and product extraction |
| **Frontend Templating** | Thymeleaf, HTML5, CSS3, JS (ES6+) | Glassmorphism UI, Keyframe Animations, Chart.js |
| **Build & Tooling** | Maven (with Maven Wrapper `mvnw`) | Self-contained build script (`run.bat` / `run.ps1`) |

---

## 📊 Detection Engine Rules & Scoring Matrix

The engine (`ReviewAnalyzer.java`) evaluates each review through a deterministic scoring matrix to compute a **Fake Probability Score (0% – 100%)**:

| Rule | Trigger Condition | Risk Points Added | Signal Icon |
|---|---|---|---|
| **Excessive Punctuation** | 3+ exclamation marks (`!`) in text | +15 to +20 pts | ⚠ |
| **Excessive Capitalization** | > 35% uppercase letters (SHOUTING) | +12 pts | ⚡ |
| **Consecutive Repetition** | Repeated words/phrases (e.g., *"amazing amazing"*) | +15 pts per match | ⚠ |
| **Promotional Hyperbole** | Overused marketing keywords (*"100% perfect"*, *"must buy"*, *"game changer"*) | +12 to +25 pts | 📢 |
| **Short Review Length** | Total word count < 12 words | +10 pts | 🔍 |
| **Low Specificity** | Absence of feature nouns (*battery*, *sound*, *material*, *cushion*) in short reviews | +12 pts | 🎯 |
| **Unrealistic Sentiment** | 5-star rating combined with heavy hype & punctuation | +10 pts | 🌟 |
| **Unverified Buyer Weight** | Submission from unverified purchase profile | +8 pts | 🏷️ |

### 🚦 Risk Classification Thresholds
- 🟢 **0% – 35%**: **LIKELY GENUINE** (Low Risk)
- 🟡 **36% – 65%**: **SUSPICIOUS** (Medium Risk)
- 🔴 **66% – 100%**: **LIKELY FAKE** (High Risk)

---

## 🚀 Quick Start Guide

### Prerequisites
* **Java JDK 17** or higher installed and set in your `PATH`.
* *No global Maven installation required* (Maven Wrapper `mvnw.cmd` is included).

### Running the Application (Windows)

Simply double-click or run `run.bat` from VS Code or PowerShell:

```cmd
.\run.bat
```

*Alternatively on PowerShell:*
```powershell
.\run.ps1
```

*Or using Maven directly:*
```bash
./mvnw clean spring-boot:run
```

Once started, open your browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 📡 REST API Documentation

### 1. Analyze Single Review
* **Endpoint**: `POST /api/analyze`
* **Content-Type**: `application/json`
* **Request Body**:
  ```json
  {
    "productName": "Wireless Noise-Canceling Headphones",
    "rating": 5,
    "reviewTitle": "Amazing amazing product!!!",
    "reviewContent": "Best product ever made! Must buy right now, dont hesitate 5 stars!!!",
    "verifiedPurchase": false
  }
  ```

### 2. Analyze Product URL
* **Endpoint**: `POST /api/analyze-url`
* **Request Body**:
  ```json
  {
    "url": "https://www.flipkart.com/example-product/p/itm123456"
  }
  ```

### 3. Get Dashboard Analytics
* **Endpoint**: `GET /api/dashboard/stats`

### 4. Delete Review Record
* **Endpoint**: `DELETE /api/reviews/{id}`

---

## 🗄️ H2 Database Web Console

To view or query the stored review database directly:
- **Console URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:reviewsentineldb`
- **User Name**: `sa`
- **Password**: *(leave empty)*

---

## 📂 Project Structure

```
Fake Product Review Detection System/
├── .gitignore
├── pom.xml
├── README.md
├── run.bat
├── run.ps1
├── mvnw
├── mvnw.cmd
└── src/
    └── main/
        ├── java/com/reviewsentinel/
        │   ├── ReviewSentinelApplication.java
        │   ├── analyzer/
        │   │   └── ReviewAnalyzer.java
        │   ├── config/
        │   │   └── DataInitializer.java
        │   ├── controller/
        │   │   ├── ApiController.java
        │   │   └── ReviewController.java
        │   ├── model/
        │   │   ├── AnalysisResult.java
        │   │   ├── PhraseHighlight.java
        │   │   ├── Review.java
        │   │   └── SignalReason.java
        │   ├── repository/
        │   │   └── ReviewRepository.java
        │   └── service/
        │       ├── ReviewAnalysisService.java
        │       └── UrlProductExtractor.java
        └── resources/
            ├── application.properties
            ├── static/
            │   ├── css/
            │   │   ├── animations.css
            │   │   └── style.css
            │   └── js/
            │       ├── analyzer.js
            │       ├── app.js
            │       ├── dashboard.js
            │       └── history.js
            └── templates/
                ├── analyzer.html
                ├── dashboard.html
                ├── history.html
                ├── index.html
                ├── result.html
                └── fragments/
                    ├── footer.html
                    └── navbar.html
```

---

## 📄 License & Attribution

This project is open-source under the [MIT License](LICENSE).  
Created for detecting non-authentic e-commerce reviews using Java Spring Boot & AI Analytics.
