package bartboersma.calculator.service;

import bartboersma.calculator.model.CalculationModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
public class SessionServiceTest {

    @Autowired
    SessionService sessionService;

    MockHttpSession mockHttpSession = new MockHttpSession();

    @Test
    public void isSessionRunningFalse() {
        assertFalse(sessionService.isSessionRunning(mockHttpSession));
    }

    @Test
    public void isSessionRunningTrue() {
        mockHttpSession.setAttribute("CALCULATOR_SESSIONS", 123.0);
        assertTrue(sessionService.isSessionRunning(mockHttpSession));
    }

    @Test
    public void isFirstNumberEqualToSessionResultFalse() {
        mockHttpSession.setAttribute("CALCULATOR_SESSIONS", 123.0);
        assertFalse(sessionService.isFirstNumberEqualToSessionResult(mockHttpSession, 1.0));
    }

    @Test
    public void isFirstNumberEqualToSessionResultTrue() {
        mockHttpSession.setAttribute("CALCULATOR_SESSIONS", 123.0);
        assertTrue(sessionService.isFirstNumberEqualToSessionResult(mockHttpSession, 123.0));
    }

    @Test
    public void setFirstNumberToSessionResult() {
        mockHttpSession.setAttribute("CALCULATOR_SESSIONS", 123.0);
        CalculationModel calculationModel = new CalculationModel();

        sessionService.setFirstNumberToSessionResult(mockHttpSession, calculationModel);

        assertEquals(calculationModel.getFirstNumber(), 123.0, 0);
    }

    @Test
    public void setSessionResult() {
        assertNull(mockHttpSession.getAttribute("CALCULATOR_SESSIONS"));

        sessionService.setSessionResult(mockHttpSession, 123.0);

        assertEquals((double) mockHttpSession.getAttribute("CALCULATOR_SESSIONS"), 123.0, 0.0);
    }

}
