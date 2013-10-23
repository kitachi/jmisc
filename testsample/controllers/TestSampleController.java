package au.gov.nla.dlir.controllers;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.gov.nla.dlir.config.JsonConfiguration;
import au.gov.nla.dlir.services.SessionService;

@Controller
public class TestSampleController {
    private String sampleFilePath = "/Users/shuangzhou/Downloads/tarkine-text-correct-bak/src/test/resources/nla.aus-an3281107-correction.json";

    @Autowired
    private JsonConfiguration config;

    @Autowired
    private SessionService sessionService;
    
    @RequestMapping(value = "/testsample", method = RequestMethod.POST)
    public String putSample(@RequestParam("jsonSample") String jsonSample, Model model, HttpServletRequest request) {
        System.out.println(jsonSample);
        sessionService.process(request, model);
        return "widgets/testsample/testsample";
    }

    @RequestMapping(value = "/loadsample", method = RequestMethod.GET)
    public String getSample(Model model, HttpServletRequest request) throws Exception {
        sessionService.process(request, model);
        String sample = new String(Files.readAllBytes(Paths.get(sampleFilePath)));
        model.addAttribute("sample", sample);
        System.out.println("sample: " + sample);
        return "widgets/testsample/testsample";
    }
}