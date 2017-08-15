package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Utils;
import emergensor.sample002.myapplication.lib.Vector;

public class NormFunction extends AbstractFunction<Vector<Double>, Double> {

    @Override
    public Double apply(Vector<Double> data) {
        return Utils.getNorm(data);
    }

}
