package emergensor.sample002.myapplication.block.source;

import emergensor.sample002.myapplication.block.Message;

public class IntervalSource extends AbstractSource<Message<Long>> {

    private final long intervalMillis;
    private long index = 0;
    private Thread thread;

    public IntervalSource(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    @Override
    protected void initImpl() {

    }

    @Override
    protected void startImpl() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(intervalMillis);
                    } catch (InterruptedException e) {
                        break;
                    }
                    output(new Message<>(System.nanoTime(), index));
                    index++;
                }
            }
        });
        thread.start();
    }

    @Override
    protected void stopImpl() {
        thread.interrupt();
    }

}
