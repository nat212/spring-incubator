package entelect.training.incubator.spring.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("{noop}the_cake").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}is_a_lie").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/bookings").hasAnyRole("USER", "LOYALTY_USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/bookings/**").hasAnyRole("USER", "LOYALTY_USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/bookings/search").hasAnyRole("USER", "LOYALTY_USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/v3/**").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                .anyRequest().denyAll()
                .and()
                .httpBasic();
    }
}
