package phonis.cannontracer.clienttracer;

import net.minecraft.util.Vec3;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import phonis.cannontracer.networking.CTLineType;

public class EntityLocationChange {

    public final Vec3 oldLocation;
    public final Vec3 newLocation;
    public final CTLineType type;

    public EntityLocationChange(Vec3 oldLocation, Vec3 newLocation, CTLineType type) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.oldLocation.xCoord)
            .append(this.oldLocation.yCoord)
            .append(this.oldLocation.zCoord)
            .append(this.newLocation.xCoord)
            .append(this.newLocation.yCoord)
            .append(this.newLocation.zCoord)
            .append(this.type.ordinal())
            .build();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof EntityLocationChange)) {
            return false;
        }

        EntityLocationChange elc = (EntityLocationChange) other;

        return new EqualsBuilder()
            .append(this.oldLocation.xCoord, elc.oldLocation.xCoord)
            .append(this.oldLocation.yCoord, elc.oldLocation.yCoord)
            .append(this.oldLocation.zCoord, elc.oldLocation.zCoord)
            .append(this.newLocation.xCoord, elc.newLocation.xCoord)
            .append(this.newLocation.yCoord, elc.newLocation.yCoord)
            .append(this.newLocation.zCoord, elc.newLocation.zCoord)
            .append(this.type, elc.type)
            .build();
    }

}
