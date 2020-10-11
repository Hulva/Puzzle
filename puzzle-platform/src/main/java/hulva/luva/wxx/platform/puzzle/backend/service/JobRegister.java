package hulva.luva.wxx.platform.puzzle.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;
import hulva.luva.wxx.platform.puzzle.execute.IFlowRegister;
import hulva.luva.wxx.platform.puzzle.execute.job.ExecuteJob;
import hulva.luva.wxx.platform.puzzle.execute.restful.RestfulRegister;
import hulva.luva.wxx.platform.puzzle.execute.service.ServiceRegister;

@Service
public class JobRegister implements IFlowRegister {

	@Autowired
	RestfulRegister restfulRegister;
	@Autowired
	ServiceRegister serviceRegister;
	@Autowired
	ExecuteJob jobExecutor;

	@Override
	public boolean start(FlowEntity job) throws Exception {
		switch (job.getTemplate().getType()) {
		case "JOB":
			if (job.getEnable()) {
				jobExecutor.execute(job);
				return true;
			}
			return false;
		case "SERVICE": {
			serviceRegister.stop(job.getId());
			if (job.getEnable()) {
				return serviceRegister.start(job);
			}
			break;
		}
		case "RESTFUL": {
			restfulRegister.stop(job.getId());
			if (job.getEnable()) {
				return restfulRegister.start(job);
			}
			break;
		}
		default:
			throw new IllegalStateException("Unsupported value: " + job.getTemplate().getType());
		}
		return false;
	}

	@Override
	public boolean isAlive(String id) throws Exception {
		if (serviceRegister.isAlive(id)) {
			return true;
		}
		if (restfulRegister.isAlive(id)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean stop(String id) throws Exception {
		serviceRegister.stop(id);
		restfulRegister.stop(id);
		return true;
	}
}
