package ru.workwear.server.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    private long id;

    private String username;
    @JsonIgnore
    private String password;
    @Transient
    private List<Authority> authorities;
    private Set<Long> authoritiesId;

    private String lastName;
    private String firstName;
    private String middleName;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthdate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;

    @Transient
    private Address address;
    private Long addressId;

    public void setAddress(Address address){
        this.addressId = address.getId();
        this.address = address;
    }

    private void setAuthorities(List<Authority> authorities){
        this.authorities = authorities;
        if(authoritiesId == null){
            authoritiesId = new HashSet<>();
        }
        for(Authority authority : authorities){
            authoritiesId.add(authority.getId());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
