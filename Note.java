package src;
/**
 * Note class represents a single note in the application.
 * Each note has a title, content, and timestamps for creation and modification.
 */
public class Note {
    // The title of the note
    private String title;
    
    // The content/body of the note
    private String content;

    // Timestamps for creation and modification
    private long creationDate;
    private long lastModifiedDate;

    /**
     * Constructor to create a new note with specified title and content
     * @param title The title of the note
     * @param content The content/body of the note
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.creationDate = System.currentTimeMillis();
        this.lastModifiedDate = this.creationDate;
    }

    /**
     * Constructor to create a new note with specified title, content, and timestamps
     * @param title The title of the note
     * @param content The content/body of the note
     * @param creationDate The creation timestamp
     * @param lastModifiedDate The last modified timestamp
     */
    public Note(String title, String content, long creationDate, long lastModifiedDate) {
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Gets the title of the note
     * @return The note's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the note
     * @param title The new title for the note
     */
    public void setTitle(String title) {
        this.title = title;
        this.lastModifiedDate = System.currentTimeMillis();
    }

    /**
     * Gets the content of the note
     * @return The note's content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the note
     * @param content The new content for the note
     */
    public void setContent(String content) {
        this.content = content;
        this.lastModifiedDate = System.currentTimeMillis();
    }

    /**
     * Gets the creation date of the note
     * @return The timestamp when the note was created
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the note
     * @param creationDate The creation timestamp
     */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the last modified date of the note
     * @return The timestamp when the note was last modified
     */
    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Sets the last modified date of the note
     * @param lastModifiedDate The last modified timestamp
     */
    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Formats a timestamp into a readable date string
     * @param timestamp The timestamp to format
     * @return A formatted date string
     */
    public static String formatDate(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
    }
} 