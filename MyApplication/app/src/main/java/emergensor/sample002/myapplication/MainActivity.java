package emergensor.sample002.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import emergensor.sample002.myapplication.block.BlockBuilder;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.block.drain.SimpleVectorConcatenateDrain;
import emergensor.sample002.myapplication.block.filter.BufferFilter;
import emergensor.sample002.myapplication.block.filter.FunctionFilter;
import emergensor.sample002.myapplication.block.filter.VectorPeriodicSampleFilter;
import emergensor.sample002.myapplication.block.sink.URLSenderSink;
import emergensor.sample002.myapplication.block.source.AccelerationSensorSource;
import emergensor.sample002.myapplication.block.source.IntervalSource;
import emergensor.sample002.myapplication.block.source.LocationSensorSource;
import emergensor.sample002.myapplication.functions.ComplexAbstractFunction;
import emergensor.sample002.myapplication.functions.FFTFunction;
import emergensor.sample002.myapplication.functions.HanningWindowFunction;
import emergensor.sample002.myapplication.functions.MapFunctionWrapper;
import emergensor.sample002.myapplication.functions.MeanFunction;
import emergensor.sample002.myapplication.functions.MessageFunctionWrapper;
import emergensor.sample002.myapplication.functions.NormFunction;
import emergensor.sample002.myapplication.functions.PassFrequencyFunction;
import emergensor.sample002.myapplication.functions.VarianceFunction;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Function;
import emergensor.sample002.myapplication.lib.Utils;
import emergensor.sample002.myapplication.lib.Vector;

import static emergensor.sample002.myapplication.block.BlockBuilder.build;

public class MainActivity extends AppCompatActivity {

    private UI ui;

    private IntervalSource intervalSource;
    private LocationSensorSource locationSensorSource;
    private AccelerationSensorSource accelerationSensorSource;

    private double userId = Math.random();
    private double[] location = null;
    private String locationText = "undefined";
    private boolean allowAlert = false;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make instances
        try {

            ui = new UI(this, "http://203.178.135.114:7030/", 500);
            intervalSource = build(new IntervalSource(10 * 1000))
                    .add(new Consumer<Message<Long>>() {
                        final URLSenderSink urlSenderSink = new URLSenderSink(
                                "a",
                                "gp-^45:w3v9]332c",
                                new URL("http://203.178.135.114:7030/__api/send/alert"));

                        @Override
                        public void accept(Message<Long> m) {
                            if (allowAlert) {
                                if (location != null) {
                                    try {
                                        String string = String.format("{userId:\"%s\",lat:\"%f\",lng:\"%f\",text:\"App\"}",
                                                userId,
                                                location[0],
                                                location[1]);
                                        urlSenderSink.accept(string);
                                    } catch (Throwable e) {
                                        StringWriter out = new StringWriter();
                                        e.printStackTrace(new PrintWriter(out));
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("title")
                                                .setMessage(out.toString())
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }
                            }
                        }
                    })
                    .get();
            locationSensorSource = build(new LocationSensorSource(this))
                    .add(new Consumer<Message<Vector<Double>>>() {
                        @Override
                        public void accept(Message<Vector<Double>> m) {
                            location = new double[]{m.value.get(0), m.value.get(1)};
                            locationText = "" + m.value.get(0) + " / " + m.value.get(1);
                            ui.setText(locationText);
                        }
                    })
                    .get();
            final SimpleVectorConcatenateDrain<Double> drain = build(new SimpleVectorConcatenateDrain<Double>(6))
                    .add(new Consumer<Message<Vector<Double>>>() {
                        private Thread thread;

                        @Override
                        public void accept(Message<Vector<Double>> m) {
                            EnumState state = classify(m.value.get(0), m.value.get(1), m.value.get(2), m.value.get(3), m.value.get(4), m.value.get(5));

                            if (state == EnumState.RUN) {
                                allowAlert = true;

                                // タイマリセット
                                if (thread != null) thread.interrupt();
                                thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(60 * 1000);
                                        } catch (InterruptedException e) {
                                            return;
                                        }
                                        allowAlert = false;
                                    }
                                });
                                thread.start();
                            }

                            ui.setText2(state.name());
                            ui.setEntry2(index,
                                    (float) (double) m.value.get(0),
                                    (float) (double) m.value.get(1),
                                    (float) (double) m.value.get(2) * 0.1f,
                                    (float) (double) m.value.get(3) * 0.1f,
                                    (float) (double) m.value.get(4) * 0.1f,
                                    (float) (double) m.value.get(5) * 0.1f);
                        }

                        private EnumState classify(Double... args) {
                            try {
                                return EnumState.values()[(int) Tree.classify(args)];
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .get();
            accelerationSensorSource = build(new AccelerationSensorSource(this, false))
                    .add(build(new VectorPeriodicSampleFilter(1 * 1000 * 1000 / 100))
                            .add(new Consumer<Message<Vector<Double>>>() {
                                @Override
                                public void accept(Message<Vector<Double>> m) {
                                    ui.setEntry(index,
                                            (float) (double) m.value.get(0),
                                            (float) (double) m.value.get(1),
                                            (float) (double) m.value.get(2),
                                            (float) Utils.getNorm(m.value));
                                    index = (index + 1) % ui.getGraphSize();
                                }
                            })
                            .add(bfm(new NormFunction())
                                    .add(build(new BufferFilter<Double>(256))
                                            .add(bfm(new MeanFunction())
                                                    .add(drain.createDrain(0)))
                                            .add(bfm(new VarianceFunction())
                                                    .add(drain.createDrain(1)))
                                            .add(bfm(new HanningWindowFunction()
                                                    .andThen(new FFTFunction()))
                                                    .add(bfm(new PassFrequencyFunction(5, 8)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbstractFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(2)))
                                                    .add(bfm(new PassFrequencyFunction(9, 16)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbstractFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(3)))
                                                    .add(bfm(new PassFrequencyFunction(17, 32)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbstractFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(4)))
                                                    .add(bfm(new PassFrequencyFunction(33, 64)
                                                            .andThen(new MapFunctionWrapper<>(new ComplexAbstractFunction()))
                                                            .andThen(new NormFunction()))
                                                            .add(drain.createDrain(5)))))))
                    .get();

            // pre init
            {
                ui.preInit();
            }

            // init
            {
                if (!locationSensorSource.checkPermission()) return;
                if (!accelerationSensorSource.checkPermission()) return;

                intervalSource.init();
                locationSensorSource.init();
                accelerationSensorSource.init();

                intervalSource.start();
                locationSensorSource.start();
                accelerationSensorSource.start();

                ui.init();
            }

            // post init
            {
                ui.setText(locationText);
            }

        } catch (
                Exception e)

        {
            e.printStackTrace();
            return;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        intervalSource.stop();
        locationSensorSource.stop();
        accelerationSensorSource.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        intervalSource.start();
        locationSensorSource.start();
        accelerationSensorSource.start();
    }

    private static enum EnumState {
        RUN,
        OTHER,
    }

    private <I, O> BlockBuilder<FunctionFilter<Message<I>, Message<O>>, Message<O>> bfm
            (Function<I, O> function) {
        return build(new FunctionFilter<>(new MessageFunctionWrapper<>(function)));
    }

}
