package emergensor.sample002.myapplication.block;

import emergensor.sample002.myapplication.lib.Consumer;

public class BlockBuilder<B extends Block<O>, O> {

    private B block;

    public BlockBuilder(B block) {
        this.block = block;
    }

    public static <B extends Block<M>, M> BlockBuilder<B, M> build(B block) {
        return new BlockBuilder<>(block);
    }

    public BlockBuilder<B, O> add(Consumer<O> consumer) {
        block.addListener(consumer);
        return this;
    }

    public <B2 extends Block<O2> & Consumer<O>, O2> BlockBuilder<B, O> add(BlockBuilder<B2, O2> blockBuilder) {
        block.addListener(blockBuilder.block);
        return this;
    }

    public B get() {
        return block;
    }

}
