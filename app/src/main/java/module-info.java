/**
 * Framework for developing HTML-based REST applications.
 */
@SuppressWarnings({
    "requires-automatic", "requires-transitive-automatic" // com.github.spotbugs.annotations
})
module ninja.javahacker.jaspasema.app {
    requires transitive static com.github.spotbugs.annotations;
    requires transitive static lombok;
    requires transitive ninja.javahacker.jaspasema.core;
    requires transitive ninja.javahacker.jpasimpletransactions.core;
    requires transitive ninja.javahacker.reifiedgeneric;
    exports ninja.javahacker.jaspasema.app;
}