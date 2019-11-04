/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.lz.kafka.test.support.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.*;

import org.springframework.core.io.Resource;

import javax.net.ssl.X509TrustManager;
import java.security.*;

/**
 * @author yougao
 * @version : OkHttp3Config, v 0.1 2019-11-02 1:40 下午 yougao Exp $
 * @date 2019/11/02
 */
@Configuration
public class OkHttp3Config {

    //证书类型
    private static final String KEY_STORE_TYPE_P12 = "pkcs12";

    //证书密码（客户端证书密码）
    private static final String KEY_STORE_PASSWORD = "123456";

    @Value("classpath:config/key/mep-auth-cert.p12")
    private Resource resource;

    @Bean
    public SSLSocketFactory trustedSSLSocketFactory(X509TrustManager x509TrustManager) throws IOException, GeneralSecurityException {

        KeyManager[] getKeyManagers = getKeyManagers(resource.getInputStream());
        /*
         * 默认信任所有的证书 TODO 最好加上证书认证，主流App都有自己的证书
         */
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(getKeyManagers, new TrustManager[]{x509TrustManager}, new SecureRandom());

        return sslContext.getSocketFactory();
    }

    /**
     * 客户端证书
     *
     * @param in
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private KeyManager[] getKeyManagers(InputStream in) throws GeneralSecurityException, IOException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = getKeyStore(in);
        keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        return keyManagers;
    }

    /**
     * get key store
     *
     * @param in
     * @return
     * @throws GeneralSecurityException
     */
    private KeyStore getKeyStore(InputStream in) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
