import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File; // Import the File class

public class DataStreams extends JFrame {

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchTextField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;

    private String currentDirectory; // Store the current directory

    public DataStreams() {
        setTitle("Data Streams Processing");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Get the current directory
        currentDirectory = System.getProperty("user.dir"); // Get the user's current working directory [cite: 4]

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        JPanel controlPanel = new JPanel();

        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);

        textPanel.add(originalScrollPane);
        textPanel.add(filteredScrollPane);

        searchTextField = new JTextField(20);
        JLabel searchLabel = new JLabel("Search String:");

        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");
        searchButton.setEnabled(false);

        controlPanel.add(loadButton);
        controlPanel.add(searchLabel);
        controlPanel.add(searchTextField);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
                searchButton.setEnabled(true);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchFile();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private String filePath;

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser(currentDirectory); // Set the current directory [cite: 1]
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(textFilter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
            filePath = path.toString();
            try (Stream<String> lines = Files.lines(path)) {
                StringBuilder sb = new StringBuilder();
                lines.forEach(line -> sb.append(line).append("\n"));
                originalTextArea.setText(sb.toString());
                filteredTextArea.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchFile() {
        String searchString = searchTextField.getText();
        if (filePath != null && !searchString.isEmpty()) {
            try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
                StringBuilder sb = new StringBuilder();
                lines.filter(line -> line.contains(searchString))
                        .forEach(line -> sb.append(line).append("\n"));
                filteredTextArea.setText(sb.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error searching file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file loaded or search string is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStreams().setVisible(true);
        });
    }
}