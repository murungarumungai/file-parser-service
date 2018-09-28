package application;

import jodd.props.Props;
import jodd.props.PropsEntry;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

public class ApplicationContext {

    private static ApplicationContext applicationContext;

    @Getter public static Props props = null;
    private static Logger log = LogManager.getLogger(ApplicationContext.class);

    public synchronized static ApplicationContext getInstance() {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }

        getConfig();
        return applicationContext;
    }

    private static Props getConfig() {

        if (props == null) {
            props = new Props();

            try {

                String filename = "config.properties";

                @Cleanup InputStream input = new FileInputStream(filename);

                if (input == null) {
                    log.warn("Unable to find " + filename);
                    return null;
                }

                props.load(input);

                for (String profile : props.getAllProfiles()) {
                    props.setActiveProfiles(profile);
                }

                Iterator<PropsEntry> e = props.iterator();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return props;
    }
}
