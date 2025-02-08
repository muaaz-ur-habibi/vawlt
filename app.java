import javax.swing.*;

import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class app {
    // Simple JSON parser code
    public static Map<String, String> parseJson(final String jsonStr) {
        final Map<String, String> map = new HashMap<>();
        // Remove the opening and closing bracket
        final String jsonString = jsonStr.trim().substring(1, jsonStr.length() - 1);

        // extract key value pairs
        final String[] keyValuePairs = jsonString.split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }

        return map;
    }
    // create the main window
    static JFrame INIT() {
        JFrame main = new JFrame();
        main.setSize(1000, 700);
        main.setTitle("Vawlt");

        JPanel heading_panel = new JPanel();
        Image img = new ImageIcon("assets/vawlt-heading.png").getImage();
        heading_panel.add(new JLabel(new ImageIcon(img)));
        main.add(heading_panel);

        main.setLayout(new GridLayout(3, 1));

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return main;
    }

    static void EncryptPassword() {}

    static void SaveNewPassword(String password, String site, String uname, String notes) {}

    static boolean IsNew() {
        File dir = new File(".");
        File[] files = dir.listFiles();
        
        for (File f : files) {
            if ("notnew.txt".equals(f.getName())) {
                return false;
            }
        }
        return true;
    }

    static void CreatenotnewtxtFile(JFrame root) {
        File notnewtxt = new File("notnew.txt");
        
        try {
            notnewtxt.createNewFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(root, "Could not create notnew.txt. Error: " + e.getMessage());
        }
    }

    static void SetFirstTimeMasterPassword(JFrame root) {
        Object master_pass = JOptionPane.showInputDialog(root, "Its your first time using Vawlt\nSet a Master Password for your Vawlt. Make sure its Strong! This is going to be your private key.", "Master Password Set", JOptionPane.PLAIN_MESSAGE, null, null, null);
        String master_pass_str = String.valueOf(master_pass);
    
        if (master_pass_str.equals("")) {
            JOptionPane.showMessageDialog(root, "Please enter a password!");
            SetFirstTimeMasterPassword(root);
        } else
        if (master_pass == null) {
            System.exit(0);
        }


    }


    public static void main(String[] args) {
        JFrame root = INIT();

        if (IsNew()) {
            SetFirstTimeMasterPassword(root);
            //CreatenotnewtxtFile(root);
        }

        root.setVisible(true);
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}