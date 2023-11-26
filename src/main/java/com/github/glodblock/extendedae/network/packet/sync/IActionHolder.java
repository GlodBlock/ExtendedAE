package com.github.glodblock.extendedae.network.packet.sync;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public interface IActionHolder {

    @NotNull
    Map<String, Consumer<Object[]>> getActionMap();

}
