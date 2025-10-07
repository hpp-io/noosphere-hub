package io.hpp.noosphere.hub.exception;

import static java.util.stream.Collectors.joining;

import io.hpp.noosphere.hub.config.Constants;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.zalando.problem.Exceptional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

public class NotFoundException extends Exception implements Problem, Exceptional {

  private final URI type;
  private final String title;
  private final StatusType status;
  private final String detail;
  private final URI instance;
  private final Map<String, Object> parameters;

  private String propertyName;

  private String value;
  private String errorKey;

  public String getPropertyName() {
    return propertyName;
  }

  public String getValue() {
    return value;
  }

  public String getErrorKey() {
    return errorKey;
  }


  @Override
  public String getMessage() {
    return Stream.of(getTitle(), getDetail())
        .filter(Objects::nonNull)
        .collect(joining(": "));
  }

  @Override
  public ThrowableProblem getCause() {
    // cast is safe, since the only way to set this is our constructor
    return (ThrowableProblem) super.getCause();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("errorKey", errorKey)
        .append("propertyName", propertyName)
        .append("value", value)
        .append(Problem.toString(this))
        .toString();
  }

  protected NotFoundException(@Nullable final URI type,
      @Nullable final String title,
      @Nullable final StatusType status,
      @Nullable final String detail,
      @Nullable final URI instance,
      @Nullable final ThrowableProblem cause,
      @Nullable final Map<String, Object> parameters) {
    super(cause);
    this.type = Optional.ofNullable(type).orElse(DEFAULT_TYPE);
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
    this.parameters = Optional.ofNullable(parameters).orElseGet(LinkedHashMap::new);
  }

  public NotFoundException(String propertyName, String value) {
    this(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, Status.NOT_FOUND, Constants.ERROR_KEY_NOT_FOUND, propertyName, value);
  }

  public NotFoundException(URI type, StatusType status, String defaultMessage, String errorKey, String propertyName, String value) {
    this(type, defaultMessage, status, null, null, null, CommonUtils.getAlertParameters(type, errorKey, propertyName, value));
    this.errorKey = errorKey;
    this.propertyName = propertyName;
    this.value = value;
  }

  public NotFoundException(URI type, String errorKey, String propertyName, String value) {
    this(type, Status.BAD_REQUEST, errorKey + "." + propertyName + "." + value, errorKey, propertyName, value);
  }

  public NotFoundException(URI type, StatusType status, String errorKey, String propertyName, String value) {
    this(type, status, errorKey + "." + propertyName + "." + value, errorKey, propertyName, value);
  }

  public NotFoundException(URI type, String defaultMessage, String errorKey, String propertyName, String value) {
    this(type, Status.BAD_REQUEST, defaultMessage, errorKey, propertyName, value);
  }

}
