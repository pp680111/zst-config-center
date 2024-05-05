package com.zst.configcenter.client.remote;

import com.alibaba.fastjson2.JSONArray;
import com.zst.configcenter.client.properties.ConfigServerProperties;
import com.zst.configcenter.client.remote.dto.ConfigDTO;
import com.zst.configcenter.client.utils.HttpInvoker;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 负责执行调用server端接口的类
 */
public class RemoteServerClient {
    private HttpInvoker httpInvoker;
    private ConfigServerProperties configServerProperties;

    public RemoteServerClient(ConfigServerProperties configServerProperties) {
        if (configServerProperties == null) {
            throw new IllegalArgumentException();
        }

        this.configServerProperties = configServerProperties;
        httpInvoker = new HttpInvoker();
    }

    public List<ConfigDTO> list(String app, String namespace, String environment) {
        if (!StringUtils.hasLength(app) || !StringUtils.hasLength(namespace)
                || !StringUtils.hasLength(environment)) {
            throw new IllegalArgumentException();
        }

        String url = String.format("%s/configs/list", configServerProperties.getAddress());
        Map<String, String> urlParams = Map.of("app", app,
                "namespace", namespace,
                "environment", environment);

        try {
            Future<HttpResponse> responseFuture = httpInvoker.doGet(url, null, urlParams);
            HttpResponse response = responseFuture.get();

            String rawBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return JSONArray.parseArray(rawBody, ConfigDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("调用Config Server接口时发生错误", e);
        }
    }
}