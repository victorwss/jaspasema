/**
 * Framework for routing HTML-based REST applications.
 */
@SuppressWarnings({ "requires-automatic", "requires-transitive-automatic" })
module ninja.javahacker.jaspasema.sampleproject {
    requires static lombok;
    requires static com.github.spotbugs.annotations;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires ninja.javahacker.reifiedgeneric;
    requires io.javalin;
    requires ninja.javahacker.jaspasema.app;
    requires org.hibernate.orm.core;
    requires ninja.javahacker.jpasimpletransactions.core;
    requires ninja.javahacker.jpasimpletransactions.hibernate;
    requires mysql.connector.java;
}