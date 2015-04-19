package com.industriousgnomes.logging.aop;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class LoggingInterceptorTests {

    @Mocked ProceedingJoinPoint mockJoinPoint;
    
    @Mocked LogManager mockLogManager;
    @Mocked Logger mockLogger;
    
    LoggingInterceptor interceptor;
    
    @Before
    public void setUp() {
        interceptor = new LoggingInterceptor();
    }
    
    @Test
    public void logIncomingAndOutgoing() throws Throwable {
        // Setup
        
        new Expectations() {{
            mockJoinPoint.getTarget(); result = Math.class;
            LogManager.getLogger((Class<?>)any); result = mockLogger; 
            mockLogger.isTraceEnabled(); result = true;

            mockJoinPoint.getSignature().getName(); result = "main";
            mockJoinPoint.getArgs(); returns("a", 2, asList(1, 2), null);
            
            mockJoinPoint.proceed(); result = null;
        }};
        
        // Execution
        interceptor.logIncomingAndOutgoing(mockJoinPoint);
        
        // Verification
        new Verifications() {{
            List<String> messages = new LinkedList<>();
            mockLogger.trace(withCapture(messages));

            assertEquals(2, messages.size());
            assertEquals("main(String, Integer, ArrayList, NULL) - Start", messages.get(0));
            assertEquals("main(String, Integer, ArrayList, NULL) - End", messages.get(1));
        }};
    }

    @Test
    public void logIncomingAndOutgoing_NotTraceEnabled() throws Throwable {
        // Setup
        
        new Expectations() {{
            mockJoinPoint.getTarget(); result = Math.class;
            LogManager.getLogger((Class<?>)any); result = mockLogger; 
            mockLogger.isTraceEnabled(); result = false;
            mockJoinPoint.proceed(); result = null;
        }};
        
        // Execution
        interceptor.logIncomingAndOutgoing(mockJoinPoint);
        
        // Verification
        new Verifications() {{
            mockLogger.trace(withSuffix(" - Start")); times = 0;
            mockLogger.trace(withSuffix(" - End")); times = 0;
        }};
    }
}
