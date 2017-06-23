package interaction.util;

import java.io.File;
import java.util.Properties;

/**
 * Created by xiaolin  on 2017/4/1.
 */
@SuppressWarnings("serial")
public class MasterConfiguration extends KattaConfiguration {

    public final static String DEPLOY_POLICY = "master.deploy.policy";
    public final static String SAFE_MODE_MAX_TIME = "safemode.maxTime";

    public MasterConfiguration() {
        super("/katta.master.properties");
    }

    public MasterConfiguration(File file) {
        super(file);
    }

    public MasterConfiguration(Properties properties) {
        super(properties, null);
    }

    public String getDeployPolicy() {
        return getProperty(DEPLOY_POLICY, "net.sf.katta.master.DefaultDistributionPolicy");
    }

}
