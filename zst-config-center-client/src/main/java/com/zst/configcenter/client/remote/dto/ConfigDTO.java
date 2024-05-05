package com.zst.configcenter.client.remote.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigDTO {
    String id;
    String namespace;
    String environment;
    String key;
    String val;
}
