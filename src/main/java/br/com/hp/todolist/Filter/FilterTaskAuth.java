package br.com.hp.todolist.Filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.hp.todolist.Repository.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                var servletPath = request.getServletPath();

        //usa a validação de login apenas para a rota de tasks
        if (servletPath.startsWith("/tasks/")){
            
            //desfaz encode de login e senha
            var authorization = request.getHeader("Authorization");
            var password_encoded = authorization.substring("basic".length()).trim();
            byte[] pass_decoded = Base64.getDecoder().decode(password_encoded);
            var pass_decoded_string = new String(pass_decoded);
            String[] credentials = pass_decoded_string.split(":");
            String userName = credentials[0];
            String password = credentials[1];
    
            var user = this.userRepository.findByUsername(userName);
    
            if(user == null){
                response.sendError(401, "Usuário sem autorização.");
            }else{
    
                var password_veryfied = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if(password_veryfied.verified){
                    request.setAttribute("isUser", user.getId());
                    filterChain.doFilter(request, response);
                }else{
                    response.sendError(401);
                }
            }
        }else{
            filterChain.doFilter(request, response);
        }
        

    }
    
}
