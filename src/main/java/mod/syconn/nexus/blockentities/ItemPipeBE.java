package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPipeBE extends BasePipeBE {


    public ItemPipeBE(BlockPos pos, BlockState state) {
        super(Registration.ITEM_PIPE_BE.get(), pos, state);
    }

    public void tickServer() {

    }
}