package org.lpw.tephra.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author lpw
 */
@Component("tephra.util.validator")
public class ValidatorImpl implements Validator {
    private static final String EMAIL = "^(?:\\w+\\.?-?)*\\w+@(?:\\w+\\.?-?)*\\w+$";

    protected Map<String, Pattern> patterns = new ConcurrentHashMap<>();

    @Override
    public boolean isEmpty(Object object) {
        if (object == null)
            return true;

        if (object instanceof String)
            return ((String) object).trim().length() == 0;

        if (object.getClass().isArray())
            return Array.getLength(object) == 0;

        if (object instanceof Collection)
            return ((Collection<?>) object).isEmpty();

        if (object instanceof Map)
            return ((Map<?, ?>) object).isEmpty();

        return false;
    }

    @Override
    public boolean isEmail(String email) {
        return isEmpty(email) ? false : isMatchRegex(EMAIL, email);
    }

    @Override
    public boolean isMatchRegex(String regex, String string) {
        if (regex == null || string == null)
            return false;

        return getPattern(regex).matcher(string).matches();
    }

    protected Pattern getPattern(String regex) {
        Pattern pattern = patterns.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            patterns.put(regex, pattern);
        }

        return pattern;
    }

    @Override
    public boolean isImage(String contentType, String name) {
        int indexOf;
        if (isEmpty(contentType) || isEmpty(name) || !contentType.startsWith("image/") || (indexOf = name.lastIndexOf('.')) == -1)
            return false;

        String suffix = name.substring(indexOf);
        return ((contentType.equals("image/jpeg") && (suffix.equals(".jpg") || suffix.equals(".jpeg"))) ||
                (contentType.equals("image/png") && suffix.equals(".png")) || (contentType.equals("image/gif") && suffix.equals(".gif")));
    }
}
