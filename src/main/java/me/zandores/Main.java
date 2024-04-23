package me.zandores;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static List<Pizza> pizzas;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Main <csvFilePath> <metric1> <metric2> ...\n");
            return;
        }

        String csvFile = args[0];
        if (!Files.exists(Paths.get(csvFile))) {
            System.out.printf("File '%s' not found.\n", csvFile);
            return;
        }

        pizzas = parseCSV(csvFile);
        if (!pizzas.isEmpty()) {
            for (int i = 1; i < args.length; i++) {
                processMetric(args[i]);
            }
        }
    }

    private static List<Pizza> parseCSV(String csvFilePath) {
        List<Pizza> pizzas = new ArrayList<>();
        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build().parse(reader)) {
            for (CSVRecord csvRecord : csvParser) {
                // Parse each record into a Pizza object
                Pizza pizza = new Pizza(
                        (int) Double.parseDouble(csvRecord.get("pizza_id")),
                        csvRecord.get("order_id"),
                        csvRecord.get("pizza_name_id"),
                        (int) Double.parseDouble(csvRecord.get("quantity")),
                        csvRecord.get("order_date"),
                        csvRecord.get("order_time"),
                        Double.parseDouble(csvRecord.get("unit_price")),
                        Double.parseDouble(csvRecord.get("total_price")),
                        csvRecord.get("pizza_size"),
                        csvRecord.get("pizza_category"),
                        csvRecord.get("pizza_ingredients"),
                        csvRecord.get("pizza_name")
                );
                pizzas.add(pizza);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.printf("Error parsing CSV file: %s\n", e.getMessage());
        }
        return pizzas;
    }

    static class Pizza {
        private final int pizzaId;
        private final String orderId;
        private final String pizzaNameId;
        private final int quantity;
        private final String orderDate;
        private final String orderTime;
        private final double unitPrice;
        private final double totalPrice;
        private final String pizzaSize;
        private final String pizzaCategory;
        private final String pizzaIngredients;
        private final String pizzaName;

        public Pizza(int pizzaId, String orderId, String pizzaNameId, int quantity, String orderDate, String orderTime,
                     double unitPrice, double totalPrice, String pizzaSize, String pizzaCategory,
                     String pizzaIngredients, String pizzaName) {
            this.pizzaId = pizzaId;
            this.orderId = orderId;
            this.pizzaNameId = pizzaNameId;
            this.quantity = quantity;
            this.orderDate = orderDate;
            this.orderTime = orderTime;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.pizzaSize = pizzaSize;
            this.pizzaCategory = pizzaCategory;
            this.pizzaIngredients = pizzaIngredients;
            this.pizzaName = pizzaName;
        }

        // Getters and Setters

        @Override
        public String toString() {
            return "Pizza{" +
                    "pizzaId=" + pizzaId +
                    ", orderId='" + orderId + '\'' +
                    ", pizzaNameId='" + pizzaNameId + '\'' +
                    ", quantity=" + quantity +
                    ", orderDate='" + orderDate + '\'' +
                    ", orderTime='" + orderTime + '\'' +
                    ", unitPrice=" + unitPrice +
                    ", totalPrice=" + totalPrice +
                    ", pizzaSize='" + pizzaSize + '\'' +
                    ", pizzaCategory='" + pizzaCategory + '\'' +
                    ", pizzaIngredients='" + pizzaIngredients + '\'' +
                    ", pizzaName='" + pizzaName + '\'' +
                    '}';
        }

        // Method to get or set various fields based on the provided parameter
        public Object field(String field) {
            return switch (field) {
                case "pizzaId" -> this.pizzaId;
                case "orderId" -> this.orderId;
                case "pizzaNameId" -> this.pizzaNameId;
                case "quantity" -> this.quantity;
                case "orderDate" -> this.orderDate;
                case "orderTime" -> this.orderTime;
                case "unitPrice" -> this.unitPrice;
                case "totalPrice" -> this.totalPrice;
                case "pizzaSize" -> this.pizzaSize;
                case "pizzaCategory" -> this.pizzaCategory;
                case "pizzaIngredients" -> this.pizzaIngredients;
                case "pizzaName" -> this.pizzaName;
                default -> null;
            };
        }
    }

    private static void processMetric(String metric) {
        switch (metric.toLowerCase()) {
            case "pms":
                findMostOrderedPizza();
                break;
            case "pls":
                findLeastOrderedPizza();
                break;
            case "dms":
                findMostRevenueDay();
                break;
            case "dls":
                findLeastRevenueDay();
                break;
            case "dmsp":
                findMostSoldPizza();
                break;
            case "dlsp":
                findLeastSoldPizza();
                break;
            case "apo":
                findAverageOrderedPizzasPerOrder();
                break;
            case "apd":
                findAverageOrderedPizzasPerDay();
                break;
            case "ims":
                findMostOrderedIngredient();
                break;
            case "hp":
                findMostOrderedPizzaCategory();
                break;
            default:
                System.out.printf("Command '%s' not found.\n", metric);
                break;
        }
    }

    private static void filterPizzas(String message, String most_or_least, String getValue, String filter) {
        Map<String, Double> dictionary = new HashMap<>();
        for (Pizza pizza : pizzas) {
            String field = (String) pizza.field(getValue);
            double value = dictionary.getOrDefault(field, 0.0);
            Object fieldValue = pizza.field(filter);
            if (fieldValue instanceof Double) {
                value += (double) pizza.field(filter);
            } else if (fieldValue instanceof Integer) {
                value += ((Integer) fieldValue).doubleValue();
            }
            dictionary.put(field, value);
        }

        String key = "";
        double value = switch (most_or_least.toLowerCase()) {
            case "most" -> {
                key = Collections.max(dictionary.entrySet(), Map.Entry.comparingByValue()).getKey();
                yield dictionary.get(key);
            }
            case "least" -> {
                key = Collections.min(dictionary.entrySet(), Map.Entry.comparingByValue()).getKey();
                yield dictionary.get(key);
            }
            case "average" -> {
                double sum = dictionary.values().stream().mapToDouble(Double::doubleValue).sum();
                yield sum / dictionary.size();
            }
            default -> 0.0;
        };

        if (!key.isBlank()) {
            System.out.printf(message + "\n", key, value);
        } else {
            System.out.printf(message + "\n", value);
        }
    }

    // metrics functions
    private static void findMostOrderedPizza() {
        filterPizzas("The most ordered pizza is %s.", "most", "pizzaName", "quantity");
    }
    private static void findLeastOrderedPizza() {
        filterPizzas("The least ordered pizza is %s.", "least", "pizzaName", "quantity");
    }
    private static void findMostRevenueDay() {
        filterPizzas("The date with the most revenue is %s with a total of $%.2f.", "most", "orderDate", "totalPrice");
    }
    private static void findLeastRevenueDay() {
        filterPizzas("The date with the least revenue is %s with a total of $%.2f.", "least", "orderDate", "totalPrice");
    }
    private static void findMostSoldPizza() {
        filterPizzas("The date with the most sold pizzas is %s with a total of %.0f.", "most", "orderDate", "quantity");
    }
    private static void findLeastSoldPizza() {
        filterPizzas("The date with the least sold pizzas is %s with a total of %.0f.", "least", "orderDate", "quantity");
    }
    private static void findAverageOrderedPizzasPerOrder() {
        filterPizzas("The average ordered pizzas per order is %.0f.", "average", "orderId", "quantity");
    }
    private static void findAverageOrderedPizzasPerDay() {
        filterPizzas("The average ordered pizzas per day is %.0f.", "average", "orderDate", "quantity");
    }
    private static void findMostOrderedIngredient() {
        String message = "The most ordered ingredient is %s.";
        Map<String, Integer> dictionary = new HashMap<>();
        for (Pizza pizza : pizzas) {
            String[] ingredientsArray = ((String) pizza.field("pizzaIngredients")).split(", ");
            for (String ingredient : ingredientsArray) {
                int amount = 0;
                if (dictionary.get(ingredient) != null) {
                    amount = dictionary.get(ingredient);
                }
                amount += (int) pizza.field("quantity");
                dictionary.put(ingredient, amount);
            }
        }

        String key = Collections.max(dictionary.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf(message + "\n", key);

        dictionary.clear();
    }
    private static void findMostOrderedPizzaCategory() {
        filterPizzas("The most ordered pizza category is %s.", "most", "pizzaCategory", "quantity");
    }

}
