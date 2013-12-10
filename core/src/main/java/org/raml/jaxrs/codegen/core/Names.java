
package org.raml.jaxrs.codegen.core;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;

public class Names
{
    public static String buildResourceInterfaceName(final Resource resource)
    {
        final String resourceInterfaceName = buildJavaFriendlyName(defaultIfBlank(resource.getDisplayName(),
            resource.getRelativeUri()));

        return isBlank(resourceInterfaceName) ? "Root" : resourceInterfaceName;
    }

    public static String buildVariableName(final String source)
    {
        final String name = uncapitalize(buildJavaFriendlyName(source));

        return Constants.JAVA_KEYWORDS.contains(name) ? "$" + name : name;
    }

    public static String buildJavaFriendlyName(final String source)
    {
        final String baseName = source.replaceAll("[\\W_]", " ");

        String friendlyName = capitalize(baseName).replaceAll("[\\W_]", "");

        if (isDigits(left(friendlyName, 1)))
        {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }

    public static String buildResourceMethodName(final Action action, final MimeType bodyMimeType)
    {
        final String methodBaseName = buildJavaFriendlyName(action.getResource()
            .getUri()
            .replace("{", " By "));

        return action.getType().toString().toLowerCase() + buildBodyTypeInfix(bodyMimeType) + methodBaseName;
    }

    public static String buildResponseMethodName(final int statusCode, final MimeType responseMimeType)
    {
        final String status = EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.ENGLISH);

        return getShortMimeType(responseMimeType)
               + buildJavaFriendlyName(defaultIfBlank(status, "_" + statusCode));
    }

    private static String buildBodyTypeInfix(final MimeType bodyMimeType)
    {
        return bodyMimeType != null ? buildJavaFriendlyName(getShortMimeType(bodyMimeType)) : "";
    }

    private static String getShortMimeType(final MimeType bodyMimeType)
    {
        if (bodyMimeType == null)
        {
            return "";
        }

        return StringUtils.remove(substringAfter(bodyMimeType.getType(), "/").toLowerCase(), "x-www-");
    }

    private Names()
    {
        throw new UnsupportedOperationException();
    }
}