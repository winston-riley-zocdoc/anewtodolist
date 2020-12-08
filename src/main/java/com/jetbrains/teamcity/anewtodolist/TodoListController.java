package com.jetbrains.teamcity.anewtodolist;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Controller
public class TodoListController {

    private Integer latestBuild = null;

    @RequestMapping(method =  { RequestMethod.GET}, value = "deploy")
    public String deployHtml(Model model) {
        model.addAttribute("latestBuildNumber", latestBuild);
        return "deploy";
    }

    @RequestMapping(method =  {RequestMethod.POST, RequestMethod.PUT}, value = "approve/{latestBuildNumber}")
    @ResponseBody
    public String approve(@PathVariable  Integer latestBuildNumber) {
        System.out.println("latestBuildNumber = " + latestBuildNumber);
        this.latestBuild = latestBuildNumber;
        return "Success";
    }


    @RequestMapping(method =  {RequestMethod.POST, RequestMethod.PUT}, value = "deploy")
    @ResponseBody
    public String deploy() {
        final ResponseEntity<String> result = new RestTemplate().exchange("http://marco.teamcity/app/rest/builds/id:" + latestBuild + "/finish", HttpMethod.PUT,  new HttpEntity<>(createHeaders("marco", "marco")), String.class);
        System.out.println(result.getBody());
        latestBuild = null;
        return "Success";
    }

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

}
