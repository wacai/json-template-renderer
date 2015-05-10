package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Request;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class VelocityHandlerTest {

    @Test
    public void test_issue_8() throws Exception {
        final JsonModel model = new JsonModel(new File("src/test/resources/models"));
        final VelocityHandler handler = new VelocityHandler(model, new File("src/test/resources/webapp"));
        final Request request = mock(Request.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(new PrintWriter(System.out)).when(response).getWriter();
        handler.handle("/issue8.vm", request, request, response);
    }
}