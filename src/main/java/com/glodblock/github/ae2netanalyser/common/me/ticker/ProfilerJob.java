package com.glodblock.github.ae2netanalyser.common.me.ticker;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.GlobalPos;

public class ProfilerJob {

    private long waitingTicks;
    private final Object2ReferenceMap<GlobalPos, GridTickProfiler> results = new Object2ReferenceOpenHashMap<>();

    public ProfilerJob(long waitingTicks) {
        this.waitingTicks = waitingTicks;
    }

    public boolean tick(GlobalPos pos, long ns, long tick) {
        this.waitingTicks -= ns;
        var profiler = this.results.get(pos);
        if (profiler == null) {
            profiler = new GridTickProfiler();
            profiler.start();
            this.results.put(pos, profiler);
        }
        profiler.update(tick, ns);
        return this.waitingTicks <= 0;
    }

    public ProfileData generateData() {
        return new ProfileData(this.results.entrySet()
                .stream()
                .map(e -> new ProfileData.ATick(e.getKey(), e.getValue().rate() / 1000))
                .toArray(ProfileData.ATick[]::new)
        );
    }

}
