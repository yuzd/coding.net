package coding.net.plugin;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
@Extension
public class CodingWebHook implements UnprotectedRootAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodingWebHook.class);
    public static final String URLNAME = "coding-webhook";

    public static final String URL_VALIDATION_HEADER = "X-Coding-Event";
    public static final String X_INSTANCE_IDENTITY = "X-Instance-Identity";
    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return URLNAME;
    }

    @SuppressWarnings("unused")
    @RequirePostWithCoding
    public void doIndex(@Nonnull String event, @Nonnull String payload) {
        LOGGER.info(payload);
    }
}
