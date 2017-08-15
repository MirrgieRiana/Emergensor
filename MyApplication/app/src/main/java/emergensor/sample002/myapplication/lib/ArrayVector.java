package emergensor.sample002.myapplication.lib;

public class ArrayVector<T> implements Vector<T> {

    private final T[] data;

    public ArrayVector(T... data) {
        this.data = data;
    }

    @Override
    public T get(int index) {
        return data[index];
    }

    @Override
    public int size() {
        return data.length;
    }

}
