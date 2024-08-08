package com.example.Sales.controller;


import com.example.Sales.entity.Survey;
import com.example.Sales.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Survey>> getSurveyReport() {
        List<Survey> surveys = surveyService.generateSurveyReport();
        return ResponseEntity.ok(surveys);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportSurveyReport() {
        List<Survey> surveys = surveyService.generateSurveyReport();

        StreamingResponseBody stream = out -> {
            try (OutputStream os = out) {
                String header = "Product ID,Product Name,Profit,Loss\n";
                os.write(header.getBytes());

                for (Survey survey : surveys) {
                    String line = String.format("%d,%s,%s,%s\n",
                            survey.getProduct().getId(),
                            survey.getProduct().getName(),
                            survey.getProfit().toString(),
                            survey.getLoss().toString());
                    os.write(line.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"survey-report.csv\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }
}
