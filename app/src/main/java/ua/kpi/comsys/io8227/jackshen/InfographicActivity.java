package ua.kpi.comsys.io8227.jackshen;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.jackandphantom.customtogglebutton.CustomToggle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfographicActivity extends AppCompatActivity {
    String clickedButton;
    CustomToggle customToggle;
    PieChart pieChart;
    Button interGraph;
    EditText valueA, valueB, valueN;
    TextView amountX, semicolon, boundariesX, formulae, myFunc, polynomial, interpolation, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_infographic_main);

        pieChart = findViewById(R.id.pieChart_view);
        interGraph = findViewById(R.id.show_cos);

        valueA = findViewById(R.id.value_a);
        valueB = findViewById(R.id.value_b);
        valueN = findViewById(R.id.value_n);

        amountX = findViewById(R.id.amount);
        boundariesX = findViewById(R.id.boundaries);
        formulae = findViewById(R.id.formulae);
        myFunc = findViewById(R.id.function);
        polynomial = findViewById(R.id.polynomial);
        interpolation = findViewById(R.id.interpolation);
        semicolon = findViewById(R.id.semicolon);
        title = findViewById(R.id.title_graph);

        customToggle = findViewById(R.id.switch_infograph);
        customToggle.setAnimationTime(700);
        customToggle.setOnToggleClickListener(new CustomToggle.OnToggleClickListener() {
            @Override
            public void onLefToggleEnabled(boolean enabled) {
                pieChart.setVisibility(View.GONE);

                title.setText("Line Chart (Interpolation)");
                interGraph.setVisibility(View.VISIBLE);
                valueA.setVisibility(View.VISIBLE);
                valueB.setVisibility(View.VISIBLE);
                valueN.setVisibility(View.VISIBLE);
                semicolon.setVisibility(View.VISIBLE);
                amountX.setVisibility(View.VISIBLE);
                boundariesX.setVisibility(View.VISIBLE);
                formulae.setVisibility(View.VISIBLE);
                myFunc.setVisibility(View.VISIBLE);
                polynomial.setVisibility(View.VISIBLE);
                interpolation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRightToggleEnabled(boolean enabled) {
                showPieChart();

                interGraph.setVisibility(View.GONE);
                valueA.setVisibility(View.GONE);
                valueB.setVisibility(View.GONE);
                valueN.setVisibility(View.GONE);
                semicolon.setVisibility(View.GONE);
                amountX.setVisibility(View.GONE);
                boundariesX.setVisibility(View.GONE);
                formulae.setVisibility(View.GONE);
                myFunc.setVisibility(View.GONE);
                polynomial.setVisibility(View.GONE);
                interpolation.setVisibility(View.GONE);

                title.setText("Pie Chart");
                pieChart.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            customToggle.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            customToggle.setVisibility(View.VISIBLE);
        }
    }

    private void showPieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Blue", 45);
        typeAmountMap.put("Violet", 5);
        typeAmountMap.put("Yellow",25);
        typeAmountMap.put("Grey", 25);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#bf66e5"));
        colors.add(Color.parseColor("#2196f3"));
        colors.add(Color.parseColor("#ffc107"));
        colors.add(Color.parseColor("#9e9e9e"));

        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.parseColor("#141518"));
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);

        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.parseColor("#141518"));
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.parseColor("#80000000"));
        pieChart.setHoleRadius(60);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(15);
        legend.setFormToTextSpace(2);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }


    public void showGraphics(View v) {
        clickedButton = ((Button)findViewById(v.getId())).getText().toString();
        Intent intent = new Intent(this, InterpolateGraphActivity.class);

        String val_a = valueA.getText().toString();
        String val_b = valueB.getText().toString();
        String val_n = valueN.getText().toString();

        if (val_a.equals("") || val_b.equals("") || val_n.equals("")) {
            startActivity(intent);
        } else {
            try {
                int a = Integer.parseInt(val_a);
                int b = Integer.parseInt(val_b);
                int n = Integer.parseInt(val_n);

                if (a >= b) {
                    Toast.makeText(this, "а >= b!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("a", a);
                    intent.putExtra("b", b);
                    intent.putExtra("n", n);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(this, "You have entered incorrect data!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
