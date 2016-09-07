package com.sapienter.jbilling.server.mediation.converter.customMediations;

import com.sapienter.jbilling.server.mediation.converter.common.MediationJob;

/**
 * Created by marcomanzicore on 25/11/15.
 */
public class DefaultMediationJob  implements MediationJob {
    private String lineConverter;
    private String resolver;
    private String writer;
    private String job;
    private String recycleJob;

    private DefaultMediationJob() {}

    protected DefaultMediationJob(String job, String lineConverter, String resolver, String writer, String recycleJob) {
        this.lineConverter = lineConverter;
        this.job = job;
        this.resolver = resolver;
        this.writer = writer;
        this.recycleJob = recycleJob;
    }

    public String getResolver() {
        return resolver;
    }

    public String getWriter() {
        return writer;
    }

    public String getJob() {
        return job;
    }

    @Override
    public boolean handleRootRateTables() {
        return false;
    }

    @Override
    public boolean needsInputDirectory() {
        return false;
    }

    public String getLineConverter() {
        return lineConverter;
    }

    public String getRecycleJob() {
        return recycleJob;
    }
}