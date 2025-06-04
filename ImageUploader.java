import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Component;


public class ImageUploader {
    public static File uploadImage(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Upload an Image");
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Create destination path
            File destDir = new File("assets/images/");
            if (!destDir.exists()) destDir.mkdirs();

            File destFile = new File(destDir, selectedFile.getName());
            try {
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return destFile;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Failed to upload image.");
            }
        }
        return null;
    }
}
