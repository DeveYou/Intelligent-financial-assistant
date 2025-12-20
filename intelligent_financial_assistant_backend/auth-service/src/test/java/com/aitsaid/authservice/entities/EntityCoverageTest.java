package com.aitsaid.authservice.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class EntityCoverageTest {

    @Test
    void user_test() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@test.com");
        user.setPassword("pass");
        user.setCin("cin");
        user.setPhoneNumber("123");
        user.setAddress("Addr");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setTokenBlockList(new ArrayList<>());

        assertEquals(1L, user.getId());
        assertEquals("First", user.getFirstName());
        assertEquals("Last", user.getLastName());
        assertEquals("email@test.com", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals("cin", user.getCin());
        assertEquals("123", user.getPhoneNumber());
        assertEquals("Addr", user.getAddress());
        assertEquals(Role.ROLE_USER, user.getRole());
        assertTrue(user.isEnabled());
        assertEquals(now, user.getCreatedAt());
        assertNotNull(user.getTokenBlockList());
        
        // UserDetails methods
        assertEquals("email@test.com", user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        
        assertNotNull(user.getAuthorities());
        assertFalse(user.getAuthorities().isEmpty());
        
        User allArgs = new User(1L, "f", "l", "c", "e", "p", "ph", "ad", Role.ROLE_ADMIN, now, true, null);
        assertEquals("f", allArgs.getFirstName());
    }

    @Test
    void tokenBlockList_test() {
        TokenBlockList token = new TokenBlockList();
        token.setId(1L);
        token.setToken("abc");
        LocalDateTime created = LocalDateTime.now();
        token.setCreatedAt(created);
        token.setUser(new User());

        assertEquals(1L, token.getId());
        assertEquals("abc", token.getToken());
        assertEquals(created, token.getCreatedAt());
        assertNotNull(token.getUser());

        TokenBlockList allArgs = new TokenBlockList(1L, "abc", created, new User());
        assertEquals("abc", allArgs.getToken());
    }
}
