package emergensor.sample002.myapplication.block;

public class Message<T> {

    /**
     * microseconds
     */
    public final long timestamp;
    public final T value;

    public Message(long timestamp, T value) {
        this.timestamp = timestamp;
        this.value = value;
    }

}
