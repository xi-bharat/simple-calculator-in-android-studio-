package com.calculator.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView inputTextView, outputTextView;
    private String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTextView = findViewById(R.id.input);
        outputTextView = findViewById(R.id.output);

        setNumericButtons();
        setOperatorButtons();
        setSpecialButtons();
    }

    private void setNumericButtons() {
        int[] numericButtonIds = {
                R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4,
                R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9
        };

        View.OnClickListener listener = view -> {
            Button button = (Button) view;
            input += button.getText().toString();
            inputTextView.setText(input);
        };

        for (int id : numericButtonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorButtons() {
        int[] operatorButtonIds = {
                R.id.plus, R.id.minus, R.id.multiply, R.id.divide, R.id.modulo
        };

        View.OnClickListener listener = view -> {
            Button button = (Button) view;
            if (!input.isEmpty() && isValidOperator(input.charAt(input.length() - 1))) {
                input += button.getText().toString();
                inputTextView.setText(input);
            }
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setSpecialButtons() {

        findViewById(R.id.equal).setOnClickListener(view -> {
            if (!input.isEmpty()) {
                try {
                    double result = evaluateExpression(input);
                    outputTextView.setText(String.valueOf(result));
                } catch (Exception e) {
                    outputTextView.setText("Error");
                }
            }
        });

        findViewById(R.id.ac).setOnClickListener(view -> {
            input = "";
            inputTextView.setText("");
            outputTextView.setText("");
        });

        findViewById(R.id.del).setOnClickListener(view -> {
            if (!input.isEmpty()) {
                input = input.substring(0, input.length() - 1);
                inputTextView.setText(input);
            }
        });

        findViewById(R.id.dot).setOnClickListener(view -> {
            if (!input.isEmpty() && !input.endsWith(".")) {
                input += ".";
                inputTextView.setText(input);
            }
        });
    }

    private boolean isValidOperator(char c) {
        return c != '+' && c != '-' && c != 'x' && c != '÷' && c != '%';
    }

    private double evaluateExpression(String expression) {
        // Replace custom operators with standard ones
        expression = expression.replace("x", "*").replace("÷", "/");

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(number.toString()));
                i--;
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    char op = operators.pop();
                    numbers.push(performOperation(a, b, op));
                }
                operators.push(c);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            double b = numbers.pop();
            double a = numbers.pop();
            char op = operators.pop();
            numbers.push(performOperation(a, b, op));
        }

        return numbers.pop();
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    private double performOperation(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return b != 0 ? a / b : 0;
            case '%':
                return a % b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}