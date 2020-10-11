package hulva.luva.wxx.platform.core.aop;

import hulva.luva.wxx.platform.core.Context;
import hulva.luva.wxx.platform.core.PluginConfig;

@FunctionalInterface
public interface MailListener{
    void send(Context context, PluginConfig config, String message, Throwable throwable);
}