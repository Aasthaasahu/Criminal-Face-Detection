# Criminal Face Detection System

## Requirements
- Java JDK 11 or newer
- OpenCV for Java
- Haar Cascade XML file for face detection
- Optional: MySQL for real criminal lookup

## Setup Instructions
1. Install OpenCV and locate the Java JAR and native DLLs.
   - Example JAR: `C:\Users\hp\Downloads\opencv\build\java\opencv-4110.jar`
   - Example native library directory: `C:\Users\hp\Downloads\opencv\build\java\x64`
2. Copy the Haar cascade XML into the project root if needed:
   - `xml/haarcascade_frontalface_alt.xml`
3. Compile the project from the project root:

```powershell
set OPENCV_JAR=C:\Users\hp\Downloads\opencv\build\java\opencv-4110.jar
javac -cp %OPENCV_JAR%;. process\*.java models\*.java
```

4. Run the app using the OpenCV native path:

```powershell
set OPENCV_NATIVE=C:\Users\hp\Downloads\opencv\build\java\x64
java --enable-native-access=ALL-UNNAMED -cp %OPENCV_JAR%;. -Djava.library.path=%OPENCV_NATIVE% process.App
```

5. If camera index `0` does not work, retry with another index:

```powershell
java --enable-native-access=ALL-UNNAMED -cp %OPENCV_JAR%;. -Djava.library.path=%OPENCV_NATIVE% process.App 1
```

## Notes
- The project uses `process/` and `models/` package directories.
- `criminal_database.sql` is in the project root and creates `criminal_db` with a `criminals` table.
- `models.Criminal` currently includes a local fallback for testing without MySQL.
- Training images should be placed in the `training/` folder.
  - Use numeric subfolders for labels, for example `training/1/`, `training/2/`.
  - `training/1/` should contain images of criminal ID `1`.
- If your webcam does not open, try changing the camera index in `process.App` from `new VideoCapture(0)` to `new VideoCapture(1)`.
- If OpenCV native libs fail to load, verify the `-Djava.library.path` points to the folder containing `opencv_java4110.dll`.

## Features
✅ Face detection with Haar cascade  
✅ Face recognition using training images  
✅ Webcam GUI feed  
✅ Criminal lookup support  
✅ Local database fallback for quick start  
