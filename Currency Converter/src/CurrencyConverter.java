import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyConverter {

    // Replace with your API URL and Key
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/01bd40dc0cc458ae1549e165/latest/";

    public static void main(String[] args) {
        // Map to store currency codes
        HashMap<Integer, String> currencyCodes = new HashMap<>();
        currencyCodes.put(1, "USD");
        currencyCodes.put(2, "CAD");
        currencyCodes.put(3, "EUR");
        currencyCodes.put(4, "HKD");
        currencyCodes.put(5, "INR");

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Currency Converter!");

        // Select currency to convert from
        System.out.println("Currency converting FROM?");
        displayOptions(currencyCodes);
        int fromChoice = sc.nextInt();
        if (!currencyCodes.containsKey(fromChoice)) {
            System.out.println("Invalid choice. Exiting.");
            sc.close();
            return;
        }
        String fromCode = currencyCodes.get(fromChoice);

        // Select currency to convert to
        System.out.println("Currency converting TO?");
        displayOptions(currencyCodes);
        int toChoice = sc.nextInt();
        if (!currencyCodes.containsKey(toChoice)) {
            System.out.println("Invalid choice. Exiting.");
            sc.close();
            return;
        }
        String toCode = currencyCodes.get(toChoice);

        // Enter the amount to convert
        System.out.println("Enter the amount you wish to convert:");
        double amount = sc.nextDouble();

        // Perform conversion
        double convertedAmount = convertCurrency(fromCode, toCode, amount);
        if (convertedAmount >= 0) {
            System.out.printf("%.2f %s is equivalent to %.2f %s.%n", amount, fromCode, convertedAmount, toCode);
        } else {
            System.out.println("Error retrieving exchange rates. Please try again later.");
        }

        sc.close();
    }

    // Function to display currency options
    private static void displayOptions(HashMap<Integer, String> currencyCodes) {
        currencyCodes.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    // Function to perform the currency conversion using the API
    private static double convertCurrency(String fromCode, String toCode, double amount) {
        try {
            // Construct the API URL
            String urlStr = API_URL + fromCode;
            URL url = new URL(urlStr);

            // Open connection and set HTTP method
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject rates = jsonResponse.getJSONObject("conversion_rates");

            // Get the exchange rate for the target currency
            if (rates.has(toCode)) {
                double exchangeRate = rates.getDouble(toCode);
                return amount * exchangeRate;
            } else {
                System.out.println("Currency code not found in rates.");
                return -1;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }
}
