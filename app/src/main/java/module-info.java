/**
 * Framework for developing HTML-based REST applications.
 */
@SuppressWarnings({ "requires-automatic", "requires-transitive-automatic" })
module ninja.javahacker.jaspasema.app {
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive ninja.javahacker.reifiedgeneric;
    requires transitive ninja.javahacker.jaspasema.core;
    requires transitive ninja.javahacker.jpasimpletransactions.core;
    exports ninja.javahacker.jaspasema.app;
}