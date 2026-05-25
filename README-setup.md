Setup and run instructions (Windows)

1) Install OpenCV for Windows and locate the Java files.
   - Example OpenCV Java JAR: `C:\Users\hp\Downloads\opencv\build\java\opencv-4110.jar`
   - Example native library directory: `C:\Users\hp\Downloads\opencv\build\java\x64`

2) From the project root, compile the sources:

```powershell
set OPENCV_JAR=C:\Users\hp\Downloads\opencv\build\java\opencv-4110.jar
javac -cp %OPENCV_JAR%;. process\*.java models\*.java
```

3) Run the app with OpenCV native access enabled:

```powershell
set OPENCV_NATIVE=C:\Users\hp\Downloads\opencv\build\java\x64
java --enable-native-access=ALL-UNNAMED -cp %OPENCV_JAR%;. -Djava.library.path=%OPENCV_NATIVE% process.App
```

4) If camera index `0` does not work, pass a different index:

```powershell
java --enable-native-access=ALL-UNNAMED -cp %OPENCV_JAR%;. -Djava.library.path=%OPENCV_NATIVE% process.App 1
```

5) Notes:
   - `xml/haarcascade_frontalface_alt.xml` must exist relative to the project root.
   - This repo currently includes a local fallback for `models.Criminal` so the app can start without MySQL.
   - If you want real DB support, create `criminal_db` with `criminal_database.sql` and restore JDBC code in `models/Criminal.java`.
   - If the camera does not open, try changing the camera index in `process.App` from `new VideoCapture(0)` to `new VideoCapture(1)`.
   - If the app complains about missing OpenCV native libs, verify the `-Djava.library.path` value points to the folder containing `opencv_java4110.dll`.
