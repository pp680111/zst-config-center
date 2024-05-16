package com.zst.configcenter.server.module.config;

import com.zst.configcenter.server.module.config.dto.ConfigDTO;
import com.zst.configcenter.server.module.config.form.ConfigForm;
import com.zst.configcenter.server.module.exception.DataNotFoundException;
import com.zst.configcenter.server.module.version.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/configs")
public class ConfigController {
    @Autowired
    private ConfigService configService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private ApplicationContext applicationContext;

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

    @PostMapping("/edit")
    public void edit(@RequestBody @Validated ConfigForm form) {
        configService.insertOrUpdate(form);
        Integer version = versionService.getConfigVersion(form.getApp(), form.getNamespace(), form.getEnvironment());
        applicationContext.publishEvent(new ConfigUpdateEvent(this, form.getApp(), form.getNamespace(), form.getEnvironment(), version));
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("app") String app,
                       @RequestParam("namespace") String namespace,
                       @RequestParam("environment") String environment,
                       @RequestParam("key") List<String> key) {
        configService.deleteByKey(app, namespace, environment, key);
    }
}
