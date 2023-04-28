package info.code2learn.springboot.sampleclient.config;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;


/**
 * @author Benny
 * @description:
 * @date 2023/4/21
 */

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] reqBody, ClientHttpRequestExecution execution) throws IOException {

        LOG.info("Request Headers: {}", request.getHeaders().toString());
        LOG.info("Request body: {}", new String(reqBody, StandardCharsets.UTF_8));

        // 在这里获取请求头信息，确认是否包含客户端证书
        HttpHeaders headers = request.getHeaders();
        List<String> certHeaderValues = headers.get("X-SSL-CERT");
        if (CollectionUtils.isNotEmpty(certHeaderValues)) {
            try {

                String certHeaderValue = certHeaderValues.get(0);
                byte[] certBytes = Base64.getDecoder().decode(certHeaderValue);

                LOG.info("Request certBytes: {}", certBytes);
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate(
                        new ByteArrayInputStream(certBytes));
            } catch (CertificateException e) {
                e.printStackTrace();
            }
        }

        // ...其他操作
        return execution.execute(request, reqBody);
    }


}
