package com.danielblanco.movieinfo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MovieDictionary {
    private DictionaryNode root;

    public MovieDictionary() {
        root = new DictionaryNode();
    }

    public void insert(Movie movie) {
        if (movie == null) return;

        DictionaryNode currentNode = root;
        String titleLowerCase = movie.getTitle().toLowerCase();
        for (char c : titleLowerCase.toCharArray()) {
            DictionaryNode child = currentNode.children.get(c);
            if (child == null) {
                child = new DictionaryNode();
                currentNode.children.put(c, child);
            }
            currentNode = child;
        }
        currentNode.id = movie.getId();
        currentNode.title = movie.getTitle();
    }

    public List<Movie> getSuggestions(String titleStart, int limit) {
        if (titleStart == null || limit < 0) return Collections.emptyList();

        DictionaryNode currentNode = getNodeFromString(titleStart.toLowerCase());
        if (currentNode == null) return Collections.emptyList();

        List<Movie> suggestions = new ArrayList<Movie>();
        suggestionsHelper(currentNode, limit, suggestions);
        return suggestions;
    }

    private DictionaryNode getNodeFromString(String str) {
        DictionaryNode currentNode = root;

        for (char c : str.toCharArray()) {
            if (!currentNode.children.containsKey(c)) return null;
            currentNode = currentNode.children.get(c);
        }

        return currentNode;
    }

    private void suggestionsHelper(DictionaryNode node, int limit, List<Movie> suggestions) {
        if (node == null) return;
        if (node.id != null && node.title != null) suggestions.add(new Movie(node.id, node.title));

        for (DictionaryNode child : node.children.values()) {
            if (suggestions.size() == limit) return;
            suggestionsHelper(child, limit, suggestions);
        }
    }
}
