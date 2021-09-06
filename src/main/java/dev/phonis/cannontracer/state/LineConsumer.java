package dev.phonis.cannontracer.state;

import dev.phonis.cannontracer.networking.CTLine;

public interface LineConsumer {

    void accept(CTLine line);

}
