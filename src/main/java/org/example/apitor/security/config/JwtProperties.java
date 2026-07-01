package org.example.apitor.security.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
        private String secret;
        private long expiration;

        public void setSecret(String secret){
            this.secret=secret;
        }

        public void setExpiration(long expiration){
            this.expiration=expiration;
        }


        public String getSecret(){
            return this.secret;
        }

        public long getExpiration(){
            return this.expiration;
        }
}
