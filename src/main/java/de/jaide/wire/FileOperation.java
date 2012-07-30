package de.jaide.wire;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

/**
 * TODO
 * 
 * @author TODO NAME
 */
public abstract class FileOperation {
  /**
   * The WebClient configuration and the WebRequest settings to use to connect/operate against the webmailer/platform.
   */
  protected URL url = null;

  protected String outputFileName = "";

  protected String outputPath = "";

  protected String inputLocale = "";

  protected String locale = "";

  protected String value = "";

  protected PrintWriter output = null;

  protected File outputFile = null;

  /**
   * A helper method that reads the file and returns the results in arraylist
   * 
   * @param fileName
   * @return retList.
   */
  protected ArrayList<String> readFile(String fileName) {
    ArrayList<String> retList = new ArrayList<String>();

    FileInputStream fstream = null;

    try {
      fstream = new FileInputStream(fileName);
      /*
       * Get the object of DataInputStream
       */
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader textReader = new BufferedReader(new InputStreamReader(in));

      /*
       * Read File Line By Line
       */
      while ((value = textReader.readLine()) != null) {
        /*
         * Print the content on the console
         */
        retList.add(value.toString().trim());
      }

      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return retList;
  }

  /**
   * A helper method that sets the output file path
   */
  protected void setOutputFile() throws IOException {
    String currentDir = System.getProperty("user.dir");
    outputPath = currentDir + "/output";

    if (new File(outputPath).exists()) {
      outputFile = new File(outputPath + "/" + outputFileName + "_" + locale + ".txt");
    } else {
      new File(outputPath).mkdir();
      outputFile = new File(outputPath + "/" + outputFileName + "_" + locale + ".txt");
    }
    output = new PrintWriter(new FileWriter(outputFile));
  }

  /**
   * A helper method that writes the content into the file
   * 
   * @param word, URL
   */
  protected void writeOutput(String words, String anc) {

    try {

      output.println(words + "::" + anc);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * A helper method to close the output file
   */
  protected void closeOutputFile() throws IOException {
    output.close();
  }
}
