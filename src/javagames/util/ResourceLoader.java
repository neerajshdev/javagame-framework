package javagames.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceLoader {

    public static InputStream load(Class<?> klass, String filepath, String resPath ) {
        InputStream in = null;
        //try with res path
        if (!(resPath == null || resPath.isBlank())) {
            in = klass.getResourceAsStream(resPath);
            if (in != null) {
                return in;
            }
        }

        // try with file path
        try {
            in = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return in;
    }
}
