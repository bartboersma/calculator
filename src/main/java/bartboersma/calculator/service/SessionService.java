package bartboersma.calculator.service;

import bartboersma.calculator.model.CalculationModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class SessionService {

    private final String SESSION_KEY = "CALCULATOR_SESSIONS";

    public boolean isSessionRunning(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }

    public boolean isFirstNumberEqualToSessionResult(HttpSession session, double firstNumber) {
        return (double) session.getAttribute(SESSION_KEY) == firstNumber;
    }

    public void setFirstNumberToSessionResult(HttpSession session, CalculationModel calculationModel) {
        calculationModel.setFirstNumber((double) session.getAttribute(SESSION_KEY));
    }

    public void setSessionResult(HttpSession session, Double result) {
        session.setAttribute(SESSION_KEY, result);
    }
}
