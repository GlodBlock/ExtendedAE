package com.glodblock.github.ae2netanalyser.common.me.ticker;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.GlobalPos;

import java.util.concurrent.TimeUnit;

public class ProfilerJob {

    private final long waitingTicks;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private final Object2ReferenceMap<GlobalPos, GridTickProfiler> results = new Object2ReferenceOpenHashMap<>();

    public ProfilerJob(long waitingTicks) {
        this.waitingTicks = waitingTicks;
        this.stopwatch.start();
    }

    public void tick(GlobalPos pos, long ns, long tick) {
        var profiler = this.results.get(pos);
        if (profiler == null) {
            profiler = new GridTickProfiler();
            profiler.start();
            this.results.put(pos, profiler);
        }
        profiler.update(tick, ns);
    }

    public boolean isFinished() {
        return this.waitingTicks <= this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
    }

    public ProfileData generateData() {
        return new ProfileData(this.results.entrySet()
                .stream()
                .map(e -> new ProfileData.ATick(e.getKey(), e.getValue().rate() / 1000))
                .toArray(ProfileData.ATick[]::new)
        );
    }

}
