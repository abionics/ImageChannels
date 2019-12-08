import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Imaginator {
    private static final int RED = 0xffff0000;

    private int height;
    private int width;
    private byte[][][] pixels;
    private BufferedImage red;
    private BufferedImage green;
    private BufferedImage blue;
    private BufferedImage alpha;


    Imaginator(File input) throws IOException {
        open(input);
    }

    private void open(File input) throws IOException {
        BufferedImage image = ImageIO.read(input);
        height = image.getHeight();
        width = image.getWidth();
        int[] buff = image.getRGB(0, 0, width, height, null, 0, width);
        pixels = new byte[height][width][4];
        int k = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int color = buff[k++];
                pixels[i][j][0] = (byte) ((color >> 24) & 0xff);
                pixels[i][j][1] = (byte) ((color >> 16) & 0xff);
                pixels[i][j][2] = (byte) ((color >> 8) & 0xff);
                pixels[i][j][3] = (byte) (color & 0xff);
            }
    }

    void generate(boolean highlight) {
        long start = System.currentTimeMillis();
        System.out.println("\nGenerating channels...");
        red = create(select(Channel.RED, highlight));
        green = create(select(Channel.GREEN, highlight));
        blue = create(select(Channel.BLUE, highlight));
        alpha = create(select(Channel.ALPHA, false));
        System.out.println("Generating success, it takes " + (System.currentTimeMillis() - start) + "ms");
    }

    private int[][] select(Channel channel, boolean highlight) {
        int[][] data = new int[height][width];
        int channelID = channel.ordinal();
        int invisible = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int origin = Byte.toUnsignedInt(pixels[i][j][channelID]);
                int color = 0xff - origin;
                if (highlight && pixels[i][j][0] == 0 && origin != 0) {
                    data[i][j] = RED;
                    invisible++;
                } else {
                    data[i][j] = 0xff000000 | (color << 16) | (color << 8) | color;
                }
            }
        if (highlight) {
            System.out.println("Invisible pixels detected in channel " + channel + ":\t" + invisible);
        }
        return data;
    }

    private BufferedImage create(int[][] data) {
        BufferedImage slice = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] buff = new int[height * width];
        int k = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                buff[k++] = data[i][j];
        slice.setRGB(0, 0, width, height, buff, 0, width);
        return slice;
    }

    BufferedImage getRed() {
        return red;
    }
    BufferedImage getGreen() {
        return green;
    }
    BufferedImage getBlue() {
        return blue;
    }
    BufferedImage getAlpha() {
        return alpha;
    }

    private enum Channel {
        ALPHA, RED, GREEN, BLUE
    }
}
