package cs1501_p2;

import java.util.ArrayList;

public class DLB implements Dict {
    private static final char TERMINATOR = '*';  // To mark the end of a word
    private DLBNode root;  // Root node of the trie
    private DLBNode searchState;  // Used for searchByChar
    private StringBuilder currentPrefix; // Tracks the current prefix for suggestions

    // Constructor
    public DLB() {
        root = new DLBNode('\0');  // Root initialized with null char
        searchState = null;
        currentPrefix = new StringBuilder(); // Initialize prefix tracker
    }

    // Add a new word to the DLB
    @Override
    public void add(String word) {
        DLBNode current = root;

        for (char ch : word.toCharArray()) {
            current = findOrAddSibling(current, ch);
            if (current.getDown() == null) {
                current.setDown(new DLBNode(ch));
            }
            current = current.getDown();
        }

        // Add the terminator to mark the end of the word
        findOrAddSibling(current, TERMINATOR);
    }

    // Find or add a sibling node with the given character
    private DLBNode findOrAddSibling(DLBNode node, char ch) {
        if (node == null) return null;
        DLBNode current = node;

        // Traverse siblings to find the right node or add a new sibling
        while (current != null) {
            if (current.getLet() == ch) {
                return current;
            }
            if (current.getRight() == null) {
                current.setRight(new DLBNode(ch));
                return current.getRight();
            }
            current = current.getRight();
        }

        return current;
    }

    // Check if the word exists in the DLB
    @Override
    public boolean contains(String word) {
        DLBNode current = root;

        for (char ch : word.toCharArray()) {
            current = findSibling(current, ch);
            if (current == null) return false;
            current = current.getDown();
        }

        return findSibling(current, TERMINATOR) != null;
    }

    // Check if the prefix is valid
    @Override
    public boolean containsPrefix(String prefix) {
        DLBNode current = root;

        for (char ch : prefix.toCharArray()) {
            current = findSibling(current, ch);
            if (current == null) return false;
            current = current.getDown();
        }

        return true;
    }

    // Search by character in the DLB
    @Override
    public int searchByChar(char next) {
        if (searchState == null) {
            searchState = root;
            currentPrefix.setLength(0);  // Reset the prefix tracker
        }

        searchState = findSibling(searchState, next);
        if (searchState == null) {
            resetByChar();  // Invalid, reset state
            return -1;
        }

        currentPrefix.append(next);  // Add the character to the prefix
        searchState = searchState.getDown();
        boolean isWord = findSibling(searchState, TERMINATOR) != null;
        boolean hasChildren = searchState != null;

        if (isWord && hasChildren) return 2;  // Valid word and prefix
        else if (isWord) return 1;  // Only a valid word
        else return 0;  // Only a valid prefix
    }

    // Reset the search state for searchByChar
    @Override
    public void resetByChar() {
        searchState = null;
        currentPrefix.setLength(0);  // Reset the prefix tracker
    }

    // Suggest words based on the current search state
    @Override
    public ArrayList<String> suggest() {
        ArrayList<String> suggestions = new ArrayList<>();
        if (searchState == null) return suggestions;

        suggestHelper(searchState, currentPrefix, suggestions, 5);
        return suggestions;
    }

    // Helper method for suggesting words
    private void suggestHelper(DLBNode node, StringBuilder currentPrefix, ArrayList<String> suggestions, int limit) {
        if (node == null || suggestions.size() >= limit) return;

        if (node.getLet() == TERMINATOR) {
            suggestions.add(currentPrefix.toString());
        } else {
            currentPrefix.append(node.getLet());
            suggestHelper(node.getDown(), currentPrefix, suggestions, limit);
            currentPrefix.deleteCharAt(currentPrefix.length() - 1);  // Backtrack
        }

        suggestHelper(node.getRight(), currentPrefix, suggestions, limit);
    }

    // Traverse and list all words in the DLB
    @Override
    public ArrayList<String> traverse() {
        ArrayList<String> allWords = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        traverseHelper(root, currentWord, allWords);
        return allWords;
    }

    // Helper method for traversing
    private void traverseHelper(DLBNode node, StringBuilder currentWord, ArrayList<String> words) {
        if (node == null) return;

        if (node.getLet() == TERMINATOR) {
            words.add(currentWord.toString());
        } else {
            currentWord.append(node.getLet());
            traverseHelper(node.getDown(), currentWord, words);
            currentWord.deleteCharAt(currentWord.length() - 1);  // Backtrack
        }

        traverseHelper(node.getRight(), currentWord, words);
    }

    // Count the number of words in the DLB
    @Override
    public int count() {
        return traverse().size();  // Traverse the entire trie to count the words
    }

    // Find a sibling node with the given character
    private DLBNode findSibling(DLBNode node, char ch) {
        if (node == null) return null;
        DLBNode current = node;

        while (current != null) {
            if (current.getLet() == ch) {
                return current;
            }
            current = current.getRight();
        }

        return null;
    }
}
