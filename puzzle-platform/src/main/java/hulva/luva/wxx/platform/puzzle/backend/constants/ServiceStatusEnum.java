package hulva.luva.wxx.platform.puzzle.backend.constants;

public enum ServiceStatusEnum {
    RUNNING("running",1), NO_STATUS("the flow have no status",0), STOP("stop",-1), ERROR("error",-2);

    private String status;
    private int code;

    private ServiceStatusEnum(String status, int code) {
        this.status = status;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ServiceStatusEnum getStatusByCode(int code) {
        for (ServiceStatusEnum serviceStatus : ServiceStatusEnum.values()) {
            if (serviceStatus.getCode() == code) {
                return serviceStatus;
            }
        }
        return null;
    }

    public static int getStatusCodeByStatus(String status) {
        return ServiceStatusEnum.valueOf(status.toUpperCase()).getCode();
    }

}
