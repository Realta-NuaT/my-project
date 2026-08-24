package org.example.utils;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProhibitedUtils {

    private static final String filePath = "./prohibited.json";

    private static List<String> prohibitedWords;

    @PostConstruct
    private void init()
    {
        prohibitedWords = new ArrayList<>();
    }
}
