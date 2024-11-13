/**
 * Example HTML and REST applications.
 */
@SuppressWarnings({
    "requires-automatic", "requires-transitive-automatic" // com.github.spotbugs.annotations
})
open module ninja.javahacker.jaspasema.sampleproject {
    requires static com.github.spotbugs.annotations;
    requires static lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires io.javalin;
    requires mysql.connector.j;
    requires ninja.javahacker.reifiedgeneric;
    requires ninja.javahacker.jaspasema.app;
    requires ninja.javahacker.jpasimpletransactions.core;
    requires ninja.javahacker.jpasimpletransactions.eclipselink;
    requires org.eclipse.persistence.jpa;
}