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
- Otar Tchelidze

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

## CI/CD Link and Summary
Link to the actions page (where the pipelines are):
https://github.com/Ozaku21/Bootcamp2025.3_Team2/actions/workflows/tests.yml

This project uses GitHub Actions to automatically run tests on every push and pull request.
The pipeline sets up Java, installs dependencies and Playwright browsers, executes TestNG tests,
and generates an Allure report. 
The results are uploaded as artifacts, and on the main branch the report is deployed to GitHub Pages,
providing a live, shareable view of test results for the team.

## AI Summary

## Bug Summary
Link to the bug report 1: https://dachikazo.atlassian.net/browse/SCRUM-5?atlOrigin=eyJpIjoiNDNjOGZmZmRiNTNmNDcwNDgxOTZhOGNjYTg0OGQ3NzUiLCJwIjoiaiJ9

SCRUM-5: The currency converter fails to handle empty input states during a "Swap" action, resulting in a "NaN" error being displayed in the input fields.
Description: Clicking the "Swap" button when the input is empty triggers a technical "NaN" (Not a Number) error.

Expected Result: The field should remain empty or default to "0" without showing technical errors.

Link to the bug report 2: https://dachikazo.atlassian.net/browse/SCRUM-6?atlOrigin=eyJpIjoiZjFkMDBkYzY2NGQ1NGJhZmFmYzQ3YmFiNWVlODZmMTMiLCJwIjoiaiJ9

SCRUM-6: Currency Converter field fails to accept or process decimal values
Description: The input field restricts the use of decimal separators (dots or commas), preventing precise calculations.

Expected Result: The system should support decimal inputs (e.g., 12.4 or 23.4) for accurate currency conversion
