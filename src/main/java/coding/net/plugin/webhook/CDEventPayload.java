package coding.net.plugin.webhook;

import coding.net.plugin.misc.NullSafeFunction;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.AnnotationHandler;
import org.kohsuke.stapler.InjectedParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
@InjectedParameter(CDEventPayload.PayloadHandler.class)
public @interface CDEventPayload {
    class PayloadHandler extends AnnotationHandler<CDEventPayload> {
        private static final Logger LOGGER = getLogger(PayloadHandler.class);

        public static final String APPLICATION_JSON = "application/json; charset=UTF-8";
        public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

        private static final Map<String, Function<StaplerRequest, String>> PAYLOAD_PROCESS =
                ImmutableMap.<String, Function<StaplerRequest, String>>builder()
                        .put(APPLICATION_JSON, fromApplicationJson())
                        .put(FORM_URLENCODED, fromForm())
                        .build();


        @Override
        public Object parse(StaplerRequest req, CDEventPayload a, Class type, String param) throws ServletException {
//            if (notNull(req, "Why StaplerRequest is null?").getHeader(CodingWebHook.URL_VALIDATION_HEADER) != null) {
//                // if self test for custom hook url
//                return null;
//            }

            String contentType = req.getContentType();

            if (!PAYLOAD_PROCESS.containsKey(contentType)) {
                LOGGER.error("Unknown content type {}", contentType);
                return null;
            }
            String payload = PAYLOAD_PROCESS.get(contentType).apply(req);

            return payload;
        }

        /**
         * used for application/x-www-form-urlencoded content-type
         *
         * @return function to extract payload from form request parameters
         */
        protected static Function<StaplerRequest, String> fromForm() {
            return new NullSafeFunction<StaplerRequest, String>() {
                @Override
                protected String applyNullSafe(@Nonnull StaplerRequest request) {
                    return request.getParameter("payload");
                }
            };
        }

        /**
         * used for application/json content-type
         *
         * @return function to extract payload from body
         */
        protected static Function<StaplerRequest, String> fromApplicationJson() {
            return new NullSafeFunction<StaplerRequest, String>() {
                @Override
                protected String applyNullSafe(@Nonnull StaplerRequest request) {
                    try {
                        return IOUtils.toString(request.getInputStream(), Charsets.UTF_8);
                    } catch (IOException e) {
                        LOGGER.error("Can't get payload from request: {}", e.getMessage());
                        return null;
                    }
                }
            };
        }
    }
}
