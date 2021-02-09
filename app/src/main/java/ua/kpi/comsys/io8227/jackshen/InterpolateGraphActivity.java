package ua.kpi.comsys.io8227.jackshen;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InterpolateGraphActivity extends AppCompatActivity {
    double a, b;
    int n;
    GraphView graph1, graph2;
    private File imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph);

        a = getIntent().getDoubleExtra("a", -Math.PI);
        b = getIntent().getDoubleExtra("b", Math.PI);
        n = getIntent().getIntExtra("n", 10);
        makeGraphics();
        Button share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) graph1.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            graph2.setVisibility(View.GONE);
            params.topMargin = 900;

            findViewById(R.id.error).setVisibility(View.GONE);
            findViewById(R.id.bias).setVisibility(View.GONE);
            findViewById(R.id.share).setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            graph2.setVisibility(View.VISIBLE);
            params.topMargin = 200;
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            findViewById(R.id.bias).setVisibility(View.VISIBLE);
            findViewById(R.id.share).setVisibility(View.VISIBLE);
        }
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/my_graph.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "Hey! Look at my graph. Screenshot sent from JackShen App. And it`s free as a bird can fly!";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My graph");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    void makeGraphics() {
        graph1 = findViewById(R.id.graph1);
        graph2 = findViewById(R.id.graph2);

        graph1.getViewport().setScalable(false);
        graph1.getViewport().setScrollable(false);
        graph1.getViewport().setScalableY(false);
        graph1.getViewport().setScrollableY(false);

        graph2.getViewport().setScalable(false);
        graph2.getViewport().setScrollable(false);
        graph2.getViewport().setScalableY(false);
        graph2.getViewport().setScrollableY(false);

        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(a);
        graph1.getViewport().setMaxX(b);
        graph1.getViewport().setYAxisBoundsManual(true);

        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(a);
        graph2.getViewport().setMaxX(b);
        graph2.getViewport().setYAxisBoundsManual(true);

        double precision = 0.05;
        double[] xt = new double[n];
        double[] yt = new double[n];
        double x = a;

        DataPoint[] linePoints = new DataPoint[(int) ((b - a) / precision + 1)];
        DataPoint[] pointPoints = new DataPoint[n];
        DataPoint[] interpolationPoints = new DataPoint[(int) ((b - a) / precision + 1)];
        DataPoint[] errorPoints = new DataPoint[(int) ((b - a) / precision + 1)];

        graph1.getViewport().setMinY(-1.5);
        graph1.getViewport().setMaxY(1.5);
        graph2.getViewport().setMinY(-1);
        graph2.getViewport().setMaxY(2);

        for (int i = 0; i < n; i++) {
            double x1 = a + ((b - a) / n) * i;
            xt[i] = x1;
            yt[i] = Math.cos(x1);
            pointPoints[i] = new DataPoint(x1, Math.cos(x1));
        }

        for (int j = 0; j < (int) ((b - a) / precision + 1); j++) {
            linePoints[j] = new DataPoint(x, Math.cos(x));
            interpolationPoints[j] = new DataPoint(x, getInterpolationNewton(x, xt, n, yt));
            errorPoints[j] = new DataPoint(x, Math.cos(x) - getInterpolationNewton(x, xt, n, yt));
            x += precision;
        }

        LineGraphSeries<DataPoint> lSeries = new LineGraphSeries<>(linePoints);
        lSeries.setAnimated(true);
        lSeries.setTitle("Theoretical");

        PointsGraphSeries<DataPoint> pSeries = new PointsGraphSeries<>(pointPoints);
        pSeries.setShape(PointsGraphSeries.Shape.POINT);
        pSeries.setColor(Color.parseColor("#f53937"));
        pSeries.setTitle("Table Points");

        LineGraphSeries<DataPoint> interpolationSeries = new LineGraphSeries<>(interpolationPoints);
        interpolationSeries.setAnimated(true);
        interpolationSeries.setTitle("Interpolation");
        interpolationSeries.setColor(Color.BLACK);

        LineGraphSeries<DataPoint> errorSeries = new LineGraphSeries<>(errorPoints);
        errorSeries.setAnimated(true);
        errorSeries.setTitle("Errors");
        errorSeries.setColor(Color.BLACK);

        graph1.addSeries(lSeries);
        graph1.addSeries(pSeries);
        graph1.addSeries(interpolationSeries);

        graph1.setLegendRenderer(new LegendRenderer(graph1));
        graph1.getLegendRenderer().setVisible(true);
        graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph2.addSeries(errorSeries);
        graph2.setLegendRenderer(new LegendRenderer(graph2));
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    public static double getInterpolationNewton(double x, double[] xt, int r, double[] ft) {
        double result = ft[0];
        double buf = 1;
        for (int k = 1; k < r; k++) {
            double tempSum = 0;
            for (int i = 0; i <= k; i++) {
                double temp = 1;
                for (int j = 0; j < i; j++) {
                    temp = temp * (xt[i] - xt[j]);
                }
                for (int j = i + 1; j <= k; j++) {
                    temp = temp * (xt[i] - xt[j]);
                }
                temp = ft[i] / temp;
                tempSum += temp;
            }
            buf = buf * (x - xt[k - 1]);
            result = result + tempSum * buf;
        }
        return result;
    }

}
