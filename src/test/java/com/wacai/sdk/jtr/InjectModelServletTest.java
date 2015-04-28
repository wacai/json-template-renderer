package com.wacai.sdk.jtr;

import org.junit.Test;

import static com.wacai.sdk.jtr.InjectModelServlet.base;
import static com.wacai.sdk.jtr.InjectModelServlet.get;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class InjectModelServletTest {
    @Test
    public void should_get_base_name() throws Exception {
        assertThat(base("/a.jsp"), is("/a"));
        assertThat(base("/a/b.jsp"), is("/a/b"));
    }

    @Test
    public void should_get_query_value_by_key() throws Exception {
        assertThat(get("m=a","m", "b"), is("a"));
        assertThat(get("m=a&","m", "b"), is("a"));
        assertThat(get("","m", "b"), is("b"));
    }
}