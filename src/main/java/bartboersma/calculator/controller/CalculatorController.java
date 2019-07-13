package bartboersma.calculator.controller;

import bartboersma.calculator.model.CalculationModel;
import bartboersma.calculator.service.CalculationService;
import bartboersma.calculator.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/calculator")
@Slf4j
public class CalculatorController {

    @Autowired
    CalculationService calculationService;

    @Autowired
    SessionService sessionService;


    @PostMapping(value = "/calculate", consumes = "application/json")
    public ResponseEntity<Object> calculate(@RequestBody CalculationModel calculationModel, HttpSession httpSession) {

        if (sessionService.isSessionRunning(httpSession)) {
            if (sessionService.isFirstNumberEqualToSessionResult(httpSession, calculationModel.getFirstNumber())) {
                log.info("Result in session is same as first number so user wants to continue calculation with this number.");
                sessionService.setFirstNumberToSessionResult(httpSession, calculationModel);
            }
        }

        calculationModel.setResult(calculationService.calculate(calculationModel));

        if (calculationModel.getResult() == null || Double.isInfinite(calculationModel.getResult())) {
            return ResponseEntity.badRequest().body("Calculated number is too big or null");
        }

        sessionService.setSessionResult(httpSession, calculationModel.getResult());

        return ResponseEntity.ok(calculationModel.getResult());
    }
}
