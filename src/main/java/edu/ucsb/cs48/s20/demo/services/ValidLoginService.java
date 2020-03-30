package edu.ucsb.cs48.s20.demo.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidLoginService {
  public static boolean validLogin(String login) {
    if (login == null) {
      return false;
    }
    // See: https://github.com/shinnn/github-username-regex
    String regex = "^[a-z\\d](?:[a-z\\d]|-(?=[a-z\\d])){0,38}$";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(login);
    return matcher.matches();
  }
}