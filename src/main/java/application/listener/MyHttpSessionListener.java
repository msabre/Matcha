package application.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class MyHttpSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setMaxInactiveInterval(15 * 60); // in seconds
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        event.getSession().invalidate();
    }
}
