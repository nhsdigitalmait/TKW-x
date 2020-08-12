/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Java extension class for use as a Class expression in a simulator rules file
 * compares fhir datetime or date strings
 *
 * @author simonfarrow
 */
public class FhirDateCompareExpression implements ExpressionValue {

    @Override
    public boolean getValue(String[] values, String[] args) throws Exception {
        if (values.length != 2) {
            String msg = "Values array has invalid length " + values.length;
            Logger.getInstance().log(SEVERE, FhirDateCompareExpression.class.getName(), msg);
            throw new IllegalArgumentException(msg);
        }

        if (args.length != 1) {
            String msg = "Args array has invalid length " + args.length;
            Logger.getInstance().log(SEVERE, FhirDateCompareExpression.class.getName(), msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            // remove any url encoding
            for ( int i = 0; i <values.length ; i ++ ) {
                values[i] = URLDecoder.decode(values[i], "UTF-8");
            }
            ZonedDateTime date1 = parseDate(values[0]);
            ZonedDateTime date2 = parseDate(values[1]);
            if (args[0] != null && !args[0].trim().isEmpty()) {
                switch (args[0]) {
                    case "<":
                        return date1.isBefore(date2);
                    case "<=":
                        return date1.isBefore(date2) || date1.isEqual(date2);
                    case ">":
                        return date1.isAfter(date2);
                    case ">=":
                        return date1.isAfter(date2) || date1.isEqual(date2);
                    case "==":
                        return date1.isEqual(date2);
                    case "!=":
                        return !date1.isEqual(date2);
                    default:
                        String msg = "Unsupported comparison operator " + args[0];
                        Logger.getInstance().log(SEVERE, FhirDateCompareExpression.class.getName(), msg);
                        throw new IllegalArgumentException(msg);
                }
            } else {
                String msg = "Empty args value - no comparator supplied";
                Logger.getInstance().log(SEVERE, FhirDateCompareExpression.class.getName(), msg);
                throw new IllegalArgumentException(msg);
            }
        } catch (DateTimeParseException ex) {
            String msg = "Invalid date format " + ex.getMessage();
            Logger.getInstance().log(WARNING, FhirDateCompareExpression.class.getName(), msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * expects either simple date strings or full ISO format fhir datetime strings
     *
     * @param s the date/datetime string to be parsed
     * @return Parsed ZonedDateTime object
     */
    private ZonedDateTime parseDate(String s) {
        DateTimeFormatter formatter = null;
        if (s.matches("^[0-9]{4}(-[0-9]{2}){2}$")) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate localDate = LocalDate.parse(s, formatter);
            // we assume the start of the day for a date string
            return localDate.atStartOfDay(ZoneId.systemDefault());
        } else {
            formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            return ZonedDateTime.parse(s, formatter);
        }
    }
}
