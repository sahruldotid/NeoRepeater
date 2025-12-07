package dev.syahrul;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.hotkey.HotKey;
import burp.api.montoya.ui.hotkey.HotKeyContext;
import burp.api.montoya.ui.hotkey.HotKeyHandler;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.persistence.PersistedObject;

public class NeoRepeater implements BurpExtension {
    private static final String COUNTER_KEY = "neorepeater_tab_counter";
    private static final int MAX_PATH_LENGTH = 30;
    private MontoyaApi api;
    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("NeoRepeater");
        registerHotKey(api);
    }

    public void registerHotKey(MontoyaApi api) {
        try {
            HotKey key = HotKey.hotKey("Send to NeoRepeater", "Ctrl+R");
            HotKeyHandler handler = event -> {
            event.messageEditorRequestResponse().ifPresent(editor -> {
                HttpRequest request = editor.requestResponse().request();
                api.repeater().sendToRepeater(request, parseRequestToTabName(request));
            });
        };
        api.userInterface().registerHotKeyHandler(
            HotKeyContext.HTTP_MESSAGE_EDITOR,
                key,
            handler
        );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current tab counter from persistent storage
     * 
     * @return The current counter value (defaults to 1 if not found)
     */
    private int getTabCounter() {
        try {
            PersistedObject persistedObject = api.persistence().extensionData();
            Integer counter = persistedObject.getInteger(COUNTER_KEY);
            return (counter != null) ? counter : 1;
        } catch (Exception e) {
            api.logging().logToError("Failed to get counter from persistence: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Saves the tab counter to persistent storage
     * 
     * @param value The counter value to save
     */
    private void setTabCounter(int value) {
        try {
            PersistedObject persistedObject = api.persistence().extensionData();
            persistedObject.setInteger(COUNTER_KEY, value);
        } catch (Exception e) {
            api.logging().logToError("Failed to save counter to persistence: " + e.getMessage());
        }
    }

    /**
     * Gets the current counter, increments it, and saves the new value
     * 
     * @return The counter value before incrementing
     */
    private int getAndIncrementCounter() {
        int current = getTabCounter();
        setTabCounter(current + 1);
        return current;
    }

    /**
     * Truncates a full path to maximum length, adding "..." on both ends if
     * truncated
     * 
     * @param path The full path to truncate
     * @return Truncated path with "..." prefix and suffix if needed
     */
    private String truncatePath(String path) {
        if (path.length() <= MAX_PATH_LENGTH) {
            return path;
        }

        // Calculate how many characters we can show (minus 6 for "..." on both sides)
        int availableLength = MAX_PATH_LENGTH - 6;

        // If the path is too short to meaningfully truncate, just show start with ...
        if (availableLength <= 0) {
            return "..." + path.substring(path.length() - (MAX_PATH_LENGTH - 3));
        }

        // Take characters from the end of the path
        int startPos = path.length() - availableLength;
        return "..." + path.substring(startPos) + "...";
    }

    private String parseRequestToTabName(HttpRequest request) {
        try {
            String method = request.method();
            String path = request.path();

            // Remove query parameters
            int queryIndex = path.indexOf('?');
            if (queryIndex != -1) {
                path = path.substring(0, queryIndex);
            }

            // Remove trailing slash if present (but keep if it's just "/")
            if (path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }

            // Handle root path
            if (path.isEmpty() || path.equals("/")) {
                return getAndIncrementCounter() + ". [" + method + "] /";
            }

            // Use the full path, not just the last segment
            String fullPath = path;

            // Truncate the path if it exceeds the maximum length
            fullPath = truncatePath(fullPath);

            return getAndIncrementCounter() + ". [" + method + "] " + fullPath;

        } catch (Exception e) {
            return getAndIncrementCounter() + ". [REQUEST] " + System.currentTimeMillis();
        }
    }
    
}