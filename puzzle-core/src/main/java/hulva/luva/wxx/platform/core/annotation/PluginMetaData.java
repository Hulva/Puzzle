package hulva.luva.wxx.platform.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hulva.luva.wxx.platform.core.plugin.metadata.PluginMetadata.PluginType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PluginMetaData {
	String name();

	String aliasName() default "";

	String jarName() default "";

//	String className();

	PluginType type();

	int version();
}
