/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*

package com.todostudy.iot.mqtt.server.handler;

import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.msg.EncryptionUtil;
import org.thingsboard.server.common.transport.TransportService;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import org.thingsboard.server.common.transport.config.ssl.SslCredentials;
import org.thingsboard.server.common.transport.config.ssl.SslCredentialsConfig;
import org.thingsboard.server.common.transport.util.SslUtil;
import org.thingsboard.server.gen.transport.TransportProtos;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

*/
/**
 * Created by valerii.sosliuk on 11/6/16.
 *//*

@Slf4j
@Component("MqttSslHandlerProvider")
@ConditionalOnProperty(prefix = "transport.mqtt.ssl", value = "enabled", havingValue = "true", matchIfMissing = false)
public class MqttSslProvider {

    @Value("${transport.mqtt.ssl.protocol}")
    private String sslProtocol;


    private SSLContext sslContext;

    public SslHandler getSslHandler() {
        if (sslContext == null) {
            sslContext = createSslContext();
        }
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(false);
        sslEngine.setWantClientAuth(true);
        sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        sslEngine.setEnableSessionCreation(true);
        return new SslHandler(sslEngine);
    }

    private SSLContext createSslContext() {
        try {
            SslCredentials sslCredentials = this.mqttSslCredentialsConfig.getCredentials();
            TrustManagerFactory tmFactory = sslCredentials.createTrustManagerFactory();
            KeyManagerFactory kmf = sslCredentials.createKeyManagerFactory();

            KeyManager[] km = kmf.getKeyManagers();
            TrustManager x509wrapped = getX509TrustManager(tmFactory);
            TrustManager[] tm = {x509wrapped};
            if (StringUtils.isEmpty(sslProtocol)) {
                sslProtocol = "TLS";
            }
            SSLContext sslContext = SSLContext.getInstance(sslProtocol);
            sslContext.init(km, tm, null);
            return sslContext;
        } catch (Exception e) {
            log.error("Unable to set up SSL context. Reason: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get SSL context", e);
        }
    }

    private TrustManager getX509TrustManager(TrustManagerFactory tmf) throws Exception {
        X509TrustManager x509Tm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                x509Tm = (X509TrustManager) tm;
                break;
            }
        }
        return new ThingsboardMqttX509TrustManager(x509Tm, transportService);
    }

    static class ThingsboardMqttX509TrustManager implements X509TrustManager {

        private final X509TrustManager trustManager;

        ThingsboardMqttX509TrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return trustManager.getAcceptedIssuers();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
            trustManager.checkServerTrusted(chain, authType);
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
            String credentialsBody = null;
            for (X509Certificate cert : chain) {
                try {
                    String strCert = SslUtil.getCertificateString(cert);
                    String sha3Hash = EncryptionUtil.getSha3Hash(strCert);
                    final String[] credentialsBodyHolder = new String[1];
                    CountDownLatch latch = new CountDownLatch(1);
                   */
/* transportService.process(DeviceTransportType.MQTT, TransportProtos.ValidateDeviceX509CertRequestMsg.newBuilder().setHash(sha3Hash).build(),
                            new TransportServiceCallback<ValidateDeviceCredentialsResponse>() {
                                @Override
                                public void onSuccess(ValidateDeviceCredentialsResponse msg) {
                                    if (!StringUtils.isEmpty(msg.getCredentials())) {
                                        credentialsBodyHolder[0] = msg.getCredentials();
                                    }
                                    latch.countDown();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    log.error(e.getMessage(), e);
                                    latch.countDown();
                                }
                            });*//*

                    latch.await(10, TimeUnit.SECONDS);
                    if (strCert.equals(credentialsBodyHolder[0])) {
                        credentialsBody = credentialsBodyHolder[0];
                        break;
                    }
                } catch (InterruptedException | CertificateEncodingException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (credentialsBody == null) {
                throw new CertificateException("Invalid Device Certificate");
            }
        }
    }
}
*/
