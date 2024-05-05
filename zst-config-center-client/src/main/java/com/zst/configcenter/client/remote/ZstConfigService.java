package com.zst.configcenter.client.remote;

public interface ZstConfigService {
    String[] getPropertyNames();

    String getProperty(String name);
}
