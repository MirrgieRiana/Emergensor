package emergensor.sample002.myapplication.block.filter;

import emergensor.sample002.myapplication.block.Block;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.RingBufferVector;
import emergensor.sample002.myapplication.lib.Vector;

public class BufferFilter<T> extends Block<Message<Vector<T>>> implements Consumer<Message<T>> {

    private final RingBufferVector<T> data;
    private final long[] timestamps;
    private boolean filled;

    public BufferFilter(int samples) {
        this.data = new RingBufferVector<>(samples);
        this.timestamps = new long[samples];
    }

    @Override
    public void accept(Message<T> m) {

        data.set(m.value);
        timestamps[data.getStartIndex()] = m.timestamp;
        data.add();

        if (data.getStartIndex() == 0) filled = true;
        if (filled) output(new Message<>(timestamps[data.getStartIndex()], (Vector<T>) data));
    }

}
