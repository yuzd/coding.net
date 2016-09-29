package coding.net.plugin;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class CodingProjectProperty extends JobProperty<Job<?, ?>> {

    private String projectUrl;
    private String displayName;
    @DataBoundConstructor
    public CodingProjectProperty(String projectUrlStr) {
        this.projectUrl =projectUrlStr;
    }

    public String getProjectUrlStr() {
        return projectUrl;
    }

    public CodingUrl getProjectUrl() {
        return new CodingUrl(projectUrl);
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    @CheckForNull
    public String getDisplayName() {
        return displayName;
    }

    @DataBoundSetter
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static String displayNameFor(@Nonnull Job<?, ?> job) {
        CodingProjectProperty ghProp = job.getProperty(CodingProjectProperty.class);
        if (ghProp != null && isNotBlank(ghProp.getDisplayName())) {
            return ghProp.getDisplayName();
        }

        return job.getFullName();
    }



    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {
        private static final Logger LOGGER = Logger.getLogger(CodingProjectProperty.class.getName());

        public static final String CODING_PROJECT_BLOCK_NAME = "codingProject";

        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        private String login;
        public String getLogin() {
            return login;
        }

        private String password;
        public String getPassword() {
            return password;
        }


        public DescriptorImpl() {
            load();
        }
        @Override
        public String getDisplayName() {
            return "Coding project page";
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            CodingProjectProperty tpp = req.bindJSON(
                    CodingProjectProperty.class,
                    formData.getJSONObject(CODING_PROJECT_BLOCK_NAME)
            );

            if (tpp == null) {
                LOGGER.fine("Couldn't bind JSON");
                return null;
            }

            if (tpp.projectUrl == null) {
                LOGGER.fine("projectUrl not found, nullifying CodingProjectProperty");
                return null;
            }

            return tpp;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            login = formData.getString("login");
            password = formData.getString("password");
            save();
            return super.configure(req,formData);
        }
    }
}
