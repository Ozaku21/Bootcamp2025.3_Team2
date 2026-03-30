# Bootcamp2025.3_Team2

## Project Description

This project is an end-to-end QA automation framework for the **TBC Currency Exchange** flow on **tbcbank.ge**.

It uses Java + Playwright + TestNG for UI automation, Rest Assured for API validation, and K6 for performance testing, covering positive, negative, and edge-case scenarios across desktop and mobile execution profiles.

Reporting is handled with Allure (including logs/screenshots), while GitHub Actions runs the CI pipeline with parallel, headless execution to keep feedback fast and reliable.

The workflow also includes AI-assisted quality engineering for smarter scenario design, bug analysis, and stronger overall test coverage.

## Team Members

- Nini Mikadze
- Giorgi Kavtaria
- Nika Amirkhanovi
- Elene Gabrielashvili
- Otar Chelidze

## Team Captain

- Dachi Kazaishvili

## Feature

- Currency exchange

## Tech Stack

- **Language:** Java 17
- **Build Tool:** Maven
- **Test Framework:** TestNG
- **UI Automation:** Playwright for Java
- **API Testing:** Rest Assured
- **Performance Testing:** K6
- **Reporting:** Allure + Log4j2
- **CI/CD:** GitHub Actions
- **Utilities:** Lombok, AspectJ
- **Data Handling:** Jackson
- **Version Control:** Git + GitHub

---

## Scenarios

This project validates the **TBC Currency Converter** flow on both desktop and mobile profiles.

- Validate invalid input handling (`TP-T1`)
- Validate currency swap recalculation (`TP-T2`)
- Validate conversion accuracy across pairs and amounts (`TP-T3`)
- Validate non-numeric input rejection (`TP-T4`)
- Validate input boundary behavior (max/length constraints) (`TP-T5`)

## Run Instructions

Scenarios run through a TestNG factory (`Executor`) and are executed for configured browser/device combinations from `testNg.xml`.

### Prerequisites

- Java 17 installed
- Maven installed

### Run all tests

```bash
mvn clean test
```

- Run using TestNG suite file

```bash
  mvn clean test -Dsurefire.suiteXmlFiles=testNg.xml
```

- Generate and view Allure report

```bash
  mvn allure:report
  mvn allure:serve
```

## CI/CD Link

## AI Summary

## Bug Summary

```

```
