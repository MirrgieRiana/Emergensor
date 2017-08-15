package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Function;

public abstract class AbstractFunction<I, O> implements Function<I, O> {

    @Override
    public <O2> Function<I, O2> andThen(final Function<O, O2> function) {
        return new AbstractFunction<I, O2>() {
            @Override
            public O2 apply(I i) {
                return function.apply(AbstractFunction.this.apply(i));
            }
        };
    }

}
