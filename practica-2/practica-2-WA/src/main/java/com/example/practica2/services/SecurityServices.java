package com.example.practica2.services;

import com.example.practica2.encapsulators.security.Rol;
import com.example.practica2.encapsulators.security.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class SecurityServices implements UserDetailsService {

    @Autowired
    UserServices usuarioService;

    public SecurityServices() {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("Autenticación JPA");
        Usuario user = usuarioService.findByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("Usuario no existe.");
        }

        System.out.print("Trying to authenticate: " + username + ", Roles: ");

        Set<GrantedAuthority> rols = new HashSet<>();
        for (Rol rol : user.getRols()){
            rols.add(new SimpleGrantedAuthority(rol.getRole()));
            System.out.print(rol.getRole() + " ");
        }
        System.out.println();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(rols);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isActivo(), true, true, true, grantedAuthorities);
    }

    public Usuario getAuthorizedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null){
            return null;
        }
        return usuarioService.findByUsername(auth.getName());
    }
}

