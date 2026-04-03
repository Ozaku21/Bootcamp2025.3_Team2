# AI REVIEW: TBC Currency Exchange Automation Framework

---

## Project
**Bootcamp2025.3_Team2 - TBC Currency Exchange QA Automation**

## Purpose
End-to-end automation for **TBC Bank currency converter** (`tbcbank.ge`)



## 1. ConvertorPage - LOCATORS ARE BRITTLE

### Issue
Using `.nth(0)` and `.nth(1)` to select inputs — breaks if DOM changes.

#### ❌ BAD - Brittle
```java
this.currencyInput1 = page.locator("div.input-with-label input").nth(0);
this.currencyInput2 = page.locator("div.input-with-label input").nth(1);
```

#### ✅ GOOD - Use data-testid or aria-label
```java
this.currencyInput1 = page.locator("input[data-testid='currency-input-from']")
    .or(page.getByLabel("From Amount"));
this.currencyInput2 = page.locator("input[data-testid='currency-input-to']")
    .or(page.getByLabel("To Amount"));
```

**Score:** `Stability 6/10` | `Maintainability 7/10`

---

## 2. ConvertorSteps - 3 CRITICAL BUGS

### Bug #1: Decimal Truncation (Violates SCRUM-6)

#### ❌ BAD - Loses decimals: `100.5 → 100`
```java
public ConvertorSteps enterCurrencyAmount(double amount) {
    convertorPage.currencyInput1.fill(String.valueOf((int) amount));
    return this;
}
```

#### ✅ GOOD
```java
public ConvertorSteps enterCurrencyAmount(double amount) {
    convertorPage.currencyInput1.fill(String.valueOf(amount));
    return this;
}
```

---

### Bug #2: Missing Dropdown Synchronization (Race Condition)

#### ❌ BAD - Dropdown may still be open
```java
public ConvertorSteps selectFromCurrency(String currency) {
    convertorPage.fromCurrencyButton.click();
    convertorPage.currencyItem(currency).click();
    return this;
}
```

#### ✅ GOOD - Wait for overlay to close
```java
public ConvertorSteps selectFromCurrency(String currency) {
    convertorPage.fromCurrencyButton.click();
    convertorPage.currencyItem(currency).scrollIntoViewIfNeeded();
    convertorPage.currencyItem(currency).click();

    // Wait for dropdown to close
    convertorPage.page.locator(".cdk-overlay-pane").waitFor(
        new Locator.WaitForOptions().setState(PlaywrightPage.WaitForSelectorState.HIDDEN)
    );

    return this;
}
```

---

### Bug #3: Arbitrary Tolerance (0.5) — No Business Context

#### ❌ BAD - Why `0.5`? No documentation
```java
Assert.assertEquals(actual, expected, 0.5, "Conversion error");
```

#### ✅ GOOD - Document and justify tolerance
```java
private static final double CONVERSION_TOLERANCE = 0.01; // ±1 cent GEL-EUR

Assert.assertEquals(actual, expected, CONVERSION_TOLERANCE,
    String.format("Conversion error. Expected: %.2f, Actual: %.2f", expected, actual));
```

**Score:** `Stability 6/10` | `Maintainability 8/10`

---

## 3. BaseTest - HARDCODED, NOT FLEXIBLE

### Issues
- Device profiles hardcoded (can't add tablet/iPad)
- Headless always true (can't debug with headed mode)
- Unconditional cookie click (fragile)

---

### ❌ BAD - Hardcoded, not extensible
```java
private Browser.NewContextOptions buildContextOptions() {
    return switch (deviceType) {
        case DESKTOP -> options.setViewportSize(1920, 1080);
        case MOBILE  -> options.setViewportSize(430, 932).setIsMobile(true).setHasTouch(true);
        default -> throw new IllegalArgumentException(UNSUPPORTED + deviceType);
    };
}
```

### ✅ GOOD - Externalize to enum
```java
private Browser.NewContextOptions buildContextOptions() {
    DeviceProfile profile = DeviceProfile.fromString(deviceType);
    return new Browser.NewContextOptions()
            .setViewportSize(profile.width, profile.height)
            .setIsMobile(profile.isMobile)
            .setHasTouch(profile.hasTouch)
            .setDeviceScaleFactor(profile.scaleFactor);
}
```

### Device Profile Enum
```java
public enum DeviceProfile {
    DESKTOP("desktop", 1920, 1080, false, false, 1.0),
    MOBILE("mobile", 430, 932, true, true, 3.0),
    TABLET("tablet", 768, 1024, true, true, 2.0);

    public final String name;
    public final int width, height;
    public final boolean isMobile, hasTouch;
    public final double scaleFactor;

    DeviceProfile(String name, int width, int height, boolean isMobile, boolean hasTouch, double scaleFactor) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.isMobile = isMobile;
        this.hasTouch = hasTouch;
        this.scaleFactor = scaleFactor;
    }

    public static DeviceProfile fromString(String name) {
        return Arrays.stream(values())
                .filter(p -> p.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported device: " + name));
    }
}
```

### Also add headed mode support
```java
boolean headless = !Boolean.parseBoolean(System.getProperty("headed", "false"));
BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
    .setHeadless(headless);
```

### Run
```bash
mvn test -Dheaded=true
```

**Score:** `Stability 7/10` | `Maintainability 6/10`

---

## 4. ConversionSwapTest - TEST COUPLING + MISSING REGRESSIONS

### Issue
Navigation as separate test violates independence principle.

---

### ❌ BAD - Navigation as separate test (`priority = 1`)
```java
@Test(priority = 1)
public void navigateToCurrencyConvertorPage() {
    commonSteps.clickKebabMenu().clickCurrencyLariOutlined();
}

@Test(priority = 2)
public void currencySwap(...) { ... }
```

### ✅ GOOD - Use `@BeforeMethod`
```java
@BeforeMethod(alwaysRun = true)
public void navigateToCurrencyConverter() {
    commonSteps.clickKebabMenu().clickCurrencyLariOutlined();
}

@Test(dataProvider = "currencyPairs", dataProviderClass = CurrencyDataProvider.class)
public void currencySwap(...) { ... }
```

---

## Missing Regression Tests for Known Bugs

### SCRUM-5: Empty swap should not show `NaN`
```java
@Test(groups = {"SCRUM-5"})
public void swapWithEmptyInputShouldNotShowNaN() {
    convertorSteps
            .selectFromCurrency(GEL)
            .selectToCurrency(EUR)
            .clickSwap()
            .validateSwapDoesNotShowNaN();
}
```

### SCRUM-6: Decimal input support
```java
@Test(groups = {"SCRUM-6"})
public void decimalConversionValidation() {
    convertorSteps
            .selectFromCurrency(GEL)
            .selectToCurrency(EUR)
            .enterCurrencyAmount(12.5)  // decimal
            .verifyConversion(12.5);
}
```

### Add to `ConvertorSteps`
```java
@Step("Validate swap does not show NaN")
public ConvertorSteps validateSwapDoesNotShowNaN() {
    String input1 = convertorPage.currencyInput1.inputValue().trim();
    String input2 = convertorPage.currencyInput2.inputValue().trim();

    Assert.assertFalse(input1.contains("NaN"), "Input 1 shows NaN error");
    Assert.assertFalse(input2.contains("NaN"), "Input 2 shows NaN error");

    return this;
}
```

**Score:** `Stability 7/10` | `Maintainability 6/10`

---

## 5. Executor - SPLIT API & UI TESTS

### ❌ BAD - Mixed API and UI in single factory
```java
return new Object[]{
    new InvalidInputTest(browser, deviceType),
    new CommercialListApiCallTest(),  // API mixed with UI
    new ExchangeRateApiCallTest()
};
```

### ✅ GOOD - Separate executors

#### `UIExecutor.java`
```java
public class UIExecutor {
    @Factory
    @Parameters({BROWSER, DEVICETYPE})
    public Object[] createUITests(
            @Optional(CHROME) String browser,
            @Optional(DESKTOP) String deviceType) {
        return new Object[]{
            new InvalidInputTest(browser, deviceType),
            new ConversionValidationTest(browser, deviceType),
            new ConversionSwapTest(browser, deviceType),
            new InputBoundaryTest(browser, deviceType),
            new NonNumericInputTest(browser, deviceType)
        };
    }
}
```

#### `APIExecutor.java`
```java
public class APIExecutor {
    @Factory
    public Object[] createAPITests() {
        return new Object[]{
            new CommercialListApiCallTest(),
            new ForwardRatesApiCallTest(),
            new ExchangeRateApiCallTest(),
            new ExchangeRateApiCallRejectionTest()
        };
    }
}
```

**Score:** `Stability 8/10` | `Maintainability 6/10`

---

# FRAMEWORK-LEVEL ISSUES

| Issue | Fix |
|------|-----|
| No logging | Add Log4j2: `<dependency><groupId>org.apache.logging.log4j</groupId><artifactId>log4j-core</artifactId><version>2.20.0</version></dependency>` |
| Weak error messages | Include browser, device, URL in failure context |
| No granular retries | Add step-level retry, not just method-level |
| Missing wait strategies | Add explicit state waits for async UI changes |

---

# PRIORITY FIXES

## CRITICAL
- Remove `(int)` cast in `enterCurrencyAmount()` → **SCRUM-6 bug**
- Add dropdown close wait in `selectFromCurrency/selectToCurrency()` → **Race condition**

## HIGH
- Add **SCRUM-5** and **SCRUM-6** regression tests
- Move navigation to `@BeforeMethod`
- Replace `.nth()` with semantic selectors

## MEDIUM
- Split `Executor` into **UI + API**
- Externalize device profiles

## LOW
- Add logging framework

---

# SCORES SUMMARY

| Component | Stability | Maintainability |
|----------|-----------|-----------------|
| ConvertorPage | 6/10 | 7/10 |
| ConvertorSteps | 6/10 | 8/10 |
| BaseTest | 7/10 | 6/10 |
| ConversionSwapTest | 7/10 | 6/10 |
| Executor | 8/10 | 6/10 |
| **OVERALL** | **6.8/10** | **6.6/10** |

---

# TL;DR

Framework has good structure (**fluent patterns, data-driven tests**) but three critical bugs:

1. **Decimal truncation** violates **SCRUM-6**
2. **Missing dropdown sync** causes **race conditions**
3. **Arbitrary assertion tolerance** hides issues

Also:
- test coupling
- brittle locators
- hardcoded configs

These reduce maintainability.

## Fix these 5 items first:
- Remove decimal truncation
- Add dropdown synchronization
- Add missing regression tests
- Move navigation to `@BeforeMethod`
- Replace brittle locators

Then:
- add logging
- split API/UI tests
