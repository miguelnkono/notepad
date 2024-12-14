package miguel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NotepadGUI extends JFrame {
    // File explorer.
    private JFileChooser file_chooser;

    // current file.
    private File current_file;

    // text area.
    private JTextArea text_area;

    public NotepadGUI() {
        super("Notepad");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // file chooser setup
        file_chooser = new JFileChooser();
        file_chooser.setCurrentDirectory(new File("src\\assets"));
        // filter to just show the .txt files in the dialog.
        file_chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        addGuiComponents();
    }

    /*
     * Add some components to our GUI.
     */
    private void addGuiComponents() {
        addToolbar();

        // area to type text into.
        text_area = new JTextArea();
        add(text_area, BorderLayout.CENTER);
    }

    /*
     * Add a tool bar to our GUI.
     */
    private void addToolbar() {
        // create a toolbar.
        JToolBar tool_bar = new JToolBar();
        tool_bar.setFloatable(false);

        // create and add a menu bar to the tool bar.
        JMenuBar menu_bar = new JMenuBar();
        tool_bar.add(menu_bar);

        // add menus to the menu bar.
        menu_bar.add(addFileMenu());

        // add the tool bar to the GUI app.
        add(tool_bar, BorderLayout.NORTH);
    }

    /*
     * Add file menu to the menu bar of the tool bar.
     */
    private JMenu addFileMenu() {
        JMenu file_menu = new JMenu("File");

        // 'new' functionality.
        JMenuItem new_file_item = new JMenuItem("New");
        // implement the logic of the new feature.
        new_file_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // change title header.
                setTitle(getTitle()); // maybe need to 'Notepad'.

                // reset the text area.
                text_area.setText("");

                // reset the current file variable.
                current_file = null;
            }
        });
        file_menu.add(new_file_item);

        // 'open' functionality.
        JMenuItem open_file_item = new JMenuItem("Open");
        // implement the open logic.
        open_file_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opn file explorer.
                int result = file_chooser.showOpenDialog(NotepadGUI.this);

                if (result != JFileChooser.APPROVE_OPTION)
                    return;

                // reset notepad.
                // will execute the logic implemented for the open feature.
                new_file_item.doClick();

                // get the selected file.
                File selected_file = file_chooser.getSelectedFile();

                // update the current file.
                current_file = selected_file;

                // change the title of the header.
                setTitle(selected_file.getName());
                try (// read the file.
                        FileReader file_reader = new FileReader(selected_file)) {
                    BufferedReader buffer_reader = new BufferedReader(file_reader);

                    // store the text.
                    StringBuilder file_text = new StringBuilder();
                    String read_text;
                    while ((read_text = buffer_reader.readLine()) != null) {
                        file_text.append(read_text + "\n");
                    }

                    // update the area gui.
                    text_area.setText(file_text.toString());

                    buffer_reader.close();
                    file_reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        file_menu.add(open_file_item);

        // 'save as' functionality.
        JMenuItem save_as_item = new JMenuItem("Save as");
        // implementing the logic for the save as feature.
        save_as_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open save dialog.
                int result = file_chooser.showOpenDialog(NotepadGUI.this);

                // continu only if the user pressed save button.
                if (result != JFileChooser.APPROVE_OPTION)
                    return;
                try {
                    // get the selected file from the dialog.
                    File selected_file = file_chooser.getSelectedFile();

                    // we need to append the '.txt' extension to file of it does not
                    // already have it.
                    String file_name = selected_file.getName();
                    if (!file_name.substring(file_name.length() - 4).equalsIgnoreCase(".txt")) {
                        selected_file = new File(selected_file.getAbsoluteFile() + ".txt");
                    }

                    // create a new file.
                    selected_file.createNewFile();

                    // write the user text into the file we just created.
                    FileWriter file_writer = new FileWriter(selected_file);
                    BufferedWriter buffer_writer = new BufferedWriter(file_writer);
                    // write the content of the file into the text area.
                    buffer_writer.write(text_area.getText());
                    // close both buffer_writer and file_writer.
                    buffer_writer.close();
                    file_writer.close();

                    // update the title header of the gui to the same text file.
                    setTitle(file_name);

                    // update the current file.
                    current_file = selected_file;

                    // show display dialog.
                    JOptionPane.showMessageDialog(NotepadGUI.this, "Saved File!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        file_menu.add(save_as_item);

        // 'save' functionality.
        JMenuItem save_item = new JMenuItem("Save");
        // implement the logic for the save option.
        save_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if the current file is null then we have to perform
                // save as functionality.
                if (current_file == null)
                    save_as_item.doClick();

                // if the user choose to cancel saving the file this means that the 
                // current file will still be null, then we want to prevent executing 
                // the rest of the code.
                if (current_file == null)
                    return;

                try {
                    // write to the current file.
                    FileWriter file_writer = new FileWriter(current_file);
                    BufferedWriter buffer_writer = new BufferedWriter(file_writer);
                    buffer_writer.write(text_area.getText());
                    buffer_writer.close();
                    file_writer.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        file_menu.add(save_item);

        // 'exit' functionality.
        JMenuItem exit_item = new JMenuItem("Exit");
        exit_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // dispose of this gui.
                NotepadGUI.this.dispose();
            }
        });
        file_menu.add(exit_item);

        return file_menu;
    }

}
