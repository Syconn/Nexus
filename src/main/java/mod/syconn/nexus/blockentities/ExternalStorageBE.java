package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import mod.syconn.nexus.world.savedata.PipeNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class ExternalStorageBE extends BasePipeBE {

    public ExternalStorageBE(BlockPos pos, BlockState state) {
        super(Registration.EXTERNAL_STORAGE_BE.get(), pos, state);
    }

    public void tickServer() {

    }
}
