package de.jaide.wire;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import de.jaide.exception.FileOperationFailedException;

/**
 * File reader and writer.
 * 
 * @author Janarthanan Ramar (janarthanan.ramar@jaide.de)
 * @author Rias A. Sherzad (rias.sherzad@jaide.de)
 */
public abstract class FileOperation {

  protected URL url = null;

  protected String outputFileName = "";

  protected String outputPath = "";

  protected String inputLocale = "";

  protected String locale = "";

  protected String value = "";

  protected Writer output = null;

  protected File outputFile = null;

  /**
   * A helper method that reads the file and returns the results in an ArrayList.
   * 
   * @param fileName
   * @return retList.
   * @throws FileOperationFailedException
   */
  protected ArrayList<String> readFile(String fileName) throws FileOperationFailedException {
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
    } catch (FileNotFoundException e) {
      throw new FileOperationFailedException(e.fillInStackTrace());
    } catch (IOException e) {
      throw new FileOperationFailedException(e.fillInStackTrace());
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
    output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), "UTF-8"));
  }

  /**
   * A helper method that writes the content into the file
   * 
   * @param word, URL
   * @throws FileOperationFailedException
   */
  protected void writeOutput(String words, String anc) throws FileOperationFailedException {
    try {
      output.write(words + "::" + anc);
      output.write("\n");
    } catch (IOException e) {
      throw new FileOperationFailedException(e.fillInStackTrace());
    }
  }

  /**
   * A helper method to close the output file
   */
  protected void closeOutputFile() throws IOException {
    output.close();
  }
}
