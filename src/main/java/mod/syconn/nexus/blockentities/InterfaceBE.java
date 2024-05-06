package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InterfaceBE extends AbstractInterfaceBE {

    public InterfaceBE(BlockPos pos, BlockState state) {
        super(Registration.INTERFACE_BE.get(), pos, state);
    }
}
