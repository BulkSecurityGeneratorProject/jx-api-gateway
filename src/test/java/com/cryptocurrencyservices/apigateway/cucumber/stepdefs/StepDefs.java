package com.cryptocurrencyservices.apigateway.cucumber.stepdefs;

import com.cryptocurrencyservices.apigateway.JxApiGatewayApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = JxApiGatewayApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
