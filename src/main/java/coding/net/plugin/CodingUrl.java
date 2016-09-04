package coding.net.plugin;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public final class CodingUrl {

    private static String normalize(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // Strip "/tree/..."
        if (url.contains("/tree/")) {
            url = url.replaceFirst("/tree/.*$", "");
        }
        if (!url.endsWith("/")) {
            url += '/';
        }
        return url;
    }

    private final String baseUrl;

    CodingUrl(final String input) {
        this.baseUrl = normalize(input);
    }

    @Override
    public String toString() {
        return this.baseUrl;
    }

    public String baseUrl() {
        return this.baseUrl;
    }

    public String commitId(final String id) {
        return new StringBuilder().append(baseUrl).append("commit/").append(id).toString();
    }
}
