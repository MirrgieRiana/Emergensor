package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Function;
import emergensor.sample002.myapplication.lib.Vector;

public class MapFunctionWrapper<I, O> extends AbstractFunctionWrapper<Vector<I>, Vector<O>, I, O> {

    public MapFunctionWrapper(Function<I, O> function) {
        super(function);
    }

    @Override
    public Vector<O> apply(Vector<I> data) {
        O[] data2 = (O[]) new Object[data.size()];
        for (int i = 0; i < data.size(); i++) {
            data2[i] = function.apply(data.get(i));
        }
        return new ArrayVector<>(data2);
    }

}
