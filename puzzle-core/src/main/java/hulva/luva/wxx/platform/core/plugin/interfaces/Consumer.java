package hulva.luva.wxx.platform.core.plugin.interfaces;

import hulva.luva.wxx.platform.core.exception.PluginException;

@FunctionalInterface
public interface Consumer<T> {
    T accept(T t) throws PluginException;
}
