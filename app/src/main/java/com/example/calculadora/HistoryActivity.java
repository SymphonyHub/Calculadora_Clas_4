package com.example.calculadora;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView tvHistory = findViewById(R.id.tvHistory);
        Button btnBackToCalculator = findViewById(R.id.btnBackToCalculator);

        ArrayList<String> history = getIntent().getStringArrayListExtra("history");
        if (history != null && !history.isEmpty()) {
            StringBuilder historyText = new StringBuilder();
            for (String entrada : history) {
                historyText.append(entrada).append("\n");
            }
            tvHistory.setText(historyText.toString());
        } else {
            tvHistory.setText("No hay historial");
        }

        btnBackToCalculator.setOnClickListener(v -> finish());
    }
}
