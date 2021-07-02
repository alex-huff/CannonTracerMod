package phonis.cannontracer.state;

import phonis.cannontracer.networking.CTArtifact;
import phonis.cannontracer.networking.CTLine;
import phonis.cannontracer.networking.CTLineType;
import phonis.cannontracer.networking.CTNewLines;

import java.util.*;

public class CTLineManager {

    public static final CTLineManager instance = new CTLineManager();
    private static Set<CTLineType> tntTypes;
    private static Set<CTLineType> sandTypes;
    private static Set<CTLineType> playerTypes;

    static {
        CTLineManager.tntTypes = new TreeSet<CTLineType>();

        CTLineManager.tntTypes.add(CTLineType.TNT);
        CTLineManager.tntTypes.add(CTLineType.TNTENDPOS);

        CTLineManager.sandTypes = new TreeSet<CTLineType>();

        CTLineManager.sandTypes.add(CTLineType.SAND);
        CTLineManager.sandTypes.add(CTLineType.SANDENDPOS);

        CTLineManager.playerTypes = new TreeSet<CTLineType>();

        CTLineManager.playerTypes.add(CTLineType.PLAYER);
    }

    private final Map<UUID, List<CTLine>> lineMap = new TreeMap<UUID, List<CTLine>>();

    public synchronized void forEachLineInCurrentWorld(LineConsumer consumer) {
        UUID currentWorld = CTState.currentWorld;

        if (currentWorld == null) return;

        List<CTLine> lines = this.getLinesForWorld(currentWorld);

        for (CTLine line : lines) {
            consumer.accept(line);

            for (CTArtifact artifact : line.artifactList) {
                for (CTLine artifactLine : artifact.getLines()) {
                    consumer.accept(artifactLine);
                }
            }
        }
    }

    private List<CTLine> getLinesForWorld(UUID world) {
        List<CTLine> lines = this.lineMap.get(world);

        if (lines == null) {
            lines = new ArrayList<CTLine>();

            this.lineMap.put(world, lines);
        }

        return lines;
    }

    public synchronized void addLines(CTNewLines ctNewLines) {
        this.getLinesForWorld(ctNewLines.world).addAll(ctNewLines.lines);
    }

    public synchronized void onTick() {
        for (UUID world : this.lineMap.keySet()) {
            List<CTLine> lines = this.lineMap.get(world);
            Iterator<CTLine> iterator = lines.iterator();

            while(iterator.hasNext()) {
                CTLine line = iterator.next();

                if (line.decrementAndGet() == 0) {
                    iterator.remove();
                }
            }
        }
    }

    public synchronized void clearByType(CTLineType type) {
        if (type.equals(CTLineType.ALL)) {
            this.lineMap.clear();

            return;
        }

        if (type.equals(CTLineType.TNT)) {
            this.clearByTypes(CTLineManager.tntTypes);
        } else if (type.equals(CTLineType.SAND)) {
            this.clearByTypes(CTLineManager.sandTypes);
        } else if (type.equals(CTLineType.PLAYER)){
            this.clearByTypes(CTLineManager.playerTypes);
        }
    }

    public synchronized void clearByTypes(Set<CTLineType> types) {
        for (UUID world : this.lineMap.keySet()) {
            List<CTLine> lines = this.lineMap.get(world);
            Iterator<CTLine> iterator = lines.iterator();

            while(iterator.hasNext()) {
                CTLine line = iterator.next();

                if (types.contains(line.type)) {
                    iterator.remove();
                }
            }
        }
    }

}
