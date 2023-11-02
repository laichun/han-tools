package com.todostudy;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public class SSLContextFactory {

    private static final String PROTOCOL = "TLS";

    private static final String JKS = "JKS";
    private static final String SunX509="SunX509";

    private static SSLContext SERVER_CONTEXT;// 服务器安全套接字协议

    private static SslContext openSslContext;

    private static SSLContext CLIENT_CONTEXT;// 客户端安全套接字协议

    private static SslContext openSslClientContext;

    public static SSLContext getServerContext(String pkPath, String passwd) {
        if (SERVER_CONTEXT != null)
            return SERVER_CONTEXT;
        InputStream in = null;

        try {
            // 密钥管理器
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                // 密钥库KeyStore
                KeyStore ks = KeyStore.getInstance(JKS);
                // 加载服务端证书
                in = new FileInputStream(pkPath);
                // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
                ks.load(in, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance(SunX509);
                // 初始化密钥管理器
                kmf.init(ks, passwd.toCharArray());
            }
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

    public static SslContext getOpenSslServerContext(String pkPath,
                                                     String passwd) {
        if (openSslContext != null) {
            return openSslContext;
        }

        InputStream in = null;
        try {

            // 密钥管理器
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                // 密钥库KeyStore
                KeyStore ks = KeyStore.getInstance(JKS);
                // 加载服务端证书
                in = new FileInputStream(pkPath);
                // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
                ks.load(in, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance(SunX509);
                // 初始化密钥管理器
                kmf.init(ks, passwd.toCharArray());
            }
            openSslContext = SslContextBuilder.forServer(kmf)
                    .sslProvider(SslProvider.OPENSSL).build();
            return openSslContext;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }

        }

        return null;

    }

    public static SSLContext getClientContext(String pkPath, String passwd) {
        if (CLIENT_CONTEXT != null)
            return CLIENT_CONTEXT;

        InputStream tIN = null;
        try {
            // 信任库
            TrustManagerFactory tf = null;
            if (pkPath != null) {
                // 密钥库KeyStore
                KeyStore tks = KeyStore.getInstance(JKS);
                // 加载客户端证书
                tIN = new FileInputStream(pkPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance(SunX509);
                // 初始化信任库
                tf.init(tks);
            }

            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
            // 设置信任证书
            CLIENT_CONTEXT.init(null,
                    tf == null ? null : tf.getTrustManagers(), null);

        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext");
        } finally {
            if (tIN != null) {
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return CLIENT_CONTEXT;
    }

    public static SslContext getOpenSslClientContext(String pkPath,
                                                     String passwd) {

        if (openSslClientContext != null) {
            return openSslClientContext;
        }

        InputStream tIN = null;
        try {

            // 信任库
            TrustManagerFactory tf = null;
            if (pkPath != null) {
                // 密钥库KeyStore
                KeyStore tks = KeyStore.getInstance(JKS);
                // 加载客户端证书
                tIN = new FileInputStream(pkPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance(SunX509);
                // 初始化信任库
                tf.init(tks);
            }

            openSslClientContext = SslContextBuilder.forClient()
                    .sslProvider(SslProvider.OPENSSL).trustManager(tf).build();

            return openSslClientContext;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tIN != null) {
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }

        }

        return null;

    }

    /**
     *
     * Description: 生成sslContext
     *
     * @param pkPath
     * @param caPath
     * @param passwd
     * @return
     * @see
     */
    public static SSLContext getServerContext(String pkPath, String caPath,
                                              String passwd) {
        if (SERVER_CONTEXT != null)
            return SERVER_CONTEXT;
        InputStream in = null;
        InputStream tIN = null;

        try {
            // 密钥管理器
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                KeyStore ks = KeyStore.getInstance(JKS);
                in = new FileInputStream(pkPath);
                ks.load(in, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance(SunX509);
                kmf.init(ks, passwd.toCharArray());
            }
            // 信任库
            TrustManagerFactory tf = null;
            if (caPath != null) {
                KeyStore tks = KeyStore.getInstance(JKS);
                tIN = new FileInputStream(caPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance(SunX509);
                tf.init(tks);
            }

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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }

            if (tIN != null) {
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }
        }
        return SERVER_CONTEXT;
    }

    public static SslContext getOpenSslServerContext(String pkPath,
                                                     String caPath, String passwd) {
        if (openSslContext != null)
            return openSslContext;

        InputStream in = null;
        InputStream tIN = null;
        try {

            // 密钥管理器
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                // 密钥库KeyStore
                KeyStore ks = KeyStore.getInstance(JKS);
                // 加载服务端证书
                in = new FileInputStream(pkPath);
                // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
                ks.load(in, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance(SunX509);
                // 初始化密钥管理器
                kmf.init(ks, passwd.toCharArray());
            }

            // 信任库
            TrustManagerFactory tf = null;
            if (caPath != null) {
                KeyStore tks = KeyStore.getInstance(JKS);
                tIN = new FileInputStream(caPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance(SunX509);
                tf.init(tks);
            }

            openSslContext = SslContextBuilder.forServer(kmf).trustManager(tf)
                    .sslProvider(SslProvider.OPENSSL).build();
            return openSslContext;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }

            if (tIN != null) {
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }
        }

        return null;

    }

    public static SSLContext getClientContext(String pkPath, String caPath,
                                              String passwd) {
        if (CLIENT_CONTEXT != null)
            return CLIENT_CONTEXT;

        InputStream in = null;
        InputStream tIN = null;
        try {
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                KeyStore ks = KeyStore.getInstance(JKS);
                in = new FileInputStream(pkPath);
                ks.load(in, passwd.toCharArray());
                kmf = KeyManagerFactory.getInstance(SunX509);
                kmf.init(ks, passwd.toCharArray());
            }

            TrustManagerFactory tf = null;
            if (caPath != null) {
                KeyStore tks = KeyStore.getInstance(JKS);
                tIN = new FileInputStream(caPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance(SunX509);
                tf.init(tks);
            }

            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
            // 初始化此上下文
            // 参数一：认证的密钥 参数二：对等信任认证 参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            CLIENT_CONTEXT.init(kmf.getKeyManagers(), tf.getTrustManagers(),
                    null);

        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }

            if (tIN != null) {
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }
        }

        return CLIENT_CONTEXT;
    }

    public static SslContext getOpenSslClientContext(String pkPath,
                                                     String caPath, String passwd) {

        if (openSslClientContext != null) {
            return openSslClientContext;
        }

        InputStream in = null;
        InputStream tIN = null;
        try {

            // 密钥管理器
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                // 密钥库KeyStore
                KeyStore ks = KeyStore.getInstance(JKS);
                // 加载服务端证书
                in = new FileInputStream(pkPath);
                // 加载服务端的KeyStore ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
                ks.load(in, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance(SunX509);
                // 初始化密钥管理器
                kmf.init(ks, passwd.toCharArray());
            }

            // 信任库
            TrustManagerFactory tf = null;
            if (caPath != null) {
                KeyStore tks = KeyStore.getInstance(JKS);
                tIN = new FileInputStream(caPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance(SunX509);
                tf.init(tks);
            }

            openSslClientContext = SslContextBuilder.forClient()
                    .sslProvider(SslProvider.OPENSSL).keyManager(kmf)
                    .trustManager(tf).build();

            return openSslClientContext;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (tIN != null) {
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }

        }

        return null;

    }



    public static SSLEngine getSslClientEngine(String pkPath, String caPath,
                                               String passwd, boolean isNeedClientAuth) {

        SSLEngine sslEngine = null;
        if (isNeedClientAuth) {
            sslEngine = getClientContext(pkPath, caPath, passwd)
                    .createSSLEngine();
        } else {
            sslEngine = getClientContext(pkPath, passwd).createSSLEngine();

        }
        sslEngine.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1",
                "TLSv1.2" });
        sslEngine.setUseClientMode(true);
        return sslEngine;
    }

    public static SSLEngine getOpenSslClientEngine(String pkPath,
                                                   String caPath, String passwd, ByteBufAllocator alloc,
                                                   boolean isNeedClientAuth) {

        SSLEngine sslEngine = null;
        if (isNeedClientAuth) {
            sslEngine = getOpenSslClientContext(pkPath, caPath, passwd)
                    .newEngine(alloc);
        } else {
            sslEngine = getOpenSslClientContext(pkPath, passwd)
                    .newEngine(alloc);
        }
        sslEngine.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1",
                "TLSv1.2" });
        sslEngine.setUseClientMode(true);
        return sslEngine;
    }


}
