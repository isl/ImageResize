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

import utilities.resizer.resize_image;
import java.io.File;
import java.util.Properties;

/**
 * Main class used together with a Prop.properties file to resize images<br/>
 */
public class Main {

    /**
     * Resizes either a single image file or a folder full of image files<br/>
     * If an argument is used then this argument is the single file to resize<br/>
     * If there is no argument then it resizes all files found in the source directory<br/>
     * Source directory, destination directory, dimension to resize and pixels are all specified in the Properties file<br/>
     * @param args Filename of file to resize as a <CODE>String</CODE>
     */
    public static void main(String[] args) {


        System.out.println("Computer Java handles following image formats:");
        
        int argLen = args.length;
        try {
            String currentPath = new File(".").getCanonicalPath();
            Properties prop = utilities.resizer.resize_image.loadProperties(currentPath + "/Prop.properties");
            String src_dir = prop.getProperty("SOURCE_DIR");
            String dest_dir = prop.getProperty("DEST_DIR");
            int new_dimention = Integer.parseInt(prop.getProperty("PIXELS"));
            String propDimension = prop.getProperty("DIMENSION");
            int dimention_choice = utilities.resizer.resize_image.chooseDimension(propDimension);


            resize_image wi = new resize_image();
            wi.set_dir_location(src_dir, dest_dir);

            wi.set_width_or_height(new_dimention, dimention_choice);

            // called for resizing 1 image file in images/original directory
            if (argLen == 1) {
                wi.resize_imageFile(args[0]);
            } // called for resizing the images/original directory with image files
            else {
                wi.resize_imageFilesOriginalDirectory();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


//        resize_image img = new resize_image(); 
//        img.set_dir_location("C:/Projects/etia/etia1.0/etia_images/uploads/DigitalCopy/Photos/original", "C:/XAXA");
//        img.set_width_or_height(73, img.HEIGHT);
//        img.resize_imageFilesOriginalDirectory();
//        img.resize_imageFile("P2_246920208.tif");

    }
}
