package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Complex;

public class ComplexAbstractFunction extends AbstractFunction<Complex, Double> {

    @Override
    public Double apply(Complex data) {
        return data.abs();
    }

}
