package bartboersma.calculator.service;

import bartboersma.calculator.model.CalculationModel;
import bartboersma.calculator.model.CalculationType;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
public class CalculationServiceTest {

    @Autowired
    CalculationService calculationService;

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    private CalculationModel calculationModel = new CalculationModel();

    @Before
    public void setUp() {
        calculationModel.setFirstNumber(5);
        calculationModel.setSecondNumber(5);

        Logger logger = (Logger) LoggerFactory.getLogger(CalculationService.class);
        logger.addAppender(mockAppender);
    }

    @Test
    public void testSum() {
        calculationModel.setCalculationType(CalculationType.SUM);

        Double result = calculationService.calculate(calculationModel);

        assertEquals(result, 10.0, 0);
    }

    @Test
    public void testDeduct() {
        calculationModel.setCalculationType(CalculationType.DEDUCT);

        Double result = calculationService.calculate(calculationModel);

        assertEquals(result, 0.0, 0);
    }

    @Test
    public void testTimes() {
        calculationModel.setCalculationType(CalculationType.TIMES);

        Double result = calculationService.calculate(calculationModel);

        assertEquals(result, 25.0, 0);
    }

    @Test
    public void testDivide() {
        calculationModel.setCalculationType(CalculationType.DIVIDE);

        Double result = calculationService.calculate(calculationModel);

        assertEquals(result, 1.0, 0);
    }

    @Test
    public void testDivideBy0() {
        calculationModel.setSecondNumber(0);

        calculationModel.setCalculationType(CalculationType.DIVIDE);

        Double result = calculationService.calculate(calculationModel);

        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getValue().toString(), containsString("Cannot divide by 0, returning null for number:"));
        assertNull(result);
    }

    @Test
    public void testPower() {
        calculationModel.setCalculationType(CalculationType.POWER);

        Double result = calculationService.calculate(calculationModel);

        assertEquals(result, 3125.0, 0);
    }

    @Test
    public void testRoot() {
        calculationModel.setFirstNumber(9);

        calculationModel.setCalculationType(CalculationType.ROOT);

        Double result = calculationService.calculate(calculationModel);

        assertEquals(result, 3, 0);
    }

    @Test
    public void testRootNumberLowerThan0() {
        calculationModel.setFirstNumber(-1);

        calculationModel.setCalculationType(CalculationType.ROOT);

        Double result = calculationService.calculate(calculationModel);

        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        assertThat(captorLoggingEvent.getValue().toString(), containsString("Cannot take the root of a negative number, returning null for number:"));
        assertNull(result);
    }
}
