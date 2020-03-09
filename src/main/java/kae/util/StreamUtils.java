/*
 * 
 * 
 * Kapralov A.
 * 08.07.2010
 */

package kae.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/** @author A. Kapralov 08.07.2010 18:51:07 */
public class StreamUtils {

  public static final int BUFFER_SIZE = 10 * 1024;

  public static String readStream(InputStream inputStream) throws IOException {
    return readStream(inputStream, Charset.defaultCharset().name());
  }

  public static String readStream(InputStream inputStream, String charsetName) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));

    StringBuilder sb = new StringBuilder();
    char[] buf = new char[BUFFER_SIZE];
    int read;
    while ((read = reader.read(buf)) != -1) {
      sb.append(buf, 0, read);
    }

    return sb.toString();
  }

}
