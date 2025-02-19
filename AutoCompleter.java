package cs1501_p2;

import java.io.*;
import java.util.ArrayList;

public class AutoCompleter implements AutoComplete_Inter {

    private DLB dictionary;
    private UserHistory userHistory;
    private StringBuilder currentPrefix; // Keeps track of current prefix for suggestions
    private ArrayList<String> suggestions; // List of suggestions to be returned

    // Constructor for loading dictionary and user history
    public AutoCompleter(String dictFile, String userHistFile) {
        dictionary = new DLB();
        userHistory = new UserHistory();
        currentPrefix = new StringBuilder();
        suggestions = new ArrayList<>();

        try {
            // Load dictionary
            loadDictionary(dictFile);

            // Load user history manually from text
            loadUserHistory(userHistFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructor for loading only the dictionary
    public AutoCompleter(String dictFile) {
        dictionary = new DLB();
        userHistory = new UserHistory();
        currentPrefix = new StringBuilder();
        suggestions = new ArrayList<>();

        try {
            loadDictionary(dictFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the dictionary from a file
    private void loadDictionary(String dictFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(dictFile))) {
            String word;
            while ((word = reader.readLine()) != null) {
                dictionary.add(word.trim()); // Add each word to the dictionary trie
            }
        }
    }

    // Load user history from a text file (each line is a word)
    private void loadUserHistory(String userHistFile) throws IOException {
        File file = new File(userHistFile);
        if (!file.exists()) return; // If the file doesn't exist, return without loading
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userHistFile))) {
            String word;
            while ((word = reader.readLine()) != null) {
                userHistory.add(word.trim()); // Add each word to the user history
            }
        }
    }

    // Save the user history to a text file (each word on a new line)
    public void saveUserHistory(String userHistFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userHistFile))) {
            ArrayList<String> userWords = userHistory.traverse();
            for (String word : userWords) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add the next character and return suggestions
    @Override
    public ArrayList<String> nextChar(char next) {
        currentPrefix.append(next);
        suggestions.clear(); // Clear previous suggestions

        // Search user history first for suggestions
        userHistory.resetByChar();
        for (char c : currentPrefix.toString().toCharArray()) {
            userHistory.searchByChar(c);
        }
        ArrayList<String> userHistSuggestions = userHistory.suggest();
        suggestions.addAll(userHistSuggestions);

        // Search dictionary for remaining suggestions, avoiding duplicates
        dictionary.resetByChar();
        for (char c : currentPrefix.toString().toCharArray()) {
            dictionary.searchByChar(c);
        }
        ArrayList<String> dictSuggestions = dictionary.suggest();

        for (String suggestion : dictSuggestions) {
            if (!suggestions.contains(suggestion)) {
                suggestions.add(suggestion);
            }
            if (suggestions.size() >= 5) break;
        }

        return suggestions;
    }

    // Process the user finishing a word (e.g., selecting it)
    @Override
    public void finishWord(String cur) {
        userHistory.add(cur); // Add selected word to user history
        currentPrefix.setLength(0); // Reset the current prefix
    }
}
