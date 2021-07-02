package phonis.cannontracer.state;

import phonis.cannontracer.networking.CTLine;

public interface LineConsumer {

    void accept(CTLine line);

}
