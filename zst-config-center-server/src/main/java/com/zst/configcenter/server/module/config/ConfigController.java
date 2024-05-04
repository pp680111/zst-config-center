package com.zst.configcenter.server.module.config;

import com.zst.configcenter.server.module.config.dto.ConfigDTO;
import com.zst.configcenter.server.module.exception.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/configs")
public class ConfigController {
    @Autowired
    private ConfigService configService;

    @GetMapping("/list")
    public List<ConfigDTO> list(@RequestParam("app") String app,
                                @RequestParam("namespace") String namespace,
                                @RequestParam("environment") String environment) {
        List<Config> entityList = configService.list(app, namespace, environment);
        if (entityList != null && !entityList.isEmpty()) {
            return ConfigDTO.mapToDTOs(entityList);
        } else {
            throw new DataNotFoundException();
        }
    }
}
