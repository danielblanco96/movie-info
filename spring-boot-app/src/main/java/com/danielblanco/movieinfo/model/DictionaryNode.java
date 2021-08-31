package com.danielblanco.movieinfo.model;

import java.util.HashMap;
import java.util.Map;

public class DictionaryNode {
    Map<Character, DictionaryNode> children;
    Long id;
    String title;

    public DictionaryNode() {
        children = new HashMap<Character, DictionaryNode>();
    }
}
