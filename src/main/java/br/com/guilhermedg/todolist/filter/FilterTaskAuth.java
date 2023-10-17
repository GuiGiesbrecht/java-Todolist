package br.com.guilhermedg.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.guilhermedg.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // If the request is to /tasks validate the user
        if (request.getServletPath().startsWith("/tasks/")) {
            // Get the Authorization header from the request
            var authorization = request.getHeader("Authorization");

            // If the Authorization header is not present, return a 401
            if (authorization == null) {
                response.sendError(401);
                return;
            }

            // Get the encoded username and password from the Authorization header
            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecoded);

            String[] credentials = authString.split(":");
            var username = credentials[0];
            var password = credentials[1];

            // If the username and password are not present, return a 401
            if (username == null || password == null) {
                response.sendError(401);
                return;
            }

            var user = this.userRepository.findByUsername(username);

            // If the user is not present, return a 401
            if (user == null) {
                response.sendError(401);
                return;
            }

            // If the password is not correct, return a 401
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

            if (!passwordVerify.verified) {
                response.sendError(401);
                return;
            }

            // If the user is present and the password is correct, continue
            request.setAttribute("idUser", user.getId());
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
