package com.zst.configcenter.server.module.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/version")
public class VersionController {
    @Autowired
    private VersionService versionService;

    @GetMapping("/getVersion")
    public DeferredResult<Integer> updateConfigVersion(@RequestParam("app") String app,
                                              @RequestParam("namespace") String namespace,
                                              @RequestParam("environment") String environment,
                                              @RequestParam(value = "clientVersion", required = false) Integer clientVersion) {
        CompletableFuture<Integer> future = versionService.getConfigVersion(app, namespace, environment, clientVersion);
        
        DeferredResult<Integer> result = new DeferredResult<>();
        future.whenComplete((version, throwable) -> {
            if (throwable != null) {
                result.setErrorResult(throwable);
            } else {
                result.setResult(version);
            }
        });

        return result;
    }
}
