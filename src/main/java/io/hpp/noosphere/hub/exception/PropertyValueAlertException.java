package io.hpp.noosphere.hub.exception;

import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.net.URI;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

public class PropertyValueAlertException extends AbstractThrowableProblem {

  private final String propertyName;

  private final String value;
  private final String errorKey;

  public PropertyValueAlertException(URI type, StatusType status, String defaultMessage, String errorKey, String propertyName, String value) {
    super(type, defaultMessage, status, null, null, null, CommonUtils.getAlertParameters(type, errorKey, propertyName, value));
    this.errorKey = errorKey;
    this.propertyName = propertyName;
    this.value = value;
  }

  public PropertyValueAlertException(URI type, String errorKey, String propertyName, String value) {
    this(type, Status.BAD_REQUEST, errorKey + "." + propertyName + "." + value, errorKey, propertyName, value);
  }

  public PropertyValueAlertException(URI type, StatusType status, String errorKey, String propertyName, String value) {
    this(type, status, errorKey + "." + propertyName + "." + value, errorKey, propertyName, value);
  }

  public PropertyValueAlertException(URI type, String defaultMessage, String errorKey, String propertyName, String value) {
    this(type, Status.BAD_REQUEST, defaultMessage, errorKey, propertyName, value);
  }


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
  public String toString() {
    return new ToStringBuilder(this)
        .append("errorKey", errorKey)
        .append("propertyName", propertyName)
        .append("value", value)
        .append(Problem.toString(this))
        .toString();
  }

}
