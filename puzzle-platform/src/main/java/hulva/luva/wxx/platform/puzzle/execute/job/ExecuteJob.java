package hulva.luva.wxx.platform.puzzle.execute.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hulva.luva.wxx.platform.core.JobPlatform;
import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.aop.ExecutePoint;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.ExecuteJobModel;
import hulva.luva.wxx.platform.puzzle.backend.service.JobService;

@Service
public class ExecuteJob {
    private static final Logger LOG = LoggerFactory.getLogger(ExecuteJob.class);

    @Autowired
    JobService jobService;
    
    public void execute(final FlowEntity job) throws Exception {
    	JobPlatform platform = null;
    	PluginConfig config = null;
    	long start = System.currentTimeMillis();
    	try {
    		LOG.info(String.format("Execute job: [%s]", job));
    		LOG.info("\tInitializing...");
    		ExecuteJobModel jobModel = jobService.initJob(job);
    		Assert.isTrue("JOB".equals(jobModel.getType()), "type is not job");
    		LOG.info(String.format("\tDone initializing, takes [%s] ms.", System.currentTimeMillis() - start));
    		config = JobService.buildPluginConfig(jobModel, jobModel.getChildStucts(-1).get(0), null);
    		
    		platform = new JobPlatform(job.getId(), job.getName(), config, jobModel.getContext());
    		platform.start();
		} catch (Exception e) {
			ExecutePoint.onExecuteException(platform.getContext(), config, "Execute Job failed!", e);
			throw new RuntimeException(e);
		} finally {
			platform.close();
		}
    }

}
