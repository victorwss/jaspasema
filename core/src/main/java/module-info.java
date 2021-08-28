/**
 * Framework for routing HTML-based REST applications.
 */
@SuppressWarnings({ "requires-automatic", "requires-transitive-automatic" })
module ninja.javahacker.jaspasema.core {
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive ninja.javahacker.reifiedgeneric;
    requires transitive io.javalin;
    exports ninja.javahacker.jaspasema;
    exports ninja.javahacker.jaspasema.exceptions;
    exports ninja.javahacker.jaspasema.exceptions.badmapping;
    exports ninja.javahacker.jaspasema.exceptions.http;
    exports ninja.javahacker.jaspasema.exceptions.messages;
    exports ninja.javahacker.jaspasema.exceptions.paramproc;
    exports ninja.javahacker.jaspasema.exceptions.paramvalue;
    exports ninja.javahacker.jaspasema.exceptions.retproc;
    exports ninja.javahacker.jaspasema.exceptions.retvalue;
    exports ninja.javahacker.jaspasema.ext;
    exports ninja.javahacker.jaspasema.format;
    exports ninja.javahacker.jaspasema.processor;
    exports ninja.javahacker.jaspasema.service;
    exports ninja.javahacker.jaspasema.template;
    exports ninja.javahacker.jaspasema.verbs;
}