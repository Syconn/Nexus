package mod.syconn.nexus.blockentities;

import mod.syconn.nexus.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CraftingInterfaceBE extends AbstractInterfaceBE {

    public CraftingInterfaceBE(BlockPos pos, BlockState state) {
        super(Registration.CRAFTING_INTERFACE_BE.get(), pos, state);
    }
}
