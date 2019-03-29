package imageViewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.util.List;

public class ImageViewerFrame extends JFrame
{
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 600;

    private JLabel label;
    private String path;

    private void drowImage()
    {
        ImageIcon img = new ImageIcon(path);
        setSize(img.getIconWidth() + 16, img.getIconHeight() + 56);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setIcon(img);
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
        label.addMouseWheelListener(new Zoomer());
        label.addMouseListener(new ImageMover());
        label.addMouseMotionListener(new ImageMover());
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
                    label.setIcon(ic);
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
    }
}
