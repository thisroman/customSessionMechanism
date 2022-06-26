package code.rondev;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@WebServlet("/homework")
public class HomeworkServlet extends HttpServlet {

    private static final Map<String, User> sessionMap = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AtomicReference<String> nameData = new AtomicReference<>("Boddy");
        if (sessionMap.get(req.getSession().getId()) != null && req.getParameter("clear") == null) {
            nameData.set(sessionMap.get(req.getSession().getId()).name);
        } else {
            Optional.ofNullable(req.getParameter("name"))
                    .ifPresentOrElse(
                            name -> {
                                nameData.set(name);
                                sessionMap.put(req.getSession().getId(), new User(name));
                            },
                            () -> {
                                Optional.ofNullable(req.getParameter("clear"))
                                        .ifPresent(c -> sessionMap.remove(req.getSession().getId()));
                            });
        }


        var writer = resp.getWriter();
        writer.println(String.format("Good evening, %s", nameData.get()));
        writer.flush();
    }

    private static class User {
        private final String name;

        private User(String name) {
            this.name = name;
        }
    }
}
