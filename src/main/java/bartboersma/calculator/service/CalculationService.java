package bartboersma.calculator.service;

import bartboersma.calculator.model.CalculationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationService {


    public Double calculate(CalculationModel calculationModel) {
        return findCalculationMethodToApply(calculationModel);
    }

    private Double findCalculationMethodToApply(CalculationModel calculationModel) {
        switch(calculationModel.getCalculationType()) {
            case SUM:
                return sum(calculationModel.getFirstNumber(), calculationModel.getSecondNumber());
            case DEDUCT:
                return deduct(calculationModel.getFirstNumber(), calculationModel.getSecondNumber());
            case TIMES:
                return times(calculationModel.getFirstNumber(), calculationModel.getSecondNumber());
            case DIVIDE:
                return divide(calculationModel.getFirstNumber(), calculationModel.getSecondNumber());
            case POWER:
                return power(calculationModel.getFirstNumber(), calculationModel.getSecondNumber());
            case ROOT:
                return root(calculationModel.getFirstNumber());
            default: log.error("Unknown calculation type, returning null: {}", calculationModel.getCalculationType());
        }
        return null;
    }

    private Double sum(Double firstNumber, Double secondNumber) {
        return firstNumber + secondNumber;
    }

    private Double deduct(Double firstNumber, Double secondNumber) {
        return firstNumber - secondNumber;
    }

    private Double times(Double firstNumber, Double secondNumber) {
        return firstNumber * secondNumber;
    }

    private Double divide(Double firstNumber, Double secondNumber) {
        if (secondNumber == 0) {
            log.error("Cannot divide by 0, returning null for number: {}", firstNumber);
            return null;
        }

        return firstNumber / secondNumber;
    }

    private Double power(Double firstNumber, Double secondNumber) {
        return Math.pow(firstNumber, secondNumber);
    }

    private Double root(Double firstNumber) {
        if (firstNumber < 0) {
            log.error("Cannot take the root of a negative number, returning null for number: {}", firstNumber);
            return null;
        }

        return Math.sqrt(firstNumber);
    }


}
