package io.redskap.swagger.brake.core.rule.request.parameter.constraint.number;

import java.math.BigDecimal;
import java.util.Optional;

import io.redskap.swagger.brake.core.model.parameter.NumberRequestParameter;
import io.redskap.swagger.brake.core.rule.request.parameter.constraint.RequestParameterConstraint;
import io.redskap.swagger.brake.core.rule.request.parameter.constraint.RequestParameterConstraintChange;
import io.redskap.swagger.brake.core.util.BigDecimalComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class NumberMinimumConstraint implements RequestParameterConstraint<NumberRequestParameter> {
    public static final String MINIMUM_ATTRIBUTE_NAME = "minimum";

    @Override
    public Optional<RequestParameterConstraintChange> validateConstraints(NumberRequestParameter oldRequestParameter, NumberRequestParameter newRequestParameter) {
        RequestParameterConstraintChange result = null;
        if (oldRequestParameter != null && newRequestParameter != null) {
            if (oldRequestParameter.isIntegerTyped() && newRequestParameter.isIntegerTyped()) {
                BigDecimal oldMinimum = oldRequestParameter.getMinimum();
                BigDecimal newMinimum = newRequestParameter.getMinimum();
                if (oldMinimum == null && newMinimum != null) {
                    result = new RequestParameterConstraintChange(MINIMUM_ATTRIBUTE_NAME,
                        null,
                        new PrettyFormattedBigDecimal(newMinimum)
                    );
                }
                if (oldMinimum != null && newMinimum != null) {
                    boolean oldExclusiveMinimum = oldRequestParameter.isExclusiveMinimum();
                    boolean newExclusiveMinimum = newRequestParameter.isExclusiveMinimum();
                    if (oldExclusiveMinimum == newExclusiveMinimum && BigDecimalComparator.isGreaterThan(newMinimum, oldMinimum)) {
                        result = new RequestParameterConstraintChange(MINIMUM_ATTRIBUTE_NAME,
                            new PrettyFormattedBigDecimal(oldMinimum),
                            new PrettyFormattedBigDecimal(newMinimum)
                        );
                    } else {
                        BigDecimal oldMaxWithExclusivity = oldMinimum.add(oldExclusiveMinimum ? BigDecimal.ONE : BigDecimal.ZERO);
                        BigDecimal newMaxWithExclusivity = newMinimum.add(newExclusiveMinimum ? BigDecimal.ONE : BigDecimal.ZERO);
                        if (BigDecimalComparator.isGreaterThan(newMaxWithExclusivity, oldMaxWithExclusivity)) {
                            result = new RequestParameterConstraintChange("exclusiveMinimum",
                                oldExclusiveMinimum,
                                newExclusiveMinimum
                            );
                        }
                    }
                }
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public Class<NumberRequestParameter> handledRequestParameter() {
        return NumberRequestParameter.class;
    }
}
