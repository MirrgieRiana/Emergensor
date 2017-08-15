package emergensor.sample002.myapplication.lib;

public class RingBufferVector<T> implements Vector<T> {

    private T[] data;
    private int startIndex;

    public RingBufferVector(int size) {
        this.data = (T[]) new Object[size];
    }

    public void set(T value) {
        data[startIndex] = value;
    }

    public void add() {
        startIndex = (startIndex + 1) % data.length;
    }

    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public T get(int index) {
        return data[(startIndex + index) % data.length];
    }

    @Override
    public int size() {
        return data.length;
    }

}
