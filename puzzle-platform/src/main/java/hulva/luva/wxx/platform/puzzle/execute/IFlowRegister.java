package hulva.luva.wxx.platform.puzzle.execute;

import hulva.luva.wxx.platform.puzzle.backend.entity.FlowEntity;

public interface IFlowRegister {
    boolean start(final FlowEntity job) throws Exception;
    boolean isAlive(String id) throws Exception;
    boolean stop(String id) throws Exception;
}
