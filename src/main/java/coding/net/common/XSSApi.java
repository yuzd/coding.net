package coding.net.common;

import org.apache.commons.codec.binary.Base64;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static String load(String urlString, String userName,String password,boolean post,String crumbField,String crumbToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (post){
            connection.setRequestMethod("POST");
        }
        if (null != userName && !"".equals(userName)) {
            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((userName + ":" + password).getBytes("UTF-8")));
        }
        if(null != crumbField && null != crumbToken){
            connection.setRequestProperty(crumbField,crumbToken);
        }

        if (300 < connection.getResponseCode()) {
            throw new IOException(urlString + " remote build fail, wrong response code " + connection.getResponseCode());
        }
        return readFullyAsString(connection);
    }

    public static String readFullyAsString(HttpURLConnection connection) throws IOException {
        return readFully(connection.getInputStream()).toString("UTF-8");
    }

    private static ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }
}
