package emergensor.sample002.myapplication.block.source;

import emergensor.sample002.myapplication.block.Block;

public abstract class AbstractSource<M> extends Block<M> {

    protected boolean initialized;

    public void init() {
        initImpl();
        initialized = true;
    }

    protected abstract void initImpl();

    public void start() {
        if (!initialized) return;
        startImpl();
    }

    protected abstract void startImpl();

    public void stop() {
        if (!initialized) return;
        stopImpl();
    }

    protected abstract void stopImpl();

}
