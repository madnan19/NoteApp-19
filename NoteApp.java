package src;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * NoteApp - A simple note-taking application with a graphical user interface.
 * This application allows users to create, save, edit, and delete notes.
 * Notes are stored as text files in a 'notes' directory.
 */
public class NoteApp extends JFrame {
    // --- GUI Components and State ---
    private JTextArea noteArea;        // Main text area for note content
    private JList<String> noteList;    // List showing all note titles
    private DefaultListModel<String> listModel;  // Model for the note list
    private List<Note> notes;          // List to store Note objects in memory
    private JTextField titleField;     // Text field for note title
    private JTextField searchField;    // Text field for searching notes (legacy)
    private JLabel dateLabel;          // Label to show note dates
    private JLabel statusLabel;        // Label to show application status
    private JComboBox<String> categoryComboBox;  // Combo box for selecting note category (not used in minimal UI)
    private JButton boldButton, italicButton, underlineButton;  // Formatting buttons (not shown in minimal UI)
    private JColorChooser colorChooser;  // Color chooser for text formatting
    private JPanel toolbarPanel;        // Panel for toolbar buttons
    private static final String NOTES_DIR = "notes";  // Directory to store note files
    private static final String[] CATEGORIES = {"All", "Work", "Personal", "Ideas", "Tasks", "Other"};
    
    // --- Modern color scheme and fonts ---
    private static Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static Color ACCENT_COLOR = new Color(41, 128, 185);
    private static Color TEXT_COLOR = new Color(44, 62, 80);
    private static Color BORDER_COLOR = new Color(236, 240, 241);
    private static Color HOVER_COLOR = new Color(236, 240, 241);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final int CORNER_RADIUS = 8;
    private static final int PADDING = 12;
    private static final int COMPONENT_HEIGHT = 36;
    private static final Font UNIFIED_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // --- Sidebar search and dark mode state ---
    private PlaceholderTextField sidebarSearchField;
    private List<Note> filteredNotes = new ArrayList<>();
    private boolean darkMode = false;

    /**
     * Constructor initializes the application and sets up the UI
     */
    public NoteApp() {
        notes = new ArrayList<>();
        setupUI(); // Build the main UI
        loadNotes();  // Load existing notes from the notes directory
        setupKeyboardShortcuts(); // Register global shortcuts
        setupModernLookAndFeel(); // Apply modern look and feel
    }

    /**
     * Register global keyboard shortcuts for search and escape (others handled by menu accelerators)
     */
    private void setupKeyboardShortcuts() {
        // Global keyboard shortcuts
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlE = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlPlus = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlMinus = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK);
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        // Remove redundant shortcut registrations for actions already in the menu bar
        // getRootPane().registerKeyboardAction(e -> newNote(), ctrlN, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // getRootPane().registerKeyboardAction(e -> saveNote(), ctrlS, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // getRootPane().registerKeyboardAction(e -> deleteNote(), ctrlD, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // getRootPane().registerKeyboardAction(e -> editNote(), ctrlE, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // getRootPane().registerKeyboardAction(e -> System.exit(0), ctrlQ, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // getRootPane().registerKeyboardAction(e -> zoomIn(), ctrlPlus, JComponent.WHEN_IN_FOCUSED_WINDOW);
        // getRootPane().registerKeyboardAction(e -> zoomOut(), ctrlMinus, JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Keep only for search and escape
        getRootPane().registerKeyboardAction(e -> searchField.requestFocus(), ctrlF, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> clearSearch(), escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Clear the search field and reset the note list
     */
    private void clearSearch() {
        searchField.setText("");
        updateNoteList();
        noteArea.requestFocus();
    }

    /**
     * Apply a modern look and feel to the UI
     */
    private void setupModernLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Modern UI defaults
            UIManager.put("Button.arc", CORNER_RADIUS);
            UIManager.put("Component.arc", CORNER_RADIUS);
            UIManager.put("ProgressBar.arc", CORNER_RADIUS);
            UIManager.put("TextComponent.arc", CORNER_RADIUS);
            
            // Modern colors
            UIManager.put("Button.background", Color.WHITE);
            UIManager.put("Button.foreground", TEXT_COLOR);
            UIManager.put("Button.select", HOVER_COLOR);
            
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", TEXT_COLOR);
            UIManager.put("ComboBox.selectionBackground", ACCENT_COLOR);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            
            UIManager.put("List.background", Color.WHITE);
            UIManager.put("List.foreground", TEXT_COLOR);
            UIManager.put("List.selectionBackground", ACCENT_COLOR);
            UIManager.put("List.selectionForeground", Color.WHITE);
            
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextField.caretForeground", ACCENT_COLOR);
            
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("TextArea.foreground", TEXT_COLOR);
            UIManager.put("TextArea.caretForeground", ACCENT_COLOR);
            
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a modern styled text field
     */
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(field.getWidth(), COMPONENT_HEIGHT));
        return field;
    }

    /**
     * Create a modern styled text area
     */
    private JTextArea createModernTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(MAIN_FONT);
        area.setForeground(TEXT_COLOR);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return area;
    }

    /**
     * Create a modern styled action button
     */
    private JButton createActionButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setPreferredSize(new Dimension(button.getWidth(), COMPONENT_HEIGHT));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        button.addActionListener(listener);
        return button;
    }

    /**
     * Create the right panel with action buttons and date label
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        buttonPanel.add(createActionButton("New Note", e -> newNote()));
        buttonPanel.add(createActionButton("Save", e -> saveNote()));
        buttonPanel.add(createActionButton("Delete", e -> deleteNote()));
        buttonPanel.add(createActionButton("Export", e -> exportNote()));
        buttonPanel.add(createActionButton("Import", e -> importNote()));
        buttonPanel.add(createActionButton("Settings", e -> showSettings()));
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(dateLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Show the settings dialog (with dark mode toggle)
     */
    private void showSettings() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setLayout(new BorderLayout(10, 10));
        settingsDialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(BACKGROUND_COLOR);
        JCheckBox darkModeCheck = new JCheckBox("Enable Dark Mode");
        darkModeCheck.setFont(UNIFIED_FONT);
        darkModeCheck.setSelected(darkMode);
        darkModeCheck.setBackground(BACKGROUND_COLOR);
        darkModeCheck.setForeground(TEXT_COLOR);
        settingsPanel.add(darkModeCheck);
        settingsPanel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton applyButton = createUnifiedButton("Apply", e -> {
            darkMode = darkModeCheck.isSelected();
            applyTheme();
            settingsDialog.dispose();
        });
        JButton cancelButton = createUnifiedButton("Cancel", e -> settingsDialog.dispose());
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }

    /**
     * Apply the current theme (dark or light) to all components
     */
    private void applyTheme() {
        if (darkMode) {
            BACKGROUND_COLOR = new Color(34, 34, 34);
            TEXT_COLOR = new Color(220, 220, 220);
            BORDER_COLOR = new Color(60, 60, 60);
            ACCENT_COLOR = new Color(41, 128, 185);
            HOVER_COLOR = new Color(44, 62, 80);
        } else {
            BACKGROUND_COLOR = new Color(250, 250, 250);
            TEXT_COLOR = new Color(44, 62, 80);
            BORDER_COLOR = new Color(236, 240, 241);
            ACCENT_COLOR = new Color(41, 128, 185);
            HOVER_COLOR = new Color(236, 240, 241);
        }
        SwingUtilities.updateComponentTreeUI(this);
        getContentPane().setBackground(BACKGROUND_COLOR);
        updateComponentColors(this.getContentPane());
    }

    /**
     * Recursively update all component colors for dark/light mode
     */
    private void updateComponentColors(Component comp) {
        if (comp instanceof JPanel || comp instanceof JScrollPane) {
            comp.setBackground(BACKGROUND_COLOR);
        }
        if (comp instanceof JLabel) {
            comp.setForeground(TEXT_COLOR);
        }
        if (comp instanceof JTextField || comp instanceof PlaceholderTextField) {
            comp.setBackground(BACKGROUND_COLOR);
            comp.setForeground(TEXT_COLOR);
            ((JTextField) comp).setCaretColor(TEXT_COLOR);
            comp.setFont(UNIFIED_FONT);
            if (comp instanceof PlaceholderTextField) comp.repaint();
        }
        if (comp instanceof JTextArea || comp instanceof PlaceholderTextArea) {
            comp.setBackground(BACKGROUND_COLOR);
            comp.setForeground(TEXT_COLOR);
            ((JTextArea) comp).setCaretColor(TEXT_COLOR);
            comp.setFont(UNIFIED_FONT);
            if (comp instanceof PlaceholderTextArea) comp.repaint();
        }
        if (comp instanceof JList) {
            comp.setBackground(BACKGROUND_COLOR);
            comp.setForeground(TEXT_COLOR);
            comp.setFont(UNIFIED_FONT);
        }
        if (comp instanceof JButton) {
            comp.setBackground(BACKGROUND_COLOR);
            comp.setForeground(TEXT_COLOR);
            comp.setFont(UNIFIED_FONT);
        }
        if (comp instanceof JComboBox) {
            comp.setBackground(BACKGROUND_COLOR);
            comp.setForeground(TEXT_COLOR);
            comp.setFont(UNIFIED_FONT);
        }
        if (comp instanceof JMenuBar || comp instanceof JMenu || comp instanceof JMenuItem) {
            comp.setBackground(BACKGROUND_COLOR);
            comp.setForeground(TEXT_COLOR);
            comp.setFont(UNIFIED_FONT);
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                updateComponentColors(child);
            }
        }
    }

    /**
     * Sets up the graphical user interface components and layout
     * Uses GridBagLayout for responsive design
     */
    private void setupUI() {
        setTitle("Note App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        initializeComponents();
        setupKeyboardShortcuts();

        setJMenuBar(createMenuBar());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
        gbc.fill = GridBagConstraints.BOTH;

        // 0,0: Left sidebar panel (search + JList)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.18;
        gbc.weighty = 1.0;
        JPanel sidebarPanel = new JPanel(new BorderLayout(0, PADDING));
        sidebarPanel.setBackground(BACKGROUND_COLOR);
        // Search label + field
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(BACKGROUND_COLOR);
        JLabel searchLabel = new JLabel("Search Notes");
        searchLabel.setFont(UNIFIED_FONT.deriveFont(Font.BOLD, 13f));
        searchLabel.setForeground(TEXT_COLOR);
        searchLabel.setAlignmentX(LEFT_ALIGNMENT);
        sidebarSearchField = new PlaceholderTextField("Search notes...");
        sidebarSearchField.setFont(UNIFIED_FONT);
        sidebarSearchField.setPreferredSize(new Dimension(140, 28));
        sidebarSearchField.setBackground(Color.WHITE);
        sidebarSearchField.setForeground(TEXT_COLOR);
        sidebarSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        sidebarSearchField.setAlignmentX(LEFT_ALIGNMENT);
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createVerticalStrut(2));
        searchPanel.add(sidebarSearchField);
        // Notes label + list
        JPanel notesListPanel = new JPanel();
        notesListPanel.setLayout(new BoxLayout(notesListPanel, BoxLayout.Y_AXIS));
        notesListPanel.setBackground(BACKGROUND_COLOR);
        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setFont(UNIFIED_FONT.deriveFont(Font.BOLD, 13f));
        notesLabel.setForeground(TEXT_COLOR);
        notesLabel.setAlignmentX(LEFT_ALIGNMENT);
        noteList.setFont(UNIFIED_FONT);
        noteList.setBackground(Color.WHITE);
        noteList.setForeground(TEXT_COLOR);
        noteList.setFixedCellWidth(140);
        noteList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String title = value.toString();
                int maxLen = 16;
                if (title.length() > maxLen) {
                    label.setText(title.substring(0, maxLen - 3) + "...");
                    label.setToolTipText(title);
                } else {
                    label.setText(title);
                    label.setToolTipText(null);
                }
                label.setFont(UNIFIED_FONT);
                return label;
            }
        });
        JScrollPane listScrollPane = createModernScrollPane(noteList);
        listScrollPane.setPreferredSize(new Dimension(150, 0));
        listScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        notesListPanel.add(notesLabel);
        notesListPanel.add(Box.createVerticalStrut(2));
        notesListPanel.add(listScrollPane);
        // Add to sidebar
        sidebarPanel.add(searchPanel, BorderLayout.NORTH);
        sidebarPanel.add(notesListPanel, BorderLayout.CENTER);
        mainPanel.add(sidebarPanel, gbc);

        // 1,0: Title label + field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.75;
        gbc.weighty = 0.05;
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Title");
        titleLabel.setFont(UNIFIED_FONT.deriveFont(Font.BOLD, 13f));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleField = new PlaceholderTextField("Enter note title...");
        titleField.setFont(UNIFIED_FONT);
        titleField.setPreferredSize(new Dimension(200, 28));
        titleField.setBackground(Color.WHITE);
        titleField.setForeground(TEXT_COLOR);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        titleField.setAlignmentX(LEFT_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(titleField);
        mainPanel.add(titlePanel, gbc);

        // 1,1: Buttons row (Save, Edit, Delete, New Note)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.75;
        gbc.weighty = 0.05;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0));
        buttonPanel.add(createUnifiedButton("Save", e -> saveNote()));
        buttonPanel.add(createUnifiedButton("Edit", e -> editNote()));
        buttonPanel.add(createUnifiedButton("Delete", e -> deleteNote()));
        buttonPanel.add(createUnifiedButton("New Note", e -> newNote()));
        mainPanel.add(buttonPanel, gbc);

        // 1,2: Note content label + area
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.75;
        gbc.weighty = 0.9;
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.Y_AXIS));
        notePanel.setBackground(BACKGROUND_COLOR);
        JLabel noteLabel = new JLabel("Note Content");
        noteLabel.setFont(UNIFIED_FONT.deriveFont(Font.BOLD, 13f));
        noteLabel.setForeground(TEXT_COLOR);
        noteLabel.setAlignmentX(LEFT_ALIGNMENT);
        noteArea = new PlaceholderTextArea("Write your note here...");
        noteArea.setFont(UNIFIED_FONT);
        noteArea.setBackground(Color.WHITE);
        noteArea.setForeground(TEXT_COLOR);
        noteArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane noteScrollPane = createModernScrollPane(noteArea);
        noteScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        notePanel.add(noteLabel);
        notePanel.add(Box.createVerticalStrut(2));
        notePanel.add(noteScrollPane);
        mainPanel.add(notePanel, gbc);

        // 1,3: JLabel (status/welcome message)
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.75;
        gbc.weighty = 0.0;
        statusLabel = new JLabel("Welcome to Note App!");
        statusLabel.setFont(UNIFIED_FONT.deriveFont(Font.ITALIC));
        statusLabel.setForeground(ACCENT_COLOR);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, gbc);

        add(mainPanel);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Add search functionality
        sidebarSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterSidebarNotes(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterSidebarNotes(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterSidebarNotes(); }
        });
    }

    /**
     * Initialize all UI components and models
     */
    private void initializeComponents() {
        titleField = createModernTextField();
        noteArea = createModernTextArea();
        searchField = createModernTextField();
        dateLabel = new JLabel();
        statusLabel = new JLabel("Ready");
        listModel = new DefaultListModel<>();
        noteList = new JList<>(listModel);
        categoryComboBox = createModernComboBox(CATEGORIES);
        
        // Initialize formatting buttons
        boldButton = new JButton("B");
        italicButton = new JButton("I");
        underlineButton = new JButton("U");
        
        // Style the buttons
        styleFormatButtons();
    }

    /**
     * Style the formatting buttons (bold, italic, underline)
     */
    private void styleFormatButtons() {
        Font boldFont = new Font("Arial", Font.BOLD, 12);
        Font italicFont = new Font("Arial", Font.ITALIC, 12);
        Font underlineFont = new Font("Arial", Font.PLAIN, 12);
        
        boldButton.setFont(boldFont);
        italicButton.setFont(italicFont);
        underlineButton.setFont(underlineFont);
        
        Dimension buttonSize = new Dimension(30, 30);
        boldButton.setPreferredSize(buttonSize);
        italicButton.setPreferredSize(buttonSize);
        underlineButton.setFont(underlineFont);
    }

    /**
     * Create a modern styled panel
     */
    private JPanel createModernPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return panel;
    }

    /**
     * Create a modern styled scroll pane
     */
    private JScrollPane createModernScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * Create a modern styled combo box
     */
    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(MAIN_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        comboBox.setPreferredSize(new Dimension(comboBox.getWidth(), COMPONENT_HEIGHT));
        return comboBox;
    }

    /**
     * Create a modern styled button with icon
     */
    private JButton createModernButton(String text, String icon, ActionListener listener) {
        JButton button = new JButton(text + " " + icon);
        button.setFont(MAIN_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setPreferredSize(new Dimension(button.getWidth(), COMPONENT_HEIGHT));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        button.addActionListener(listener);
        return button;
    }

    /**
     * Create the search panel (not used in sidebar)
     */
    private JPanel createSearchPanel() {
        JPanel panel = createModernPanel();
        panel.setLayout(new BorderLayout(PADDING, 0));
        
        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(MAIN_FONT);
        searchField = new JTextField();
        searchField.setFont(MAIN_FONT);
        
        panel.add(searchIcon, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create the category panel (not used in minimal UI)
     */
    private JPanel createCategoryPanel() {
        JPanel panel = createModernPanel();
        panel.setLayout(new BorderLayout(PADDING, 0));
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(MAIN_FONT);
        categoryComboBox = createModernComboBox(CATEGORIES);
        
        panel.add(categoryLabel, BorderLayout.WEST);
        panel.add(categoryComboBox, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create the title panel (not used in minimal UI)
     */
    private JPanel createTitlePanel() {
        JPanel panel = createModernPanel();
        panel.setLayout(new BorderLayout(PADDING, 0));
        
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(MAIN_FONT);
        titleField = new JTextField();
        titleField.setFont(MAIN_FONT);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(titleField, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create the status bar panel
     */
    private JPanel createStatusBar() {
        JPanel panel = createModernPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, PADDING, 8, PADDING)
        ));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(MAIN_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }

    /**
     * Create the menu bar with File, Edit, View, and Settings menus
     * Adds accelerators and unified font
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BACKGROUND_COLOR);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        menuBar.setFont(UNIFIED_FONT);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(UNIFIED_FONT);
        JMenuItem newNoteItem = new JMenuItem("New Note");
        newNoteItem.setFont(UNIFIED_FONT);
        newNoteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newNoteItem.addActionListener(e -> newNote());
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setFont(UNIFIED_FONT);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveNote());
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setFont(UNIFIED_FONT);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        deleteItem.addActionListener(e -> deleteNote());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(UNIFIED_FONT);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(newNoteItem);
        fileMenu.add(saveItem);
        fileMenu.add(deleteItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setFont(UNIFIED_FONT);
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setFont(UNIFIED_FONT);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> noteArea.cut());
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setFont(UNIFIED_FONT);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> noteArea.copy());
        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.setFont(UNIFIED_FONT);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> noteArea.paste());
        JMenuItem editNoteItem = new JMenuItem("Edit Note");
        editNoteItem.setFont(UNIFIED_FONT);
        editNoteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        editNoteItem.addActionListener(e -> editNote());
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(editNoteItem);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(UNIFIED_FONT);
        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        zoomInItem.setFont(UNIFIED_FONT);
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        zoomInItem.addActionListener(e -> zoomIn());
        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        zoomOutItem.setFont(UNIFIED_FONT);
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        zoomOutItem.addActionListener(e -> zoomOut());
        JMenuItem resetZoomItem = new JMenuItem("Reset Zoom");
        resetZoomItem.setFont(UNIFIED_FONT);
        resetZoomItem.addActionListener(e -> resetZoom());
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetZoomItem);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(UNIFIED_FONT);
        JMenuItem settingsItem = new JMenuItem("Preferences...");
        settingsItem.setFont(UNIFIED_FONT);
        settingsItem.addActionListener(e -> showSettings());
        settingsMenu.add(settingsItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(settingsMenu);
        return menuBar;
    }

    /**
     * Create a modern styled menu item (not used in current UI)
     */
    private JMenuItem createModernMenuItem(String text, String accelerator, int keyCode, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(MAIN_FONT);
        item.setAccelerator(KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_DOWN_MASK));
        item.addActionListener(listener);
        return item;
    }

    /**
     * Zoom in the note area font size
     */
    private void zoomIn() {
        Font currentFont = noteArea.getFont();
        noteArea.setFont(currentFont.deriveFont((float) (currentFont.getSize() + 2)));
        statusLabel.setText("Zoom: " + (currentFont.getSize() + 2) + "%");
    }

    /**
     * Zoom out the note area font size
     */
    private void zoomOut() {
        Font currentFont = noteArea.getFont();
        if (currentFont.getSize() > 8) {
            noteArea.setFont(currentFont.deriveFont((float) (currentFont.getSize() - 2)));
            statusLabel.setText("Zoom: " + (currentFont.getSize() - 2) + "%");
        }
    }

    /**
     * Reset the note area font size to default (100%)
     */
    private void resetZoom() {
        noteArea.setFont(UNIFIED_FONT);
        statusLabel.setText("Zoom: 100%");
    }

    /**
     * Search notes based on the search field text
     */
    private void searchNotes() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            updateNoteList();
            return;
        }

        List<Note> filteredNotes = notes.stream()
            .filter(note -> note.getTitle().toLowerCase().contains(searchText) ||
                          note.getContent().toLowerCase().contains(searchText))
            .collect(Collectors.toList());

        listModel.clear();
        for (Note note : filteredNotes) {
            listModel.addElement(note.getTitle());
        }
    }

    /**
     * Creates a new empty note by clearing the title and content fields
     */
    private void newNote() {
        titleField.setText("");
        noteArea.setText("");
        noteList.clearSelection();
    }

    /**
     * Saves the current note to both memory and file system
     * Shows an error message if the title is empty
     */
    private void saveNote() {
        String title = titleField.getText().trim();
        String content = noteArea.getText();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title for the note.");
            return;
        }

        Note note = new Note(title, content);
        int selectedIndex = noteList.getSelectedIndex();
        
        // Update existing note or add new one
        if (selectedIndex != -1) {
            notes.set(selectedIndex, note);
        } else {
            notes.add(note);
        }

        saveToFile(note);
        updateNoteList();
    }

    /**
     * Deletes the currently selected note from both memory and file system
     */
    private void deleteNote() {
        int selectedIndex = noteList.getSelectedIndex();
        if (selectedIndex != -1) {
            Note note = notes.get(selectedIndex);
            File noteFile = new File(NOTES_DIR, note.getTitle() + ".txt");
            noteFile.delete();
            notes.remove(selectedIndex);
            updateNoteList();
            newNote();
        }
    }

    /**
     * Loads the selected note's content into the editor
     */
    private void loadSelectedNote() {
        int selectedIndex = noteList.getSelectedIndex();
        if (selectedIndex != -1) {
            Note note = notes.get(selectedIndex);
            titleField.setText(note.getTitle());
            noteArea.setText(note.getContent());
            dateLabel.setText(String.format("Created: %s | Last Modified: %s",
                Note.formatDate(note.getCreationDate()),
                Note.formatDate(note.getLastModifiedDate())));
        }
    }

    /**
     * Updates the note list display with current notes
     */
    private void updateNoteList() {
        listModel.clear();
        if (sidebarSearchField != null && !sidebarSearchField.getText().trim().isEmpty()) {
            filterSidebarNotes();
        } else {
        for (Note note : notes) {
            listModel.addElement(note.getTitle());
            }
        }
    }

    /**
     * Loads all existing notes from the notes directory into memory
     */
    private void loadNotes() {
        File notesDir = new File(NOTES_DIR);
        notes.clear();
        if (notesDir.exists()) {
            File[] files = notesDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String title = file.getName().replace(".txt", "");
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        Note note = new Note(title, content.toString(), file.lastModified(), file.lastModified());
                        notes.add(note);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateNoteList();
            }
        }
    }

    /**
     * Saves a note to a text file in the notes directory
     * @param note The note to be saved
     */
    private void saveToFile(Note note) {
        File noteFile = new File(NOTES_DIR, note.getTitle() + ".txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(noteFile))) {
            writer.write(note.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Export the current note to a text file
     */
    private void exportNote() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("Title: " + titleField.getText());
                writer.println("Date: " + dateLabel.getText());
                writer.println("\nContent:\n" + noteArea.getText());
                statusLabel.setText("Note exported successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting note: " + e.getMessage());
            }
        }
    }

    /**
     * Import a note from a text file
     */
    private void importNote() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                noteArea.setText(content.toString());
                statusLabel.setText("Note imported successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing note: " + e.getMessage());
            }
        }
    }

    /**
     * Edit the selected note (load into fields for editing)
     */
    private void editNote() {
        int selectedIndex = noteList.getSelectedIndex();
        if (selectedIndex != -1) {
            Note note = notes.get(selectedIndex);
            titleField.setText(note.getTitle());
            noteArea.setText(note.getContent());
            statusLabel.setText("Editing note: " + note.getTitle());
        } else {
            statusLabel.setText("No note selected to edit.");
        }
    }

    /**
     * Create a unified styled button for actions
     */
    private JButton createUnifiedButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(UNIFIED_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setPreferredSize(new Dimension(110, 36));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        button.addActionListener(listener);
        return button;
    }

    /**
     * Filter notes in the sidebar based on search text
     */
    private void filterSidebarNotes() {
        String searchText = sidebarSearchField.getText().trim().toLowerCase();
        filteredNotes.clear();
        listModel.clear();
        if (searchText.isEmpty()) {
            for (Note note : notes) {
                filteredNotes.add(note);
                listModel.addElement(note.getTitle());
            }
        } else {
            for (Note note : notes) {
                if (note.getTitle().toLowerCase().contains(searchText)) {
                    filteredNotes.add(note);
                    listModel.addElement(note.getTitle());
                }
            }
        }
    }

    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NoteApp app = new NoteApp();
            app.setVisible(true);
        });
    }
}

/**
 * PlaceholderTextField - JTextField with placeholder support
 */
class PlaceholderTextField extends JTextField {
    private String placeholder;
    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setUI(new BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                super.paintSafely(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(Color.GRAY);
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets insets = getInsets();
                    g.drawString(placeholder, insets.left + 2, getHeight() / 2 + getFont().getSize() / 2 - 2);
                }
            }
        });
        // Repaint on focus changes to show/hide placeholder
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { repaint(); }
            public void focusLost(java.awt.event.FocusEvent e) { repaint(); }
        });
    }
    @Override
    public void setText(String t) {
        super.setText(t);
        repaint(); // Ensure placeholder is shown after clearing
    }
}

/**
 * PlaceholderTextArea - JTextArea with placeholder support
 */
class PlaceholderTextArea extends JTextArea {
    private String placeholder;
    public PlaceholderTextArea(String placeholder) {
        this.placeholder = placeholder;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            g.setColor(Color.GRAY);
            g.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            g.drawString(placeholder, insets.left + 2, insets.top + getFont().getSize());
        }
    }
} 
/**
 * Finally, End of code :/
 */