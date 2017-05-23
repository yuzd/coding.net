package coding.net.common;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * @author lanwen (Merkushev Kirill)
 */
@Restricted(NoExternalUse.class)
public final class XSSApi {
    private static final Logger LOG = LoggerFactory.getLogger(XSSApi.class);

    private XSSApi() {
    }

    /**
     * Method to filter invalid url for XSS. This url can be inserted to href safely
     *
     * @param urlString unsafe url
     *
     * @return safe url
     */
    public static String asValidHref(String urlString) {
        try {
            return new URL(urlString).toExternalForm();
        } catch (MalformedURLException e) {
            LOG.debug("Malformed url - {}, empty string will be returned", urlString);
            return "";
        }
    }

    public static HttpURLConnection load(String urlString, String userName,String password,boolean post) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (post){
            connection.setRequestMethod("POST");
        }
//        if (null != userName && !"".equals(userName)) {
//            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((userName + ":" + password).getBytes("UTF-8")));
//        }
        if (300 < connection.getResponseCode()) {
            throw new IOException("remote build fail, wrong response code " + connection.getResponseCode());
        }
        return connection;
    }
}
