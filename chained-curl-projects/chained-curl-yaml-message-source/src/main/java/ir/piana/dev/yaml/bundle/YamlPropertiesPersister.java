package ir.piana.dev.yaml.bundle;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.IOException;
import java.util.Properties;

public class YamlPropertiesPersister extends DefaultPropertiesPersister {
    public static final YamlPropertiesPersister INSTANCE = new YamlPropertiesPersister();

    public void load(Properties props, String resourceName) throws IOException {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ClassPathResource(resourceName));
        props.putAll(bean.getObject());
    }

    public void load(Properties props, Resource resource) throws IOException {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(resource);
        props.putAll(bean.getObject());
    }
}
