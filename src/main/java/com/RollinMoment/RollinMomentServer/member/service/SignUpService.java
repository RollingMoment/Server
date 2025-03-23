package com.RollinMoment.RollinMomentServer.member.service;

import com.RollinMoment.RollinMomentServer.common.util.AESUtil;
import com.RollinMoment.RollinMomentServer.exception.member.InvalidEmailException;
import com.RollinMoment.RollinMomentServer.exception.member.UsernameAlreadyExistsException;
import com.RollinMoment.RollinMomentServer.member.dto.SignUpDto;
import com.RollinMoment.RollinMomentServer.member.entity.UserEntity;
import com.RollinMoment.RollinMomentServer.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@RequiredArgsConstructor
@Service
public class SignUpService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AESUtil aesUtil;


    public void SignUp(SignUpDto signUpDto) {
        if(!isValidEmail(signUpDto.getUserId())){
            throw new InvalidEmailException("잘못된 이메일 형식을 입력했습니다.");
        }
        //이미 있는 계정이면 만들 수 없습니다
        if(isEmailExists(signUpDto.getUserId())){
            throw new UsernameAlreadyExistsException("이미 존재하는 회원 입니다");
        }
        String decryptedPassword = aesUtil.decrypt(signUpDto.getPassword());
        //비밀번호 암호화(bcrypt)
        signUpDto.setPassword(bCryptPasswordEncoder.encode(decryptedPassword));

        UserEntity userEntity = UserEntity.transDTO(signUpDto);

        userRepository.save(userEntity);

    }

    public boolean isValidEmail(String username){

        // 이메일 주소 형식이 아닌 경우 false
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if(!username.matches(regex)){
            return false;
        }

        String domain = username.substring(username.indexOf('@') + 1);

        // 정상적인 이메일 이라면 MX 레코드를 갖는다.
        // Dns를 이용해 MX 레코드를 조회
        // 참조 - https://velog.io/@danielyang-95/%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9C%A0%ED%9A%A8%EC%84%B1-%EA%B2%80%EC%A6%9D-by-MX-%EB%A0%88%EC%BD%94%EB%93%9C
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext dirContext = new InitialDirContext(env);
            Attributes attrs = dirContext.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            // MX 레코드가 존재하면 true 반환
            return attr != null && attr.size() > 0;
        } catch (NamingException e) {
            return false;
        }
    }

    // 아이디 확인
    public boolean isEmailExists(String userId) {
        return userRepository.existsByUserId(userId);
    }
}
