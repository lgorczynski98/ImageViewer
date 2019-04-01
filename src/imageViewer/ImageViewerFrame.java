package imageViewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageViewerFrame extends JFrame
{
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 600;

    private JLabel label;
    private String path;
    private ArrayList<String> files;

    private void reading_files()
    {
        try
        {
            int end = path.lastIndexOf('\\');
            String filePath = path.substring(0, end);
            File file = new File(filePath);
            String [] str= file.list();
            files = new ArrayList<>(Arrays.asList(str));
            for(int i = 0; i < files.size(); i++)
            {
                String s = files.get(i);
                files.set(i, filePath + '\\' + s);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void init_label(ImageIcon img)
    {
        reading_files();
        if(img.getIconHeight() > 1920 || img.getIconHeight() > 1080)
        {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int w = (int) screenSize.getWidth();
            int h = (int) screenSize.getHeight();
            Zoomer resized = new Zoomer();
            Image resizedImg = resized.ZoomImage(w, h, img.getImage());
            img = new ImageIcon(resized.ZoomImage(w, h, resizedImg));
            setSize(w,h);
        }
        else {setSize(img.getIconWidth() + 16, img.getIconHeight() + 56);}

        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        setLocationRelativeTo(null);
        label.setIcon(img);
        String name = path.substring(path.lastIndexOf('\\') + 1);
        setTitle("Przegladarka Obrazow : " + name);
    }

    private void drowImage()
    {
        ImageIcon img = new ImageIcon(path);
        if(img.getIconWidth() > 0 && img.getIconHeight() > 0)
        {
            init_label(img);
        }
    }

    private boolean drowImage(String file_name)
    {
        ImageIcon img = new ImageIcon(file_name);
        if(img.getIconWidth() > 0 && img.getIconHeight() > 0)
        {
            path = file_name;
            init_label(img);
            return true;
        }
        return false;
    }

    private void connectToDragDrop()
    {
        DragListener d = new DragListener();
        new DropTarget(this, d);
    }

    public ImageViewerFrame()
    {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("Plik");
        menuBar.add(menu);

        JMenuItem openItem = new JMenuItem("Otworz");
        menu.add(openItem);
        openItem.addActionListener(new FileOpenListener());
        JMenuItem exitItem = new JMenuItem("Zamknij");
        menu.add(exitItem);

        JMenu about = new JMenu("About");
        menuBar.add(about);
        JMenuItem tworca = new JMenuItem("O Autorze");
        about.add(tworca);
        tworca.addActionListener(new MessageBoxAuthor());

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        label = new JLabel();
        add(label);
        addKeyListener(new KeyImageChanger());
        label.addMouseWheelListener(new Zoomer());
        label.addMouseListener(new ImageMover());
        label.addMouseMotionListener(new ImageMover());
        //label.addKeyListener(new KeyImageChanger());
        connectToDragDrop();
    }

    private class FileOpenListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));

            chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
                public boolean accept(File f)
                {
                    return f.getName().toLowerCase().endsWith(".gif") || f.isDirectory();
                }

                public String getDescription() {
                    return "Obrazy GIF";
                }
            });

            chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
                public boolean accept(File f)
                {
                    return f.getName().toLowerCase().endsWith(".jpg") || f.isDirectory();
                }

                public String getDescription() {
                    return "Obrazy JPG";
                }
            });

            chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
                public boolean accept(File f)
                {
                    return f.getName().toLowerCase().endsWith(".png") || f.isDirectory();
                }

                public String getDescription() {
                    return "Obrazy PNG";
                }
            });

            int r = chooser.showOpenDialog(ImageViewerFrame.this);

            if (r == JFileChooser.APPROVE_OPTION)
            {
                path = chooser.getSelectedFile().getPath();
                drowImage();
            }
        }
    }

    private class MessageBoxAuthor implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            JOptionPane.showMessageDialog(null, "Autor: \nLukasz Gorczynski", "O Autorze", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class DragListener implements DropTargetListener
    {
        @Override
        public void dragEnter(DropTargetDragEvent dtde)
        {
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde)
        {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde)
        {
        }

        @Override
        public void dragExit(DropTargetEvent dte)
        {
        }

        @Override
        public void drop(DropTargetDropEvent dtde)
        {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable t = dtde.getTransferable();
            DataFlavor[] df = t.getTransferDataFlavors();
            for(DataFlavor f : df)
            {
                try
                {
                    if(f.isFlavorJavaFileListType())
                    {
                        List<File> files = (List<File>) t.getTransferData(f);
                        for (File file : files)
                        {
                            path = file.getPath();
                            drowImage();
                        }
                    }
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }
    }

    private class Zoomer implements MouseWheelListener
    {
        private Image ZoomImage(int w, int h, Image img)
        {
            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D grf = buf.createGraphics();
            grf.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            grf.drawImage(img,0,0,w,h,null);
            grf.dispose();
            return buf;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            int point = e.getWheelRotation();
            if(point < 0)
            {
                int w = label.getIcon().getIconWidth();
                int h = label.getIcon().getIconHeight();
                File file = new File(path);
                try
                {
                    Image img = ImageIO.read(file);
                    ImageIcon ic = new ImageIcon(ZoomImage(w + 50, h + 50, img));
                    label.setIcon(ic);
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
            else
            {
                int w = label.getIcon().getIconWidth();
                int h = label.getIcon().getIconHeight();
                File file = new File(path);
                try
                {
                    Image img = ImageIO.read(file);
                    ImageIcon ic = new ImageIcon(ZoomImage(w - 50, h - 50, img));
                    if(ic.getIconWidth() > 0 || ic.getIconHeight() > 0)
                        label.setIcon(ic);
                }
                catch(IllegalArgumentException ex)
                {
                    //ImageIcon ic = new ImageIcon(ZoomImage(w + 50, h + 50, img));
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
    }

    private class KeyImageChanger implements KeyListener
    {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_LEFT && files != null)
                {
                int index = files.indexOf(path);
                int i = (index - 1) % (files.size());
                if (i == -1) i = files.size() - 1;
                while(i != index)
                {
                    String name = files.get(i);
                    if(drowImage(name))
                        break;
                    i = (i - 1) % (files.size());
                    if (i == -1) i = files.size() - 1;
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT && files != null)
            {
                int index = files.indexOf(path);
                int i = (index + 1) % (files.size());
                while(i != index)
                {
                    String name = files.get(i);
                    if(drowImage(name))
                        break;
                    i = (i + 1) % (files.size());
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
