package com.todostudy.iot.mqtt.server.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * 包括单向认证和双向认证
 */
public class SSLContextFactory {

    private static final String PROTOCOL = "TLS";

    private static SSLContext SERVER_CONTEXT;// 服务器安全套接字协议

    private static SslContext openSslContext;

    private static SSLContext CLIENT_CONTEXT;// 客户端安全套接字协议

    private static SslContext openSslClientContext;

    /**
     * @param isAuth false为单向认证，true为双向认证
     * @param pkInputStream
     * @param caInputStream
     * @param passwd
     * @return
     */
    public static SSLEngine getSslServerEngine(boolean isAuth,InputStream pkInputStream,InputStream caInputStream, String passwd ) {

        SSLEngine sslEngine = null;
        if (isAuth) {
            sslEngine = getServerContext(pkInputStream,
                    caInputStream, passwd)
                    .createSSLEngine();
        } else {
            sslEngine = getServerContext(pkInputStream,
                    passwd).createSSLEngine();
        }

        sslEngine.setUseClientMode(false);
        sslEngine.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1",
                "TLSv1.2" });
        // false为单向认证，true为双向认证
        sslEngine.setNeedClientAuth(isAuth);
        return sslEngine;
    }

    public static SSLContext getServerContext(InputStream pkInputStream, String passwd) {
        if (SERVER_CONTEXT != null)
            return SERVER_CONTEXT;
        InputStream in = null;

        try {
            // 密钥管理器
            KeyManagerFactory kmf = null;
            // 密钥库KeyStore
            KeyStore ks = KeyStore.getInstance("JKS");

            // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
            ks.load(pkInputStream, passwd.toCharArray());

            kmf = KeyManagerFactory.getInstance("SunX509");
            // 初始化密钥管理器
            kmf.init(ks, passwd.toCharArray());

            // 获取安全套接字协议（TLS协议）的对象
            SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
            // 初始化此上下文
            // 参数一：认证的密钥 参数二：对等信任认证 参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            SERVER_CONTEXT.init(kmf.getKeyManagers(), null, null);

        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext",
                    e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return SERVER_CONTEXT;
    }

    public static SslContext getOpenSslServerContext(InputStream pkInputStream,
                                                     String passwd) {
        if (openSslContext != null) {
            return openSslContext;
        }

        try {
            // 密钥管理器
            KeyManagerFactory kmf = null;

            // 密钥库KeyStore
            KeyStore ks = KeyStore.getInstance("JKS");
            // 加载服务端证书
            // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
            ks.load(pkInputStream, passwd.toCharArray());

            kmf = KeyManagerFactory.getInstance("SunX509");
            // 初始化密钥管理器
            kmf.init(ks, passwd.toCharArray());

            openSslContext = SslContextBuilder.forServer(kmf)
                    .sslProvider(SslProvider.OPENSSL).build();
            return openSslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static SSLContext getClientContext(InputStream pkInputStream, String passwd) {
        if (CLIENT_CONTEXT != null)
            return CLIENT_CONTEXT;

        try {
            // 信任库
            TrustManagerFactory tf = null;
            // 密钥库KeyStore
            KeyStore tks = KeyStore.getInstance("JKS");
            // 加载客户端证书
            tks.load(pkInputStream, passwd.toCharArray());
            tf = TrustManagerFactory.getInstance("SunX509");
            // 初始化信任库
            tf.init(tks);


            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
            // 设置信任证书
            CLIENT_CONTEXT.init(null,
                    tf == null ? null : tf.getTrustManagers(), null);

        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext");
        }

        return CLIENT_CONTEXT;
    }

    public static SslContext getOpenSslClientContext(InputStream pkInputStream,
                                                     String passwd) {

        if (openSslClientContext != null) {
            return openSslClientContext;
        }

        try {

            // 信任库
            TrustManagerFactory tf = null;
            // 密钥库KeyStore
            KeyStore tks = KeyStore.getInstance("JKS");
            // 加载客户端证书
            tks.load(pkInputStream, passwd.toCharArray());
            tf = TrustManagerFactory.getInstance("SunX509");
            // 初始化信任库
            tf.init(tks);

            openSslClientContext = SslContextBuilder.forClient()
                    .sslProvider(SslProvider.OPENSSL).trustManager(tf).build();

            return openSslClientContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Description: 生成sslContext
     *
     * @param caInputStream
     * @param passwd
     * @return
     * @see
     */
    public static SSLContext getServerContext(InputStream pkInputStream, InputStream caInputStream,
                                              String passwd) {
        if (SERVER_CONTEXT != null)
            return SERVER_CONTEXT;

        InputStream tIN = null;
        try {
            // 密钥管理器
            KeyManagerFactory kmf = null;
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(pkInputStream, passwd.toCharArray());

            kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, passwd.toCharArray());

            // 信任库
            TrustManagerFactory tf = null;
            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(caInputStream, passwd.toCharArray());
            tf = TrustManagerFactory.getInstance("SunX509");
            tf.init(tks);


            SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);

            // 初始化此上下文
            // 参数一：认证的密钥 参数二：对等信任认证 参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            // 单向认证？无需验证客户端证书
            if (tf == null) {
                SERVER_CONTEXT.init(kmf.getKeyManagers(), null, null);
            }
            // 双向认证，需要验证客户端证书
            else {
                SERVER_CONTEXT.init(kmf.getKeyManagers(),
                        tf.getTrustManagers(), null);
            }

        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext",
                    e);
        }
        return SERVER_CONTEXT;
    }

    public static SslContext getOpenSslServerContext(InputStream pkInputStream,
                                                     InputStream caInputStream, String passwd) {
        if (openSslContext != null)
            return openSslContext;

        try {

            // 密钥管理器
            KeyManagerFactory kmf = null;
                // 密钥库KeyStore
                KeyStore ks = KeyStore.getInstance("JKS");
                // 加载服务端证书
                // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
                ks.load(pkInputStream, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance("SunX509");
                // 初始化密钥管理器
                kmf.init(ks, passwd.toCharArray());


            // 信任库
            TrustManagerFactory tf = null;
                KeyStore tks = KeyStore.getInstance("JKS");
                tks.load(caInputStream, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance("SunX509");
                tf.init(tks);


            openSslContext = SslContextBuilder.forServer(kmf).trustManager(tf)
                    .sslProvider(SslProvider.OPENSSL).build();
            return openSslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static SSLContext getClientContext(InputStream pkInputStream,
                                              InputStream caInputStream,
                                              String passwd) {
        if (CLIENT_CONTEXT != null)
            return CLIENT_CONTEXT;

        try {
            KeyManagerFactory kmf = null;

                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(pkInputStream, passwd.toCharArray());
                kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, passwd.toCharArray());

            TrustManagerFactory tf = null;
                KeyStore tks = KeyStore.getInstance("JKS");

                tks.load(caInputStream, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance("SunX509");
                tf.init(tks);

            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
            // 初始化此上下文
            // 参数一：认证的密钥 参数二：对等信任认证 参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            CLIENT_CONTEXT.init(kmf.getKeyManagers(), tf.getTrustManagers(),
                    null);

        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext");
        }

        return CLIENT_CONTEXT;
    }

    public static SslContext getOpenSslClientContext(InputStream pkInputStream,
                                                     InputStream caInputStream, String passwd) {

        if (openSslClientContext != null) {
            return openSslClientContext;
        }
        try {
            // 密钥管理器
            KeyManagerFactory kmf = null;
                // 密钥库KeyStore
                KeyStore ks = KeyStore.getInstance("JKS");
                // 加载服务端证书
                // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
                ks.load(pkInputStream, passwd.toCharArray());
                kmf = KeyManagerFactory.getInstance("SunX509");
                // 初始化密钥管理器
                kmf.init(ks, passwd.toCharArray());

            // 信任库
            TrustManagerFactory tf = null;
                KeyStore tks = KeyStore.getInstance("JKS");
                tks.load(caInputStream, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance("SunX509");
                tf.init(tks);

            openSslClientContext = SslContextBuilder.forClient()
                    .sslProvider(SslProvider.OPENSSL).keyManager(kmf)
                    .trustManager(tf).build();

            return openSslClientContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SSLEngine getSslClientEngine(InputStream pkInputStream,
                                               InputStream caInputStream,
                                               String passwd, boolean isNeedClientAuth) {

        SSLEngine sslEngine = null;
        if (isNeedClientAuth) {
            sslEngine = getClientContext(pkInputStream, caInputStream, passwd)
                    .createSSLEngine();
        } else {
            sslEngine = getClientContext(pkInputStream, passwd).createSSLEngine();

        }
        sslEngine.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1",
                "TLSv1.2"});
        sslEngine.setUseClientMode(true);
        return sslEngine;
    }

    public static SSLEngine getOpenSslClientEngine(InputStream pkInputStream,
                                                   InputStream caInputStream, String passwd, ByteBufAllocator alloc,
                                                   boolean isNeedClientAuth) {

        SSLEngine sslEngine = null;
        if (isNeedClientAuth) {
            sslEngine = getOpenSslClientContext(pkInputStream, caInputStream, passwd)
                    .newEngine(alloc);
        } else {
            sslEngine = getOpenSslClientContext(pkInputStream, passwd)
                    .newEngine(alloc);
        }
        sslEngine.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1",
                "TLSv1.2"});
        sslEngine.setUseClientMode(true);
        return sslEngine;
    }


}
