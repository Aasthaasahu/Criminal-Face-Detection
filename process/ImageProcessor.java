package process;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.Mat;

public class ImageProcessor {
    public static BufferedImage toBufferedImage(Mat matrix) {
        if (matrix == null || matrix.empty() || matrix.cols() <= 0 || matrix.rows() <= 0) {
            return null;
        }

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        matrix.get(0, 0, targetPixels);
        return image;
    }
}
