package emergensor.sample002.myapplication;

import org.junit.Test;

import java.util.ArrayList;

import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.block.filter.VectorPeriodicSampleFilter;
import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Vector;

import static org.junit.Assert.*;

public class FilterUnitTest {
    @Test
    public void test_VectorPeriodicSampleFilter() throws Exception {
        final ArrayList<Message<Vector<Double>>> messages = new ArrayList<>();

        VectorPeriodicSampleFilter vectorPeriodicSampleFilter = new VectorPeriodicSampleFilter(100);
        vectorPeriodicSampleFilter.addListener(new Consumer<Message<Vector<Double>>>() {
            @Override
            public void accept(Message<Vector<Double>> m) {
                messages.add(m);
            }
        });

        vectorPeriodicSampleFilter.accept(new Message<>(530, (Vector<Double>) new ArrayVector<>((double) 0)));
        vectorPeriodicSampleFilter.accept(new Message<>(540, (Vector<Double>) new ArrayVector<>((double) 200)));
        vectorPeriodicSampleFilter.accept(new Message<>(620, (Vector<Double>) new ArrayVector<>((double) 10)));
        vectorPeriodicSampleFilter.accept(new Message<>(670, (Vector<Double>) new ArrayVector<>((double) 15)));
        vectorPeriodicSampleFilter.accept(new Message<>(729, (Vector<Double>) new ArrayVector<>((double) 600)));
        vectorPeriodicSampleFilter.accept(new Message<>(730, (Vector<Double>) new ArrayVector<>((double) 60)));
        vectorPeriodicSampleFilter.accept(new Message<>(731, (Vector<Double>) new ArrayVector<>((double) 600)));
        vectorPeriodicSampleFilter.accept(new Message<>(780, (Vector<Double>) new ArrayVector<>((double) 0)));
        vectorPeriodicSampleFilter.accept(new Message<>(1280, (Vector<Double>) new ArrayVector<>((double) 500)));

        assertEquals(8, messages.size());
        assertEquals(530, messages.get(0).timestamp);
        assertEquals(50, messages.get(0).value.get(0), 0.0001);
        assertEquals(630, messages.get(1).timestamp);
        assertEquals(11, messages.get(1).value.get(0), 0.0001);
        assertEquals(730, messages.get(2).timestamp);
        assertEquals(60, messages.get(2).value.get(0), 0.0001);
        assertEquals(830, messages.get(3).timestamp);
        assertEquals(50, messages.get(3).value.get(0), 0.0001);
        assertEquals(930, messages.get(4).timestamp);
        assertEquals(150, messages.get(4).value.get(0), 0.0001);
        assertEquals(1030, messages.get(5).timestamp);
        assertEquals(250, messages.get(5).value.get(0), 0.0001);
        assertEquals(1130, messages.get(6).timestamp);
        assertEquals(350, messages.get(6).value.get(0), 0.0001);
        assertEquals(1230, messages.get(7).timestamp);
        assertEquals(450, messages.get(7).value.get(0), 0.0001);
    }
}