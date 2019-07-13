package bartboersma.calculator.controller;

import bartboersma.calculator.model.CalculationModel;
import bartboersma.calculator.model.CalculationType;
import bartboersma.calculator.service.CalculationService;
import bartboersma.calculator.service.SessionService;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpSession;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CalculatorController calculatorController;

    @MockBean
    CalculationService calculationServiceMock;

    @MockBean
    SessionService sessionService;

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    private JSONObject calculationModelJSON = new JSONObject();
    private CalculationModel calculationModel = new CalculationModel();


    @Before
    public void setUp() throws JSONException {
        calculationModelJSON.put("firstNumber", 5);
        calculationModelJSON.put("secondNumber", 5);
        calculationModelJSON.put("calculationType", CalculationType.TIMES);

        calculationModel.setFirstNumber(5);
        calculationModel.setSecondNumber(5);
        calculationModel.setCalculationType(CalculationType.TIMES);

        Logger logger = (Logger) LoggerFactory.getLogger(CalculatorController.class);
        logger.addAppender(mockAppender);
    }

    @Test
    public void testCalculate() throws Exception {
        when(calculationServiceMock.calculate(any(CalculationModel.class))).thenReturn(anyDouble());

        mockMvc.perform(post("/api/calculator/calculate")
                .content(calculationModelJSON.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        verify(calculationServiceMock, times(1)).calculate(any(CalculationModel.class));
    }

    @Test
    public void testSessionReplacingFirstNumber() throws Exception {
        when(sessionService.isSessionRunning(any(HttpSession.class))).thenReturn(false);
        when(calculationServiceMock.calculate(any(CalculationModel.class))).thenReturn(new Double(25));

        ResultActions firstResult = mockMvc.perform(post("/api/calculator/calculate")
                .content(calculationModelJSON.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        assertEquals("25.0", firstResult.andReturn().getResponse().getContentAsString());

        when(sessionService.isSessionRunning(any(HttpSession.class))).thenReturn(true);
        when(sessionService.isFirstNumberEqualToSessionResult(any(HttpSession.class), anyDouble())).thenReturn(true);
        when(calculationServiceMock.calculate(any(CalculationModel.class))).thenReturn(new Double(125));

        calculationModelJSON.put("firstNumber", 25);

        ResultActions secondResult = mockMvc.perform(post("/api/calculator/calculate")
                .content(calculationModelJSON.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        verify(calculationServiceMock, times(2)).calculate(any(CalculationModel.class));
        verify(sessionService, times(2)).isSessionRunning(any(HttpSession.class));
        verify(sessionService, times(1)).isFirstNumberEqualToSessionResult(any(HttpSession.class), anyDouble());
        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getValue().toString(), containsString("Result in session is same as first number so user wants to continue calculation with this number"));
        assertEquals("125.0", secondResult.andReturn().getResponse().getContentAsString());
    }

    @Test
    public void testInfiniteResult() throws Exception {
        when(sessionService.isSessionRunning(any(HttpSession.class))).thenReturn(false);
        when(calculationServiceMock.calculate(any(CalculationModel.class))).thenReturn(Math.pow(10000, 10000));

        mockMvc.perform(post("/api/calculator/calculate")
                .content(calculationModelJSON.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(calculationServiceMock, times(1)).calculate(any(CalculationModel.class));
        verify(sessionService, times(1)).isSessionRunning(any(HttpSession.class));
    }

    @Test
    public void testErrors() throws Exception {
        calculationModelJSON.put("calculationType", "INCORRECT CALCULATION TYPE");

        mockMvc.perform(post("/api/calculator/calculate")
                .content(calculationModelJSON.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(sessionService, times(0)).isSessionRunning(any(HttpSession.class));
    }

}


