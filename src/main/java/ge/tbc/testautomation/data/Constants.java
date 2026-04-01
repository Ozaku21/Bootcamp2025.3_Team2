package ge.tbc.testautomation.data;

public class Constants {

    // ── Base ──────────────────────────────────────────────────────────
    public static final String BASE_URI          = "https://tbcbank.ge/en";
    public static final double AMOUNT_TO_CONVERT = 217.0;
    public static final String INVALID_AMOUNT    = "-265";
    public static final String AMOUNT           = "9999999993339999999999999988";
    public static final String NON_NUMERIC = "non-numeric";

    // ── Browsers ──────────────────────────────────────────────────────
    public static final String BROWSER    = "browser";
    public static final String CHROMIUM   = "chromium";
    public static final String CHROME     = "chrome";
    public static final String FIREFOX    = "firefox";
    public static final String WEBKIT     = "webkit";
    public static final String SAFARI     = "safari";
    public static final String EDGE       = "edge";
    public static final String UNSUPPORTED = "Unsupported browser: ";

    // ── Device Types ──────────────────────────────────────────────────
    public static final String DEVICETYPE = "deviceType";
    public static final String DESKTOP    = "desktop";
    public static final String MOBILE     = "mobile";

    // ── Currencies ────────────────────────────────────────────────────
    public static final String GEL = "GEL";
    public static final String EUR = "EUR";

    // ── Api ────────────────────────────────────────────────────
    public static final String TBC_BASE_URL = "https://apigw.tbcbank.ge";
    public static final String TBC_COMMERCIAL_LIST_BASE_PATH = "/api/v1/exchangeRates/commercialList";
    public static final String TBC_EXCHANGE_RATE_BASE_PATH = "/api/v1/exchangeRates/getExchangeRate";
    public static final String TBC_FORWARD_RATES_BASE_PATH = "/api/v1/forwardRates/getForwardRates";

    public static final String CURRENCY_NAME = "პოლონური ზლოტი";
}
