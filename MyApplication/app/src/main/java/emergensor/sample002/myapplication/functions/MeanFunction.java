package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Vector;

public class MeanFunction extends AbstractFunction<Vector<Double>, Double> {

    @Override
    public Double apply(Vector<Double> data) {

        double a = 0;
        for (int i = 0; i < data.size(); i++) {
            a += data.get(i);
        }
        a /= data.size();

        return a;
    }

}
