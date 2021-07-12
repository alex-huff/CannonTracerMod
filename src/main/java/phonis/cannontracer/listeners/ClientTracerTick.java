package phonis.cannontracer.listeners;

import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import phonis.cannontracer.clienttracer.EntityLocation;
import phonis.cannontracer.clienttracer.EntityLocationChange;
import phonis.cannontracer.networking.CTLine;
import phonis.cannontracer.networking.CTLineType;
import phonis.cannontracer.networking.CTVec3;
import phonis.cannontracer.state.CTLineManager;

import javax.annotation.Nullable;
import java.util.*;

public class ClientTracerTick {

    private static final Map<Integer, EntityLocation> locationMap = new HashMap<Integer, EntityLocation>();
    private static final Set<EntityLocationChange> locationChanges = new HashSet<EntityLocationChange>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        WorldClient world = Minecraft.getMinecraft().theWorld;

        if (world == null) {
            return;
        }

        this.setDirty();

        for (EntityFallingBlock fallingBlock :
            world.getEntities(
                EntityFallingBlock.class,
                new Predicate<EntityFallingBlock>() {
                    @Override
                    public boolean apply(@Nullable EntityFallingBlock input) {
                        return true;
                    }
                }
            )
        ) {
            this.handleEntity(fallingBlock);
        }

        for (EntityTNTPrimed tnt :
            world.getEntities(
                EntityTNTPrimed.class,
                new Predicate<EntityTNTPrimed>() {
                    @Override
                    public boolean apply(@Nullable EntityTNTPrimed input) {
                        return true;
                    }
                }
            )
        ) {
            this.handleEntity(tnt);
        }

        this.clearDirty();
        this.drawLines();
        ClientTracerTick.locationChanges.clear();
    }

    private void setDirty() {
        for (EntityLocation location : ClientTracerTick.locationMap.values()) {
            location.keepAlive = false;
        }
    }

    private void clearDirty() {
        List<Integer> toRemove = new ArrayList<Integer>();

        for (Map.Entry<Integer, EntityLocation> entry : ClientTracerTick.locationMap.entrySet()) {
            int id = entry.getKey();
            EntityLocation location = entry.getValue();

            if (!location.keepAlive) {
                toRemove.add(id);
            }
        }

        for (int id : toRemove) {
            ClientTracerTick.locationMap.remove(id);
        }
    }

    private void drawLines() {
        for (EntityLocationChange elc : ClientTracerTick.locationChanges) {
            this.createLine(elc);
        }
    }

    private void handleEntity(Entity entity) {
        EntityLocation entityLocation = ClientTracerTick.locationMap.get(entity.getEntityId());

        if (entityLocation != null) {
            entityLocation.keepAlive = true;
            Vec3 newLocation = entity.getPositionVector();

            if (entityLocation.location.equals(newLocation)) {
                return;
            }

            ClientTracerTick.locationChanges.add(new EntityLocationChange(entityLocation.location, newLocation, entity instanceof EntityTNTPrimed ? CTLineType.TNT : CTLineType.SAND));

            entityLocation.location = newLocation;
        } else {
            ClientTracerTick.locationMap.put(entity.getEntityId(), new EntityLocation(entity));
        }
    }

    private void createLine(EntityLocationChange elc) {
        this.createLine(elc.type, elc.oldLocation, elc.newLocation);
    }

    public void createLine(CTLineType type, Vec3 oldLocation, Vec3 newLocation) {
        CTLineManager.instance.addLocalLine(
            new CTLine(
                new CTVec3(
                    oldLocation.xCoord,
                    oldLocation.yCoord + .49d,
                    oldLocation.zCoord
                ),
                new CTVec3(
                    oldLocation.xCoord,
                    newLocation.yCoord + .49d,
                    oldLocation.zCoord
                ),
                type,
                (short) 10000
            )
        );
        CTLineManager.instance.addLocalLine(
            new CTLine(
                new CTVec3(
                    oldLocation.xCoord,
                    newLocation.yCoord + .49d,
                    oldLocation.zCoord
                ),
                new CTVec3(
                    newLocation.xCoord,
                    newLocation.yCoord + .49d,
                    oldLocation.zCoord
                ),
                type,
                (short) 10000
            )
        );
        CTLineManager.instance.addLocalLine(
            new CTLine(
                new CTVec3(
                    newLocation.xCoord,
                    newLocation.yCoord + .49d,
                    oldLocation.zCoord
                ),
                new CTVec3(
                    newLocation.xCoord,
                    newLocation.yCoord + .49d,
                    newLocation.zCoord
                ),
                type,
                (short) 10000
            )
        );
    }

}
