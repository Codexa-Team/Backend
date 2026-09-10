package com.codexateam.platform.shared.infrastructure.persistence.jpa.configuration.strategy;

import io.github.encryptorcode.pluralize.Pluralize;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Locale;

/**
 * Implements a physical naming strategy for Hibernate.
 * This strategy converts logical names to snake_case and pluralizes table names.
 * e.g., "BookingRequest" entity becomes "booking_requests" table.
 */
public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }

    /**
     * Converts the logical table name to a pluralized snake_case physical table name.
     */
    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        final String pluralizedName = Pluralize.plural(logicalName.getText());
        return toSnakeCase(new Identifier(pluralizedName, logicalName.isQuoted()));
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(logicalName);
    }

    /**
     * Converts the logical column name to a snake_case physical column name.
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(logicalName);
    }

    /**
     * Helper method to convert a text to snake_case.
     * e.g., "myVariableName" becomes "my_variable_name".
     * @param identifier The logical identifier.
     * @return The snake_case identifier.
     */
    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null) {
            return null;
        }

        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
                .replaceAll(regex, replacement)
                .toLowerCase(Locale.ROOT);
        return Identifier.toIdentifier(newName, identifier.isQuoted());
    }
}
