/*
 * 
 * 
 * Kapralov A.
 * 08.07.2010
 */

package kae.util;

/** @author A. Kapralov 08.07.2010 18:51:07 */
public class FileUtils {

  public static String getNameWithoutExtension(String fileName) {
    int lastDotIdx = fileName.lastIndexOf('.');
    if (lastDotIdx != -1) {
      return fileName.substring(0, lastDotIdx);
    } else {
      return fileName;
    }
  }

}
