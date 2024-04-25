# Pizza Order Analysis Project (Java)

## Usage

1. **Compilation**: Employ a Java compiler for program compilation, such as **Maven** or **Gradle**.

2. **Execution**: Run the compiled program with the following command:

    ```
    java -jar <filename> <command1> <command2> ...
    ```

   Replace `<filename>` with the path to the CSV file containing pizza orders and `<command1> <command2> ...` with the desired commands (pms for most ordered pizza, pls for least ordered pizza).

   Example:

    ```
    java -jar app2.jar pizza_sales.csv pms pls
    ```

   This command will analyze the `pizza_sales.csv` file and print the most and least ordered pizzas.

## File Format

The CSV file should have the following format:

- Each line represents a single pizza order.
- Fields are separated by commas.
- The fields are as follows:

    1. Pizza ID
    2. Order ID
    3. Pizza Name ID
    4. Quantity
    5. Order Date (MM-DD-YYYY)
    6. Order Time (HH:MM:SS)
    7. Unit Price
    8. Total Price
    9. Pizza Size
    10. Pizza Category
    11. Pizza Ingredients
    12. Pizza Name

  Example:

    ```
    1,1,margherita,2,01-01-2024,12:00:00,10.00,20.00,Medium,Vegetarian,"Tomato, Cheese, Olives",Margherita
    2,2,meat_feast,1,01-01-2024,12:15:00,12.00,12.00,Large,Meat Lovers,"Tomato, Cheese, Pepperoni, Sausage, Bacon",Meat Feast
    ```

## Output

The program will output the name of the most and least ordered pizzas based on the given commands.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
