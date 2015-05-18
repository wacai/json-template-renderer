package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

import static org.mockito.Mockito.*;

public class UrlRemappingTest {
    final File         subs    = new File("src/test/resources/url.rem");
    final UrlRemapping sub     = new UrlRemapping(subs);
    final Handler      handler = mock(Handler.class);

    final Request             request  = mock(Request.class);
    final HttpServletResponse response = mock(HttpServletResponse.class);

    @Rule
    public final ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            sub.setHandler(handler);
            sub.start();
        }

        @Override
        protected void after() {
            try { sub.stop(); } catch (Exception ignore) { }
        }
    };

    @Test
    public void should_insert_context_level() throws Exception {
        sub.handle("/a/c", request, request, response);

        verify(request).setPathInfo("/a/b/c");
        verify(handler).handle("/a/b/c", request, request, response);
    }

    @Test
    public void should_replace_with_regex_group() throws Exception {
        sub.handle("/user/123", request, request, response);

        verify(request).setPathInfo("/ctx/user/123");
        verify(handler).handle("/ctx/user/123", request, request, response);
    }

    @Test
    public void should_replace_suffix() throws Exception {
        sub.handle("/index.do", request, request, response);

        verify(request).setPathInfo("/index.jsp");
        verify(handler).handle("/index.jsp", request, request, response);
    }

    @Test
    public void should_not_replace_any_thing() throws Exception {
        sub.handle("/index.jsp", request, request, response);

        verify(request, never()).setPathInfo("/index.jsp");
        verify(handler).handle("/index.jsp", request, request, response);
    }
}