package uk.antiperson.stackmob.config;

import java.io.IOException;

public interface Config {

    void load() throws IOException;

    void reloadConfig() throws IOException;
}
