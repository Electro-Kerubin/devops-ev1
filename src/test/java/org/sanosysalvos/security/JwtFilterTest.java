package org.sanosysalvos.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtFilter jwtFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter();
        ReflectionTestUtils.setField(jwtFilter, "secret", JwtTestSupport.SECRET);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sinHeader_debeContinuarSinAutenticar() throws Exception {
        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void headerSinBearer_debeContinuarSinAutenticar() throws Exception {
        request.addHeader("Authorization", "Basic abc123");

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValido_debeAutenticarYContinuar() throws Exception {
        request.addHeader("Authorization", "Bearer " + JwtTestSupport.validToken());

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertEquals("usuario-test", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void tokenExpirado_debeResponder401YCortarLaCadena() throws Exception {
        request.addHeader("Authorization", "Bearer " + JwtTestSupport.expiredToken());

        jwtFilter.doFilterInternal(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Token inválido o expirado"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void tokenFirmadoConOtraClave_debeResponder401() throws Exception {
        String otraClave = "b3RyYS1jbGF2ZS1kaXN0aW50YS1wYXJhLXByb2Jhci1maXJtYS1pbnZhbGlkYS0xMjM0NTY=";
        request.addHeader("Authorization", "Bearer " + JwtTestSupport.tokenSignedWith(otraClave));

        jwtFilter.doFilterInternal(request, response, chain);

        assertEquals(401, response.getStatus());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void tokenMalformado_debeResponder401() throws Exception {
        request.addHeader("Authorization", "Bearer esto.no.es-un-jwt");

        jwtFilter.doFilterInternal(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
