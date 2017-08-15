package emergensor.sample002.myapplication.block.filter;

import emergensor.sample002.myapplication.block.Block;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Vector;

public class VectorPeriodicSampleFilter extends Block<Message<Vector<Double>>> implements Consumer<Message<Vector<Double>>> {

    private final long period;

    private Message<Vector<Double>> lastMessage;
    private long lastTime;

    public VectorPeriodicSampleFilter(long period) {
        this.period = period;
    }

    @Override
    public void accept(Message<Vector<Double>> m) {
        if (lastMessage == null) {
            lastMessage = m;
            lastTime = m.timestamp;
            output(m);
        } else {
            while (lastTime + period <= m.timestamp) {
                lastTime += period;

                double a = m.timestamp - lastMessage.timestamp;
                double b = lastTime - lastMessage.timestamp;
                double ratio = b / a;

                Double[] data = new Double[m.value.size()];
                for (int i = 0; i < m.value.size(); i++) {
                    data[i] = lastMessage.value.get(i) * (1 - ratio) + m.value.get(i) * ratio;
                }

                output(new Message<>(lastTime, (Vector<Double>) new ArrayVector<>(data)));
            }
            lastMessage = m;
        }
    }

}
