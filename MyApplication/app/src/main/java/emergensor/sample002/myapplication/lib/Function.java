package emergensor.sample002.myapplication.lib;

public interface Function<I, O> {

    public O apply(I i);

    public <O2> Function<I, O2> andThen(Function<O, O2> function);

}
