package io.github.jagodevreede.sdkman.api;

public interface ProgressInformation {
    void publishProgress(int current);

    default void publishState(String state) {
    }
}
