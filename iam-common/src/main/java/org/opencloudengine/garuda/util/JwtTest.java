package org.opencloudengine.garuda.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uengine on 2015. 5. 22..
 */
public class JwtTest {

    public static void main(String[] args) throws Exception {

//        String issuer = "9a1e6155-c735-4986-b654-b1269a955666";
//        String sharedSecret = "e9635a99-1c4e-4a48-b8f1-70f66ead9d3c";
//
//        //발급 시간
//        Date issueTime = new Date();
//
//        //만료시간
//        Date expirationTime = new Date(new Date().getTime() + 3600 * 1000);
//
//        //시그네이쳐 설정
//        JWSSigner signer = new MACSigner(sharedSecret);
//
//        //콘텍스트 설정
////        Map context = new HashMap();
////        context.put("managementKey", managementKey);
////        context.put("userName", userName);
//
//        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
//        JWTClaimsSet claimsSet = builder
//                .issuer(issuer)
//                .issueTime(issueTime)
//                .expirationTime(expirationTime)
//                .build();
//
//        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
//
//        signedJWT.sign(signer);
//
//        String sessionToken = signedJWT.serialize();
//
//        System.out.println(sessionToken);

        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NjIxMjQ3ODYsImNvbnRleHQiOnsic2NvcGVzIjoiZm9ybTpjcmVhdGUiLCJtYW5hZ2VtZW50SWQiOiI1ZTM0MzJhZDE3MjY0NGVhODA0MWZlNjdlYjhmNWNiZCIsInJlZnJlc2hUb2tlbiI6IjhjNTYzODViLTEwM2ItNDkyMS04ODA3LWYwYTgwZDFmZDI5ZiIsInR5cGUiOiJ1c2VyIiwib2F1dGhVc2VySWQiOiIxNTQzYzVhYzJjNTA0OWIxODA1ODY2MmRhMjM2ZjAxMSIsImNsaWVudElkIjoiOTdlZDhmMzBkYTAxNDgwMGI5MmEyNGQ4YmVkNmQ1YTUifSwiaXNzIjoib2NlLmlhbSIsImNsYWltIjp7ImFhYSI6ImJiYiJ9LCJpYXQiOjE0NjIxMjExODZ9.J8Edpn5fTtgx9-6cLXGiYV0NnRuPO2rbv28xkR55sl4";
        JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(jwt);
        System.out.println(jwtClaimsSet);
    }
}
