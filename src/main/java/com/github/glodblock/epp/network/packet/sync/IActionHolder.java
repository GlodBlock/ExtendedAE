package com.github.glodblock.epp.network.packet.sync;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public interface IActionHolder {

    @Nonnull
    Map<String, Consumer<Object[]>> getActionMap();

}
