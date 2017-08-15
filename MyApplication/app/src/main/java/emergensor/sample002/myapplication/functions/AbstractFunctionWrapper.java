package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Function;

public abstract class AbstractFunctionWrapper<I, O, FI, FO> extends AbstractFunction<I, O> {

    protected final Function<FI, FO> function;

    public AbstractFunctionWrapper(Function<FI, FO> function) {
        this.function = function;
    }

}
