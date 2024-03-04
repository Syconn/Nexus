package mod.syconn.nexus.util;

import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable {
    INPUT,
    OUTPUT,
    CABLE,
    NONE;

    public String getSerializedName() {
        return name().toLowerCase();
    }
}
