/*
 * StringUtils.java
 *
 * A. Kapralov
 * 04.11.2009 18:45:26
 */

package kae.util;

/**
 * @author A. Kapralov
 */
public class StringUtils {

  public static boolean isEmpty(String string) {
    return string == null || string.length() == 0;
  }

  public static boolean isNotEmpty(String string) {
    return string != null && string.length() != 0;
  }

}
