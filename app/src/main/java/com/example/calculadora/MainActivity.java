package com.example.calculadora;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private EditText etInput;
    private ArrayList<String> history = new ArrayList<>();
    private double currentResult = 0;
    private boolean isLastResultUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        etInput = findViewById(R.id.etInput);

        setupButtons();
    }

    // Función para configurar los botones
    private void setupButtons() {
        Button btnSum = findViewById(R.id.btnSum);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnEqual = findViewById(R.id.btnEqual);

        btnSum.setOnClickListener(v -> handleOperation("+"));
        btnSubtract.setOnClickListener(v -> handleOperation("-"));
        btnMultiply.setOnClickListener(v -> handleOperation("*"));
        btnDivide.setOnClickListener(v -> handleOperation("/"));
        btnHistory.setOnClickListener(v -> navigateToHistory());
        btnClear.setOnClickListener(v -> clearCalculator());
        btnEqual.setOnClickListener(v -> calculateResult());
    }

    private void navigateToHistory() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        intent.putStringArrayListExtra("history", history);
        startActivity(intent);
    }

    private void handleOperation(String operator) {
        if (isLastResultUsed) {
            etInput.setText(formatResult(currentResult) + operator);
            isLastResultUsed = false;
        } else {
            etInput.append(operator);
        }
    }

    private void calculateResult() {
        String inputText = etInput.getText().toString();
        if (!inputText.isEmpty()) {
            try {
                currentResult = evaluateExpression(inputText);
                tvResult.setText(formatResult(currentResult));
                history.add(inputText + " = " + formatResult(currentResult));
                etInput.setText("");
                isLastResultUsed = true;
            } catch (Exception e) {
                etInput.setError("Operación inválida");
            }
        } else {
            etInput.setError("Ingresa una operación");
        }
    }

    private String formatResult(double result) {
        return (result == (int) result) ? String.valueOf((int) result) : String.valueOf(result);
    }

    private void clearCalculator() {
        currentResult = 0;
        tvResult.setText(formatResult(currentResult));
        history.clear();
    }

    private double evaluateExpression(String expression) {
        try {
            expression = expression.replaceAll("\\s+", "");
            ArrayList<String> tokens = tokenizeExpression(expression);

            ArrayList<String> processedTokens = processMultiplicationAndDivision(tokens);
            return processAdditionAndSubtraction(processedTokens);
        } catch (Exception e) {
            throw new IllegalArgumentException("Expresión inválida");
        }
    }

    // Aquí es donde colocamos la parte modificada para tokenizar la expresión correctamente
    private ArrayList<String> tokenizeExpression(String expression) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();
        boolean lastWasOperator = true;  // Para detectar si el último token fue un operador

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                // Si es un número o un punto decimal, añadirlo al número actual
                currentNumber.append(c);
                lastWasOperator = false;
            } else if (c == '-' && lastWasOperator) {
                // Si es un '-' y el último token fue un operador, se trata de un número negativo
                currentNumber.append(c);
            } else {
                // Si hay un número actual, agregarlo como token
                if (currentNumber.length() > 0) {
                    tokens.add(currentNumber.toString());
                    currentNumber = new StringBuilder();
                }
                // Agregar el operador como un token
                tokens.add(String.valueOf(c));
                lastWasOperator = true;
            }
        }

        // Añadir el último número que haya quedado sin agregar
        if (currentNumber.length() > 0) {
            tokens.add(currentNumber.toString());
        }

        return tokens;
    }

    private ArrayList<String> processMultiplicationAndDivision(ArrayList<String> tokens) {
        // Manejar las operaciones de multiplicación y división primero
        ArrayList<String> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("*") || token.equals("/")) {
                double left = Double.parseDouble(processedTokens.remove(processedTokens.size() - 1));
                double right = Double.parseDouble(tokens.get(++i));
                double result = token.equals("*") ? left * right : left / right;
                processedTokens.add(String.valueOf(result));
            } else {
                processedTokens.add(token);
            }
        }
        return processedTokens;
    }

    private double processAdditionAndSubtraction(ArrayList<String> tokens) {
        // Manejar las operaciones de suma y resta
        double result = Double.parseDouble(tokens.get(0));
        for (int i = 1; i < tokens.size(); i += 2) {
            String operator = tokens.get(i);
            double value = Double.parseDouble(tokens.get(i + 1));
            result = operator.equals("+") ? result + value : result - value;
        }
        return result;
    }
}
