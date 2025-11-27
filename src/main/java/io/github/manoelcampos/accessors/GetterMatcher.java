package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.isGetter;

/**
 * Aa {@link ElementMatcher} to check if a method is the getter for a given field.
 * @author Manoel Campos
 * @see ClassAccessorInstrumentationPlugin
 */
class GetterMatcher extends AbstractAccessorMatcher {
    /**
     * Creates a GetterMatcher.
     * @param fieldMatcher see {@link #fieldMatcher}
     */
    public GetterMatcher(final InstanceFieldMatcher fieldMatcher) {
        super(fieldMatcher);
    }

    /**
     * {@return the name of the getter for a given field}
     * @param field the field to retrieve its getter name
     */
    public static String getterName(final FieldDescription field) {
        return "get" + capitalize(field.getName());
    }

    @Override
    public boolean matches(final MethodDescription methodDescription) {
        return isGetter().matches(methodDescription) && isAccessorForField(methodDescription);
    }

    @Override
    protected boolean isAccessorForField(final MethodDescription methodDescription) {
        final var methodName = methodDescription.getName();
        final var isBoolean = isMatchedFieldBoolean();
        final var fieldName = capitalize(getFieldName());
        return (isBoolean && methodName.equals("is"+fieldName)) || (!isBoolean && methodName.equals("get"+fieldName));
    }
}
