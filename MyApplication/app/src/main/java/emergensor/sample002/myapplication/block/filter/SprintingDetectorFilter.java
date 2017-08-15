package emergensor.sample002.myapplication.block.filter;

import emergensor.sample002.myapplication.block.Block;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.lib.Consumer;

public class SprintingDetectorFilter extends Block<Message<Void>> implements Consumer<Message<Double>> {

    private final int minPariodMilliSeconds;
    private final int threshold;

    private Long timeLastUpload = null;

    public SprintingDetectorFilter(int minPariodMilliSeconds, int threshold) {
        this.minPariodMilliSeconds = minPariodMilliSeconds;
        this.threshold = threshold;
    }

    @Override
    public void accept(Message<Double> m) {
        if (m.value > threshold) {
            long now = System.currentTimeMillis();
            if (timeLastUpload == null || now - timeLastUpload >= minPariodMilliSeconds) {
                timeLastUpload = now;

                output(new Message<>(m.timestamp, (Void) null));

            }
        }
    }

}
