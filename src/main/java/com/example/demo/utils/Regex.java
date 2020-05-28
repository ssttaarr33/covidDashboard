package com.example.demo.utils;


import lombok.Getter;

public enum Regex {
    SINGLE_CHARACTER("(^|\\\\s+)[a-zA-Z](\\\\s+|$)"),
    ALPHANUMERIC("[^a-zA-Z0-9 -]"),
    SINGLE_DIGIT("[0-9]"),
    DOUBLE_DIGIT("^[0-9]{2}$"),
    TRIPLE_DIGIT("^[0-9]{3}$"),
    QUADRUPLE_DIGIT("^[0-9]{4}$"),
    SPACE("\\s+"),
    PUNCTUATION("[^A-Za-z0-9 -]");

    @Getter private final String regex;

    Regex(String regex){
        this.regex = regex;
    }
}
