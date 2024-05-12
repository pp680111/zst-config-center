package com.zst.configcenter.demo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Prop2 {
    @Value("${zst.bb}")
    private String bb;
}
