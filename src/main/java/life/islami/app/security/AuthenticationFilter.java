package life.islami.app.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import life.islami.app.SpringApplicationContext;
import life.islami.app.dtos.UserDto;
import life.islami.app.models.request.UserLoginRequestModel;
import life.islami.app.models.response.UserRest;
import life.islami.app.services.UserService;
import java.security.Key;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private AuthenticationManager authenticationManager;

  public AuthenticationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    UserLoginRequestModel userLoginRequestModel;
    try {
      userLoginRequestModel = new ObjectMapper().readValue(request.getInputStream(),
          UserLoginRequestModel.class);

      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          userLoginRequestModel.getUserName(), userLoginRequestModel.getPassword(), new ArrayList<>()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {

    String userName = ((User) authResult.getPrincipal()).getUsername();
    UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
    UserDto userDto = userService.retrieveUserByUserName(userName);
    Key signingKey = Keys.hmacShaKeyFor(SecurityConstants.getSecurityToken().getBytes());
    String token = Jwts.builder().setSubject(userDto.getUserId())
        .setExpiration(new Date(System.currentTimeMillis() +
            SecurityConstants.EXPIRATION_TIME))
        .signWith(signingKey, SignatureAlgorithm.HS512).compact();
    response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    response.addHeader("UserId", userDto.getUserId());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    ObjectMapper objectMapper = new ObjectMapper();
    String userDtoJson = objectMapper
        .writeValueAsString(
            ((ModelMapper) SpringApplicationContext.getBean("modelMapper")).map(userDto, UserRest.class));
    response.getWriter().write(userDtoJson);
  }
}
