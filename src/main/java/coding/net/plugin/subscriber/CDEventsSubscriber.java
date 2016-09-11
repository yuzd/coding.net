package coding.net.plugin.subscriber;

import coding.net.plugin.misc.NullSafeFunction;
import coding.net.plugin.misc.NullSafePredicate;
import coding.net.plugin.webhook.CDEvent;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import hudson.ExtensionPoint;
import hudson.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
public abstract class CDEventsSubscriber implements ExtensionPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CDEventsSubscriber.class);


    protected abstract boolean isApplicable(@Nullable Job<?, ?> project);


    protected abstract Set<CDEvent> events();


    protected void onEvent(CDEvent event, String payload) {
        // do nothing by default
    }

    /**
     * @return All subscriber extensions
     */
    public static List<CDEventsSubscriber> all() {
        List<CDEventsSubscriber> list = new ArrayList<CDEventsSubscriber>();
        list.add(new PingCDEventSubscriber());
        list.add(new PushCDEventSubscriber());
        return list;
    }


    public static Function<CDEventsSubscriber, Set<CDEvent>> extractEvents() {
        return new NullSafeFunction<CDEventsSubscriber, Set<CDEvent>>() {
            @Override
            protected Set<CDEvent> applyNullSafe(@Nonnull CDEventsSubscriber subscriber) {
                return defaultIfNull(subscriber.events(), Collections.<CDEvent>emptySet());
            }
        };
    }


    public static Predicate<CDEventsSubscriber> isApplicableFor(final Job<?, ?> project) {
        return new NullSafePredicate<CDEventsSubscriber>() {
            @Override
            protected boolean applyNullSafe(@Nonnull CDEventsSubscriber subscriber) {
                return subscriber.isApplicable(project);
            }
        };
    }


    public static Predicate<CDEventsSubscriber> isInterestedIn(final CDEvent event) {
        return new NullSafePredicate<CDEventsSubscriber>() {
            @Override
            protected boolean applyNullSafe(@Nonnull CDEventsSubscriber subscriber) {
                return defaultIfNull(subscriber.events(), emptySet()).contains(event);
            }
        };
    }


    public static Function<CDEventsSubscriber, Void> processEvent(final CDEvent event, final String payload) {
        return new NullSafeFunction<CDEventsSubscriber, Void>() {
            @Override
            protected Void applyNullSafe(@Nonnull CDEventsSubscriber subscriber) {
                try {
                    subscriber.onEvent(event, payload);
                } catch (Throwable t) {
                    LOGGER.error("Subscriber {} failed to process {} hook, skipping...",
                            subscriber.getClass().getName(), event, t);
                }
                return null;
            }
        };
    }
}