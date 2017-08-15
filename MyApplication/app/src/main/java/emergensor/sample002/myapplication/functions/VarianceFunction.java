package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Vector;

public class VarianceFunction extends AbstractFunction<Vector<Double>, Double> {

    @Override
    public Double apply(Vector<Double> data) {

        double a2 = 0;
        double a1 = 0;
        for (int i = 0; i < data.size(); i++) {
            a2 += data.get(i) * data.get(i);
            a1 += data.get(i);
        }
        a2 /= data.size();
        a1 /= data.size();

        return a2 - a1 * a1;
    }

}
