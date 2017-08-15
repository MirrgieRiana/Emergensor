package emergensor.sample002.myapplication.functions;

public class HanningWindowFunction extends AbstractWindowFunction {

    @Override
    protected double getMultiplier(int i, int size) {
        return 0.5 - 0.5 * Math.cos(2 * Math.PI * i / (size - 1));
    }

}
