package emergensor.sample002.myapplication.lib;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

    public static double getNorm(Vector<Double> data) {
        double sum = 0;
        for (int i = 0; i < data.size(); i++) {
            sum += data.get(i) * data.get(i);
        }
        return Math.sqrt(sum);
    }

    public static String getStackTrace(Exception e) {
        StringWriter out = new StringWriter();
        PrintWriter out2 = new PrintWriter(out);
        e.printStackTrace(out2);
        out2.flush();
        return out.toString();
    }

}
