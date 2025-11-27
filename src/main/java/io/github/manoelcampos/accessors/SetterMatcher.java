package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.isSetter;

/**
 * Aa {@link ElementMatcher} to check if a method is the setter for a given field.
 * @author Manoel Campos
 * @see ClassAccessorInstrumentationPlugin
 */
class SetterMatcher extends AbstractAccessorMatcher {
    /**
     * Creates a SetterMatcher.
     * @param fieldMatcher see {@link #fieldMatcher}
     */
    public SetterMatcher(final InstanceFieldMatcher fieldMatcher) {
        super(fieldMatcher);
    }

    /**
     * {@return the name of the setter for a given field}
     * @param field the field to retrieve its setter name
     */
    public static String setterName(final FieldDescription field) {
        final var fieldName = capitalize(field.getName());
        return isFieldBoolean(field) ? "is" + fieldName : "set" + fieldName;
    }

    @Override
    public boolean matches(final MethodDescription methodDescription) {
        return isSetter().matches(methodDescription) && isAccessorForField(methodDescription);
    }

    @Override
    protected boolean isAccessorForField(final MethodDescription methodDescription) {
        return methodDescription.getName().equals("set" + capitalize(getFieldName()));
    }

}
