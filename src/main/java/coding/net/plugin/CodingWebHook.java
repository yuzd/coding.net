package coding.net.plugin;

import coding.net.plugin.subscriber.CDEventsSubscriber;
import coding.net.plugin.webhook.CDEvent;
import coding.net.plugin.webhook.CDEventHeader;
import coding.net.plugin.webhook.CDEventPayload;
import hudson.Extension;
import hudson.model.Job;
import hudson.model.RootAction;
import hudson.model.UnprotectedRootAction;
import hudson.security.csrf.CrumbExclusion;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static coding.net.common.FluentIterableWrapper.from;
import static coding.net.common.JobInfoHelpers.isAlive;
import static coding.net.common.JobInfoHelpers.isBuildable;
import static coding.net.plugin.subscriber.CDEventsSubscriber.isInterestedIn;
import static coding.net.plugin.subscriber.CDEventsSubscriber.processEvent;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
@Extension
public class CodingWebHook implements UnprotectedRootAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodingWebHook.class);
    public static final String URLNAME = "coding-webhook";

    public static final String URL_VALIDATION_HEADER = "X-Coding-Event";

    public static final String PLUGIN_XML_NAME = "coding.net.plugin.CodingProjectProperty.xml";

    public static final String JENKINS_CONFIG_XML_NAME = "config.xml";

    public static final String PLUGIN_XML_NODE_NAME = "coding.net.plugin.CodingProjectProperty_-DescriptorImpl";

    public static final String PLUGIN_CSRF_NODE_NAME = "crumbIssuer";
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
    public void doIndex(@Nonnull @CDEventHeader CDEvent event, @Nonnull @CDEventPayload String payload) {
        from(CDEventsSubscriber.all()).filter(isInterestedIn(event)).transform(processEvent(event, payload)).toList();
    }
    @Extension
    public static class GitlabWebHookCrumbExclusion extends CrumbExclusion {
        @Override
        public boolean process(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith('/' + URLNAME + '/')) {
                chain.doFilter(req, resp);
                return true;
            }
            return false;
        }
    }

    public List<Job> reRegisterAllHooks() {
        return from(getJenkinsInstance().getAllItems(Job.class))
                .filter(isBuildable())
                .filter(isAlive()).toList();
    }

    public static CodingWebHook get() {
        return getJenkinsInstance().getExtensionList(RootAction.class).get(CodingWebHook.class);
    }

    @Nonnull
    public static Jenkins getJenkinsInstance() throws IllegalStateException {
        Jenkins instance = Jenkins.getInstance();
        Validate.validState(instance != null, "Jenkins has not been started, or was already shut down");
        return instance;
    }
}
