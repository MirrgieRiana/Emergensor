package emergensor.sample002.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import emergensor.sample002.myapplication.block.BlockBuilder;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.block.drain.SimpleVectorConcatenateDrain;
import emergensor.sample002.myapplication.block.filter.BufferFilter;
import emergensor.sample002.myapplication.block.filter.FunctionFilter;
import emergensor.sample002.myapplication.block.filter.VectorPeriodicSampleFilter;
import emergensor.sample002.myapplication.block.source.AccelerationSensorSource;
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

    private LocationSensorSource locationSensorSource;
    private AccelerationSensorSource accelerationSensorSource;

    private String locationText = "undefined";
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make instances
        try {

            ui = new UI(this, "http://203.178.135.114:7030/", 500);
            locationSensorSource = build(new LocationSensorSource(this))
                    .add(new Consumer<Message<Vector<Double>>>() {
                        @Override
                        public void accept(Message<Vector<Double>> m) {
                            locationText = "" + m.value.get(0) + " / " + m.value.get(1);
                            ui.setText(locationText);
                        }
                    })
                    .get();
            final SimpleVectorConcatenateDrain<Double> drain = build(new SimpleVectorConcatenateDrain<Double>(6))
                    .add(new Consumer<Message<Vector<Double>>>() {
                        @Override
                        public void accept(Message<Vector<Double>> m) {
                            ui.setText2(tree(m.value.get(0), m.value.get(1), m.value.get(2), m.value.get(3), m.value.get(4), m.value.get(5)).name());
                            ui.setEntry2(index,
                                    (float) (double) m.value.get(0),
                                    (float) (double) m.value.get(1),
                                    (float) (double) m.value.get(2) * 0.1f,
                                    (float) (double) m.value.get(3) * 0.1f,
                                    (float) (double) m.value.get(4) * 0.1f,
                                    (float) (double) m.value.get(5) * 0.1f);
                        }

                        private EnumState tree(double mean, double variance, double e8, double e16, double e32, double e64) {
                            if (variance <= 1.552883) {
                                if (mean <= 1.35028) {
                                    return EnumState.OTHER;
                                } else {
                                    if (e32 <= 8.168214) {
                                        return EnumState.OTHER;
                                    } else {
                                        if (variance <= 0.238747) {
                                            return EnumState.RUN;
                                        } else {
                                            if (e8 <= 81.111556) {
                                                return EnumState.OTHER;
                                            } else {
                                                return EnumState.RUN;
                                            }
                                        }
                                    }
                                }
                            } else {
                                return EnumState.RUN;
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

            /*
                                    .add(build(new SprintingDetectorFilter(1 * 1000, 10))
                                            .add(new Consumer<Long>() {
                                                final URLSenderSink urlSenderSink = new URLSenderSink(
                                                        "a",
                                                        "gp-^45:w3v9]332c",
                                                        new URL("http://203.178.135.114:7030/send"));

                                                @Override
                                                public void accept(Long timestamp) {
                                                    urlSenderSink.accept(locationText);
                                                }
                                            })))
             */

            // pre init
            {
                ui.preInit();
            }

            // init
            {
                if (!locationSensorSource.checkPermission()) return;
                if (!accelerationSensorSource.checkPermission()) return;

                locationSensorSource.init();
                accelerationSensorSource.init();

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
        locationSensorSource.stop();
        accelerationSensorSource.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        locationSensorSource.start();
        accelerationSensorSource.start();
    }

    private static enum EnumState {
        OTHER,
        RUN,
    }

    private <I, O> BlockBuilder<FunctionFilter<Message<I>, Message<O>>, Message<O>> bfm
            (Function<I, O> function) {
        return build(new FunctionFilter<>(new MessageFunctionWrapper<>(function)));
    }

}
