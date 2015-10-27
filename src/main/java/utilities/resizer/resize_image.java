/*
Copyright 2015 Institute of Computer Science,
Foundation for Research and Technology - Hellas

Licensed under the EUPL, Version 1.1 or - as soon they will be approved
by the European Commission - subsequent versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl

Unless required by applicable law or agreed to in writing, software distributed
under the Licence is distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations
under the Licence.

Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
Tel:+30-2810-391632
Fax: +30-2810-391638
E-mail: isl@ics.forth.gr
http://www.ics.forth.gr/isl

Authors : Georgios Samaritakis, Georgios Messaritakis, Konstantina Konsolaki
This file is part of the ImageResize project.
*/

package utilities.resizer;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.io.File;
import org.devlib.schmidt.imageinfo.ImageInfo;

/**
 * Class resize_image uses class {@link ImageInfo} to resize a specific image,
 * or a directory<br/> of images.<br/><br/> See the comments inside the source
 * file resize_image.java<br/><br/> Typical usage:<br/>
 * <code>
 * resize_image imageResizer = new resize_image();				// create instance<br/><br/>
 *
 * imageResizer.set_dir_location(uploadDir, uploadDir+"thumbs");	// set input
 * and output directory<br/>
 * imageResizer.set_width_or_height(30, resize_image.HEIGHT);	// set size in
 * pixels, and which dimension to match<br/><br/>
 * imageResizer.resize_imageFile(filename);	// resize a single file, 'filename'
 * is a String object<br/><br/>
 * imageResizer.resize_imageFilesOriginalDirectory();	// resize all files in
 * input directory<br/><br/>
 * </code>
 *
 */
public class resize_image {

    static public int WIDTH = 0;
    static public int HEIGHT = 1;
    int originalImageWidth, originalImageHeight;
    String originalImageFormat;
    //Added by samarita
    String src_dir = "./images/original";  // directory where the original image is located.
    String dest_dir = "./images"; // directory where the resized image will be writen.
    int new_dimention;
    int dimention_choice = WIDTH;
    //int dimention_choice = prop.getProperty("DIMENSION");
    Vector valid_formats = new Vector();
    Vector transformed_formats = new Vector();
    Graphics2D g = null;

    /*
     * ----------------------------------------------------------------------
     * resize_image()
     * ------------------------------------------------------------------------
     */
    /**
     * Constructor to specify formats ImageInfo can handle
     */
    public resize_image() {
        valid_formats.add("JPEG");
        valid_formats.add("BMP");
        valid_formats.add("PNG");
        valid_formats.add("GIF");
        // valid_formats.add("PSD");
        transformed_formats.add("GIF"); // will be transformed to JPEG
    }

    /*
     * ----------------------------------------------------------------------
     * set_dir_location()
     * ------------------------------------------------------------------------
     */
    /**
     * Sets source and destination directories
     *
     * @param src Source directory as a <CODE>String</CODE>
     * @param dest Destination directory as a <CODE>String</CODE>
     */
    public void set_dir_location(String src, String dest) {
        src_dir = src;
        dest_dir = dest;
    }

    /*
     * ----------------------------------------------------------------------
     * set_width_or_height()
     * ------------------------------------------------------------------------
     */
    /**
     * Sets width or height to resize
     *
     * @param dimention Dimension desired in pixels as an <CODE>int</CODE>
     * @param choice WIDTH or HEIGHT
     */
    public void set_width_or_height(int dimention, int choice) {
        new_dimention = dimention;
        if (choice == WIDTH) {
            dimention_choice = WIDTH;
        } else {
            dimention_choice = HEIGHT;
        }
    }

    /*
     * ----------------------------------------------------------------------
     * GetImageData() Method used internally to check image format If format
     * cannot be handled by ImageInfo (JPEG, GIF, BMP, PCX, PNG, IFF, RAS, PBM,
     * PGM, PPM and PSD files) then image is converted to GIF and resize process
     * starts all over for the new GIF file If format cannot be read by Java
     * image codecs (e.g. not an image) then null is returned
     * ------------------------------------------------------------------------
     */
    private String GetImageData(String imageFileName) {
        ImageInfo image_info = new ImageInfo();
        FileInputStream inputStream = null;
        originalImageFormat = null;
        originalImageWidth = 0;
        originalImageHeight = 0;

        try {
            inputStream = new FileInputStream(imageFileName);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        }

        image_info.setInput(inputStream); // it can be InputStream or RandomAccessFile
        image_info.setDetermineImageNumber(true); // default is false
        image_info.setCollectComments(false); // default is false
        if (!image_info.check()) {
            System.out.println("Not a supported image file format for file: " + imageFileName);
            String newFileName = imageFileName.substring(0, imageFileName.lastIndexOf(".")) + ".gif";
            System.out.println("Creating gif file instead to repeat process!");

        
            return newFileName.substring(newFileName.lastIndexOf("/") + 1);

        }
        originalImageFormat = image_info.getFormatName();
        originalImageWidth = image_info.getWidth();
        originalImageHeight = image_info.getHeight();
        return imageFileName.substring(imageFileName.lastIndexOf("/") + 1);
    }

    /*
     * ----------------------------------------------------------------------
     * resize_imageFile()
     * ------------------------------------------------------------------------
     */
    /**
     * Resizes specified file
     *
     * @param originalImageFileName Image filename (not full path) as a
     * <CODE>String</CODE>
     * @return True if image resizing completed successfully. False otherwise.
     */
    public boolean resize_imageFile(String originalImageFileName) {
        // check the format
        String afterCheckImageFileName = GetImageData(src_dir + "/" + originalImageFileName);

        if (afterCheckImageFileName == null) {
            return false;
        }

        //In that case ImageInfo failed and we created a clone picture that ImageInfo can handle
        if (!afterCheckImageFileName.equals(originalImageFileName)) {
            originalImageFileName = afterCheckImageFileName;
            GetImageData(src_dir + "/" + afterCheckImageFileName);
        }

        if (originalImageFormat == null) {
            return false;
        }

        if (valid_formats.contains(originalImageFormat) == false) {
            System.out.println("\tNot supported image format for resizing: "
                    + originalImageFormat + " for file: " + originalImageFileName);
            return false;
        }

        System.out.println("loading original");
        float image_ratio = (float) (originalImageWidth) / originalImageHeight;
        int new_image_width, new_image_height;
        String out_format;

        if (dimention_choice == WIDTH) {
            new_image_width = new_dimention;
            new_image_height = (int) (new_image_width * (1 / image_ratio));
        } else { // choice == HEIGHT
            new_image_height = new_dimention;
            new_image_width = (int) (new_image_height * image_ratio);
        }

        // if original image is smaller than new image DO NOT resize
        // or bigger than a specific dimention DO NOT resize
        if (((image_ratio >= 1.0) && (originalImageWidth < new_image_width)) || ((image_ratio < 1.0) && (originalImageHeight < new_image_height))) {
            new_image_width = originalImageWidth;
            new_image_height = originalImageHeight;
        }

        System.out.println("WXH:" + new_image_width + " " + new_image_height);

        BufferedImage img = new BufferedImage(new_image_width, new_image_height, BufferedImage.TYPE_INT_RGB);
        out_format = originalImageFormat;
        if (transformed_formats.contains(out_format)) {
            out_format = "JPEG"; // transform the format to JPEG }
        }
        try {
            String in = src_dir + "/" + originalImageFileName;
            img.createGraphics().drawImage(ImageIO.read(new File(in)).getScaledInstance(new_image_width, new_image_height, Image.SCALE_SMOOTH), 0, 0, null);
            String out = dest_dir + "/" + originalImageFileName;
            ImageIO.write(img, out_format, new File(out));
        } catch (IOException ex) {
            System.out.println("ex");
        }

        return true;
    }

    /**
     * Load a properties file
     *
     * @param propFilename Properties filename as a <CODE>String</CODE>
     * @return Properties file as a <CODE>Properties</CODE>
     */
    public static Properties loadProperties(String propFilename) {
        Properties prop = new Properties();
        try {
            prop.loadFromXML(new FileInputStream(propFilename));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return prop;
    }

    /**
     * Chooses dimension to resize
     *
     * @param dimension WIDTH for width and HEIGHT for height as
     * <CODE>String</CODE>
     * @return WIDTH or HEIGHT as an <CODE>int</CODE>
     */
    public static int chooseDimension(String dimension) {

        if (dimension.equals("WIDTH")) {
            return WIDTH;
        } else {
            return HEIGHT;
        }

    }
    /*
     * ----------------------------------------------------------------------
     * resize_imageFilesOriginalDirectory()
     * ------------------------------------------------------------------------
     */

    /**
     * Resizes all files inside a directory<br/> Directory is specified in a
     * Properties file
     */
    public void resize_imageFilesOriginalDirectory() {
        try {
            Boolean ret;
            File imageFilesDirectory = new File(src_dir);
            String filesOfDirectory[] = imageFilesDirectory.list();

            int filesOfDirectoryCount = filesOfDirectory.length;
            for (int i = 0; i < filesOfDirectoryCount; i++) {
                String fileNameFullPath = src_dir + "/" + filesOfDirectory[i];
                System.out.println("Resizing image: " + fileNameFullPath);
                ret = resize_imageFile(filesOfDirectory[i]);
                if (ret == false) {
                    System.out.println("\tResizing failed, file not copied...");
                }
                // System.out.println("format: " + originalImageFormat);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
