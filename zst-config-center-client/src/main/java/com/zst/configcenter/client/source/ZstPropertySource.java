package com.zst.configcenter.client.source;

import com.zst.configcenter.client.remote.ZstConfigService;
import org.springframework.core.env.EnumerablePropertySource;

public class ZstPropertySource extends EnumerablePropertySource<ZstConfigService> {

    public ZstPropertySource(String name, ZstConfigService source) {
        super(name, source);
    }

    protected ZstPropertySource(String name) {
        super(name);
    }

    @Override
    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

    @Override
    public Object getProperty(String name) {
        return source.getProperty(name);
    }
}
