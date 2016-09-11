package coding.net.plugin.webhook;

import coding.net.plugin.CodingWebHook;
import org.kohsuke.stapler.AnnotationHandler;
import org.kohsuke.stapler.InjectedParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
@InjectedParameter(CDEventHeader.PayloadHandler.class)
public  @interface CDEventHeader {
    class PayloadHandler extends AnnotationHandler<CDEventHeader> {

        private static final Logger LOGGER = getLogger(PayloadHandler.class);


        @Override
        public Object parse(StaplerRequest req, CDEventHeader a, Class type, String param) throws ServletException {
            isTrue(CDEvent.class.isAssignableFrom(type),
                    "Parameter '%s' should has type %s, not %s", param,
                    CDEvent.class.getName(),
                    type.getName()
            );

            String header = req.getHeader(CodingWebHook.URL_VALIDATION_HEADER);
            LOGGER.debug("Header {} -> {}", CodingWebHook.URL_VALIDATION_HEADER, header);
            LOGGER.error("{} webhook header" ,header);
            if (header == null) {
                return null;
            }
            try {
                return CDEvent.valueOf(upperCase(header));
            } catch (IllegalArgumentException e) {
                LOGGER.debug("Unknown event - {}", e.getMessage());
                return null;
            }
        }
    }
}
