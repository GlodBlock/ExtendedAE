package com.glodblock.github.extendedae.util;

import java.util.ArrayList;
import java.util.List;

public class SequenceJobs implements Runnable {

    private final List<Runnable> jobs = new ArrayList<>();

    public SequenceJobs add(Runnable job) {
        this.jobs.add(job);
        return this;
    }

    @Override
    public void run() {
        for (var job : this.jobs) {
            job.run();
        }
    }

}
