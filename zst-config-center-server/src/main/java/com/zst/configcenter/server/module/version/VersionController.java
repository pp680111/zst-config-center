package com.zst.configcenter.server.module.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {
    @Autowired
    private VersionService versionService;

    @GetMapping("/version")
    public int updateConfigVersion(@RequestParam("app") String app,
                                   @RequestParam("namespace") String namespace,
                                   @RequestParam("environment") String environment) {
        return versionService.getConfigVersion(app, namespace, environment);
    }
}
