package emergensor.sample002.myapplication.block.source;

import android.app.Activity;

public abstract class AbstractSensorSource<M> extends AbstractSource<M> {

    protected final Activity activity;

    public AbstractSensorSource(Activity activity) {
        this.activity = activity;
    }

    public abstract boolean checkPermission();

}
