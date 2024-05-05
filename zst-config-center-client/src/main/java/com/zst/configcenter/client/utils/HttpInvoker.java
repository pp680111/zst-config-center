package com.zst.configcenter.client.utils;

import com.alibaba.fastjson2.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HttpInvoker {
    private static final int TIMEOUT_MS = 4000;
    private CloseableHttpAsyncClient httpAsyncClient = null;

    public CompletableFuture<HttpResponse> doGet(String url, Map<String, String> header, Map<String, String> params) {
        HttpGet req = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(URI.create(url));
            if (params != null) {
                params.forEach(uriBuilder::addParameter);
            }

            req = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL地址处理出错", e);
        }

        if (header != null) {
            header.forEach(req::addHeader);
        }

        return execute(req);
    }

    public CompletableFuture<HttpResponse> doPost(String url, Map<String, String> header, Map<String, String> urlParams,
                                                  Object bodyParams) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(URI.create(url));
            if (urlParams != null) {
                urlParams.forEach(uriBuilder::addParameter);
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL地址处理出错", e);
        }

        HttpPost req = new HttpPost(uri);
        if (header != null) {
            header.forEach(req::addHeader);
        }


        if (bodyParams != null) {
            req.setEntity(new StringEntity(JSON.toJSONString(bodyParams), ContentType.APPLICATION_JSON));
        }

        return execute(req);
    }

    public CompletableFuture<HttpResponse> doPut(String url, Map<String, String> header, Map<String, String> urlParams,
                                                 Object bodyParam) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(URI.create(url));
            if (urlParams != null) {
                urlParams.forEach(uriBuilder::addParameter);
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL地址处理出错", e);
        }

        HttpPut req = new HttpPut(uri);
        if (header != null) {
            header.forEach(req::addHeader);
        }


        if (bodyParam != null) {
            req.setEntity(new StringEntity(JSON.toJSONString(bodyParam), ContentType.APPLICATION_JSON));
        }

        return execute(req);
    }

    public void close() {
        if (httpAsyncClient != null) {
            try {
                httpAsyncClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            httpAsyncClient = null;
        }
    }

    private CompletableFuture<HttpResponse> execute(HttpUriRequest request) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        getAsyncClient().execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                future.complete(result);
            }

            @Override
            public void failed(Exception ex) {
                future.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                future.cancel(true);
            }
        });

        return future;
    }

    private HttpAsyncClient getAsyncClient() {
        if (httpAsyncClient != null) {
            return httpAsyncClient;
        }
        synchronized (this) {
            try {
                TrustManager[] tm = new TrustManager[]{new NoTrustManager()};
                SSLContext ssl = SSLContext.getInstance("TLS");
                ssl.init((KeyManager[]) null, tm, new SecureRandom());

                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectTimeout(TIMEOUT_MS)
                        .setSocketTimeout(TIMEOUT_MS)
                        .build();

                CloseableHttpAsyncClient newClient = HttpAsyncClients.custom()
                        .setSSLHostnameVerifier(new NoHostnameVerifier())
                        .setSSLContext(ssl)
                        .setDefaultRequestConfig(requestConfig)
                        .setMaxConnTotal(1000)
                        .setMaxConnPerRoute(1000)
                        .build();
                newClient.start();
                httpAsyncClient = newClient;
            } catch (NoSuchAlgorithmException | KeyManagementException e1) {
                throw new RuntimeException("初始化HTTP客户端时发生错误", e1);
            }
        }
        return httpAsyncClient;
    }

    private static class NoTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class NoHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
