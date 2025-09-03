package io.hpp.noosphere.hub.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.hpp.noosphere.hub.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
