package com.buysmart.ai.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityPriceHistoryBinding;
import com.buysmart.ai.models.PriceHistory;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.DummyData;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PriceHistoryActivity extends AppCompatActivity {

    private ActivityPriceHistoryBinding binding;
    private Product product;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat axisFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPriceHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        product = getIntent().getParcelableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        setupToolbar();
        displayProductInfo();
        loadHistoryData();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayProductInfo() {
        binding.tvProductName.setText(product.getName());
    }

    private void loadHistoryData() {
        // Fetch 30 days of data
        PriceHistory history = DummyData.getPriceHistory(product.getProductId(), "Amazon");
        List<PriceHistory.PricePoint> points = history.getPricePoints();
        
        if (points == null || points.isEmpty()) return;

        calculateAndDisplayStats(points);
        setupChart(points);
        setupLogList(points);
    }

    private void calculateAndDisplayStats(List<PriceHistory.PricePoint> points) {
        double low = Double.MAX_VALUE;
        double high = Double.MIN_VALUE;
        
        for (PriceHistory.PricePoint p : points) {
            if (p.getPrice() < low) low = p.getPrice();
            if (p.getPrice() > high) high = p.getPrice();
        }

        // Display stats
        binding.tvMonthLow.setText(String.format("₹%,.0f", low));
        binding.tvMonthHigh.setText(String.format("₹%,.0f", high));
        
        // Mock All Time Low (slightly lower than month low for realism)
        double allTimeLow = Math.min(low, product.getLowestPrice() * 0.9);
        binding.tvAllTimeLow.setText(String.format("₹%,.0f", allTimeLow));
    }

    private void setupChart(List<PriceHistory.PricePoint> points) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            entries.add(new Entry(i, (float) points.get(i).getPrice()));
        }

        int primaryColor = ContextCompat.getColor(this, R.color.primary);

        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(primaryColor);
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(primaryColor);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setCircleHoleColor(Color.WHITE);
        
        // Modern Curve
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        
        // Gradient Fill
        dataSet.setDrawFilled(true);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{primaryColor, Color.TRANSPARENT});
        dataSet.setFillDrawable(gradient);
        dataSet.setFillAlpha(50);
        
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        binding.chartFullHistory.setData(lineData);

        // Chart Styling
        binding.chartFullHistory.getDescription().setEnabled(false);
        binding.chartFullHistory.getLegend().setEnabled(false);
        binding.chartFullHistory.setTouchEnabled(true);
        binding.chartFullHistory.setDragEnabled(true);
        binding.chartFullHistory.setScaleEnabled(true);
        binding.chartFullHistory.setPinchZoom(true);
        binding.chartFullHistory.animateX(1000);

        XAxis xAxis = binding.chartFullHistory.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(4);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < points.size()) {
                    return axisFormat.format(new Date(points.get(index).getTimestamp()));
                }
                return "";
            }
        });

        YAxis leftAxis = binding.chartFullHistory.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.outline_variant));
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "₹" + (int) value;
            }
        });

        binding.chartFullHistory.getAxisRight().setEnabled(false);
        binding.chartFullHistory.invalidate();
    }

    private void setupLogList(List<PriceHistory.PricePoint> points) {
        binding.rvPriceLog.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPriceLog.setAdapter(new RecyclerView.Adapter<LogViewHolder>() {
            @NonNull
            @Override
            public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                return new LogViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
                PriceHistory.PricePoint point = points.get(points.size() - 1 - position);
                holder.text1.setText(String.format("₹%,.2f", point.getPrice()));
                holder.text2.setText(dateFormat.format(new Date(point.getTimestamp())));
                holder.text1.setTextColor(ContextCompat.getColor(PriceHistoryActivity.this, R.color.on_surface));
                holder.text1.setTypeface(null, android.graphics.Typeface.BOLD);
            }

            @Override
            public int getItemCount() {
                return points.size();
            }
        });
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        LogViewHolder(View v) {
            super(v);
            text1 = v.findViewById(android.R.id.text1);
            text2 = v.findViewById(android.R.id.text2);
        }
    }
}
