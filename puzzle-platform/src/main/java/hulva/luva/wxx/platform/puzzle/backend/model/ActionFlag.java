package hulva.luva.wxx.platform.puzzle.backend.model;

public enum ActionFlag {
    NONE(-1), DELETE(0), ADD(1), EDIT(1);

    private int value;

    ActionFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
