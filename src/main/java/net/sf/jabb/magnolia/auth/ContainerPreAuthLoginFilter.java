package net.sf.jabb.magnolia.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.filters.AbstractMgnlFilter;
import info.magnolia.cms.security.auth.login.LoginHandler;
import info.magnolia.cms.security.auth.login.LoginResult;
import info.magnolia.context.MgnlContext;
import info.magnolia.audit.AuditLoggingUtil;
import info.magnolia.monitoring.AccessRestrictedException;
import info.magnolia.monitoring.SystemMonitor;
import info.magnolia.objectfactory.Components;

public class ContainerPreAuthLoginFilter extends AbstractMgnlFilter {
	private static final Logger log = LoggerFactory.getLogger(ContainerPreAuthLoginFilter.class);
			
    private Collection<LoginHandler> loginHandlers = new ArrayList<LoginHandler>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        for (LoginHandler handler : this.getLoginHandlers()) {
            LoginResult loginResult = handler.handle(request, response);
            LoginResult.setCurrentLoginResult(loginResult);
            AuditLoggingUtil.log(loginResult, request);
            if (loginResult.getStatus() == LoginResult.STATUS_IN_PROCESS) {
                // special handling to support multi step login mechanisms like ntlm
                // do not continue with the filter chain
                return;
            } else if (loginResult.getStatus() == LoginResult.STATUS_SUCCEEDED) {

                if (Components.getComponent(SystemMonitor.class).isMemoryLimitReached()) {
                    final String memoryLimitReachedMessage = String.format(SystemMonitor.MEMORY_LIMIT_IS_REACHED_STRING_FORMAT, "That is why further logins have to be blocked for now.");
                    log.error(memoryLimitReachedMessage);
                    LoginResult.setCurrentLoginResult(new LoginResult(LoginResult.STATUS_FAILED, new AccessRestrictedException()));
                    break;
                }

                Subject subject = loginResult.getSubject();
                if (subject == null) {
                    log.error("Invalid login result from handler [" + handler.getClass().getName() + "] returned STATUS_SUCCEEDED but no subject");
                    throw new ServletException("Invalid login result");
                }
                if (handler instanceof ContainerPreAuthLogin){
                	log.debug("Session will not be invalidated for: " + handler);
                }else{
                    log.debug("Invalidating session if there is one: " + handler);
                    if (request.getSession(false) != null) {
                        request.getSession().invalidate();
                        log.debug("Session invalidated for: " + handler);
                    }
                }
                MgnlContext.login(subject);
                AuditLoggingUtil.log(loginResult, request);
                // do not continue the login handler chain after a successful login ... otherwise previous success will be invalidated by above session wipeout
                break;
            } else {
                // just log.
                AuditLoggingUtil.log(loginResult, request);
            }

        }
        // continue even if all login handlers failed
        chain.doFilter(request, response);
    }

    public Collection<LoginHandler> getLoginHandlers() {
        return loginHandlers;
    }

    public void setLoginHandlers(Collection<LoginHandler> loginHandlers) {
        this.loginHandlers = loginHandlers;
    }

    public void addLoginHandlers(LoginHandler handler) {
        this.loginHandlers.add(handler);
    }




}
