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

Tests are wired in `ge.tbc.testautomation.Executor` via a TestNG `@Factory`. Each suite run instantiates **UI** classes with the suite’s `browser` and `deviceType` parameters, plus **API** classes (Rest Assured) that do not depend on those parameters.

### UI (Playwright)

These run for every browser/device block defined in the active TestNG suite (`testNg.xml`).

| ID       | Scenario (TestNG group)                                      | Class                      |
| -------- | ------------------------------------------------------------ | -------------------------- |
| SCRUM-T1 | Validate currency swap and recalculation                     | `ConversionSwapTest`       |
| SCRUM-T2 | Validate currency conversion                                 | `ConversionValidationTest` |
| SCRUM-T3 | Validate converter boundary behavior                         | `InputBoundaryTest`        |
| SCRUM-T4 | Validate invalid input in the currency converter             | `InvalidInputTest`         |
| SCRUM-T5 | Verify non-numeric character restriction in the amount field | `NonNumericInputTest`      |

### API (Rest Assured)

| ID       | Scenario (TestNG group)                                   | Class                              |
| -------- | --------------------------------------------------------- | ---------------------------------- |
| SCRUM-T6 | Validate commercial exchange rates                        | `CommercialListApiCallTest`        |
| SCRUM-T7 | Validate exchange rate rejection for identical parameters | `ExchangeRateApiCallRejectionTest` |
| SCRUM-T8 | Validate successful exchange rate calculation             | `ExchangeRateApiCallTest`          |
| SCRUM-T9 | Validate successful forward rates retrieval               | `ForwardRatesApiCallTest`          |

### Performance (K6)

These scripts live in `src/test/java/ge/tbc/testautomation/performance/`. They call **TBC public API** endpoints (`apigw.tbcbank.ge`) and are **not** executed by Maven/TestNG; run them with the **k6** CLI. Each script writes an HTML report under `performance-results/` (relative to the directory you run `k6` from—use the **repository root** so those paths resolve).

| Script                             | Type                                                         | Endpoint focus                        | Report output                                       |
| ---------------------------------- | ------------------------------------------------------------ | ------------------------------------- | --------------------------------------------------- |
| `ExchangeRateLoadTest.js`          | Load (ramp to 25 VUs)                                        | `getExchangeRate` (USD/GEL)           | `performance-results/ExchangeRateLoadReport.html`   |
| `ForwardRatesLoadTest.js`          | Load (ramp to 25 VUs)                                        | `getForwardRates`                     | `performance-results/ForwardRatesLoadReport.html`   |
| `CommercialListPerformanceTest.js` | Load (ramp to 25 VUs)                                        | `commercialList`                      | `performance-results/CommercialListLoadReport.html` |
| `ExchangeRateStressTest.js`        | Stress (scales up to 150 VUs; retries, firewall/WAF metrics) | `getExchangeRate` (configurable pair) | `performance-results/ExchangeRateStressReport.html` |

The stress script accepts optional environment variables: `BASE_URL` (default `https://apigw.tbcbank.ge`), `CURRENCY_ONE`, `CURRENCY_TWO`, and `MAX_RETRIES`.

## Run Instructions

- **Default (`pom.xml`):** `mvn clean test` runs `testNg.xml` — Chromium, Firefox, and WebKit on desktop, plus a mobile (phone) profile, with `parallel="tests"` and `thread-count="4"`.
- **CI (GitHub Actions):** the workflow runs `testNg-ci.xml` — Chromium desktop and phone only, with `thread-count="2"` (faster, fewer browsers).

Scenarios are created by the TestNG factory class `ge.tbc.testautomation.Executor` for each `<test>` block in the suite XML.

### Prerequisites

- Java 17
- Maven
- Playwright browsers installed before the first local run (same command as CI):

```bash
mvn -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps" exec:java
```

### Run tests

Full local suite (all browsers/devices in `testNg.xml`):

```bash
mvn clean test
```

Explicit suite file (equivalent to the default when `pom.xml` points at `testNg.xml`):

```bash
mvn clean test -Dsurefire.suiteXmlFiles=testNg.xml
```

CI-style suite (subset of browsers, matches `.github/workflows/tests.yml`):

```bash
mvn clean test -Dsurefire.suiteXmlFiles=testNg-ci.xml
```

### Allure report

```bash
mvn allure:report
mvn allure:serve
```

### Performance tests (K6)

Performance tests are separate from the Maven suite. Install [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/) and run scripts from the **repository root** so `performance-results/` reports are created as expected.

Example (single load script):

```bash
k6 run src/test/java/ge/tbc/testautomation/performance/ExchangeRateLoadTest.js
```

Other scripts use the same pattern; replace the filename with `ForwardRatesLoadTest.js`, `CommercialListPerformanceTest.js`, or `ExchangeRateStressTest.js`.

Optional: override stress-test defaults (example):

```bash
k6 run --env BASE_URL=https://apigw.tbcbank.ge --env CURRENCY_ONE=USD --env CURRENCY_TWO=EUR src/test/java/ge/tbc/testautomation/performance/ExchangeRateStressTest.js
```

## CI/CD Link and Summary

Link to the actions page (where the pipelines are):
https://github.com/Ozaku21/Bootcamp2025.3_Team2/actions/workflows/tests.yml
Allure Report Link :
https://ozaku21.github.io/Bootcamp2025.3_Team2/

This project uses GitHub Actions to automatically run tests on every push and pull request.
The pipeline sets up Java, installs dependencies and Playwright browsers, executes TestNG tests,
and generates an Allure report.
The results are uploaded as artifacts, and on the main branch the report is deployed to GitHub Pages,
providing a live, shareable view of test results for the team.

## AI Summary

### AI Review Summary

An AI-based code review highlighted both the **strengths** and the **main improvement areas** of our automation framework.

We used AI coding assistance during review, including GitHub Copilot with Claude Haiku 4.5 for code suggestions and review support.

### Strengths

- Well-structured **Page Object Model (POM)**
- Clean and readable **fluent step design**
- Good use of **data-driven testing**
- Support for **cross-browser** and **cross-device** execution
- Integration of **UI, API, and performance testing**
- **CI/CD pipeline** and **Allure reporting**

### Key Recommendations

- Fix **decimal input handling** in currency conversion
- Add proper **dropdown synchronization** to reduce flaky tests
- Create **regression tests** for known bugs
- Replace brittle `.nth()` locators with more stable selectors
- Improve **test independence** by moving navigation to setup methods
- Separate **UI and API test execution**
- Improve **logging**, **debugging**, and framework **scalability**

### Conclusion

The AI review showed that the framework has a **solid foundation**, but several improvements were recommended to increase **stability**, **maintainability**, and **test reliability**.

## Bug Summary

Link to the bug report 1: https://dachikazo.atlassian.net/browse/SCRUM-5?atlOrigin=eyJpIjoiNDNjOGZmZmRiNTNmNDcwNDgxOTZhOGNjYTg0OGQ3NzUiLCJwIjoiaiJ9

SCRUM-5: The currency converter fails to handle empty input states during a "Swap" action, resulting in a "NaN" error being displayed in the input fields.
Description: Clicking the "Swap" button when the input is empty triggers a technical "NaN" (Not a Number) error.

Expected Result: The field should remain empty or default to "0" without showing technical errors.

Link to the bug report 2: https://dachikazo.atlassian.net/browse/SCRUM-6?atlOrigin=eyJpIjoiZjFkMDBkYzY2NGQ1NGJhZmFmYzQ3YmFiNWVlODZmMTMiLCJwIjoiaiJ9

SCRUM-6: Currency Converter field fails to accept or process decimal values
Description: The input field restricts the use of decimal separators (dots or commas), preventing precise calculations.

Expected Result: The system should support decimal inputs (e.g., 12.4 or 23.4) for accurate currency conversion
