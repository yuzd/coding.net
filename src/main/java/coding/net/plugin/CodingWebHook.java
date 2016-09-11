package coding.net.plugin;

import coding.net.plugin.subscriber.CDEventsSubscriber;
import coding.net.plugin.webhook.CDEvent;
import coding.net.plugin.webhook.CDEventHeader;
import coding.net.plugin.webhook.CDEventPayload;
import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static coding.net.common.FluentIterableWrapper.from;
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
}
