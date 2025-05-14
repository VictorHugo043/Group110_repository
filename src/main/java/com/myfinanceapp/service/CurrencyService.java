package com.myfinanceapp.service;

/**
 * Service class for handling currency conversions and currency-related operations.
 * This service provides functionality for:
 * - Converting amounts between different currencies (USD, CNY, EUR)
 * - Managing the user's selected currency preference
 * - Using fixed exchange rates for conversions
 * 
 * The service uses USD as the base currency for all conversions.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class CurrencyService {
    // Fixed exchange rates with USD as the base currency
    private static final double USD_TO_CNY = 7.1;
    private static final double USD_TO_EUR = 0.95;
    private static final double EUR_TO_USD = 1.0526;
    private static final double CNY_TO_USD = 0.1408;

    private String selectedCurrency;

    /**
     * Constructs a new CurrencyService instance with the specified initial currency.
     * If no currency is specified, defaults to USD.
     *
     * @param initialCurrency The initial currency to use (USD, CNY, or EUR)
     */
    public CurrencyService(String initialCurrency) {
        this.selectedCurrency = initialCurrency != null ? initialCurrency : "USD";
    }

    /**
     * Gets the currently selected currency.
     *
     * @return The currently selected currency code (USD, CNY, or EUR)
     */
    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    /**
     * Sets the currency to use for conversions and display.
     *
     * @param currency The currency code to set (USD, CNY, or EUR)
     */
    public void setSelectedCurrency(String currency) {
        this.selectedCurrency = currency;
    }

    /**
     * Converts an amount from the source currency to the selected currency.
     * The conversion is done through USD as the base currency.
     *
     * @param amount The amount to convert
     * @param sourceCurrency The currency of the amount (USD, CNY, or EUR)
     * @return The converted amount in the selected currency
     */
    public double convertCurrency(double amount, String sourceCurrency) {
        if (sourceCurrency.equals(selectedCurrency)) {
            return amount;
        }

        // First convert to USD as the base currency
        double amountInUSD;
        switch (sourceCurrency) {
            case "CNY":
                amountInUSD = amount * CNY_TO_USD;
                break;
            case "EUR":
                amountInUSD = amount * EUR_TO_USD;
                break;
            case "USD":
            default:
                amountInUSD = amount;
                break;
        }

        // Then convert from USD to the selected currency
        switch (selectedCurrency) {
            case "CNY":
                return amountInUSD * USD_TO_CNY;
            case "EUR":
                return amountInUSD * USD_TO_EUR;
            case "USD":
            default:
                return amountInUSD;
        }
    }
}