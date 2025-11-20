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
  public static final String HTTP_HEADER_WALLET_API_KEY = "X-W-API-KEY";
  public static final String PROPERTY_NAME_API_KEY = "apiKey";
  public static final String PROPERTY_NAME_LANG_KEY = "langKey";
  public static final String PROPERTY_NAME_IMAGE_URL = "imageUrl";
  public static final String PROPERTY_NAME_VERIFIED = "verified";
  public static final String PROPERTY_NAME_USER = "user";
  public static final String PROPERTY_NAME_GROUP = "group";
  public static final String PROPERTY_NAME_CONTAINER = "container";
  public static final String PROPERTY_NAME_USER_SUBSCRIPTION = "userSubscription";
  public static final String PROPERTY_NAME_AGENT = "agent";
  public static final String PROPERTY_NAME_AGENT_REQUEST = "agentRequest";
  public static final String PROPERTY_NAME_VERIFIER = "verifier";
  public static final String PROPERTY_NAME_PERMISSION_DENIED = "permissionDenied";
  public static final String PROPERTY_NAME_EMAIL = "email";
  public static final String PROPERTY_NAME_USER_ID = "userId";
  public static final String PROPERTY_NAME_NAME = "name";
  public static final String PROPERTY_NAME_WALLET_ADDRESS = "walletAddress";
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
  public static final String COLUMN_NAME_ID = "id";
  public static final String COLUMN_NAME_LAST_KEEP_ALIVE_AT = "lastKeepAliveAt";

  public static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";
  public static final String EVENT_NAME_WALLET_CREATED = "WalletCreated";
  public static final String KEYSTORE_TYPE = "PKCS12";
  public static final String KEYSTORE_ETH_KEY_ALIAS = "hpp-eth-key";

  public static final String KEY_ALIAS_HPP_WALLET_ADDRESS = "hpp-wallet-addr";

  private Constants() {}
}
