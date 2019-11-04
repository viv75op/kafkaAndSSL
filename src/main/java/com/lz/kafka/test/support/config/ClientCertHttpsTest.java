package com.lz.kafka.test.support.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClientCertHttpsTest {
	
	String certPath = "/key/client_cert.crt";
	String rsaKeyPath = "/key/client_cert.key";
	private OkHttpClient okhttpClient;
	
	@Before
	@SuppressWarnings("deprecation")
	public void init() throws IOException, CertificateException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			UnrecoverableKeyException, KeyManagementException, KeyStoreException {
		//必须添加
		Security.addProvider(new BouncyCastleProvider());
		
		Certificate cert = initCert();//初始化证书
		PrivateKey rsaPrivateKey = initRsaPrivateKey();//初始化rsa私钥
		SSLContext sslContext = initSslContext(cert, rsaPrivateKey);//初始化sslcontext
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		
		OkHttpClient.Builder okhttp3Builder = new OkHttpClient.Builder();
		this.okhttpClient = okhttp3Builder.sslSocketFactory(sslSocketFactory)
				.hostnameVerifier((hostName, session) -> true)
				.build();
	}
	
	private Certificate initCert() throws IOException, CertificateException {
		String certContent = null;
		try(InputStream certIsm = ClientCertHttpsTest.class.getResourceAsStream(certPath)) {
			certContent = IOUtils.toString(certIsm, StandardCharsets.UTF_8);
		}
		
		certContent = StringUtils.remove(certContent, "-----BEGIN CERTIFICATE-----");
		certContent = StringUtils.remove(certContent, "-----END CERTIFICATE-----");
		
		byte[] certBytes = Base64.decodeBase64(certContent);
		
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		return factory.generateCertificate(new ByteArrayInputStream(certBytes));
	}
	
	private PrivateKey initRsaPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String rsaPrivateKeyContent = null;
		try(InputStream keyIsm = ClientCertHttpsTest.class.getResourceAsStream(rsaKeyPath)){
			rsaPrivateKeyContent = IOUtils.toString(keyIsm, StandardCharsets.UTF_8);
		}
		
		rsaPrivateKeyContent = StringUtils.remove(rsaPrivateKeyContent, "-----BEGIN RSA PRIVATE KEY-----");
		rsaPrivateKeyContent = StringUtils.remove(rsaPrivateKeyContent, "-----END RSA PRIVATE KEY-----");
		byte[] privateKeyBytes = Base64.decodeBase64(rsaPrivateKeyContent);
		
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(spec);
	}
	
	private SSLContext initSslContext(Certificate cert, PrivateKey rsaPrivateKey) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException{
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(null);
		keystore.setCertificateEntry("sscp_cert", cert);
		keystore.setKeyEntry("sscp_key", rsaPrivateKey, "".toCharArray(), new Certificate[]{cert});
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keystore, "".toCharArray());
		
		KeyManager[] km = kmf.getKeyManagers();
		
		TrustManager trustAllManager = new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		};
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(km, new TrustManager[]{trustAllManager}, new SecureRandom());
		return sslContext;
	}
	
	@Test
	public void testRequest() throws IOException {
		Request request = new Request.Builder()
			.url("https://47.107.113.91:443/auth/realms/sscp/protocol/openid-connect/token")
			.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "grant_type=password&client_id=sscp"))
			.build();
		Response response = okhttpClient.newCall(request).execute();
		String respStr = response.body().string();
		System.out.println("resp:\t" + respStr);
	}

}