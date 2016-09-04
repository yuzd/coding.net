package coding.net.plugin;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.Interceptor;
import org.kohsuke.stapler.interceptor.InterceptorAnnotation;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static org.kohsuke.stapler.HttpResponses.error;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD})
@InterceptorAnnotation(RequirePostWithCoding.Processor.class)
public @interface RequirePostWithCoding {
    class Processor extends Interceptor {
        private static final Logger LOGGER = getLogger(Processor.class);


        @Override
        public Object invoke(StaplerRequest request, StaplerResponse response, Object instance, Object[] arguments) throws IllegalAccessException, InvocationTargetException {
            shouldBePostMethod(request);
            return target.invoke(request, response, instance, arguments);
        }

        protected void shouldBePostMethod(StaplerRequest request) throws InvocationTargetException {
            if (!request.getMethod().equals("POST")) {
                throw new InvocationTargetException(error(SC_METHOD_NOT_ALLOWED, "Method POST required"));
            }
        }


    }
}
