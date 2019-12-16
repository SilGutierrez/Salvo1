package com.salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository,ShipRepository shipRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ScoreRepository scoreRepository){
	    return (args) -> {
            Player player1 = new Player("sil", "Silvana", "Gutierrez", "silvanagriseldagutierrez@gmail.com", passwordEncoder().encode("sg"));
            playerRepository.save(player1);

            Player player2 = new Player("juli", "Julia", "Prebisch","chilolomas@hotmail.com", passwordEncoder().encode("jp"));
            playerRepository.save(player2);

            Player player3 = new Player("nico","Nicolas", "Schain","nicoschain@gmail.com", passwordEncoder().encode("ns"));
            playerRepository.save(player3);

            Player player4 = new Player( "estefy", "Estefania", "Vitale","estefivitale@yahoo.com.ar", passwordEncoder().encode("ev"));
            playerRepository.save(player4);

            Game game1 = new Game();
            gameRepository.save(game1);

            Game game2 = new Game();
            gameRepository.save(game2);

            Game game3 = new Game();
            gameRepository.save(game3);

            GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
            gamePlayerRepository.save(gamePlayer1);
            GamePlayer gamePlayer2 = new GamePlayer(player2, game1);
            gamePlayerRepository.save(gamePlayer2);
            GamePlayer gamePlayer3 = new GamePlayer(player3, game2);
            gamePlayerRepository.save(gamePlayer3);
            GamePlayer gamePlayer4 = new GamePlayer(player4, game2);
            gamePlayerRepository.save(gamePlayer4);
            GamePlayer gamePlayer5 = new GamePlayer(player1, game3);
            gamePlayerRepository.save(gamePlayer5);
            GamePlayer gamePlayer6 = new GamePlayer(player3, game3);

//posiciones barcos por jugador

            String[] arrays1={"E3","E4","E5"};
            String[] arrays2={"G9", "H9", "I9"};
            String[] arrays3={"A9","A10"};
            String[] arrays4={"J3","J4","J5","J6","J7"};
            String[] arrays5={"A2","A3","A4","A5"};

            String[] arrays6={"A10", "B10", "C10"};
            String[] arrays7={"G1","G2","G3"};
            String[] arrays8={"F7","G7"};
            String[] arrays9={"F5","G5","H5","I5","J5"};
            String[] arrays10={"C4","C5","C6","C7"};

// Nombres barcos con su posicion

            Ship bar1= new Ship("submarine", Arrays.asList(arrays1));
            Ship bar2= new Ship("destroyer", Arrays.asList(arrays2));
            Ship bar3= new Ship("patrol_boat", Arrays.asList(arrays3));
            Ship bar4= new Ship("carrier", Arrays.asList(arrays4));
            Ship bar5= new Ship("battleship", Arrays.asList(arrays5));

            Ship bar6= new Ship("submarine", Arrays.asList(arrays6));
            Ship bar7= new Ship("destroyer", Arrays.asList(arrays7));
            Ship bar8= new Ship("patrol_boat", Arrays.asList(arrays8));
            Ship bar9= new Ship("carrier", Arrays.asList(arrays9));
            Ship bar10= new Ship("battleship", Arrays.asList(arrays10));


            Score scr1 = new Score(2,game1,player1);
            Score scr2 = new Score(1,game1,player2);

            Score scr3 = new Score(3,game2,player3);
            Score scr4 = new Score(4,game2,player4);

            Score scr5 = new Score(1,game3,player1);
            Score scr6 = new Score(1,game3,player3);

            gamePlayer1.addShip(bar1);
            gamePlayer1.addShip(bar2);
            gamePlayer1.addShip(bar3);
            gamePlayer1.addShip(bar4);
            gamePlayer1.addShip(bar5);

            gamePlayer2.addShip(bar6);
            gamePlayer2.addShip(bar7);
            gamePlayer2.addShip(bar8);
            gamePlayer2.addShip(bar9);
            gamePlayer2.addShip(bar10);

            shipRepository.save(bar1);
            shipRepository.save(bar2);
            shipRepository.save(bar3);
            shipRepository.save(bar4);
            shipRepository.save(bar5);
            shipRepository.save(bar6);
            shipRepository.save(bar7);
            shipRepository.save(bar8);
            shipRepository.save(bar9);
            shipRepository.save(bar10);

            gamePlayer1.addSalvo(new Salvo(1,Arrays.asList("E4", "J5", "E7")));
            gamePlayer1.addSalvo(new Salvo(2,Arrays.asList("F9","H2","G5")));

            gamePlayer2.addSalvo(new Salvo(1,Arrays.asList("H8","D2","J9")));
            gamePlayer2.addSalvo(new Salvo(2,Arrays.asList("F10","I2","D7")));

            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);


            scoreRepository.save(scr1);
            scoreRepository.save(scr2);
            scoreRepository.save(scr3);
            scoreRepository.save(scr4);
            scoreRepository.save(scr5);
            scoreRepository.save(scr6);

//            scoreRepository.save(new Score(2,game2,player3,LocalDateTime.now()));
//            scoreRepository.save(new Score(2,game2,player4,LocalDateTime.now()));

        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder(); }

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/rest/**").hasAuthority("ADMIN")
                .antMatchers("/api/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/web/game.html").hasAnyAuthority("ADMIN", "USER");

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");
// turn off checking for CSRF tokens
        http.csrf().disable();
// if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
// if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
// if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
// if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session !=null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;
    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(inputName ->{
            Player player= playerRepository.findPlayerByUserName(inputName);
            if (player !=null) {
                if (player.getUserName().equals("sil")) {
                    return new User(player.getUserName(), player.getPassWord(),
                            AuthorityUtils.createAuthorityList("ADMIN"));
                } else {
                    return new User(player.getUserName(), player.getPassWord(),
                            AuthorityUtils.createAuthorityList("USER"));
                }
            } else{
                throw new UsernameNotFoundException("Unknown user:" + inputName);
            }
        }).passwordEncoder(passwordEncoder);
    }
}
