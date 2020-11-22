package com.purini.fw.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

    private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    /**
     * Validate the Url against the standard Java URL
     * and ensures only http or https protocol
     * @param urlString - url to validate
     * @return true if valid
     */
    public static boolean isValid(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
            return isHttp(url);
        } catch (MalformedURLException e) {
            //Ignore
        }
        return false;
    }

    /**
     * Get host port string
     * @param urlString url
     * @return null if url is malformed, if port is present then returns host:port otherwise just host
     */
    @Nullable
    public static String getHostPort(String urlString) {
        URL url = null;
        String hostPort = null;
        try {
            url = new URL(urlString);
            hostPort = url.getHost();
            final int port = url.getPort();
            if (port != -1) {
                hostPort = hostPort + ":" + port;
            }
        } catch (MalformedURLException e) {
            //Ignore malformed urls
        }
        return hostPort;
    }

    /**
     * Get protocol
     * @param urlString url
     * @return null if url is malformed, otherwise protocol
     */
    @Nullable
    public static String getProtocol(String urlString) {
        URL url = null;
        String protocol = null;
        try {
            url = new URL(urlString);
            protocol = url.getProtocol();
        } catch (MalformedURLException e) {
            //Ignore malformed urls
        }
        return protocol;
    }

    /**
     * For url from relative url
     * @param hostPort hostPort e.g. abc.com:8080
     * @param protocol http ot https
     * @param relativeUrl e.g. /about.html
     * @return complete url
     */
    public static String formUrlFromRelativeUrl(String hostPort, String protocol, String relativeUrl) {
        return protocol + "://" + hostPort + relativeUrl;
    }

    /**
     * Validate the Url against the standard Java URL
     *
     * @param url - url to validated
     * @return true if valid
     */
    private static boolean isHttp(URL url) {
        return url.getProtocol().equalsIgnoreCase("http") || url.getProtocol().equalsIgnoreCase("https");
    }

}
