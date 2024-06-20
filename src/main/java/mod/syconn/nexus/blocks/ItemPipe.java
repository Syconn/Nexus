package mod.syconn.nexus.blocks;

import mod.syconn.nexus.blockentities.ItemPipeBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPipe extends PipeAttachmentBlock {
    public ItemPipe() {
        super(Properties.of().strength(1.0f).sound(SoundType.METAL).noOcclusion());
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ItemPipeBE(pPos, pState);
    }
}