package com.security.demo;

import net.minidev.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Controller
public class HomeController {

    private String phoneNo = null;
    private String sessionId = null;
    private String isSuccess = null;
    private String username = null;
    private User globalUser = new User();
    private String emailId=null;
    private LocalDateTime localDateTime = null;
    private boolean isTrue=false;
    private boolean isValid=false;
/*    public HomeController() throws IOException {}*/

/*    File file = new File("C:\\Users\\pc1\\Documents\\apiKey.txt");
    BufferedReader br = new BufferedReader(new FileReader(file));
    String apiKey = br.readLine();*/

    String apiKey = *2FA api key*;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public JavaMailSender javaMailSender;

    @Bean
    public BCryptPasswordEncoder encodePWD() {
        return new BCryptPasswordEncoder();
    }

    @RequestMapping("/")
    public String oauth2(Model model, OAuth2AuthenticationToken authentication){
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
            HttpEntity entity = new HttpEntity("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            model.addAttribute("name", userAttributes.get("name"));
            User googleUser = userRepository.findByUsername((String)userAttributes.get("email"));
            if(googleUser!=null && googleUser.getPhoneNumber()!=null)
            {
                return "home.jsp";
            }
            else {
                User user = new User();

                user.setUsername((String) userAttributes.get("email"));
                user.setFirstName((String) userAttributes.get("given_name"));
                user.setLastName((String) userAttributes.get("family_name"));
                user.setPassword("GoogleUser");
                username = user.getUsername();

                userRepository.save(user);
            }
        }
        return "OTP.jsp";
    }

    @RequestMapping("/getPhoneNumber")
    public ModelAndView getPhoneNumber(@RequestParam("phoneNo") String phoneNumber){
        ModelAndView mv = new ModelAndView();
        phoneNo = phoneNumber;
        if(userRepository.findByPhoneNumber(phoneNumber)!=null)
            mv.setViewName("home.jsp");
        else {
            final String url = "https://2factor.in/API/V1/" + apiKey + "/SMS/" + phoneNo + "/AUTOGEN";
            RestTemplate restTemplate = new RestTemplate();
            sessionId = restTemplate.getForObject(url, String.class);
            sessionId = sessionId.substring(31, 67);
            mv.setViewName("GoogleOTPV.jsp");
        }
        return mv;
    }

    @RequestMapping("/verifyOtpGoogle")
    public ModelAndView verfiyOtp(@RequestParam String otp){

        ModelAndView mv = new ModelAndView();
        Integer index = null;
        String incorrectOtp = "";

        try {
            final String url = "https://2factor.in/API/V1/"+apiKey+"/SMS/VERIFY/" + sessionId + "/" + otp;
            RestTemplate restTemplate = new RestTemplate();
            isSuccess = restTemplate.getForObject(url, String.class);
            index = isSuccess.indexOf("Success");
        }
        catch (Exception e){
            mv.addObject("incorrectOtp","Enter correct OTP");
            mv.setViewName("GoogleOTPV.jsp");
        }
        if(index != null) {
           User user = userRepository.findByUsername(username);
            user.setPhoneNumber(phoneNo);
            userRepository.save(user);
            mv.setViewName("VerifiedPage.jsp");
        }
        else{
            mv.addObject("incorrectOtp","Enter correct OTP");
            mv.setViewName("GoogleOTPV.jsp");
        }
        return mv;
    }

    @RequestMapping("/addUserForm")
    public String addUserForm(){
        return "registrationForm.jsp";
    }

    @RequestMapping(value="/login",produces=MediaType.APPLICATION_JSON_VALUE)
    public String login(){
        ModelAndView mv = new ModelAndView();
        String registered = "";
        if(isTrue) {
            mv.addObject("registered", "You are now registered, login to access your account");
            isTrue = false;
        }
        /*mv.setViewName("login.jsp");
        return mv;*/
        return "success";
    }

    @RequestMapping("/logout")
    public String logout(){
        return "login.jsp";
    }

    @RequestMapping("/sentOtp")
    @ResponseBody
    public JSONObject getUser(@RequestBody User user){

        String validate="";
        JSONObject registrationResponse = new JSONObject();
        registrationResponse.put("isExists","");
        registrationResponse.put("validate","");
        registrationResponse.put("registration","failed");
        /*User user = new User();
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setUsername(username);*/
        /*EmailValidator validator = new EmailValidator();*/
/*        if (!validator.isValid(user.getUsername(),null)) {
            System.out.println("AERGVBAQWERBEAB"+validator.isValid(user.getUsername(),null));
            String invalidEmail = "";
            isValid =true;
            mv.addObject("invalidEmail","E-mail ID is invalid");
            mv.setViewName("/addUserForm");
        }*/
        /*else {*/

            if (userRepository.findByUsername(user.getUsername()) != null || userRepository.findByPhoneNumber(user.getPhoneNumber()) != null) {
                String isExists = "";
                registrationResponse.put("isExists","User already exists");
                return registrationResponse;
                /*mv.addObject("isExists", "User already exists");
                mv.setViewName("login.jsp");*/
            } else {
                phoneNo = user.getPhoneNumber();
                final String url = "https://2factor.in/API/V1/" + apiKey + "/SMS/" + phoneNo + "/AUTOGEN";
                RestTemplate restTemplate = new RestTemplate();
                sessionId = restTemplate.getForObject(url, String.class);
                sessionId = sessionId.substring(31, 67);

                if (user.getPassword().length() < 8) {
                    /*mv.addObject("validate", "Password must be at least 8 characters");*/
                    registrationResponse.put("validate","Password must be at least 8 characters");
                    return registrationResponse;
                    /*mv.setViewName("registrationForm.jsp");*/
                } else {
                    String pwd = encodePWD().encode(user.getPassword());
                    user.setPassword(pwd);
                    globalUser = user;
                    registrationResponse.put("registration","success");
                }
            }
        /*}*/
        return registrationResponse;
    }

    @RequestMapping("/verifyOtp")
    @ResponseBody
    public JSONObject verifyOtp(@RequestParam String otp){

         ModelAndView mv = new ModelAndView();
         JSONObject verifyOtpResponse = new JSONObject();
        verifyOtpResponse.put("incorrectOtp","");
        verifyOtpResponse.put("otpVerified","");
         Integer index = null;
         String incorrectOtp = "";

         try {
             final String url = "https://2factor.in/API/V1/"+apiKey+"/SMS/VERIFY/" + sessionId + "/" + otp;
             RestTemplate restTemplate = new RestTemplate();
             isSuccess = restTemplate.getForObject(url, String.class);
             index = isSuccess.indexOf("Success");
         }
         catch (Exception e){
             verifyOtpResponse.put("incorrectOtp","Enter correct OTP");
             return verifyOtpResponse;
             /*mv.addObject("incorrectOtp","Enter correct OTP");
             mv.setViewName("OTPV.jsp");*/
         }
         if(index != null) {
             verifyOtpResponse.put("otpVerified","OTP verified");
             /*mv.setViewName("VerifiedPage.jsp");
             isTrue = true;
             userRepository.save(globalUser);*/
             return verifyOtpResponse;
         }
         else{
             /*mv.addObject("incorrectOtp","Enter correct OTP");
             mv.setViewName("OTPV.jsp");*/
             verifyOtpResponse.put("incorrectOtp","Enter correct OTP");
         }
        return verifyOtpResponse;
    }

    @RequestMapping("/resendOtp")
    @ResponseBody
    public JSONObject /*String*/ resendOtp(){
        JSONObject resendOtpResponse = new JSONObject();
        resendOtpResponse.put("otpResend","");
        final String url = "https://2factor.in/API/V1/"+apiKey+"/SMS/"+phoneNo+"/AUTOGEN";
        RestTemplate restTemplate = new RestTemplate();
        sessionId = restTemplate.getForObject(url, String.class);
        sessionId = sessionId.substring(31,67);
        resendOtpResponse.put("otpResend","OTP Resent");
        return resendOtpResponse;
        //return "OTPV.jsp";
    }

    @RequestMapping("/home")
    public String home(){
        return "home.jsp";
    }

    @RequestMapping("/sendMessage")
    public ModelAndView sendMessage(@RequestParam String emailid) throws MessagingException {
            ModelAndView mv = new ModelAndView();
            localDateTime = LocalDateTime.now();
            if(userRepository.findByUsername(emailid)!=null) {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(emailid);
                helper.setSubject("Password Reset Link - Molly");
                helper.setText("Click the link to reset your Molly account password, http://localhost:8080/resetPassword\n" +
                        "Your link will expire 24 hours from now that is" + localDateTime);

                mv.addObject(emailid);
                emailId = emailid;
                javaMailSender.send(message);
                mv.setViewName("MessageSent.jsp");
            }
            else{
                String invalidEmail = "";
                mv.setViewName("/login");
                mv.addObject("invalidEmail","Not a registered Molly user");
            }
            return mv;
    }

    @RequestMapping("/forgotPassword")
    public String forgotPassword(){
        return "ForgotPassword.jsp";
    }

    @RequestMapping("/resetPassword")
    public String resetPassword(){
        if(LocalDateTime.now().isAfter(localDateTime.plusHours(24)))
            return "LinkExpired.jsp";
        else
            return "ResetPassword.jsp";
    }

    @RequestMapping("/resetSuccessful")
    public ModelAndView resetSuccessful(String password) {
        ModelAndView mv = new ModelAndView();
            if(userRepository.findByUsername(emailId)!=null) {
                User user = userRepository.findByUsername(emailId);
                String pwd = encodePWD().encode(password);
                user.setPassword(pwd);
                userRepository.save(user);
                mv.setViewName("ResetSuccessful.jsp");
            }
            else{
                mv.setViewName("/login");
            }
            return mv;
    }

    @RequestMapping(value="/loginPage"/*,consumes = MediaType.ALL_VALUE*//*,headers = "Accept=application/json"*/)
    public String loginPage(/*@RequestParam String username, @RequestParam String password*/){
        return "login.jsp";
    }
}
