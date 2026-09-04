package com.aitrend.analysis.infrastructure.config;

import org.apache.kafka.common.security.auth.SslEngineFactory;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class InsecureSslEngineFactory implements SslEngineFactory {

    private static final TrustManager[] INSECURE_TRUST_MANAGERS = new TrustManager[]{
        new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }
    };

    @Override
    public SSLEngine createClientSslEngine(String peerHost, int peerPort, String endpointIdentification) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, INSECURE_TRUST_MANAGERS, new SecureRandom());
            SSLEngine sslEngine = sslContext.createSSLEngine(peerHost, peerPort);
            sslEngine.setUseClientMode(true);
            return sslEngine;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create insecure SSLEngine for Kafka client", e);
        }
    }

    @Override
    public void configure(Map<String, ?> configs) {}

    @Override
    public SSLEngine createServerSslEngine(String peerHost, int peerPort) {
        return null;
    }

    @Override
    public boolean shouldBeRebuilt(Map<String, Object> nextConfigs) {
        return false;
    }

    @Override
    public java.security.KeyStore keystore() {
        return null;
    }

    @Override
    public java.security.KeyStore truststore() {
        return null;
    }

    @Override
    public Set<String> reconfigurableConfigs() {
        return Collections.emptySet();
    }

    @Override
    public void close() {}
}
