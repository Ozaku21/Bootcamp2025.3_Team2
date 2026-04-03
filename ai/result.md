# Senior QA Automation Code Review

Perfect. Now I have enough context to provide comprehensive code reviews.  
Let me prepare detailed reviews organized by component.

---

# 1. ConvertorPage (Page Object)

## ✅ What's Good

- Clean separation of concerns with **POM pattern**
- Proper use of **Locator chaining** for the currency dropdown  
  (`currencyForm.locator()` for scoped element retrieval)
- Good use of `getByText()` with `setExact(true)` to avoid partial matches

---

## ⚠️ Issues Found

### Issue: Generic CSS Selectors with `nth()` Positioning — High Risk for Flakiness

#### What is wrong
Lines **24–25** use `.nth(0)` and `.nth(1)` to select input fields:

```java
this.currencyInput1 = page.locator("div.input-with-label input").nth(0);
this.currencyInput2 = page.locator("div.input-with-label input").nth(1);
```

This assumes a fixed DOM order.  
If the DOM structure changes or if a new input is added, selectors will break silently.

#### Why it matters
`nth()` positioning is brittle in enterprise applications.  
If the page structure changes, tests fail without clear debugging information.  
It's also harder to parallelize reliably.

#### How to fix it
- Use semantic selectors like `data-testid`, `aria-label`, or locator filters
- If labels exist, use role-based queries:
```java
page.locator("input").filter(
    new Locator.FilterOptions().setHas(
        page.locator("label:has-text('From Currency')")
    )
)
```
- Fallback: Use a more stable parent selector or attribute-based differentiation

---

### Issue: Mixed Locator Strategies Without Clear Intent

#### What is wrong
The class mixes:
- CSS class selectors
- role-based selectors (in `CommonPage`)
- text-based selectors

...without a consistent strategy.

#### Why it matters
In a large framework, inconsistent locator strategies make maintenance harder and reduce readability.

#### How to fix it
Document the locator strategy.

**Recommended order of preference:**
1. Role / label / text-based selectors
2. `data-testid`
3. CSS classes only when semantic alternatives are unavailable

---

## 🔍 Technical Analysis

The POM is lean and focused.  
However, it assumes stable DOM structures and fixed element ordering, which is a common source of flakiness in cross-browser automation.

The mixing of locator strategies suggests the team is still establishing best practices around Playwright's advanced selectors.

---

## 💡 Optimization Suggestions

- Use more robust selectors (`aria-label`, `data-testid`, or relative locators when available)
- Add helper methods for common patterns  
  *(e.g., `selectCurrency(String currency)` in the POM itself, or a factory for consistent locator creation)*

---

## ✅ Improved Code Example

```java
public ConvertorPage(Page page) {
    super(page);

    // Use stable, semantic selectors where possible
    this.currencyForm = page.locator(".tbcx-pw-exchange-rates-calculator");
    this.currencyRate = page.locator("div.tbcx-pw-exchange-rates-calculator__description");
    
    // If data-testid is available, prefer it; otherwise, use getByLabel or similar
    this.currencyInput1 = page.locator("input[data-testid='currency-input-from']")
        .or(page.getByLabel("From Amount")); // fallback
    this.currencyInput2 = page.locator("input[data-testid='currency-input-to']")
        .or(page.getByLabel("To Amount"));
    
    // Button selectors are OK here but could be improved with aria-label
    this.fromCurrencyButton = page.locator("button").filter(
        new Locator.FilterOptions().setHasText("From")
    ).first();
    this.toCurrencyButton = page.locator("button").filter(
        new Locator.FilterOptions().setHasText("To")
    ).first();
    
    this.swapButton = currencyForm.locator(".tbcx-pw-exchange-rates-calculator__swap");
    this.currencyElements = page.locator(".cdk-overlay-pane .tbcx-dropdown-popover-item__title");
}
```

---

## 📊 Scores

| Dimension | Score | Reason |
|----------|-------|--------|
| Stability | 6/10 | `nth()` positioning is brittle; mixed locator strategies risk cross-browser issues |
| Maintainability | 7/10 | POM is well-organized but lacks clear locator strategy documentation |

---

## TL;DR

POM pattern is correctly applied, but heavy reliance on `nth()` and CSS selectors without semantic attributes creates flakiness risk.

**Recommendation:** Replace with role/aria-based selectors or add `data-testid` attributes for robustness across browsers and mobile.

---

# 2. ConvertorSteps (Step Definitions)

## ✅ What's Good

- Excellent use of **fluent/builder pattern** for method chaining — highly readable and maintainable
- Good parsing logic with validation: `parseRate()`, `parseAmount()`, and `validate()` with clear error messages
- Step annotations (`@Step`) with descriptive names support good **Allure reporting**
- Assertion quality is solid: uses Playwright assertions (`assertThat`) and meaningful error messages

---

## ⚠️ Issues Found

### Issue: Double Conversion in `enterCurrencyAmount()` — Type Safety Problem

#### What is wrong
Line **74**:

```java
public ConvertorSteps enterCurrencyAmount(double amount) {
    convertorPage.currencyInput1.fill(String.valueOf((int) amount));
    return this;
}
```

Casting `double` to `(int)` silently truncates decimals.

Example:
```text
100.5 → 100
```

This violates the README requirement:

> "The system should support decimal inputs (e.g., 12.4 or 23.4)"

#### Why it matters
This is a direct regression against known bugs (**SCRUM-6**).

Tests may pass even though:
- the app fails to handle decimals, or
- the app correctly rejects decimals but tests are written to expect integer-only input

#### How to fix it

```java
public ConvertorSteps enterCurrencyAmount(double amount) {
    convertorPage.currencyInput1.fill(String.valueOf(amount));
    return this;
}
```

---

### Issue: Timing / Race Condition Risk in `selectFromCurrency()` and `selectToCurrency()`

#### What is wrong
Lines **57–65** do not wait for the dropdown to close after selection:

```java
public ConvertorSteps selectFromCurrency(String currency) {
    convertorPage.fromCurrencyButton.click();
    convertorPage.currencyItem(currency).scrollIntoViewIfNeeded();
    convertorPage.currencyItem(currency).click();
    return this;
}
```

If the click is fast enough that the dropdown overlay hasn't disappeared, the next action may fail.

#### Why it matters
Cross-browser and network variations can cause inconsistent behavior.  
Slow machines or slower networks may cause the dropdown to still be open when the next step executes.

#### How to fix it

```java
public ConvertorSteps selectFromCurrency(String currency) {
    convertorPage.fromCurrencyButton.click();
    convertorPage.currencyItem(currency).scrollIntoViewIfNeeded();
    convertorPage.currencyItem(currency).click();

    // Wait for dropdown to close before returning
    convertorPage.page.locator(".cdk-overlay-pane").waitFor(
        new Locator.WaitForOptions().setState(PlaywrightPage.WaitForSelectorState.HIDDEN)
    );

    return this;
}
```

---

### Issue: Missing Null Check in `parseRate()` and `parseAmount()`

#### What is wrong
Although `validate()` checks for null/empty, if `textContent()` returns something unexpected, the split operations may fail unpredictably:

```java
private static double parseRate(String rateText) {
    validate(rateText, "Currency rate text");
    String[] parts = rateText.split("=");
    if (parts.length < 2) {
        throw new IllegalArgumentException(...);
    }
    return parseDouble(parts[1].trim().split(" ")[0], "currency rate");
}
```

The second split:

```java
split(" ")[0]
```

assumes at least one space after `=`.  
If format is different, this throws `ArrayIndexOutOfBoundsException`.

#### Why it matters
Weak error handling leads to flaky tests that fail unpredictably.  
Clear, descriptive exceptions are better for debugging.

#### How to fix it

```java
private static double parseRate(String rateText) {
    validate(rateText, "Currency rate text");
    String[] parts = rateText.split("=");

    if (parts.length < 2) {
        throw new IllegalArgumentException(
            "Invalid rate format. Expected 'X = Y', got: " + rateText
        );
    }

    String rateValue = parts[1].trim();
    String[] rateParts = rateValue.split("\\s+");

    if (rateParts.length == 0) {
        throw new IllegalArgumentException(
            "No rate value found after '='. Got: " + rateValue
        );
    }

    return parseDouble(rateParts[0], "currency rate");
}
```

---

### Issue: Assertion Tolerance (`0.5`) Is Vague — Business Logic Missing

#### What is wrong
Line **121**:

```java
Assert.assertEquals(actual, expected, 0.5,
    String.format("Conversion incorrect. Expected: %.2f, Actual: %.2f, Rate: %.4f", ...));
```

A tolerance of `0.5` is arbitrary.

Questions:
- Why `0.5`?
- Is this acceptable for currency exchange?
- Should it be percentage-based or fixed?

#### Why it matters
In **fintech / banking**, tolerance thresholds must be explicitly defined by business requirements.

An arbitrary tolerance hides rounding or calculation issues.

#### How to fix it

##### Fixed tolerance
```java
private static final double CONVERSION_TOLERANCE = 0.01; // ±1 cent for GEL-EUR
```

##### Or percentage-based tolerance
```java
double percentageError = Math.abs((actual - expected) / expected) * 100;

Assert.assertTrue(percentageError <= 1.0, // ±1%
    String.format("Conversion error exceeds 1%%: %.2f%%", percentageError));
```

---

## 🔍 Technical Analysis

The step definition is well-structured and uses modern patterns:

- fluent builders
- good error handling
- readable method chaining

However, it has **two critical issues**:

1. **Decimal truncation** directly violates known bug reports (**SCRUM-6**)
2. **Missing synchronization** after dropdown actions creates race conditions

These are not over-engineering concerns — they're **fundamental reliability issues** for an automation framework.

---

## 💡 Optimization Suggestions

- Wrap **"select and wait"** into a single method to avoid repetition
- Extract parsing logic into a separate utility class for reuse across API and UI steps
- Document assertion tolerances with business requirements

---

## ✅ Improved Code Example

```java
// Wrap dropdown selection with proper wait
@Step("Select from currency: {currency}")
public ConvertorSteps selectFromCurrency(String currency) {
    return selectCurrency(0, currency);
}

@Step("Select to currency: {currency}")
public ConvertorSteps selectToCurrency(String currency) {
    return selectCurrency(1, currency);
}

private ConvertorSteps selectCurrency(int index, String currency) {
    Locator currencyButton = index == 0 
        ? convertorPage.fromCurrencyButton 
        : convertorPage.toCurrencyButton;
    
    currencyButton.click();
    convertorPage.currencyItem(currency).scrollIntoViewIfNeeded();
    convertorPage.currencyItem(currency).click();
    
    // Wait for overlay to disappear
    convertorPage.page.locator(".cdk-overlay-pane").waitFor(
        new Locator.WaitForOptions().setState(PlaywrightPage.WaitForSelectorState.HIDDEN)
    );
    
    return this;
}

// Fix decimal truncation
@Step("Enter currency amount: {amount}")
public ConvertorSteps enterCurrencyAmount(double amount) {
    convertorPage.currencyInput1.fill(String.valueOf(amount));
    return this;
}

// Improve parsing robustness
private static double parseRate(String rateText) {
    validate(rateText, "Currency rate text");
    String[] parts = rateText.split("=");

    if (parts.length < 2) {
        throw new IllegalArgumentException(
            "Invalid rate format. Expected 'FROM = TO RATE', got: " + rateText
        );
    }

    String[] rateParts = parts[1].trim().split("\\s+");
    if (rateParts.length == 0) {
        throw new IllegalArgumentException(
            "No numeric rate found after '='. Got: " + parts[1]
        );
    }

    return parseDouble(rateParts[0], "currency rate");
}

// Document tolerance
@Step("Verify conversion for amount: {inputAmount}")
public ConvertorSteps verifyConversion(double inputAmount) {
    try {
        assertThat(convertorPage.currencyRate).isVisible();
        assertThat(convertorPage.currencyInput2).isVisible();

        double rate = parseRate(convertorPage.currencyRate.textContent());
        double actual = parseAmount(convertorPage.currencyInput2.inputValue());
        double expected = inputAmount * rate;
        
        // Tolerance: ±0.01 (1 cent) for GEL/EUR conversions
        // Adjust per business requirements
        Assert.assertEquals(actual, expected, 0.01,
            String.format("Conversion incorrect. Expected: %.2f, Actual: %.2f, Rate: %.4f",
                expected, actual, rate));
    } catch (IllegalArgumentException e) {
        Assert.fail(e.getMessage(), e);
    }

    return this;
}
```

---

## 📊 Scores

| Dimension | Score | Reason |
|----------|-------|--------|
| Stability | 6/10 | Race condition risk in dropdown selection; missing synchronization |
| Maintainability | 8/10 | Fluent pattern is excellent; error messages are clear; but decimal truncation is a bug |

---

## TL;DR

Excellent fluent pattern and error handling, but two critical issues must be fixed:

1. **Decimal truncation** violates **SCRUM-6**
2. **Missing dropdown synchronization** creates race conditions

Both are easy to fix but should be addressed before production.

---

# 3. BaseTest (Test Fixture)

## ✅ What's Good

- Proper setup/teardown isolation: `@BeforeClass` and `@AfterClass` with `alwaysRun=true`
- Correct use of **switch expressions** for browser/device mapping (**Java 14+** style, clean)
- Screenshot capture on failure is a good practice
- Cross-device support (**desktop 1920x1080, mobile 430x932**) is well-implemented

---

## ⚠️ Issues Found

### Issue: Hardcoded Values in Device Configuration — Not Scalable

#### What is wrong
Lines **99–105** hardcode viewport sizes for only two device types:

```java
return switch (deviceType) {
    case DESKTOP -> options.setViewportSize(1920, 1080);
    case MOBILE  -> options
            .setViewportSize(430, 932)
            .setIsMobile(true)
            .setHasTouch(true)
            .setDeviceScaleFactor(3);
    default -> throw new IllegalArgumentException(UNSUPPORTED + deviceType);
};
```

#### Why it matters
If you want to test on:
- tablet
- custom resolutions
- iPad
- Android variants

...you’d have to modify this core class.

#### How to fix it
Move device profiles to a configuration file or enum:

```java
public enum DeviceProfile {
    DESKTOP(1920, 1080, false, false, 1),
    MOBILE(430, 932, true, true, 3),
    TABLET(768, 1024, true, true, 2),
    IPAD_PRO(2048, 2732, true, true, 2);

    public final int width, height;
    public final boolean isMobile, hasTouch;
    public final double scaleFactor;

    DeviceProfile(int width, int height, boolean isMobile, boolean hasTouch, double scaleFactor) {
        this.width = width;
        this.height = height;
        this.isMobile = isMobile;
        this.hasTouch = hasTouch;
        this.scaleFactor = scaleFactor;
    }
}
```

---

### Issue: `acceptCookies()` Called Unconditionally — Fragile and May Mask Issues

#### What is wrong
Lines **71–72**:

```java
page.navigate(BASE_URI);
acceptCookies();
```

This assumes cookies dialog is always present.

#### Why it matters
If cookies dialog is:
- location-dependent
- conditionally shown
- already dismissed

...this creates a flaky test suite.

#### How to fix it

```java
page.navigate(BASE_URI);
acceptCookiesIfPresent();
```

### Example improvement in `CommonSteps`

```java
@Step("Accept cookies if present")
public CommonSteps acceptCookiesIfPresent() {
    try {
        commonPage.acceptCookiesButton.click(new Locator.ClickOptions().setTimeout(2000));
    } catch (PlaywrightException e) {
        // Cookies dialog not present or already dismissed
        log.debug("Cookies dialog not found; proceeding...");
    }
    return this;
}
```

---

### Issue: No Browser Configuration Flexibility — Hardcoded Headless

#### What is wrong
Line **50**:

```java
BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
    .setHeadless(true);
```

Headless is always `true`.

#### Why it matters
In CI, headless is correct.  
But for local debugging and troubleshooting, developers often need to run tests with the browser visible.

#### How to fix it

```java
BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
    .setHeadless(System.getProperty("headed") == null);
```

Now developers can run:

```bash
mvn test -Dheaded=true
```

---

### Issue: Cookie Acceptance Page Dependency — Test Independence Risk

#### What is wrong
The framework navigates to `BASE_URI` and immediately tries to accept cookies in every test.

#### Why it matters
If the cookie dialog changes:
- styles
- button text
- rendering delay

...all tests break.

#### How to fix it
Use **context-level setup** instead of UI-level setup:

```java
context = browser.newContext(buildContextOptions());
context.addCookies(List.of(
    new BrowserContext.AddCookiesPayload()
        .setName("cookies_accepted")
        .setValue("true")
        .setUrl(BASE_URI)
));

page = context.newPage();
page.navigate(BASE_URI);
```

---

## 🔍 Technical Analysis

The base test fixture is solid for initial setup but lacks flexibility.

Hardcoded values and assumptions such as:
- cookies always present
- headless always true

...reduce maintainability for a growing framework.

The cross-device support is good, but configuration should be externalized for easy extension.

---

## 💡 Optimization Suggestions

- Move device profiles to an enum or config file
- Add system properties for headless/headed and other launch options
- Use context-level cookie handling instead of UI-level
- Log browser/context creation for easier debugging

---

## ✅ Improved Code Example

```java
@BeforeClass(alwaysRun = true)
public void setUp() {
    playwright = Playwright.create();

    // Use system property for headless mode (default: true for CI)
    boolean headless = !Boolean.parseBoolean(System.getProperty("headed", "false"));
    
    BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(headless);

    BrowserType browserType = switch (browserName) {
        case CHROMIUM, CHROME, EDGE -> playwright.chromium();
        case FIREFOX                -> playwright.firefox();
        case WEBKIT, SAFARI         -> playwright.webkit();
        default -> throw new IllegalArgumentException(UNSUPPORTED + browserName);
    };

    if (EDGE.equals(browserName)) {
        launchOptions.setChannel("msedge");
    }

    browser = browserType.launch(launchOptions);
    context = browser.newContext(buildContextOptions());
    page = context.newPage();

    // Initialize step objects
    commonSteps = new CommonSteps(page);
    convertorSteps = new ConvertorSteps(page);
    commercialListApiCallSteps = new CommercialListApiCallSteps();
    exchangeRateApiCallSteps = new ExchangeRateApiCallSteps();
    apiSteps = new ForwardRatesApiCallSteps();

    // Navigate and handle cookies
    page.navigate(BASE_URI);
    commonSteps.acceptCookiesIfPresent();
    
    System.out.println(String.format(
        "Browser: %s | Device: %s | Headless: %s",
        browserName, deviceType, headless
    ));
}

private Browser.NewContextOptions buildContextOptions() {
    Browser.NewContextOptions options = new Browser.NewContextOptions();
    DeviceProfile profile = DeviceProfile.fromString(deviceType);

    return options
            .setViewportSize(profile.width, profile.height)
            .setIsMobile(profile.isMobile)
            .setHasTouch(profile.hasTouch)
            .setDeviceScaleFactor(profile.scaleFactor);
}

// Device profile enum
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

---

## 📊 Scores

| Dimension | Score | Reason |
|----------|-------|--------|
| Stability | 7/10 | Robust setup/teardown; but cookie handling is fragile; no headless flexibility |
| Maintainability | 6/10 | Hardcoded values; not easily extensible for new devices/configs |

---

## TL;DR

Solid base fixture with good setup/teardown isolation, but hardcoded values (device sizes, headless mode) and UI-dependent cookie handling limit scalability.

**Recommendation:** Externalize device profiles and add system properties for flexibility.

---

# 4. ConversionSwapTest (UI Test)

## ✅ What's Good

- Clear test structure with navigation as priority 1 and functional test as priority 2
- Good use of data providers (`currencyPairs`) for parametrization
- Proper use of Allure annotations (`@Epic`, `@Feature`, `@Story`, `@Severity`)
- Retry logic is in place (`@RetryAnalyzer`, `@RetryCount`) for flaky test mitigation

---

## ⚠️ Issues Found

### Issue: Test Coupling — Navigation Test Should Not Be a Separate Test

#### What is wrong
Lines **24–28** separate navigation into its own test with `priority = 1`:

```java
@Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
@RetryCount(count = 1)
public void navigateToCurrencyConvertorPage() {
    commonSteps
            .clickKebabMenu()
            .clickCurrencyLariOutlined();
}
```

#### Why it matters
This creates **test interdependency**.

The `currencySwap()` test assumes navigation has already happened.  
TestNG doesn't guarantee execution order across test methods.

#### Risks
- If navigation fails, the main test may still run
- Failures become confusing and indirect
- Execution order may become unpredictable

#### How to fix it
Move navigation into `@BeforeMethod`:

```java
@BeforeMethod
public void navigateToCurrencyConverter() {
    commonSteps
            .clickKebabMenu()
            .clickCurrencyLariOutlined();
}

@Story("Swap currencies and validate recalculation")
@Severity(SeverityLevel.CRITICAL)
@Test(dataProvider = "currencyPairs", dataProviderClass = CurrencyDataProvider.class,
      retryAnalyzer = RetryAnalyzer.class)
@RetryCount(count = 2)
public void currencySwap(String fromCurrency, String toCurrency, double amount) {
    convertorSteps
            .selectFromCurrency(fromCurrency)
            .selectToCurrency(toCurrency)
            .enterCurrencyAmount(amount)
            .verifyConversion(amount)
            .clickSwap()
            .validateSwap(toCurrency, fromCurrency, amount);
}
```

---

### Issue: Empty Swap Validation — SCRUM-5 Bug Not Tested

#### What is wrong
The test assumes swap always succeeds with valid currency and amount.

There’s no regression test for:

> **SCRUM-5:** Clicking the Swap button when the input is empty triggers a `"NaN"` error

#### Why it matters
This is a **known defect** and should have regression coverage.

#### How to fix it

```java
@Story("Swap with empty input should not show NaN")
@Severity(SeverityLevel.CRITICAL)
@Test(retryAnalyzer = RetryAnalyzer.class)
@RetryCount(count = 2)
public void swapWithEmptyInputShouldNotShowNaN() {
    convertorSteps
            .selectFromCurrency(GEL)
            .selectToCurrency(EUR)
            // Don't fill input — leave it empty
            .clickSwap()
            .validateSwapDoesNotShowNaN();
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

---

### Issue: No Decimal Input Test — SCRUM-6 Bug Not Validated

#### What is wrong
`enterCurrencyAmount()` accepts `double`, but tests only use integer values.

There’s no test validating:
- `0.5`
- `12.4`
- `23.5`

#### Why it matters
**SCRUM-6** explicitly states the app should accept decimals.

#### How to fix it

```java
@Story("Validate decimal input support")
@Severity(SeverityLevel.CRITICAL)
@Test(retryAnalyzer = RetryAnalyzer.class)
@RetryCount(count = 2)
public void decimalConversionValidation() {
    convertorSteps
            .selectFromCurrency(GEL)
            .selectToCurrency(EUR)
            .enterCurrencyAmount(12.5)  // decimal value
            .verifyConversion(12.5);
}
```

---

## 🔍 Technical Analysis

The test structure is clean and follows good practices:

- data-driven
- retries
- Allure annotations

But it violates a fundamental principle:

> **Each test should be independent**

Also, known bugs (**SCRUM-5**, **SCRUM-6**) do not have explicit regression tests.

---

## 💡 Optimization Suggestions

- Move navigation to `@BeforeMethod` or `BaseTest`
- Add regression tests for **SCRUM-5** and **SCRUM-6**
- Use test groups to organize tests by feature/bug

---

## ✅ Improved Code Example

```java
@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate Currency Swap and Recalculation - SCRUM-T1"})
public class ConversionSwapTest extends BaseTest {

    public ConversionSwapTest(String browser, String deviceType) {
        super(browser, deviceType);
    }

    @BeforeMethod(alwaysRun = true)
    public void navigateToCurrencyConverter() {
        commonSteps
                .clickKebabMenu()
                .clickCurrencyLariOutlined();
    }

    @Story("Swap currencies and validate recalculation")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "currencyPairs", dataProviderClass = CurrencyDataProvider.class,
          retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencySwap(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
                .selectFromCurrency(fromCurrency)
                .selectToCurrency(toCurrency)
                .enterCurrencyAmount(amount)
                .verifyConversion(amount)
                .clickSwap()
                .validateSwap(toCurrency, fromCurrency, amount);
    }

    @Story("Swap with empty input should not show NaN (SCRUM-5 regression)")
    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"SCRUM-5"}, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void swapWithEmptyInputShouldNotShowNaN() {
        convertorSteps
                .selectFromCurrency(GEL)
                .selectToCurrency(EUR)
                .clickSwap()
                .validateSwapDoesNotShowNaN();
    }

    @Story("Decimal input support (SCRUM-6 regression)")
    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"SCRUM-6"}, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void decimalConversionValidation() {
        convertorSteps
                .selectFromCurrency(GEL)
                .selectToCurrency(EUR)
                .enterCurrencyAmount(12.5)
                .verifyConversion(12.5);
    }
}
```

---

## 📊 Scores

| Dimension | Score | Reason |
|----------|-------|--------|
| Stability | 7/10 | Good use of retries and data providers, but navigation test coupling is risky |
| Maintainability | 6/10 | Clean structure, but missing regression tests for known bugs (SCRUM-5, SCRUM-6) |

---

## TL;DR

Good test structure with data-driven approach, but navigation as a separate test violates independence principle.

**Recommendation:**
- Move navigation to `@BeforeMethod`
- Add explicit regression tests for:
    - **SCRUM-5** → NaN on empty swap
    - **SCRUM-6** → decimal input support

---

# 5. Executor (Factory Pattern)

## ✅ What's Good

- Clean factory pattern: correctly uses `@Factory` to instantiate multiple test instances
- Proper parameter injection via `@Parameters`
- Supports cross-browser / cross-device matrix execution

---

## ⚠️ Issues Found

### Issue: No Separation of API vs UI Tests — Mixing Concerns

#### What is wrong
Lines **20–29** return both UI tests and API tests in a single factory:

```java
return new Object[]{
    new InvalidInputTest(browser, deviceType),
    new ConversionValidationTest(browser, deviceType),
    // ... UI tests ...
    new CommercialListApiCallTest(),  // API test without browser/deviceType
    new ForwardRatesApiCallTest(),
    new ExchangeRateApiCallTest(),
    new ExchangeRateApiCallRejectionTest()
};
```

#### Why it matters
API tests don’t need browser parameters, but they’re bundled with UI tests.

#### Problems this causes
- You can’t run **only UI tests**
- You can’t run **only API tests**
- CI execution becomes less efficient
- Test organization becomes less clean

#### How to fix it
Create separate factories or use TestNG groups.

### `UIExecutor`

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

### `APIExecutor`

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

---

### Issue: Hard to Run Subset of Tests — No Grouping Support

#### What is wrong
The factory returns all tests.  
You can’t easily run just:
- one browser/device combination
- one specific test class
- only UI / only API

#### Why it matters
During debugging, developers often want to run a single test quickly without full matrix execution.

#### How to fix it
Use **TestNG groups**:

```xml
<groups>
    <run>
        <include name="ui" />
        <include name="api" />
    </run>
</groups>
```

And annotate tests accordingly:

```java
@Test(groups = {"ui"})
@Test(groups = {"api"})
```

---

## 🔍 Technical Analysis

The factory is correct but **over-simplistic**.

Mixing API and UI tests in one factory reduces:
- CI flexibility
- test execution efficiency
- local debugging convenience

---

## 💡 Optimization Suggestions

- Split into separate factories for UI and API
- Use TestNG groups for finer-grained test selection
- Document which tests should run in CI vs local development

---

## ✅ Improved Code Example

### `UIExecutor.java`

```java
@Listeners(TestResultListener.class)
public class UIExecutor {
    @Factory
    @Parameters({BROWSER, DEVICETYPE})
    public Object[] createUITests(
            @Optional(CHROME) String browser,
            @Optional(DESKTOP) String deviceType) {
        System.out.println(String.format("Creating UI tests for %s on %s", browser, deviceType));
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

### `APIExecutor.java`

```java
public class APIExecutor {
    @Factory
    public Object[] createAPITests() {
        System.out.println("Creating API tests");
        return new Object[]{
                new CommercialListApiCallTest(),
                new ForwardRatesApiCallTest(),
                new ExchangeRateApiCallTest(),
                new ExchangeRateApiCallRejectionTest()
        };
    }
}
```

### `testNg.xml`

```xml
<test name="API Tests" verbose="2">
    <classes>
        <class name="ge.tbc.testautomation.APIExecutor" />
    </classes>
</test>

<test name="UI Tests - Chrome Desktop">
    <parameter name="browser" value="chrome" />
    <parameter name="deviceType" value="desktop" />
    <classes>
        <class name="ge.tbc.testautomation.UIExecutor" />
    </classes>
</test>

<test name="UI Tests - Firefox Mobile">
    <parameter name="browser" value="firefox" />
    <parameter name="deviceType" value="mobile" />
    <classes>
        <class name="ge.tbc.testautomation.UIExecutor" />
    </classes>
</test>
```

---

## 📊 Scores

| Dimension | Score | Reason |
|----------|-------|--------|
| Stability | 8/10 | Factory correctly instantiates tests; no stability issues |
| Maintainability | 6/10 | Mixing API/UI makes it hard to subset tests; lacks grouping |

---

## TL;DR

Factory pattern is correct, but mixing API and UI tests in one factory reduces CI flexibility.

**Recommendation:** Split into separate factories:
- `UIExecutor`
- `APIExecutor`

This allows independent execution and better test organization.

---

# 6. Cross-Framework Issues (Comprehensive Summary)

## ⚠️ Issue: Lack of Explicit Wait Strategies

### Details
Throughout the codebase, there are implicit reliances on Playwright’s auto-waiting, but no explicit synchronization for dynamic UI elements such as:
- dropdowns
- overlays
- async calls

### Impact
Cross-browser and network variations cause intermittent failures.

### Fix
Add explicit waits for state changes  
*(see `ConvertorSteps` improvements above)*

---

## ⚠️ Issue: No Logging / Debugging Support

### Details
There’s no logging framework (`Log4j`, `SLF4J`, etc.).  
Failed tests show minimal context.

### Impact
Debugging failures is hard, especially in CI.

### Fix

#### Add to `pom.xml`
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>
```

#### Use in steps
```java
private static final Logger log = LogManager.getLogger(ConvertorSteps.class);

@Step("Fill input field with amount: {amount}")
public ConvertorSteps fillInputField(String amount) {
    log.debug("Filling input field with: {}", amount);
    convertorPage.currencyInput1.clear();
    convertorPage.currencyInput1.fill(amount);
    return this;
}
```

---

## ⚠️ Issue: No Error Context in Failures

### Details
Assert messages don’t include:
- page state
- browser info
- device type

### Impact
Failures are hard to reproduce or debug, especially in parallel / multi-browser runs.

### Fix

```java
private void assertWithContext(boolean condition, String message) {
    if (!condition) {
        String context = String.format(
            "FAILED: %s | Browser: %s | Device: %s | URL: %s",
            message, browserName, deviceType, page.url()
        );
        Assert.fail(context);
    }
}
```

---

## ⚠️ Issue: Retry Logic Not Granular Enough

### Details
`@RetryCount` is applied at method level.

If **step 3 of 5** fails, the entire method retries.

### Impact
- Wastes time
- May mask flaky steps
- Makes root-cause harder to isolate

### Fix
Use step-level retry logic in the steps themselves:

```java
@Step("Select currency with retry: {currency}")
public ConvertorSteps selectFromCurrencyWithRetry(String currency) {
    return selectFromCurrencyWithRetry(currency, 3);
}

private ConvertorSteps selectFromCurrencyWithRetry(String currency, int maxRetries) {
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
        try {
            convertorPage.fromCurrencyButton.click();
            convertorPage.currencyItem(currency).scrollIntoViewIfNeeded();
            convertorPage.currencyItem(currency).click();

            // Wait for dropdown to close
            convertorPage.page.locator(".cdk-overlay-pane").waitFor(
                new Locator.WaitForOptions().setState(PlaywrightPage.WaitForSelectorState.HIDDEN)
            );

            return this;
        } catch (PlaywrightException e) {
            log.warn("Attempt {} failed: {}", attempt, e.getMessage());
            if (attempt == maxRetries) throw e;
        }
    }
    return this;
}
```

---

# Consolidated Scoring & Recommendations

| Component | Stability | Maintainability | Key Issues |
|----------|-----------|-----------------|------------|
| ConvertorPage | 6/10 | 7/10 | `nth()` positioning, mixed locators |
| ConvertorSteps | 6/10 | 8/10 | Decimal truncation, missing waits, SCRUM-5/6 |
| BaseTest | 7/10 | 6/10 | Hardcoded values, cookie handling |
| ConversionSwapTest | 7/10 | 6/10 | Test coupling, missing regression tests |
| Executor | 8/10 | 6/10 | API/UI mixed, no grouping |
| **Framework Overall** | **6.8/10** | **6.6/10** | Sync issues, logging gaps, retry limitations |

---

# Priority Action Items

## 🔴 CRITICAL
- Fix decimal truncation in `enterCurrencyAmount()` — violates **SCRUM-6**

## 🟡 HIGH
- Add explicit dropdown synchronization in `selectFromCurrency()` / `selectToCurrency()`
- Add regression tests for:
    - **SCRUM-5** → NaN on empty swap
    - **SCRUM-6** → decimal support

## 🟠 MEDIUM
- Move navigation to `@BeforeMethod` to ensure test independence
- Replace `nth()` selectors with semantic / role-based locators
- Split `Executor` into `UIExecutor` and `APIExecutor`

## ⚪ LOW
- Add logging framework and enhance error context in assertions
- Externalize device profiles to enum or config file

---

# Final Note

Ready for your next code review.  
Share any files or specific components you'd like analyzed!
