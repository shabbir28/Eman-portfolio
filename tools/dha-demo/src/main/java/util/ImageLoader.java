package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;

/**
 * Utility class for loading images reliably in JavaFX applications.
 * Handles resource loading from the classpath with proper error handling.
 */
public class ImageLoader {

    /**
     * Load an image from the resources folder
     * @param imagePath Path to the image (e.g., "/bg.png" or "/logo.png")
     * @return Image object or null if not found
     */
    public static Image loadImage(String imagePath) {
        try {
            URL imgUrl = ImageLoader.class.getResource(imagePath);
            if (imgUrl != null) {
                return new Image(imgUrl.toExternalForm());
            } else {
                System.err.println("Image not found: " + imagePath);
                return null;
            }
        } catch (Exception ex) {
            System.err.println("Error loading image: " + imagePath);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Load an image and set it to an ImageView
     * @param imageView The ImageView to set the image to
     * @param imagePath Path to the image
     * @return true if successfully loaded, false otherwise
     */
    public static boolean setImageToView(ImageView imageView, String imagePath) {
        if (imageView == null) {
            System.err.println("ImageView is null");
            return false;
        }

        Image image = loadImage(imagePath);
        if (image != null) {
            imageView.setImage(image);
            return true;
        }
        return false;
    }
}

