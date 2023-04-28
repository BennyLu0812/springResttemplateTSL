package info.code2learn.springboot.sampleserver.config;

import org.apache.tomcat.jni.SSLSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import javax.net.ssl.SSLSession;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * @author Benny
 * @description:
 * @date 2023/4/25
 */
public class DemoInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DemoInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs != null && certs.length > 0) {
            // 您可以在此處對客戶端證書進行操作，例如提取信息、驗證等。
            System.out.println("1Client certificate subject: " + certs[0].getSubjectDN());
            logger.info("1Client certificate subject:{}", certs[0].getSubjectDN());
        }
        ServletContext servletContext= request.getSession().getServletContext();

        if (servletContext != null) {
            X509Certificate[] certs2 = (X509Certificate[]) servletContext.getAttribute("javax.servlet.request.X509Certificate.CERTIFICATE");
            if (certs2 != null && certs2.length > 0) {
                // 您可以在此處對客戶端證書進行操作，例如提取信息、驗證等。
                System.out.println("2Client certificate subject: " + certs2[0].getSubjectDN());
                logger.info("2Client certificate subject:{}", certs2[0].getSubjectDN());
            }
        }

        logger.info("DemoInterceptor certificate end");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
