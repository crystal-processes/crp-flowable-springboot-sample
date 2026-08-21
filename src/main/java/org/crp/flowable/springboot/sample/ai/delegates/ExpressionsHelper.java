package org.crp.flowable.springboot.sample.ai.delegates;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ExpressionsHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ExpressionsHelper.class);

    public static <T> T getValue(Expression expression, DelegateExecution execution, Class<T> expectedClass) {
        if (expression == null) {
            return null;
        }
        Object value = expression.getValue(execution);
        if (value == null) {
            return null;
        }
        if (expectedClass.isInstance(value)) {
            return expectedClass.cast(value);
        }
        throw new ClassCastException("Unable to cast " + value.getClass().getName() + " to expected " + expectedClass.getName());
    }

    public static <T> T getMandatoryValue(String name, Expression expression, DelegateExecution execution, Class<T> expectedClass) {
        T value = getValue(expression, execution, expectedClass);
        if (value == null) {
            LOG.error("{} is mandatory.", name);
            throw new RuntimeException(name + " is mandatory");
        }
        return value;
    }
}
