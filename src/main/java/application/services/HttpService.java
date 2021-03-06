package application.services;


import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpService {

    public static Cookie getCookie(ServletRequest req, String name) {

        HttpServletRequest request = (HttpServletRequest) req;

        Cookie[] cookiesArr = ((HttpServletRequest) req).getCookies();
        if (cookiesArr != null && cookiesArr.length > 0) {
            List<Cookie> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toList());
            Optional<Cookie> cookie = cookies.stream().filter(c -> name
                    .equals(c.getName())).findFirst();
            if (cookie.isPresent())
                return cookie.get();
        }

        return null;
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            String ip = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ip))
                return getCurrentIP();
            return ip;

        } else {
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }

    private static String getCurrentIP() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))) {
            return in.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getBody(HttpServletRequest request) throws UnsupportedEncodingException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public static void putBody(HttpServletResponse response, String body)  {

        if (body == null)
            return ;

//        body = new String(UTF_8.encode(body).array(), UTF_8);
        //response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type","text/html; charset=utf-8");

        BufferedWriter bufferedWriter = null;

        try {
            OutputStream outputStream = response.getOutputStream();
            if (outputStream != null) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
                bufferedWriter.write(body, 0, body.length());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String getUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +
               request.getServerName() +
               ("http".equals(request.getScheme()) && request.getServerPort() == 80
                       || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
               request.getRequestURI() +
               (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }
}
