package process;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.*;
import models.*;

public class App {
    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Warning: OpenCV native library not found. Running with Java stubs.");
        }
    }

    private JFrame frame;
    private JLabel imageLabel;
    private VideoCapture camera;
    private FaceRecognizer recognizer;
    private Criminal criminalDb;
    private int cameraIndex;

    public App(int cameraIndex) {
        this.cameraIndex = cameraIndex;
        recognizer = new FaceRecognizer("xml/haarcascade_frontalface_alt.xml", "training");
        criminalDb = new Criminal();
        initUI();
        startCamera();
    }

    public App() {
        this(0);
    }

    private void initUI() {
        frame = new JFrame("Criminal Face Detection");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);
    }

    private void startCamera() {
        camera = new VideoCapture(cameraIndex);
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(frame,
                    "Cannot open the camera at index " + cameraIndex + ".\n" +
                            "Try running the app again with a different camera index, for example:\n" +
                            "java process.App 1",
                    "Camera Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Mat frameMat = new Mat();

        while (camera.isOpened()) {
            if (!camera.read(frameMat) || frameMat.empty() || frameMat.cols() <= 0 || frameMat.rows() <= 0) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ignored) {
                }
                continue;
            }

            detectFaces(frameMat);
            Image img = ImageProcessor.toBufferedImage(frameMat);
            if (img != null) {
                imageLabel.setIcon(new ImageIcon(img));
                frame.pack();
            }
        }
    }

    private void detectFaces(Mat frame) {
        MatOfRect faces = recognizer.detectFaces(frame);

        for (Rect rect : faces.toArray()) {
                Imgproc.rectangle(frame, new org.opencv.core.Point(rect.x, rect.y),
                    new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0), 2);

            int criminalId = recognizer.recognizeFace(frame.submat(rect));
            String labelText;
            Scalar labelColor;
            if (criminalId != -1) {
                String name = criminalDb.getCriminalName(criminalId);
                labelText = "Criminal: " + name;
                labelColor = new Scalar(0, 0, 255);
            } else {
                labelText = "Innocent";
                labelColor = new Scalar(0, 255, 0);
            }
            Imgproc.putText(frame, labelText, new org.opencv.core.Point(rect.x, Math.max(rect.y - 10, 20)),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, labelColor, 2);
        }
    }

    public static void main(String[] args) {
        int cameraIndex = 0;
        if (args.length > 0) {
            try {
                cameraIndex = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid camera index argument. Using default camera index 0.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        new App(cameraIndex);
    }
}
