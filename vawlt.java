import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;;


public class vawlt {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String PASSWORDS_LOC = "passwords/pw.json";
    // SECRET KEY IS NOT STORED!!! This is for the app to use to encrypt/decrypt your passwords
    // Otherwise you would have to enter this everytime you wanted to encrypt/decrypt
    private static String SECRET_KEY;
    private static SecretKey SECRET_KEY_NON_STR;

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
        heading_panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, heading_panel.getMinimumSize().height));
        main.add(heading_panel);

        main.setLayout(new BoxLayout(main.getContentPane(), BoxLayout.Y_AXIS));

        ///*
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        //*/
            
        return main;
    }

    // generates a new secret vawlt key for the user
    static void GenerateSecretKey(String masterpassword) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
            // generate a 256 bits key
            generator.init(256);
            SECRET_KEY_NON_STR = generator.generateKey();
            SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY_NON_STR.getEncoded());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error occured while trying to generate vawlt key. Error: " + e.getMessage());
        }
    }

    static void EncryptPassword(String password) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY_NON_STR);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error occured while trying to encrypt password. Error: " + e.getMessage());
        }
    }

    static void SaveNewPassword(String password, String site, String uname, String notes) {}

    // checks if the user is new using the notnew.txt file
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

    // creates the file that is used as a check whether the user is new or not
    static void CreatenotnewtxtFile(JFrame root) {
        File notnewtxt = new File("notnew.txt");
        
        try {
            notnewtxt.createNewFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(root, "Could not create notnew.txt. Error: " + e.getMessage());
        }
    }

    // creates the password json file
    static void CreatePasswordDatabase(JFrame root) {
        File notnewtxt = new File(PASSWORDS_LOC);
        
        try {
            notnewtxt.createNewFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(root, "Could not create " + PASSWORDS_LOC + ". Error: " + e.getMessage());
        }
    }

    // generates a new vawlt key and auto copies it to the clipboard
    static void SetFirstTimeMasterPassword(JFrame root) {
        JOptionPane.showMessageDialog(root, "Its your first time using Vawlt. I'll generate a Master Password for your Vawlt.\nThis is going to be your secret Vawlt key");

        String master_pass_str = "";
        
        
        Random r = new Random();
        int init_len = master_pass_str.length();
            
        for (int i = 0; i < 256-init_len; i++) {
            if (r.nextBoolean()) {
                master_pass_str = master_pass_str + (char)(r.nextInt(26) + 'a');
            } else {
                master_pass_str = master_pass_str + String.valueOf(r.nextInt(9));
            }
        }
        
        GenerateSecretKey(master_pass_str);

        JOptionPane.showMessageDialog(root, "Success! Your secret vawlt key is: " + SECRET_KEY + "\nMake sure to keep it safe and sound\nJust press OK and itll be copied into your clipboard");
        
        StringSelection select_master_pass = new StringSelection(SECRET_KEY);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(select_master_pass, null);
    }

    // prompts for vawlt key
    static void PromptForSecretKey(JFrame root) {
        String resp = JOptionPane.showInputDialog(root, "Please enter your Vawlt Key: ", "Vawlt Key Prompt", JOptionPane.QUESTION_MESSAGE);

        if (resp == null) {
            System.exit(0);
        } else {
            try {
                SECRET_KEY = String.valueOf(resp);
                byte[] secretkey_bytes = Base64.getDecoder().decode(SECRET_KEY);
                SECRET_KEY_NON_STR = new SecretKeySpec(secretkey_bytes, ENCRYPTION_ALGORITHM);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(root, "Error in trying to decode your Vawlt Key. Please make sure to input a valid Vawlt Key. Error: " + e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    static void GenerateUI(JFrame root) {
        // CREATE TABLE
        String[] col_names = {"Site/App", "Username", "Password", "Encypt/Decrypt"};

        DefaultTableModel tableModel = new DefaultTableModel(col_names, 0);
        JTable passwords_table = new JTable(tableModel);
        JScrollPane table_scrollpane = new JScrollPane(passwords_table);

        passwords_table.getColumnModel().getColumn(0).setMinWidth(200);
        passwords_table.getColumnModel().getColumn(1).setMinWidth(150);
        passwords_table.getColumnModel().getColumn(2).setMinWidth(300);

        JPanel table_panel = new JPanel();
        table_panel.add(table_scrollpane);
        table_panel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        
        table_panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        table_scrollpane.setBorder(BorderFactory.createLineBorder(Color.RED));
        table_scrollpane.setPreferredSize(new Dimension(root.getWidth()-30, 240));
        passwords_table.setPreferredSize(new Dimension(table_scrollpane.getWidth(), table_scrollpane.getHeight()));
        //END CREATE TABLE

        root.add(table_panel);
    }

    public static void main(String[] args) {
        JFrame root = INIT();

        // check if its a new user
        // if yes then generate a new vawlt key
        if (IsNew()) {
            SetFirstTimeMasterPassword(root);
            CreatenotnewtxtFile(root);
            CreatePasswordDatabase(root);
        } else { // else prompt for the vawlt key they have
            PromptForSecretKey(root);
        }

        GenerateUI(root);

        root.setVisible(true);
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}