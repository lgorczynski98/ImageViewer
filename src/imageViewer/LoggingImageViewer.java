package imageViewer;

import java.awt.*;
import javax.swing.*;

public class LoggingImageViewer
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() ->
        {
            JFrame frame = new ImageViewerFrame();
            frame.setTitle("Przegladarka Obrazow");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ImageIcon icon = new ImageIcon("ananas.jpg");
            frame.setIconImage(icon.getImage());
            frame.getContentPane().setBackground(Color.GRAY);

            frame.setVisible(true);
        });
    }
}
