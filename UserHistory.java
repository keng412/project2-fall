package cs1501_p2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class UserHistory implements Dict {
    private HashMap<String, Integer> history;  // store words with frequency
    private StringBuilder currentSearch;  // maintain the current by-character search state

    public UserHistory() {
        history = new HashMap<>();
        currentSearch = new StringBuilder();  // initialize search state
    }

    @Override
    public void add(String key) {
        history.put(key, history.getOrDefault(key, 0) + 1);  // add or increment frequency
    }

    @Override
    public boolean contains(String key) {
        return history.containsKey(key);
    }

    @Override
    public boolean containsPrefix(String pre) {
        for (String word : history.keySet()) {
            if (word.startsWith(pre)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resetByChar() {
        currentSearch.setLength(0);  // reset the search state
    }

    @Override
    public int searchByChar(char next) {
        currentSearch.append(next);
        String prefix = currentSearch.toString();
        
        // check if there is any word starting with the current prefix
        boolean isPrefix = false;
        boolean isWord = false;
        for (String word : history.keySet()) {
            if (word.startsWith(prefix)) {
                isPrefix = true;
                if (word.equals(prefix)) {
                    isWord = true;
                }
            }
        }

        if (isWord && isPrefix) {
            return 2;  // valid word and prefix
        } else if (isWord) {
            return 1;  // valid word but not a prefix to other words
        } else if (isPrefix) {
            return 0;  // valid prefix but not a complete word
        } else {
            return -1;  // not a valid word or prefix
        }
    }

    @Override
    public ArrayList<String> suggest() {
        ArrayList<String> suggestions = new ArrayList<>();
        String prefix = currentSearch.toString();
        
        for (String word : history.keySet()) {
            if (word.startsWith(prefix)) {
                suggestions.add(word);
            }
        }

        // sort by frequency and limit to 5 suggestions
        suggestions.sort((a, b) -> history.get(b) - history.get(a));
        return new ArrayList<>(suggestions.subList(0, Math.min(5, suggestions.size())));
    }

    @Override
    public ArrayList<String> traverse() {
        return new ArrayList<>(history.keySet());  // return all words in history
    }

    @Override
    public int count() {
        return history.size();
    }

    public void loadFromFile(String filename) {
        try (Scanner scanner = new Scanner(new FileInputStream(filename))) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim();
                if (!word.isEmpty()) {
                    add(word);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading user history.");
        }
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            for (String word : history.keySet()) {
                writer.println(word);
            }
        } catch (Exception e) {
            System.out.println("Error saving user history.");
        }
    }
}
