package coding.net.plugin;

import coding.net.common.XSSApi;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.TransientActionFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class CodingLinkAction implements Action {
    private final transient CodingProjectProperty projectProperty;
    public CodingLinkAction(CodingProjectProperty githubProjectProperty) {
        this.projectProperty = githubProjectProperty;
    }


    @Override
    public String getIconFileName() {
        return "/plugin/github/logov3.png";
    }

    @Override
    public String getDisplayName() {
        return "CodingNet";
    }

    @Override
    public String getUrlName() {
        return  XSSApi.asValidHref(projectProperty.getProjectUrl().baseUrl());
    }

    @SuppressWarnings("rawtypes")
    @Extension
    public static class GithubLinkActionFactory extends TransientActionFactory<Job> {
        @Override
        public Class<Job> type() {
            return Job.class;
        }

        @Override
        public Collection<? extends Action> createFor(Job j) {
            CodingProjectProperty prop = ((Job<?, ?>) j).getProperty(CodingProjectProperty.class);

            if (prop == null) {
                return Collections.emptySet();
            } else {
                return Collections.singleton(new CodingLinkAction(prop));
            }
        }
    }
}
