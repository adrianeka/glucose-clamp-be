package com.tujuhsembilan.glucoseclamp.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DynamicRbacFilter extends OncePerRequestFilter {

    private static final Map<String, String> URL_TO_MENU_MAP = new HashMap<>();

    static {
        URL_TO_MENU_MAP.put("/user-management/users", "USER");
        URL_TO_MENU_MAP.put("/roles", "USER");
        URL_TO_MENU_MAP.put("/participants", "PARTICIPANT");
        URL_TO_MENU_MAP.put("/protocol-management/protocols", "PROTOCOLSAMPLINGSCHEDULE");
        URL_TO_MENU_MAP.put("/protocol-management/sampling-schedules", "PROTOCOLSAMPLINGSCHEDULE");
        // URL_TO_MENU_MAP.put("/session-devices", "SESSIONDEVICE");
        URL_TO_MENU_MAP.put("/session", "SESSION");
        URL_TO_MENU_MAP.put("/activities", "SESSION");
        URL_TO_MENU_MAP.put("/infusion-monitoring", "INFUSIONMONITORING");
        URL_TO_MENU_MAP.put("/lab-results", "BLOODDRAW");
        URL_TO_MENU_MAP.put("/blood-samples", "BLOODDRAW");
        URL_TO_MENU_MAP.put("/vital-signs", "PREPARATIONCHECK");
        URL_TO_MENU_MAP.put("/anthropometries", "PREPARATIONCHECK");
        URL_TO_MENU_MAP.put("/anamneses", "PREPARATIONCHECK");
        URL_TO_MENU_MAP.put("/preparation-check", "PREPARATIONCHECK");
        // URL_TO_MENU_MAP.put("/devices", "DEVICE");
        // URL_TO_MENU_MAP.put("/access-menus", "ACCESSMENU");
        URL_TO_MENU_MAP.put("/global-configuration", "GLOBALCONFIGURATION");
        URL_TO_MENU_MAP.put("/phase-configuration", "PHASECONFIGURATION");
        URL_TO_MENU_MAP.put("/role-access", "ROLEACCESS");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip endpoints that do not require RBAC checks
        if (path.contains("/sign-in") || path.contains("/sign-up") || path.contains("/swagger-ui") || path.contains("/api-docs") || path.contains("/actuator") || path.contains("/my-permissions") || path.contains("/next-progress-activity")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bypass RBAC checks for GET requests to /global-configuration, /phase-configuration, /participants, dropdown, and
        if ("GET".equalsIgnoreCase(method) && (path.contains("/global-configuration") || path.contains("/phase-configuration") || path.contains("/participants") || path.contains("/dropdown") || path.contains("/protocol-management/protocols"))) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bypass RBAC checks for POST requests to /activities/start and /activities/complete
        if (path.contains("/activities") && (path.endsWith("/start") || path.endsWith("/complete"))) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            filterChain.doFilter(request, response);
            return;
        }

        String menuName = null;
        for (Map.Entry<String, String> entry : URL_TO_MENU_MAP.entrySet()) {
            if (path.contains(entry.getKey())) {
                menuName = entry.getValue();
                break;
            }
        }

        if (menuName != null) {
            String requiredAction = "VIEW";

            if ("POST".equalsIgnoreCase(method)) {
                // Adjust for start/complete endpoints that use POST but are conceptually EDIT
                if (path.endsWith("/start") || path.endsWith("/complete")) {
                    requiredAction = "EDIT";
                } else {
                    requiredAction = "ADD";
                }
            } else if ("PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
                requiredAction = "EDIT";
            } else if ("DELETE".equalsIgnoreCase(method)) {
                requiredAction = "DELETE";
            } else if ("GET".equalsIgnoreCase(method)) {
                requiredAction = "VIEW";
            }

            String requiredAuthority = menuName + ":" + requiredAction;
            
            // Check if Superadmin overrides everything OR match exact permission
            boolean hasAccess = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(auth -> auth.equals(requiredAuthority));

            if (!hasAccess) {
                log.warn("Dynamic RBAC Blocked: User {} attempted to {} on {} without {} authority",
                        authentication.getName(), method, path, requiredAuthority);
                
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"statusCode\": 403, \"message\": \"Akses Ditolak: Anda tidak memiliki izin untuk melakukan tindakan ini (" + requiredAuthority + ")\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}