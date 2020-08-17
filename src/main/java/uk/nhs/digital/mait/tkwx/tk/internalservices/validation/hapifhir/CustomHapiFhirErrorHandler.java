package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.parser.IParserErrorHandler;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.parser.json.JsonLikeValue.ScalarType;
import ca.uhn.fhir.parser.json.JsonLikeValue.ValueType;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import java.util.ArrayList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Refined error handler to enable warning messages to be output to the
 * validation report
 *
 * This is largely based upon the ca.uhn.fhir.parser.LenientErrorHandler but
 * stores the list of warnings until reset
 */
public class CustomHapiFhirErrorHandler implements IParserErrorHandler {

    private static final StrictErrorHandler STRICT_ERROR_HANDLER = new StrictErrorHandler();
    private boolean myErrorOnInvalidValue = true;
    private boolean myLogErrors;
    private ArrayList<SingleValidationMessage> warningMessages = new ArrayList();

    /**
     * Constructor which configures this handler to log all errors
     */
    public CustomHapiFhirErrorHandler() {
        myLogErrors = true;
    }

    /**
     * Constructor
     *
     * @param theLogErrors Should errors be logged?
     * @since 1.2
     */
    public CustomHapiFhirErrorHandler(boolean theLogErrors) {
        myLogErrors = theLogErrors;
    }

    @Override
    public void containedResourceWithNoId(IParserErrorHandler.IParseLocation theLocation) {
        if (myLogErrors) {
            addSingleValidationMessage("Resource has contained child resource with no ID", theLocation);
        }
    }

    @Override
    public void incorrectJsonType(IParserErrorHandler.IParseLocation theLocation, String theElementName, ValueType theExpected, ScalarType theExpectedScalarType, ValueType theFound, ScalarType theFoundScalarType) {
        if (myLogErrors) {
            addSingleValidationMessage(createIncorrectJsonTypeMessage(theElementName, theExpected, theExpectedScalarType, theFound, theFoundScalarType), theLocation);
        }
    }

    @Override
    public void invalidValue(IParserErrorHandler.IParseLocation theLocation, String theValue, String theError) {
        if (isBlank(theValue) || myErrorOnInvalidValue == false) {
            if (myLogErrors) {
                addSingleValidationMessage("Invalid attribute value \"" + theValue + "\": " + theError, theLocation);
            }
        } else {
            STRICT_ERROR_HANDLER.invalidValue(theLocation, theValue, theError);
        }
    }

    /**
     * If set to <code>false</code> (default is <code>true</code>) invalid
     * values will be logged. By default invalid attribute values cause this
     * error handler to throw a {@link DataFormatException} (unlike other
     * methods in this class which default to simply logging errors).
     * <p>
     * Note that empty values (e.g. <code>""</code>) will not lead to an error
     * when this is set to <code>true</code>, only invalid values (e.g. a gender
     * code of <code>foo</code>)
     * </p>
     *
     * @see #setErrorOnInvalidValue(boolean)
     */
    public boolean isErrorOnInvalidValue() {
        return myErrorOnInvalidValue;
    }

    @Override
    public void missingRequiredElement(IParserErrorHandler.IParseLocation theLocation, String theElementName) {
        if (myLogErrors) {
            addSingleValidationMessage("Resource has contained child resource with no ID", theLocation);
        }
    }

    /**
     * If set to <code>false</code> (default is <code>true</code>) invalid
     * values will be logged. By default invalid attribute values cause this
     * error handler to throw a {@link DataFormatException} (unlike other
     * methods in this class which default to simply logging errors).
     * <p>
     * Note that empty values (e.g. <code>""</code>) will not lead to an error
     * when this is set to <code>true</code>, only invalid values (e.g. a gender
     * code of <code>foo</code>)
     * </p>
     *
     * @return Returns a reference to <code>this</code> for easy method chaining
     * @see #isErrorOnInvalidValue()
     */
    public CustomHapiFhirErrorHandler setErrorOnInvalidValue(boolean theErrorOnInvalidValue) {
        myErrorOnInvalidValue = theErrorOnInvalidValue;
        return this;
    }

    @Override
    public void unexpectedRepeatingElement(IParserErrorHandler.IParseLocation theLocation, String theElementName) {
        if (myLogErrors) {
            addSingleValidationMessage("Multiple repetitions of non-repeatable element '" + theElementName + "' found while parsing", theLocation);
        }
    }

    @Override
    public void unknownAttribute(IParserErrorHandler.IParseLocation theLocation, String theElementName) {
        if (myLogErrors) {
            addSingleValidationMessage("Unknown attribute '" + theElementName + "' found while parsing", theLocation);
        }
    }

    @Override
    public void unknownElement(IParserErrorHandler.IParseLocation theLocation, String theElementName) {
        if (myLogErrors) {
            addSingleValidationMessage("Unknown element '" + theElementName + "' found while parsing", theLocation);
        }
    }

    @Override
    public void unknownReference(IParserErrorHandler.IParseLocation theLocation, String theReference) {
        if (myLogErrors) {
            addSingleValidationMessage("Resource has invalid reference: " + theReference, theLocation);
        }
    }

    public static String createIncorrectJsonTypeMessage(String theElementName, ValueType theExpected, ScalarType theExpectedScalarType, ValueType theFound, ScalarType theFoundScalarType) {
        StringBuilder b = new StringBuilder();
        b.append("Found incorrect type for element ");
        b.append(theElementName);
        b.append(" - Expected ");
        b.append(theExpected.name());
        if (theExpectedScalarType != null) {
            b.append(" (");
            b.append(theExpectedScalarType.name());
            b.append(")");
        }
        b.append(" and found ");
        b.append(theFound.name());
        if (theFoundScalarType != null) {
            b.append(" (");
            b.append(theFoundScalarType.name());
            b.append(")");
        }
        String message = b.toString();
        return message;
    }

    public ArrayList<SingleValidationMessage> getWarningMessages() {
        return warningMessages;
    }

    public void resetWarningMessages() {
        this.warningMessages = new ArrayList();
    }

    private void addSingleValidationMessage(String message, IParserErrorHandler.IParseLocation theLocation) {
        SingleValidationMessage hapiMessage = new SingleValidationMessage();
        hapiMessage.setMessage(message);
        hapiMessage.setSeverity(ResultSeverityEnum.WARNING);
        hapiMessage.setLocationCol(-1);
        hapiMessage.setLocationLine(-1);
        if (theLocation != null) {
            if (theLocation.getParentElementName() != null) {
                hapiMessage.setLocationString(theLocation.getParentElementName());
            } else {
                hapiMessage.setLocationString(theLocation.toString());
            }
        } else {
            hapiMessage.setLocationString("Reported from customised LenientErrorHandler where location not set");
        }
        warningMessages.add(hapiMessage);
    }

    @Override
    public void extensionContainsValueAndNestedExtensions(IParserErrorHandler.IParseLocation arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
