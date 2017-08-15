package emergensor.sample002.myapplication;

import android.content.pm.ActivityInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.LinkedList;

public class UI {

    private final MainActivity mainActivity;
    private final String url;
    private final int graphSize;

    public TextView textView;
    public TextView textView2;
    public WebView webView;

    public LineChart lineChart;
    public LineData lineData;
    public LinkedList<Entry> entriesX = new LinkedList<>();
    public LinkedList<Entry> entriesY = new LinkedList<>();
    public LinkedList<Entry> entriesZ = new LinkedList<>();
    public LinkedList<Entry> entriesN = new LinkedList<>();

    public LineChart lineChart2;
    public LineData lineData2;
    public LinkedList<Entry> entriesA = new LinkedList<>();
    public LinkedList<Entry> entriesB = new LinkedList<>();
    public LinkedList<Entry> entriesC = new LinkedList<>();
    public LinkedList<Entry> entriesD = new LinkedList<>();
    public LinkedList<Entry> entriesE = new LinkedList<>();
    public LinkedList<Entry> entriesF = new LinkedList<>();

    public UI(MainActivity mainActivity, String url, int graphSize) {
        this.mainActivity = mainActivity;
        this.url = url;
        this.graphSize = graphSize;
    }

    public void preInit() {
        mainActivity.setContentView(R.layout.activity_main);
        mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void init() {

        textView = (TextView) mainActivity.findViewById(R.id.textView);
        setText("");

        textView2 = (TextView) mainActivity.findViewById(R.id.textView2);
        setText("");

        webView = (WebView) mainActivity.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        {
            lineChart = (LineChart) mainActivity.findViewById(R.id.lineChart);

            lineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setStartAtZero(false);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMinValue(-20);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMaxValue(20);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            lineChart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
            lineChart.getXAxis().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
            lineChart.setVisibleXRangeMaximum(200);
            lineChart.setTouchEnabled(false);
            lineChart.setScaleEnabled(false);
            lineChart.setDragEnabled(false);
            lineChart.setDescription("");

            {
                lineData = new LineData();
                LineDataSet lineDataSet;

                lineDataSet = new LineDataSet(entriesX, "X");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xffff0000);
                lineData.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesY, "Y");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff00ff00);
                lineData.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesZ, "Z");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff0000ff);
                lineData.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesN, "N");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff000000);
                lineData.addDataSet(lineDataSet);

                for (int i = 0; i < graphSize; i++) {
                    entriesX.addLast(new Entry(0, i));
                    entriesY.addLast(new Entry(0, i));
                    entriesZ.addLast(new Entry(0, i));
                    entriesN.addLast(new Entry(0, i));
                    lineData.addXValue("" + i);
                }
                lineChart.setData(lineData);
            }
        }

        {
            lineChart2 = (LineChart) mainActivity.findViewById(R.id.lineChart2);

            lineChart2.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
            lineChart2.getAxis(YAxis.AxisDependency.LEFT).setStartAtZero(false);
            lineChart2.getAxis(YAxis.AxisDependency.LEFT).setAxisMinValue(-20);
            lineChart2.getAxis(YAxis.AxisDependency.LEFT).setAxisMaxValue(20);
            lineChart2.getAxis(YAxis.AxisDependency.LEFT).setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            lineChart2.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
            lineChart2.getXAxis().setEnabled(false);
            lineChart2.getLegend().setEnabled(false);
            lineChart2.setVisibleXRangeMaximum(200);
            lineChart2.setTouchEnabled(false);
            lineChart2.setScaleEnabled(false);
            lineChart2.setDragEnabled(false);
            lineChart2.setDescription("");

            {
                lineData2 = new LineData();
                LineDataSet lineDataSet;

                lineDataSet = new LineDataSet(entriesA, "A");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xffff0000);
                lineData2.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesB, "B");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff00ff00);
                lineData2.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesC, "C");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff0000ff);
                lineData2.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesD, "D");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xffffff00);
                lineData2.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesE, "E");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xffff00ff);
                lineData2.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesF, "F");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff00ffff);
                lineData2.addDataSet(lineDataSet);

                for (int i = 0; i < graphSize; i++) {
                    entriesA.addLast(new Entry(0, i));
                    entriesB.addLast(new Entry(0, i));
                    entriesC.addLast(new Entry(0, i));
                    entriesD.addLast(new Entry(0, i));
                    entriesE.addLast(new Entry(0, i));
                    entriesF.addLast(new Entry(0, i));
                    lineData2.addXValue("" + i);
                }
                lineChart2.setData(lineData2);
            }
        }

    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setText2(String text) {
        textView2.setText(text);
    }

    public void setEntry(int i, float x, float y, float z, float n) {
        entriesX.set(i, new Entry(x, i));
        entriesY.set(i, new Entry(y, i));
        entriesZ.set(i, new Entry(z, i));
        entriesN.set(i, new Entry(n, i));

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void setEntry2(int i, float a, float b, float c, float d, float e, float f) {
        entriesA.set(i, new Entry(a, i));
        entriesB.set(i, new Entry(b, i));
        entriesC.set(i, new Entry(c, i));
        entriesD.set(i, new Entry(d, i));
        entriesE.set(i, new Entry(e, i));
        entriesF.set(i, new Entry(f, i));

        lineChart2.notifyDataSetChanged();
        lineChart2.invalidate();
    }

    public int getGraphSize() {
        return graphSize;
    }

}
