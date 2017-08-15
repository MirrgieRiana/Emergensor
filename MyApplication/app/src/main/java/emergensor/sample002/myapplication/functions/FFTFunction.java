package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Complex;
import emergensor.sample002.myapplication.lib.FFT;
import emergensor.sample002.myapplication.lib.Vector;

public class FFTFunction extends AbstractFunction<Vector<Double>, Vector<Complex>> {

    @Override
    public Vector<Complex> apply(Vector<Double> data) {
        Complex[] data2 = new Complex[data.size()];
        for (int i = 0; i < data.size(); i++) {
            data2[i] = new Complex(data.get(i), 0);
        }
        FFT fft = new FFT(false);
        fft.setData(data2);
        fft.execute();
        return new ArrayVector<>(fft.getData());
    }

}
