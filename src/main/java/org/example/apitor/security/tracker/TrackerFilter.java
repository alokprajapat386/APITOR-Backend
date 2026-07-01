package org.example.apitor.security.tracker;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class TrackerFilter extends OncePerRequestFilter {

    private final ProjectTokenUtil projectTokenUtil;
    public TrackerFilter(ProjectTokenUtil projectTokenUtil){
        this.projectTokenUtil=projectTokenUtil;
    }

    @Override
    protected  void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String projectToken= request.getHeader("X-Project-Token");
        if(projectToken==null || projectToken.trim().isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
        String projectKey= projectTokenUtil.extractProjectToken(projectToken);

        if(projectKey==null){
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken auth=
                new UsernamePasswordAuthenticationToken(projectKey, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
