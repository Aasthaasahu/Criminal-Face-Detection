package models;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FaceRecognizer {
    private static final int FACE_SIZE = 100;
    private static final double MATCH_THRESHOLD = 60.0;

    private CascadeClassifier faceCascade;
    private final List<Mat> trainingFaces = new ArrayList<>();
    private final List<Integer> trainingLabels = new ArrayList<>();

    public FaceRecognizer(String cascadePath, String trainingRoot) {
        faceCascade = new CascadeClassifier(cascadePath);
        loadTrainingData(trainingRoot);
    }

    public MatOfRect detectFaces(Mat frame) {
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frame, faces);
        return faces;
    }

    public int recognizeFace(Mat face) {
        Mat candidate = preprocessFace(face);
        if (candidate == null || trainingFaces.isEmpty()) {
            return -1;
        }

        double bestScore = Double.MAX_VALUE;
        int bestLabel = -1;

        for (int i = 0; i < trainingFaces.size(); i++) {
            Mat template = trainingFaces.get(i);
            double score = matchFaces(candidate, template);
            if (score < bestScore) {
                bestScore = score;
                bestLabel = trainingLabels.get(i);
            }
        }

        return bestScore <= MATCH_THRESHOLD ? bestLabel : -1;
    }

    private Mat preprocessFace(Mat face) {
        if (face == null || face.empty()) {
            return null;
        }

        Mat gray = new Mat();
        if (face.channels() > 1) {
            Imgproc.cvtColor(face, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = face.clone();
        }

        Imgproc.equalizeHist(gray, gray);
        Mat resized = new Mat();
        Imgproc.resize(gray, resized, new Size(FACE_SIZE, FACE_SIZE));
        return resized;
    }

    private double matchFaces(Mat faceA, Mat faceB) {
        Mat diff = new Mat();
        Core.absdiff(faceA, faceB, diff);
        Scalar mean = Core.mean(diff);
        return mean.val[0];
    }

    private void loadTrainingData(String trainingRoot) {
        Path root = Paths.get(trainingRoot);
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            System.err.println("Warning: Training folder not found: " + trainingRoot);
            return;
        }

        try (DirectoryStream<Path> persons = Files.newDirectoryStream(root)) {
            for (Path personDir : persons) {
                if (!Files.isDirectory(personDir)) {
                    continue;
                }
                int label = parseLabel(personDir.getFileName().toString());
                if (label < 0) {
                    continue;
                }

                try (DirectoryStream<Path> images = Files.newDirectoryStream(personDir)) {
                    for (Path imagePath : images) {
                        if (!Files.isRegularFile(imagePath)) {
                            continue;
                        }
                        String fileName = imagePath.getFileName().toString().toLowerCase();
                        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp"))) {
                            continue;
                        }

                        Mat image = Imgcodecs.imread(imagePath.toString());
                        if (image.empty()) {
                            continue;
                        }
                        MatOfRect faces = detectFaces(image);
                        Mat faceRegion = image;
                        Rect[] rects = faces.toArray();
                        if (rects.length > 0) {
                            faceRegion = image.submat(rects[0]);
                        }
                        Mat processed = preprocessFace(faceRegion);
                        if (processed != null) {
                            trainingFaces.add(processed);
                            trainingLabels.add(label);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load training data: " + e.getMessage());
        }

        if (trainingFaces.isEmpty()) {
            System.err.println("Warning: No training images were loaded from " + trainingRoot + ". Face recognition will be disabled.");
        } else {
            System.out.println("Loaded " + trainingFaces.size() + " training face(s) from " + trainingRoot);
        }
    }

    private int parseLabel(String folderName) {
        try {
            return Integer.parseInt(folderName);
        } catch (NumberFormatException e) {
            System.err.println("Skipping training folder with non-numeric label: " + folderName);
            return -1;
        }
    }
}
