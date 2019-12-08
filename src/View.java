import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

class View extends JFrame {
    private final static int SCREEN_WIDTH = 1200;
    private final static int SCREEN_HEIGHT = 700;
    private final static int CHANNEL_WIDTH = SCREEN_WIDTH / 2 - 100;
    private final static int CHANNEL_HEIGHT = SCREEN_HEIGHT / 2 - 50;

    private JLabel title;
    private JToggleButton toggle;
    private JLabel red = new JLabel();
    private JLabel green = new JLabel();
    private JLabel blue = new JLabel();
    private JLabel alpha = new JLabel();

    private File image;
    private boolean highlight;


    View() {
        setTitle("Image Channels Looker (by Abionics)");
        setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        setLocationRelativeTo(null);    //position center
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton open = new JButton("Choose image...");
        open.addActionListener(e -> choose());
        title = new JLabel("...");
        toggle = new JToggleButton("Highlight invisible pixels");
        toggle.addActionListener(e -> {
            highlight = toggle.isSelected();
            analyze();
        });

        JPanel controls = new JPanel();
        controls.add(open);
        controls.add(title);
        controls.add(toggle);

        JPanel channels = new JPanel();
        channels.setLayout(new GridLayout(2, 2, 5, 5));
        channels.add(red);
        channels.add(green);
        channels.add(blue);
        channels.add(alpha);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        add(controls, constraints);
        constraints.gridy = 1;
        constraints.gridheight = 3;
        add(channels, constraints);

        setVisible(true);
    }

    private void choose() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setDialogTitle("Choose image");
        chooser.setFileFilter(new FileNameExtensionFilter("*.png, *.jpg", "png", "jpg"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            image = chooser.getSelectedFile();
            title.setText(image.getName());
            analyze();
        }
    }

    private void analyze() {
        if (image == null) {
            System.out.println("Choose image! image == null!");
            return;
        }
        try {
            Imaginator imaginator = new Imaginator(image);
            imaginator.generate(highlight);
            setChannelImage(red, imaginator.getRed());
            setChannelImage(green, imaginator.getGreen());
            setChannelImage(blue, imaginator.getBlue());
            setChannelImage(alpha, imaginator.getAlpha());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setChannelImage(@NotNull JLabel channel, BufferedImage image) {
        Image scaled = scale(image, CHANNEL_WIDTH, CHANNEL_HEIGHT);
        ImageIcon icon = new ImageIcon(scaled);
        channel.setIcon(icon);
    }

    private Image scale(@NotNull Image image, int newWidth, int newHeight) {
        int oldWidth = image.getWidth(null);
        int oldHeight = image.getHeight(null);
        double scale = Math.min((double) newWidth / oldWidth, (double) newHeight / oldHeight);
        int width = (int) (oldWidth * scale);
        int height = (int) (oldHeight * scale);

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaled.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();

        return scaled;
    }
}
