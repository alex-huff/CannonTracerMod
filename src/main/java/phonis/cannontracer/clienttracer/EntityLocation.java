package phonis.cannontracer.clienttracer;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class EntityLocation {

    public Vec3 location;
    public boolean keepAlive;

    public EntityLocation(double x, double y, double z) {
        this.location = new Vec3(x, y, z);
        this.keepAlive = true;
    }

    public EntityLocation(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

}
