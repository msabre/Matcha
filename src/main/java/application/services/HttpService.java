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
//        String result = null;
//        try {
//            BufferedReader reader = null;
//            try {
//                URL url = new URL("https://myip.by/");
//                InputStream inputStream = url.openStream();
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//                StringBuilder allText = new StringBuilder();
//                char[] buff = new char[1024];
//
//                int count = 0;
//                while ((count = reader.read(buff)) != -1)
//                    allText.append(buff, 0, count);
//
//                int indStart = allText.indexOf("\">whois ");
//                int indEnd = allText.indexOf("</a>", indStart);
//
//                String ipAddress = new String(allText.substring(indStart + 8, indEnd));
//                if (ipAddress.split("\\.").length == 4) {
//                    result = ipAddress;
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } finally {
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
    }
    
    public static String getBody(HttpServletRequest request) throws UnsupportedEncodingException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

        response.setHeader("Content-Type","text/html; charset=utf-8");

        BufferedWriter bufferedWriter = null;

        try {
            OutputStream outputStream = response.getOutputStream();
            if (outputStream != null) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
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
