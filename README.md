# NoteApp

A modern, lightweight note-taking application built with Java Swing.

![NoteApp Screenshot](https://github.com/madnan19/NoteApp-19/blob/main/NoteApp.png)

## Features

- **Clean, Modern UI**: Sleek interface with customizable themes including dark mode
- **Note Management**: Create, edit, save, and delete notes with ease
- **Search Functionality**: Quickly find notes using the search feature
- **Keyboard Shortcuts**: Efficient navigation and operation using keyboard shortcuts
- **File Operations**: Import and export notes
- **Categorization**: Organize notes by categories
- **Responsive Design**: UI adapts to different window sizes

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- Windows, macOS, or Linux operating system

## Installation

1. Clone this repository:
   ```
   git clone https://github.com/madnan19/NoteApp-19.git
   ```

2. Navigate to the project directory:
   ```
   cd NoteApp
   ```

3. Compile the Java files:
   ```
   javac src/*.java
   ```

4. Create a manifest file:
   ```
   echo "Main-Class: src.NoteApp" > manifest.txt
   ```

5. Create a JAR file:
   ```
   jar cvfm NoteApp.jar manifest.txt src/*.class
   ```

6. Run the application:
   ```
   java -jar NoteApp.jar
   ```

## Usage

### Creating a Note
1. Click the "New" button or press `Ctrl+N`
2. Enter a title for your note
3. Type your note content in the main text area
4. Click "Save" or press `Ctrl+S` to save your note

### Editing a Note
1. Select a note from the list
2. Modify the content in the main text area
3. Click "Save" or press `Ctrl+S` to save your changes

### Deleting a Note
1. Select a note from the list
2. Click the "Delete" button or press `Ctrl+D`

### Searching Notes
1. Type your search query in the search field
2. The note list will automatically filter to show matching notes

### Keyboard Shortcuts
- `Ctrl+N`: Create a new note
- `Ctrl+S`: Save the current note
- `Ctrl+D`: Delete the selected note
- `Ctrl+E`: Edit the selected note
- `Ctrl+F`: Focus the search field
- `Ctrl+Q`: Exit the application
- `Ctrl+Plus`: Zoom in
- `Ctrl+Minus`: Zoom out

## How Notes Are Stored

The application stores all notes in the `notes/` directory at the root of the project. Each note is saved as an individual text file with the following characteristics:

1. **File Naming**: Each note file is named according to its title with a `.txt` extension (e.g., `My First Note.txt`)

2. **File Format**: Note files are plain text files with a simple structure:
   - The first line contains metadata in the format: `creationTimestamp|lastModifiedTimestamp`
   - The remaining lines contain the actual note content

3. **File Operations**:
   - When a new note is created, a corresponding file is generated in the `notes/` directory
   - When a note is edited, its file is updated with the new content and modification timestamp
   - When a note is deleted, its file is removed from the `notes/` directory

4. **Loading Notes**: On application startup, the app scans the `notes/` directory and loads all note files into memory

5. **Note Persistence**: All changes to notes (creation, edits, deletion) are immediately synchronized with the corresponding files on disk

6. **File Handling**: The application handles special characters in filenames and ensures proper file encoding for compatibility across different systems

## Project Structure

- `src/` - Contains the Java source files
  - `NoteApp.java` - Main application class with UI and functionality
  - `Note.java` - Data model for notes
- `notes/` - Directory where notes are stored as text files

## Customization

The application features a modern UI with customizable themes. You can toggle between light and dark mode through the settings menu.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License

## Acknowledgements

- Java Swing for the GUI components
- Inspired by modern note-taking applications
