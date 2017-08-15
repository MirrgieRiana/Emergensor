package emergensor.sample002.myapplication.block.filter;

import emergensor.sample002.myapplication.block.Block;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Function;

public class FunctionFilter<I, O> extends Block<O> implements Consumer<I> {

    private final Function<I, O> function;

    public FunctionFilter(Function<I, O> function) {
        this.function = function;
    }

    @Override
    public void accept(I i) {
        output(function.apply(i));
    }

}
