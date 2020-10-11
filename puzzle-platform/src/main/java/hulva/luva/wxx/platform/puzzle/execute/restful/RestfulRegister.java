package hulva.luva.wxx.platform.puzzle.execute.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hulva.luva.wxx.platform.core.PluginConfig;
import hulva.luva.wxx.platform.core.RestfulPlatform;
import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import hulva.luva.wxx.platform.puzzle.backend.model.ExecuteJobModel;
import hulva.luva.wxx.platform.puzzle.backend.service.JobService;
import hulva.luva.wxx.platform.puzzle.execute.IFlowRegister;

@Service
public class RestfulRegister implements IFlowRegister {

    @Autowired
    JobService jobService;
    
    public boolean start(final FlowEntity job) throws Exception {
        ExecuteJobModel jobModel = jobService.initJob(job);
        Assert.isTrue("RESTFUL".equals(jobModel.getType()), "type is not restful");
        PluginConfig config = JobService.buildPluginConfig(jobModel, jobModel.getChildStucts(-1).get(0), null);
        return RestfulPlatform.start(job.getId(), job.getName(), config, jobModel.getContext());
    }
    
    public boolean stop(String id) throws Exception {
    	return RestfulPlatform.stop(id);
    }
    
    public boolean isAlive(String id) throws Exception {
    	return RestfulPlatform.isAlive(id);
    }
}
