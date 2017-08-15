package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Complex;
import emergensor.sample002.myapplication.lib.Vector;

public class PassFrequencyFunction extends AbstractFunction<Vector<Complex>, Vector<Complex>> {

    private final int startFrequency;
    private final int endFrequency;

    public PassFrequencyFunction(int startFrequency, int endFrequency) {
        this.startFrequency = startFrequency;
        this.endFrequency = endFrequency;
    }

    @Override
    public Vector<Complex> apply(Vector<Complex> data) {
        Complex[] data2 = new Complex[data.size()];
        for (int i = 0; i < data.size(); i++) {
            int j = i + 1;
            if (j < startFrequency) {
                data2[i] = new Complex(0, 0);
            } else if ((endFrequency < j) && (j < data.size() + 1 - endFrequency)) {
                data2[i] = new Complex(0, 0);
            } else if (data.size() + 1 - startFrequency < j) {
                data2[i] = new Complex(0, 0);
            } else {
                data2[i] = data.get(i);
            }
        }
        return new ArrayVector<>(data2);
    }

}
