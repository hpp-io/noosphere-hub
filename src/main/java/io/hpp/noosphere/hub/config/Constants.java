package io.hpp.noosphere.hub.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String NULL_STRING = "null";

    public static final String HTTP_HEADER_API_KEY = "X-API-KEY";
    public static final String ATTRIBUTE_API_KEY = "apiKey";
    public static final String ATTRIBUTE_LANG_KEY = "langKey";
    public static final String ATTRIBUTE_IMAGE_URL = "imageUrl";

    public static final String DEFAULT_KEYCLOAK_REALM_ID = "noosphere";
    public static final String KEYCLOAK_CLIENT_ID_WEB_APP = "web_app";
    public static final String KEYCLOAK_GROUP_ADMINS = "Admins";
    public static final String KEYCLOAK_GROUP_USERS = "Users";
    public static final String KEYCLOAK_PROPERTY_NAME_VERIFIED = "verified";
    public static final String PROPERTY_NAME_USER = "user";
    public static final String PROPERTY_NAME_GROUP = "group";
    public static final String PROPERTY_NAME_EMAIL = "email";
    public static final String PROPERTY_NAME_USER_ID = "userId";
    public static final String PROPERTY_NAME_NAME = "name";
    public static final String PROPERTY_NAME_MOBILE_PHONE_NUMBER = "mobilePhoneNumber";
    public static final String PROPERTY_NAME_COMPANY = "company";
    public static final String PROPERTY_NAME_BUSINESS_REGISTRATION_NUMBER = "bizrNo";
    public static final String PROPERTY_NAME_LOCALE = "locale";
    public static final String PROPERTY_NAME_GENDER_CODE = "genderCode";
    public static final String PROPERTY_NAME_PROFILE_IMAGE_URL = "profileImageUrl";
    public static final String PROPERTY_NAME_PROFILE_THUMBNAIL_IMAGE_URL = "profileThumbnailImageUrl";

    public static final String PROPERTY_NAME_ERROR_KEY = "errorKey";
    public static final String PROPERTY_NAME_NEW_ERROR_KEY = "newErrorKey";
    public static final String PROPERTY_NAME_VALUE = "value";

    public static final String ERROR_KEY_REQUIRED = "required";
    public static final String ERROR_KEY_INVALID = "invalid";
    public static final String ERROR_KEY_NOT_FOUND = "notFound";
    public static final String ERROR_KEY_ALREADY_EXISTS = "alreadyExists";
    public static final String KEYCLOAK_GROUP_ADMIN = "Admins";
    public static final String KEYCLOAK_ROLE_ADMIN = "ROLE_ADMIN";
    public static final String KEYCLOAK_GROUP_USER = "Users";
    public static final String KEYCLOAK_ROLE_USER = "ROLE_USER";


    private Constants() {}
}
