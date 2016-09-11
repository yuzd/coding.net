package coding.net.plugin.subscriber;

import coding.net.plugin.webhook.CDEvent;
import hudson.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static coding.net.plugin.webhook.CDEvent.PING;
import static com.google.common.collect.Sets.immutableEnumSet;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
public class PingCDEventSubscriber extends CDEventsSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingCDEventSubscriber.class);

    @Override
    protected boolean isApplicable(Job<?, ?> project) {
        return false;
    }


    @Override
    protected Set<CDEvent> events() {
        return immutableEnumSet(PING);
    }

    @Override
    protected void onEvent(CDEvent event, String payload) {
        LOGGER.info("{} webhook received from repo <{}>!", event, payload);
    }
}
