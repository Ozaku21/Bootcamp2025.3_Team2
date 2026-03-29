package ge.tbc.testautomation.data;
import org.testng.annotations.DataProvider;
import static ge.tbc.testautomation.data.Constants.AMOUNT_TO_CONVERT;

public class CurrencyDataProvider {
    //Provider to check if the different currency conversion works (same amount different currencies)
    @DataProvider(name = "currencyPairs")
    public static Object[][] getCurrencyPairs() {
        return new Object[][] {
                {"USD", "EUR", AMOUNT_TO_CONVERT},
                {"USD", "GBP", AMOUNT_TO_CONVERT},
                {"EUR", "USD", AMOUNT_TO_CONVERT},
                {"EUR", "GBP", AMOUNT_TO_CONVERT},
                {"GBP", "USD", AMOUNT_TO_CONVERT},
                {"GBP", "EUR", AMOUNT_TO_CONVERT}
        };
    }

    //Provider to check if the different amount conversion works (same currency different amounts)
    @DataProvider(name = "currencyAmounts")
    public static Object[][] getCurrencyAmounts() {
        return new Object[][] {
                {"USD", "EUR", 50.0},
                {"USD", "EUR", 100.0},
                {"USD", "EUR", AMOUNT_TO_CONVERT},
                {"USD", "EUR", 500.0},
                {"USD", "EUR", 1000.0}
        };
    }
}
