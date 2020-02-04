package de.adorsys.opba.restapi.shared;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") //Checkstyle doesn't recognise Lombok
public class HttpHeaders {
    public static final String PSU_CONSENT_SESSION = "PSU-Consent-Session";
    public static final String X_REQUEST_ID = "X-Request-ID";

    @UtilityClass
    public class UserAgentContext {
        public static final String PSU_IP_ADDRESS = "PSU-IP-Address";
        public static final String PSU_IP_PORT = "PSU-IP-Port";
        public static final String PSU_ACCEPT = "PSU-Accept";
        public static final String PSU_ACCEPT_CHARSET = "PSU-Accept-Charset";
        public static final String PSU_ACCEPT_ENCODING = "PSU-Accept-Encoding";
        public static final String PSU_ACCEPT_LANGUAGE = "PSU-Accept-Language";
        public static final String PSU_DEVICE_ID = "PSU-Device-ID";
        public static final String PSU_USER_AGENT = "PSU-User-Agent";
        public static final String PSU_GEO_LOCATION = "PSU-Geo-Location";
        public static final String PSU_HTTP_METHOD = "PSU-Http-Method";
    }
}