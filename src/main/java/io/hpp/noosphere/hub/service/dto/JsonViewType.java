package io.hpp.noosphere.hub.service.dto;

public interface JsonViewType {
  public static class Shallow {}

  public static class Update extends Shallow {}
  public static class Full extends Update {}
}
