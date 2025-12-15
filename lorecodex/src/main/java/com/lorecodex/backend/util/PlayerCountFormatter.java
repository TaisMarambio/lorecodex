package com.lorecodex.backend.util;

import org.springframework.stereotype.Component;

@Component
public class PlayerCountFormatter {

    public String format(Integer likes) {
        if (likes == null || likes <= 0) {
            return "Active";
        }

        if (likes >= 1_000_000) {
            return (likes / 1_000_000) + "M+";
        }
        if (likes >= 1_000) {
            return (likes / 1_000) + "k+";
        }
        return likes + "+";
    }
}
