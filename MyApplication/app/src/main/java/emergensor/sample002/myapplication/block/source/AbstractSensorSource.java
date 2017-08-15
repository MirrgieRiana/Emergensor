package emergensor.sample002.myapplication.block.source;

import android.app.Activity;

import emergensor.sample002.myapplication.block.Block;

public abstract class AbstractSensorSource<M> extends Block<M> {

    protected final Activity activity;
    protected boolean initialized;

    public AbstractSensorSource(Activity activity) {
        this.activity = activity;
    }

    public abstract boolean checkPermission();

    public void init() {
        initImpl();
        initialized = true;
    }

    public abstract void initImpl();

    public void stop() {
        if (!initialized) return;
        stopImpl();
    }

    protected abstract void stopImpl();

    public void start() {
        if (!initialized) return;
        startImpl();
    }

    protected abstract void startImpl();

}
