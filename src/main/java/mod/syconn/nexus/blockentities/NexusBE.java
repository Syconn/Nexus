package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NexusBE extends BasePipeBE {

    public NexusBE(BlockPos pos, BlockState state) {
        super(Registration.NEXUS_BE.get(), pos, state);
    }

    public void tickServer() {}
}
