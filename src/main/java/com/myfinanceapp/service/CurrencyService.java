package com.myfinanceapp.service;

public class CurrencyService {
    // Fixed exchange rates with USD as the base currency
    private static final double USD_TO_CNY = 7.1;
    private static final double USD_TO_EUR = 0.95;
    private static final double EUR_TO_USD = 1.0526;
    private static final double CNY_TO_USD = 0.1408;

    private String selectedCurrency;

    public CurrencyService(String initialCurrency) {
        this.selectedCurrency = initialCurrency != null ? initialCurrency : "USD";
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String currency) {
        this.selectedCurrency = currency;
    }

    /**
     * Converts an amount from the source currency to the selected currency.
     * @param amount The amount to convert
     * @param sourceCurrency The currency of the amount
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