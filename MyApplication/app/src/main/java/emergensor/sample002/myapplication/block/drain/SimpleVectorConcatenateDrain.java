package emergensor.sample002.myapplication.block.drain;

import emergensor.sample002.myapplication.block.Block;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Vector;

public class SimpleVectorConcatenateDrain<T> extends Block<Message<Vector<T>>> {

    private T[] data;
    private long now = -1;
    private int filledCount;

    public SimpleVectorConcatenateDrain(int count) {
        this.data = (T[]) new Object[count];
    }

    public Consumer<Message<T>> createDrain(final int index) {
        return new Consumer<Message<T>>() {
            @Override
            public void accept(Message<T> m) {
                if (m.timestamp > now) {
                    now = m.timestamp;
                    data[index] = m.value;
                    filledCount = 1;
                } else if (m.timestamp == now) {
                    data[index] = m.value;
                    filledCount++;

                    if (filledCount == data.length) {
                        output(new Message<>(now, (Vector<T>) new ArrayVector<>(data)));
                    }
                }
            }
        };
    }

}
