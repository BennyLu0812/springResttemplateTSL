package info.code2learn.springboot.sampleclient.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;

@Configuration
public class SampleClientConfig {

	private static final Logger LOG = LoggerFactory.getLogger(SampleClientConfig.class);
	
	@Value("${app.key-store-type}")
	private String keyStoreType;
	
	@Value("${app.key-store-password}")
	private String keyStorePassword;
	
	@Value("${app.key-store}")
	private String keyStoreFile;

	@Value("${app.trust-store}")
	private String trustStoreFile;

	@Value("${app.trust-store-password}")
	private String trustStorePassword;

	@Value("${app.trust-store-type}")
	private String trustStoreType;



	/*@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		
		KeyStore keyStore;
		HttpComponentsClientHttpRequestFactory requestFactory = null;
		
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
			ClassPathResource classPathResource = new ClassPathResource(keyStoreFile);
			InputStream inputStream = classPathResource.getInputStream();
			keyStore.load(inputStream, keyStorePassword.toCharArray());

			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder()
					.loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.loadKeyMaterial(keyStore, keyStorePassword.toCharArray()).build(),
					NoopHostnameVerifier.INSTANCE);

			HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
					.setMaxConnTotal(Integer.valueOf(5))
					.setMaxConnPerRoute(Integer.valueOf(5))
					.build();

			requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			requestFactory.setReadTimeout(Integer.valueOf(10000));
			requestFactory.setConnectTimeout(Integer.valueOf(10000));
			
			restTemplate.setRequestFactory(requestFactory);

			// 配置日誌
			List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
			if (CollectionUtils.isEmpty(interceptors)) {
				interceptors = new ArrayList<>();
			}
			interceptors.add(new LoggingInterceptor());
			restTemplate.setInterceptors(interceptors);

		} catch (Exception exception) {
			System.out.println("Exception Occured while creating restTemplate "+exception);
			exception.printStackTrace();
		}
		return restTemplate;
	}*/


	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = null;
		try (
			InputStream keyStoreStream = new FileInputStream(ResourceUtils.getFile(keyStoreFile));
			InputStream trustStoreStream = new FileInputStream(ResourceUtils.getFile(trustStoreFile))
		){
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

			// 客户端证书类型
			KeyStore clientStore = KeyStore.getInstance(keyStoreType);

			// 加载客户端证书，即自己的私钥
			clientStore.load(keyStoreStream, keyStorePassword.toCharArray());

			// 创建密钥管理工厂实例
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			// 初始化客户端密钥库
			keyManagerFactory.init(clientStore, keyStorePassword.toCharArray());
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

			// 创建信任库管理工厂实例
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore trustStore = KeyStore.getInstance(trustStoreType);
			trustStore.load(trustStoreStream, trustStorePassword.toCharArray());

			// 初始化信任库
			trustManagerFactory.init(trustStore);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

			// 建立TLS连接
			SSLContext sslContext = SSLContext.getInstance("TLS");

			// 初始化SSLContext
			sslContext.init(keyManagers, trustManagers, new SecureRandom());

			// INSTANCE 忽略域名检查
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			CloseableHttpClient httpclient = HttpClients
					.custom()
					.setSSLSocketFactory(sslConnectionSocketFactory)
					.setSSLHostnameVerifier(new NoopHostnameVerifier())
					.build();
			requestFactory.setHttpClient(httpclient);
			requestFactory.setConnectTimeout((int) Duration.ofSeconds(15).toMillis());

			// restTemplate
			restTemplate = new RestTemplate(requestFactory);

			// 配置日誌
			List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
			if (CollectionUtils.isEmpty(interceptors)) {
				interceptors = new ArrayList<>();
			}
			interceptors.add(new LoggingInterceptor());
			restTemplate.setInterceptors(interceptors);

		} catch (KeyManagementException | FileNotFoundException | NoSuchAlgorithmException e) {
			LOG.info(e.getMessage());
		} catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException e) {
			LOG.info(e.getMessage());
		}
		return restTemplate;
	}

}
