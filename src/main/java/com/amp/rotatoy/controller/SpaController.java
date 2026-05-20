package com.amp.rotatoy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = {"/", "/{path:(?!static$)[^\\.]*}", "/{path:(?!static$)[^\\.]*}/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
