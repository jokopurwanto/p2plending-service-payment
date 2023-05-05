package com.p2plending.payment.service;

import com.p2plending.payment.db.paymentdb.model.MyUserAuthDetail;
import com.p2plending.payment.db.paymentdb.model.UserAuthModel;
import com.p2plending.payment.db.paymentdb.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MyUserAuthDetailService implements UserDetailsService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthModel user = userAuthRepository.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("no user found !!!");
        }
        return new MyUserAuthDetail(user);
    }
}