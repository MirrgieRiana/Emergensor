package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Vector;

public abstract class AbstractWindowFunction extends AbstractFunction<Vector<Double>, Vector<Double>> {

    @Override
    public Vector<Double> apply(Vector<Double> data) {

        Double[] vector = new Double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            vector[i] = data.get(i) * getMultiplier(i, data.size());
        }

        return new ArrayVector<>(vector);
    }

    protected abstract double getMultiplier(int i, int size);

}
